package util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class ResetTokenManager {

    private static final Map<String, String[]> codes = new HashMap<>();
    private static final long EXPIRATION_MS = 15 * 60 * 1000; // 15 minutes

    public static String genererCode(String login) {
        codes.remove(login);

        String code = String.format("%06d", new Random().nextInt(999999));
        codes.put(login, new String[]{code, String.valueOf(System.currentTimeMillis())});
        return code;
    }


    public static boolean verifierCode(String login, String codeSaisi) {
        if (!codes.containsKey(login)) return false;
        String[] entry = codes.get(login);
        long createdAt = Long.parseLong(entry[1]);
        boolean nonExpire = (System.currentTimeMillis() - createdAt) < EXPIRATION_MS;
        return nonExpire && entry[0].equals(codeSaisi);
    }


    public static long secondesRestantes(String login) {
        if (!codes.containsKey(login)) return 0;
        long elapsed = System.currentTimeMillis() - Long.parseLong(codes.get(login)[1]);
        long restant = (EXPIRATION_MS - elapsed) / 1000;
        return Math.max(0, restant);
    }


    public static void invalider(String login) {
        codes.remove(login);
    }


    @Deprecated
    public static String genererToken(String login) { return genererCode(login); }

    @Deprecated
    public static boolean estValide(String token) { return false; }

    @Deprecated
    public static String getLogin(String token) { return null; }


    @Deprecated
    public static void invalider_token(String token) {}
}