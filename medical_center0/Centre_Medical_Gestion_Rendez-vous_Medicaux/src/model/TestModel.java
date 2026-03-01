package model;

import model.*; 
import java.util.Date;

public class TestModel {
    public static void main(String[] args) {
        Patient p = new Patient("Moubssite", 25, "Marrakech");
        Medecin m = new Medecin("Dr. Hassan", "Cardiologue", "0661 22 33 44");
        RendezVous rdv = new RendezVous(p, m, new Date(), "Consultation", 300.0);

        System.out.println("Patient : " + p);
        System.out.println("Médecin : " + m);
        System.out.println("RDV : " + rdv);
    }
}