package dao;

import model.Medecin;
import model.Patient;
import model.RendezVous;
import util.Connexion;

import java.sql.*;
import java.util.*;

/**
 * DAO RendezVous — table réelle : rendez_vous, colonne date : date_rdv
 */
public class RendezVousDao {

    // ── Lire tous les RDV ─────────────────────────────────────────────────────
    public List<RendezVous> findAll() throws Exception {
        String sql = """
            SELECT r.id, r.date_rdv, r.acte, r.tarif, r.statut,
                   p.id pid, p.nom pnom, p.age page, p.ville pville,
                   m.id mid, m.nom mnom, m.specialite mspec, m.telephone mtel
            FROM rendez_vous r
            LEFT JOIN patient  p ON r.patient_id = p.id
            LEFT JOIN medecin  m ON r.medecin_id = m.id
            ORDER BY r.date_rdv DESC
            """;
        List<RendezVous> list = new ArrayList<>();
        try (Connection conn = Connexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Patient p = new Patient(
                        rs.getInt("pid"),   rs.getString("pnom"),
                        rs.getInt("page"),  rs.getString("pville"));
                Medecin m = new Medecin(
                        rs.getInt("mid"),   rs.getString("mnom"),
                        rs.getString("mspec"), rs.getString("mtel"));
                RendezVous r = new RendezVous(
                        rs.getInt("id"), p, m,
                        rs.getTimestamp("date_rdv"),
                        rs.getString("acte"),
                        rs.getDouble("tarif"),
                        rs.getString("statut"));
                list.add(r);
            }
        }
        return list;
    }

    // ── Enregistrer un nouveau RDV ────────────────────────────────────────────
    public void save(RendezVous r) throws Exception {
        String sql = "INSERT INTO rendez_vous (patient_id, medecin_id, date_rdv, acte, tarif, statut) VALUES (?,?,?,?,?,?)";
        try (Connection conn = Connexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getPatient().getId());
            ps.setInt(2, r.getMedecin().getId());
            ps.setTimestamp(3, new Timestamp(r.getDate().getTime()));
            ps.setString(4, r.getActe());
            ps.setDouble(5, r.getTarif());
            ps.setString(6, r.getStatut());
            ps.executeUpdate();
        }
    }

    // ── Modifier un RDV ───────────────────────────────────────────────────────
    public void update(RendezVous r) throws Exception {
        String sql = "UPDATE rendez_vous SET patient_id=?, medecin_id=?, date_rdv=?, acte=?, tarif=?, statut=? WHERE id=?";
        try (Connection conn = Connexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getPatient().getId());
            ps.setInt(2, r.getMedecin().getId());
            ps.setTimestamp(3, new Timestamp(r.getDate().getTime()));
            ps.setString(4, r.getActe());
            ps.setDouble(5, r.getTarif());
            ps.setString(6, r.getStatut());
            ps.setInt(7, r.getId());
            ps.executeUpdate();
        }
    }

    // ── Mettre à jour uniquement le statut ────────────────────────────────────
    public void updateStatut(int id, String statut) throws Exception {
        String sql = "UPDATE rendez_vous SET statut=? WHERE id=?";
        try (Connection conn = Connexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    // ── Supprimer un RDV ──────────────────────────────────────────────────────
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM rendez_vous WHERE id=?";
        try (Connection conn = Connexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ── Nombre de consultations par mois ─────────────────────────────────────
    public Map<String, Integer> countByMonth() throws Exception {
        String sql = """
            SELECT DATE_FORMAT(date_rdv, '%Y-%m') AS mois, COUNT(*) AS total
            FROM rendez_vous
            GROUP BY mois
            ORDER BY mois
            """;
        Map<String, Integer> map = new LinkedHashMap<>();
        try (Connection conn = Connexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) map.put(rs.getString("mois"), rs.getInt("total"));
        }
        return map;
    }
}