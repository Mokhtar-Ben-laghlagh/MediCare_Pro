import util.Connexion;

public class TestConnexion {
    public static void main(String[] args) {
        try {
            Connexion c = Connexion.getInstance();
            System.out.println("Connexion réussie !");
            c.getConnection().close();
        } catch (Exception e) {
            System.err.println("Erreur connexion : " + e.getMessage());
            e.printStackTrace();
        }
    }
}