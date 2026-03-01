package model;

public class Patient {
    private int id;
    private String nom;
    private int age;
    private String ville;

    public Patient() {}

    public Patient(String nom, int age, String ville) {
        this.nom = nom;
        this.age = age;
        this.ville = ville;
    }

    public Patient(int id, String nom, int age, String ville) {
        this.id = id;
        this.nom = nom;
        this.age = age;
        this.ville = ville;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", age=" + age +
                ", ville='" + ville + '\'' +
                '}';
    }
}