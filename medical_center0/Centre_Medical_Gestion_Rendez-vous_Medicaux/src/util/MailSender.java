package util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;


public class MailSender {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    private static final String EMAIL_FROM = System.getenv().getOrDefault(
            "MEDICARE_MAIL",     "mokhtarbenlaghlagh@gmail.com");
    private static final String EMAIL_PASS = System.getenv().getOrDefault(
            "MEDICARE_MAIL_PWD", "spgeudvavlqpmwlw");


    public static void envoyerBienvenue(String emailDest, String login) throws Exception {
        Session session = creerSession();
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(EMAIL_FROM, "MediCare Pro"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDest));
        msg.setSubject("Bienvenue sur MediCare Pro \uD83C\uDFE5");
        msg.setContent(buildWelcomeHtml(login, emailDest), "text/html; charset=UTF-8");
        Transport.send(msg);
    }


    public static void envoyerCodeVerification(String emailDest, String login, String code) throws Exception {
        Session session = creerSession();
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(EMAIL_FROM, "MediCare Pro"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDest));
        msg.setSubject("Votre code de vérification — MediCare Pro");
        msg.setContent(buildCodeHtml(login, code), "text/html; charset=UTF-8");
        Transport.send(msg);
    }


    private static Session creerSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host",            SMTP_HOST);
        props.put("mail.smtp.port",            SMTP_PORT);
        props.put("mail.smtp.ssl.protocols",   "TLSv1.2");
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASS);
            }
        });
    }

    private static String buildWelcomeHtml(String login, String email) {
        return "<!DOCTYPE html><html lang='fr'><head><meta charset='UTF-8'></head>"
                + "<body style='margin:0;padding:0;background:#F8F9FA;font-family:Arial,sans-serif;'>"
                + "<table width='100%' cellpadding='0' cellspacing='0'><tr><td align='center' style='padding:40px 20px;'>"
                + "<table width='560' cellpadding='0' cellspacing='0' style='background:#fff;border-radius:12px;"
                + "overflow:hidden;box-shadow:0 2px 12px rgba(0,0,0,0.08);'>"
                + "<tr><td style='background:linear-gradient(135deg,#1557B0,#34A853);padding:36px 40px;text-align:center;'>"
                + "<p style='margin:0;font-size:44px;'>&#127973;</p>"
                + "<h1 style='margin:10px 0 0;color:#fff;font-size:24px;font-weight:700;'>MediCare Pro</h1>"
                + "<p style='margin:6px 0 0;color:rgba(255,255,255,0.85);font-size:13px;'>Système de gestion des rendez-vous médicaux</p>"
                + "</td></tr>"
                + "<tr><td style='padding:36px 40px;'>"
                + "<h2 style='margin:0 0 12px;color:#212529;font-size:20px;'>&#x1F44B;  Bienvenue !</h2>"
                + "<p style='margin:0 0 20px;color:#6C757D;font-size:14px;line-height:1.7;'>"
                + "Votre compte <strong>MediCare Pro</strong> a été créé avec succès.</p>"
                + "<div style='background:#F8F9FA;border:1px solid #DEE2E6;border-radius:8px;padding:16px 20px;margin:0 0 24px;'>"
                + "<p style='margin:0 0 4px;color:#6C757D;font-size:11px;font-weight:700;text-transform:uppercase;'>IDENTIFIANT DE CONNEXION</p>"
                + "<p style='margin:0;color:#1A73E8;font-size:20px;font-weight:700;'>" + login + "</p>"
                + "<p style='margin:4px 0 0;color:#ADB5BD;font-size:12px;'>Email : " + email + "</p>"
                + "</div>"
                + "<div style='background:#E8F0FE;border-left:4px solid #1A73E8;border-radius:4px;padding:12px 16px;'>"
                + "<p style='margin:0;color:#1557B0;font-size:13px;'>&#128274; Ne partagez jamais votre mot de passe.</p>"
                + "</div></td></tr>"
                + "<tr><td style='background:#F8F9FA;padding:18px 40px;border-top:1px solid #DEE2E6;text-align:center;'>"
                + "<p style='margin:0;color:#ADB5BD;font-size:12px;'>&#169; 2024 MediCare Pro &mdash; Email automatique, ne pas répondre.</p>"
                + "</td></tr></table></td></tr></table></body></html>";
    }

    private static String buildCodeHtml(String login, String code) {
        String codeDisplay = code.substring(0, 3) + " " + code.substring(3);

        return "<!DOCTYPE html><html lang='fr'><head><meta charset='UTF-8'></head>"
                + "<body style='margin:0;padding:0;background:#F8F9FA;font-family:Arial,sans-serif;'>"
                + "<table width='100%' cellpadding='0' cellspacing='0'><tr><td align='center' style='padding:40px 20px;'>"
                + "<table width='560' cellpadding='0' cellspacing='0' style='background:#fff;border-radius:12px;"
                + "overflow:hidden;box-shadow:0 2px 12px rgba(0,0,0,0.08);'>"

                + "<tr><td style='background:linear-gradient(135deg,#1557B0,#1A73E8);padding:32px 40px;text-align:center;'>"
                + "<p style='margin:0;font-size:40px;'>&#128274;</p>"
                + "<h1 style='margin:10px 0 0;color:#fff;font-size:22px;font-weight:700;'>Code de vérification</h1>"
                + "<p style='margin:6px 0 0;color:rgba(255,255,255,0.85);font-size:13px;'>MediCare Pro</p>"
                + "</td></tr>"

                + "<tr><td style='padding:36px 40px;text-align:center;'>"
                + "<p style='margin:0 0 8px;color:#6C757D;font-size:14px;'>Bonjour <strong>" + login + "</strong>,</p>"
                + "<p style='margin:0 0 28px;color:#6C757D;font-size:14px;line-height:1.6;'>"
                + "Voici votre code pour réinitialiser votre mot de passe.<br>"
                + "Il est valable <strong>15 minutes</strong>.</p>"

                + "<div style='background:#F0F4FF;border:2px dashed #1A73E8;border-radius:12px;"
                + "padding:24px 40px;margin:0 auto 28px;display:inline-block;'>"
                + "<p style='margin:0 0 4px;color:#6C757D;font-size:11px;font-weight:700;"
                + "text-transform:uppercase;letter-spacing:2px;'>VOTRE CODE</p>"
                + "<p style='margin:0;color:#1A73E8;font-size:44px;font-weight:900;"
                + "letter-spacing:8px;font-family:monospace;'>" + codeDisplay + "</p>"
                + "</div>"

                + "<p style='margin:0 0 24px;color:#6C757D;font-size:13px;line-height:1.6;'>"
                + "Saisissez ce code dans l'application MediCare Pro pour continuer.</p>"

                + "<div style='background:#FFF8E1;border-left:4px solid #FBBC05;border-radius:4px;"
                + "padding:12px 16px;text-align:left;'>"
                + "<p style='margin:0;color:#856404;font-size:13px;'>"
                + "&#9888;&#65039; Si vous n'avez pas demandé ce code, ignorez cet email. "
                + "Votre mot de passe reste inchangé.</p>"
                + "</div></td></tr>"

                + "<tr><td style='background:#F8F9FA;padding:18px 40px;border-top:1px solid #DEE2E6;text-align:center;'>"
                + "<p style='margin:0;color:#ADB5BD;font-size:12px;'>&#169; 2024 MediCare Pro</p>"
                + "</td></tr></table></td></tr></table></body></html>";
    }
}