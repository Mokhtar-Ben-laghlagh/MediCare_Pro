package model;

/**
 * Modèle utilisateur — login, password, email.
 */
public class Utilisateur {

    private int    id;
    private String login;
    private String password;
    private String email;

    // Pour l'authentification (lecture DB)
    public Utilisateur(int id, String login, String password) {
        this.id       = id;
        this.login    = login;
        this.password = password;
    }

    // Pour l'inscription (sans id)
    public Utilisateur(String login, String password, String email) {
        this.login    = login;
        this.password = password;
        this.email    = email;
    }

    // Constructeur complet (lecture DB avec email)
    public Utilisateur(int id, String login, String password, String email) {
        this.id       = id;
        this.login    = login;
        this.password = password;
        this.email    = email;
    }

    public int    getId()                    { return id; }
    public void   setId(int id)              { this.id = id; }

    public String getLogin()                 { return login; }
    public void   setLogin(String login)     { this.login = login; }

    public String getPassword()              { return password; }
    public void   setPassword(String p)      { this.password = p; }

    public String getEmail()                 { return email; }
    public void   setEmail(String email)     { this.email = email; }

    @Override
    public String toString() {
        return "Utilisateur{id=" + id + ", login='" + login + "', email='" + email + "'}";
    }
}