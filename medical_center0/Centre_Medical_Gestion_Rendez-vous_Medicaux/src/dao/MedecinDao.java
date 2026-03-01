package dao;

import model.Medecin;
import util.Connexion;

import java.sql.*;
import java.util.*;

/**
 * DAO Medecin — table réelle : medecin
 */
public class MedecinDao {

    public List<Medecin> findAll() throws Exception {
        String sql = "SELECT id, nom, specialite, telephone FROM medecin ORDER BY nom";
        List<Medecin> list = new ArrayList<>();
        try (Connection conn = Connexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(new Medecin(rs.getInt("id"), rs.getString("nom"),
                        rs.getString("specialite"), rs.getString("telephone")));
        }
        return list;
    }

    public Medecin findById(int id) throws Exception {
        String sql = "SELECT id, nom, specialite, telephone FROM medecin WHERE id=?";
        try (Connection conn = Connexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Medecin(rs.getInt("id"), rs.getString("nom"),
                            rs.getString("specialite"), rs.getString("telephone"));
            }
        }
        return null;
    }

    public void save(Medecin m) throws Exception {
        String sql = "INSERT INTO medecin (nom, specialite, telephone) VALUES (?,?,?)";
        try (Connection conn = Connexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getNom());
            ps.setString(2, m.getSpecialite());
            ps.setString(3, m.getTelephone());
            ps.executeUpdate();
        }
    }

    public void update(Medecin m) throws Exception {
        String sql = "UPDATE medecin SET nom=?, specialite=?, telephone=? WHERE id=?";
        try (Connection conn = Connexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getNom());
            ps.setString(2, m.getSpecialite());
            ps.setString(3, m.getTelephone());
            ps.setInt(4, m.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws Exception {
        String sql = "DELETE FROM medecin WHERE id=?";
        try (Connection conn = Connexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}