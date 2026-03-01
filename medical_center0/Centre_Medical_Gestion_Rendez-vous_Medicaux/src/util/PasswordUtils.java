package util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS    = "0123456789";
    private static final String SPECIAL   = "@#$%!&*";
    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL;
    private static final SecureRandom random = new SecureRandom();

    /**
     * Génère un mot de passe temporaire de 10 caractères.
     */
    public static String genererMotDePasseTemporaire() {
        return genererMotDePasse(10);
    }

    /**
     * Génère un mot de passe de longueur personnalisée.
     */
    public static String genererMotDePasse(int longueur) {
        if (longueur < 4) longueur = 4;
        StringBuilder sb = new StringBuilder();
        sb.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        sb.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        sb.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        sb.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));
        for (int i = 4; i < longueur; i++) {
            sb.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }
        return melanger(sb.toString());
    }

    /**
     * Hash un mot de passe avec SHA-256 + sel aléatoire.
     * Format stocké en BD : "sel:hash" (les deux en Base64)
     */
    public static String hasher(String motDePasse) {
        try {
            // Générer un sel aléatoire de 16 octets
            byte[] sel = new byte[16];
            random.nextBytes(sel);
            String selBase64 = Base64.getEncoder().encodeToString(sel);

            // Hasher : SHA-256(sel + motDePasse)
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(sel);
            byte[] hash = md.digest(motDePasse.getBytes("UTF-8"));
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            // Stocker sous forme "sel:hash"
            return selBase64 + ":" + hashBase64;

        } catch (Exception e) {
            throw new RuntimeException("Erreur hachage mot de passe", e);
        }
    }

    /**
     * Vérifie un mot de passe contre le hash stocké en BD.
     * @param motDePasse  le mot de passe saisi par l'utilisateur
     * @param hashStocke  la valeur "sel:hash" stockée en BD
     */
    public static boolean verifier(String motDePasse, String hashStocke) {
        try {
            String[] parties = hashStocke.split(":");
            if (parties.length != 2) return false;

            byte[] sel      = Base64.getDecoder().decode(parties[0]);
            byte[] hashRef  = Base64.getDecoder().decode(parties[1]);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(sel);
            byte[] hashSaisi = md.digest(motDePasse.getBytes("UTF-8"));

            // Comparaison sécurisée (temps constant)
            return MessageDigest.isEqual(hashRef, hashSaisi);

        } catch (Exception e) {
            return false;
        }
    }

    // Mélange les caractères pour éviter un pattern prévisible
    private static String melanger(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char tmp = chars[i];
            chars[i] = chars[j];
            chars[j] = tmp;
        }
        return new String(chars);
    }
}