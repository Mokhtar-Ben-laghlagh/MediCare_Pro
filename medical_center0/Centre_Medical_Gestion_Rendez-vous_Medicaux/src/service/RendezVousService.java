package service;

import dao.RendezVousDao;
import model.Medecin;
import model.Patient;
import model.RendezVous;

import java.util.*;

/**
 * Service RendezVous — ajout :
 *   - Filtrage par spécialité et par période
 *   - Calcul des recettes (totale, par médecin, par période)
 *   - Suivi des actes réalisés
 *   - Mise à jour du statut
 */
public class RendezVousService {

    private final RendezVousDao dao = new RendezVousDao();

    // ── CRUD de base ──────────────────────────────────────────────────────────

    public List<RendezVous> getAllRDV() throws Exception {
        return dao.findAll();
    }

    public void planifierRDV(Patient p, Medecin m, Date date, String acte, double tarif) throws Exception {
        dao.save(new RendezVous(p, m, date, acte, tarif));
    }

    public void update(RendezVous rdv) throws Exception {
        dao.update(rdv);
    }

    public void delete(int id) throws Exception {
        dao.delete(id);
    }

    // ── Mise à jour du statut ─────────────────────────────────────────────────

    public void marquerRealise(int id) throws Exception {
        dao.updateStatut(id, RendezVous.STATUT_REALISE);
    }

    public void marquerAnnule(int id) throws Exception {
        dao.updateStatut(id, RendezVous.STATUT_ANNULE);
    }

    public void marquerPlanifie(int id) throws Exception {
        dao.updateStatut(id, RendezVous.STATUT_PLANIFIE);
    }

    // ── Filtrage par spécialité ───────────────────────────────────────────────

    /**
     * Retourne les RDV dont le médecin a la spécialité donnée.
     * Si specialite est null ou vide, retourne tous les RDV.
     */
    public List<RendezVous> filtrerParSpecialite(String specialite) throws Exception {
        List<RendezVous> tous = dao.findAll();
        if (specialite == null || specialite.isBlank() || specialite.equals("Toutes")) return tous;
        List<RendezVous> resultat = new ArrayList<>();
        for (RendezVous r : tous) {
            if (r.getMedecin() != null &&
                    specialite.equalsIgnoreCase(r.getMedecin().getSpecialite())) {
                resultat.add(r);
            }
        }
        return resultat;
    }

    // ── Filtrage par période ──────────────────────────────────────────────────

    /**
     * Retourne les RDV compris entre debut et fin (inclus).
     * Si debut ou fin est null, pas de borne de ce côté.
     */
    public List<RendezVous> filtrerParPeriode(Date debut, Date fin) throws Exception {
        List<RendezVous> tous = dao.findAll();
        List<RendezVous> resultat = new ArrayList<>();
        for (RendezVous r : tous) {
            boolean apresDebut = debut == null || !r.getDate().before(debut);
            boolean avantFin   = fin   == null || !r.getDate().after(fin);
            if (apresDebut && avantFin) resultat.add(r);
        }
        return resultat;
    }

    /**
     * Filtrage combiné : spécialité + période.
     */
    public List<RendezVous> filtrer(String specialite, Date debut, Date fin) throws Exception {
        List<RendezVous> parSpec = filtrerParSpecialite(specialite);
        List<RendezVous> resultat = new ArrayList<>();
        for (RendezVous r : parSpec) {
            boolean apresDebut = debut == null || !r.getDate().before(debut);
            boolean avantFin   = fin   == null || !r.getDate().after(fin);
            if (apresDebut && avantFin) resultat.add(r);
        }
        return resultat;
    }

    // ── Suivi des actes réalisés ──────────────────────────────────────────────

    /**
     * Retourne uniquement les RDV avec statut REALISE.
     */
    public List<RendezVous> getActesRealises() throws Exception {
        List<RendezVous> tous = dao.findAll();
        List<RendezVous> resultat = new ArrayList<>();
        for (RendezVous r : tous) {
            if (RendezVous.STATUT_REALISE.equals(r.getStatut())) resultat.add(r);
        }
        return resultat;
    }

    /**
     * Retourne les actes réalisés filtrés par période.
     */
    public List<RendezVous> getActesRealisesPeriode(Date debut, Date fin) throws Exception {
        List<RendezVous> realises = getActesRealises();
        List<RendezVous> resultat = new ArrayList<>();
        for (RendezVous r : realises) {
            boolean apresDebut = debut == null || !r.getDate().before(debut);
            boolean avantFin   = fin   == null || !r.getDate().after(fin);
            if (apresDebut && avantFin) resultat.add(r);
        }
        return resultat;
    }

    // ── Calcul des recettes ───────────────────────────────────────────────────

    /**
     * Recette totale — somme des tarifs de tous les RDV REALISES.
     */
    public double calculerRecetteTotale() throws Exception {
        double total = 0;
        for (RendezVous r : getActesRealises()) total += r.getTarif();
        return total;
    }

    /**
     * Recette sur une période donnée (uniquement RDV REALISES).
     */
    public double calculerRecettePeriode(Date debut, Date fin) throws Exception {
        double total = 0;
        for (RendezVous r : getActesRealisesPeriode(debut, fin)) total += r.getTarif();
        return total;
    }

    /**
     * Recettes par médecin — Map<nomMedecin, totalTarifs> (RDV REALISES uniquement).
     */
    public Map<String, Double> recettesParMedecin() throws Exception {
        Map<String, Double> map = new LinkedHashMap<>();
        for (RendezVous r : getActesRealises()) {
            String nom = r.getMedecin() != null ? r.getMedecin().getNom() : "Inconnu";
            map.merge(nom, r.getTarif(), Double::sum);
        }
        return map;
    }

    /**
     * Recettes par médecin sur une période.
     */
    public Map<String, Double> recettesParMedecinPeriode(Date debut, Date fin) throws Exception {
        Map<String, Double> map = new LinkedHashMap<>();
        for (RendezVous r : getActesRealisesPeriode(debut, fin)) {
            String nom = r.getMedecin() != null ? r.getMedecin().getNom() : "Inconnu";
            map.merge(nom, r.getTarif(), Double::sum);
        }
        return map;
    }

    // ── Statistiques ─────────────────────────────────────────────────────────

    /**
     * Nombre de consultations par mois — tous statuts confondus.
     * Retourne Map<"YYYY-MM", count>
     */
    public Map<String, Integer> consultationsParMois() throws Exception {
        return dao.countByMonth();
    }

    /**
     * Liste des spécialités distinctes présentes dans les RDV.
     * Utile pour remplir la ComboBox de filtrage.
     */
    public List<String> getSpecialitesDisponibles() throws Exception {
        List<RendezVous> tous = dao.findAll();
        Set<String> specs = new LinkedHashSet<>();
        specs.add("Toutes");
        for (RendezVous r : tous) {
            if (r.getMedecin() != null && r.getMedecin().getSpecialite() != null)
                specs.add(r.getMedecin().getSpecialite());
        }
        return new ArrayList<>(specs);
    }
}