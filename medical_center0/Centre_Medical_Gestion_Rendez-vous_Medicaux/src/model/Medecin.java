package model;

public class Medecin {
    private int id;
    private String nom;
    private String specialite;
    private String telephone;


    public Medecin() {}

    public Medecin(String nom, String specialite, String telephone) {
        this.nom = nom;
        this.specialite = specialite;
        this.telephone = telephone;
    }

    public Medecin(int id, String nom, String specialite, String telephone) {
        this.id = id;
        this.nom = nom;
        this.specialite = specialite;
        this.telephone = telephone;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    @Override
    public String toString() {
        return "Medecin{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", specialite='" + specialite + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }
}