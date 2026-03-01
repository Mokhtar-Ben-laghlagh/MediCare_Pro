package model;

import java.util.Date;

/**
 * Modèle RendezVous — ajout du champ statut.
 * Statuts possibles : PLANIFIE | REALISE | ANNULE
 */
public class RendezVous {

    public static final String STATUT_PLANIFIE = "PLANIFIE";
    public static final String STATUT_REALISE  = "REALISE";
    public static final String STATUT_ANNULE   = "ANNULE";

    private int     id;
    private Patient patient;
    private Medecin medecin;
    private Date    date;
    private String  acte;
    private double  tarif;
    private String  statut;   // ← NOUVEAU

    // ── Constructeurs ─────────────────────────────────────────────────────────

    /** Nouveau RDV (statut = PLANIFIE par défaut) */
    public RendezVous(Patient patient, Medecin medecin, Date date, String acte, double tarif) {
        this.patient = patient;
        this.medecin = medecin;
        this.date    = date;
        this.acte    = acte;
        this.tarif   = tarif;
        this.statut  = STATUT_PLANIFIE;
    }

    /** Constructeur complet (lecture depuis la base) */
    public RendezVous(int id, Patient patient, Medecin medecin, Date date, String acte, double tarif, String statut) {
        this.id      = id;
        this.patient = patient;
        this.medecin = medecin;
        this.date    = date;
        this.acte    = acte;
        this.tarif   = tarif;
        this.statut  = statut != null ? statut : STATUT_PLANIFIE;
    }

    /** Constructeur sans statut (compatibilité ancienne version) */
    public RendezVous(int id, Patient patient, Medecin medecin, Date date, String acte, double tarif) {
        this(id, patient, medecin, date, acte, tarif, STATUT_PLANIFIE);
    }

    // ── Getters / Setters ────────────────────────────────────────────────────
    public int     getId()                  { return id; }
    public void    setId(int id)            { this.id = id; }

    public Patient getPatient()             { return patient; }
    public void    setPatient(Patient p)    { this.patient = p; }

    public Medecin getMedecin()             { return medecin; }
    public void    setMedecin(Medecin m)    { this.medecin = m; }

    public Date    getDate()                { return date; }
    public void    setDate(Date date)       { this.date = date; }

    public String  getActe()               { return acte; }
    public void    setActe(String acte)    { this.acte = acte; }

    public double  getTarif()              { return tarif; }
    public void    setTarif(double tarif)  { this.tarif = tarif; }

    public String  getStatut()             { return statut; }
    public void    setStatut(String s)     { this.statut = s; }

    /** Retourne une étiquette lisible avec emoji pour l'affichage */
    public String  getStatutLabel() {
        return switch (statut) {
            case STATUT_REALISE -> "✅ Réalisé";
            case STATUT_ANNULE  -> "❌ Annulé";
            default             -> "📅 Planifié";
        };
    }

    @Override
    public String toString() {
        return "RendezVous{id=" + id + ", acte='" + acte + "', tarif=" + tarif + ", statut=" + statut + "}";
    }
}