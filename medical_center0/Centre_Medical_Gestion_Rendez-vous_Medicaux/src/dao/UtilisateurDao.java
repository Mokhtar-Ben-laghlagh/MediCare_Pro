package dao;

import model.Utilisateur;
import util.Connexion;
import util.PasswordUtils;

import java.sql.*;

/**
 * DAO Utilisateur.
 *
 * Colonnes attendues dans la table 'utilisateurs' existante :
 *   id INT PK AUTO_INCREMENT, login VARCHAR UNIQUE,
 *   password VARCHAR(255), email VARCHAR UNIQUE
 *
 * Si la colonne email est absente, ajoutez-la :
 *   ALTER TABLE utilisateurs ADD COLUMN email VARCHAR(150) UNIQUE;
 */
public class UtilisateurDao {

    // ── Authentification ──────────────────────────────────────────────────────
    public Utilisateur authentifier(String login, String password) throws Exception {
        // ✅ On récupère d'abord l'utilisateur par login seul
        String sql = "SELECT id, login, password, email FROM utilisateurs WHERE login = ?";
        try (Connection conn = Connexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashStocke = rs.getString("password");
                    // ✅ Vérification du hash au lieu de comparer en clair
                    if (PasswordUtils.verifier(password, hashStocke)) {
                        return new Utilisateur(
                                rs.getInt("id"),
                                rs.getString("login"),
                                rs.getString("password"),
                                rs.getString("email")
                        );
                    }
                }
            }
        }
        return null;
    }

    // ── Inscription ───────────────────────────────────────────────────────────
    public void inscrire(Utilisateur u) throws Exception {
        // ✅ Hash du mot de passe avant stockage
        String hash = PasswordUtils.hasher(u.getPassword());

        String sql = "INSERT INTO utilisateurs (login, password, email) VALUES (?, ?, ?)";
        try (Connection conn = Connexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getLogin());
            ps.setString(2, hash);   // ✅ hash au lieu de mot de passe en clair
            ps.setString(3, u.getEmail());
            ps.executeUpdate();
        }
    }

    // ── Recherche par login ───────────────────────────────────────────────────
    public Utilisateur findByLogin(String login) throws Exception {
        String sql = "SELECT id, login, password, email FROM utilisateurs WHERE login = ?";
        try (Connection conn = Connexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Utilisateur(
                            rs.getInt("id"),
                            rs.getString("login"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                }
            }
        }
        return null;
    }

    // ── Recherche par email ───────────────────────────────────────────────────
    public Utilisateur findByEmail(String email) throws Exception {
        String sql = "SELECT id, login, password, email FROM utilisateurs WHERE email = ?";
        try (Connection conn = Connexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Utilisateur(
                            rs.getInt("id"),
                            rs.getString("login"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                }
            }
        }
        return null;
    }

    // ── Mise à jour du mot de passe ───────────────────────────────────────────
    public void updatePassword(int userId, String newPassword) throws Exception {
        // ✅ Hash du nouveau mot de passe avant stockage
        String hash = PasswordUtils.hasher(newPassword);

        String sql = "UPDATE utilisateurs SET password = ? WHERE id = ?";
        try (Connection conn = Connexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hash);   // ✅ hash au lieu de mot de passe en clair
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }
}