package dao;

import model.Patient;
import util.Connexion;

import java.sql.*;
import java.util.*;

/**
 * DAO Patient — table réelle : patient
 */
public class PatientDao {

    public List<Patient> findAll() throws Exception {
        String sql = "SELECT id, nom, age, ville FROM patient ORDER BY nom";
        List<Patient> list = new ArrayList<>();
        try (Connection conn = Connexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(new Patient(rs.getInt("id"), rs.getString("nom"),
                        rs.getInt("age"), rs.getString("ville")));
        }
        return list;
    }

    public Patient findById(int id) throws Exception {
        String sql = "SELECT id, nom, age, ville FROM patient WHERE id=?";
        try (Connection conn = Connexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Patient(rs.getInt("id"), rs.getString("nom"),
                            rs.getInt("age"), rs.getString("ville"));
            }
        }
        return null;
    }

    public void save(Patient p) throws Exception {
        String sql = "INSERT INTO patient (nom, age, ville) VALUES (?,?,?)";
        try (Connection conn = Connexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setInt(2, p.getAge());
            ps.setString(3, p.getVille());
            ps.executeUpdate();
        }
    }

    public void update(Patient p) throws Exception {
        String sql = "UPDATE patient SET nom=?, age=?, ville=? WHERE id=?";
        try (Connection conn = Connexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setInt(2, p.getAge());
            ps.setString(3, p.getVille());
            ps.setInt(4, p.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws Exception {
        String sql = "DELETE FROM patient WHERE id=?";
        try (Connection conn = Connexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}