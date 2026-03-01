package ui;

import dao.UtilisateurDao;
import model.Utilisateur;
import util.MailSender;
import util.ResetTokenManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LoginForm extends JFrame {

    private JTextField     usernameField;
    private JPasswordField passwordField;

    public LoginForm() {
        setTitle("Connexion — Gestion RDV Médicaux");
        setSize(900, 580);
        setMinimumSize(new Dimension(800, 520));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel left = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ModernTheme.PRIMARY_DARK, 0, getHeight(), ModernTheme.PRIMARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillOval(-60, -60, 280, 280);
                g2.fillOval(getWidth() - 120, getHeight() - 120, 240, 240);
                g2.setColor(new Color(255, 255, 255, 10));
                g2.fillOval(40, getHeight() / 2, 200, 200);
            }
        };
        left.setPreferredSize(new Dimension(380, 0));
        left.setLayout(new GridBagLayout());

        JPanel leftContent = new JPanel();
        leftContent.setOpaque(false);
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));
        leftContent.setBorder(new EmptyBorder(0, 40, 0, 40));

        JLabel iconLbl = new JLabel("🏥", SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appName = new JLabel("MediCare Pro");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 28));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("<html><div style='text-align:center'>Système de gestion<br>des rendez-vous médicaux</div></html>");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tagline.setForeground(new Color(255, 255, 255, 200));
        tagline.setHorizontalAlignment(SwingConstants.CENTER);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftContent.add(iconLbl);
        leftContent.add(Box.createVerticalStrut(12));
        leftContent.add(appName);
        leftContent.add(Box.createVerticalStrut(10));
        leftContent.add(tagline);
        left.add(leftContent);

        // ── Panneau droit ───────────────────────────────────────────────────
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(ModernTheme.BG);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                new ModernTheme.RoundBorder(ModernTheme.BORDER_COLOR, 12, 1),
                new EmptyBorder(40, 48, 36, 48)
        ));
        form.setMaximumSize(new Dimension(380, 520));

        JLabel title = new JLabel("Connexion");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(ModernTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Bienvenue ! Veuillez vous identifier.");
        sub.setFont(ModernTheme.FONT_SMALL);
        sub.setForeground(ModernTheme.TEXT_SECONDARY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userLbl = new JLabel("Identifiant");
        userLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLbl.setForeground(ModernTheme.TEXT_SECONDARY);
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = ModernTheme.createTextField(20);
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JLabel passLbl = new JLabel("Mot de passe");
        passLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLbl.setForeground(ModernTheme.TEXT_SECONDARY);
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = ModernTheme.createPasswordField(20);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        passwordField.addActionListener(e -> handleLogin());

        // Lien mot de passe oublié
        JLabel forgotLabel = new JLabel("<html><u>Mot de passe oublié ?</u></html>");
        forgotLabel.setFont(ModernTheme.FONT_SMALL);
        forgotLabel.setForeground(ModernTheme.PRIMARY);
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        forgotLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                handleForgotPassword();
            }
        });

        // Bouton connexion
        JButton loginBtn = new JButton("Se connecter") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed() ? ModernTheme.PRIMARY_DARK
                        : getModel().isRollover() ? ModernTheme.PRIMARY.brighter()
                        : ModernTheme.PRIMARY;
                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setContentAreaFilled(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setFocusPainted(false);
        loginBtn.setOpaque(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginBtn.addActionListener(e -> handleLogin());

        // Séparateur + lien inscription
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(ModernTheme.BORDER_COLOR);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel registerLabel = new JLabel("<html>Pas encore de compte ? <u style='color:#1A73E8;'>S'inscrire</u></html>");
        registerLabel.setFont(ModernTheme.FONT_SMALL);
        registerLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                new RegisterForm().setVisible(true);
            }
        });

        form.add(title);
        form.add(Box.createVerticalStrut(4));
        form.add(sub);
        form.add(Box.createVerticalStrut(28));
        form.add(userLbl);
        form.add(Box.createVerticalStrut(6));
        form.add(usernameField);
        form.add(Box.createVerticalStrut(16));
        form.add(passLbl);
        form.add(Box.createVerticalStrut(6));
        form.add(passwordField);
        form.add(Box.createVerticalStrut(8));
        form.add(forgotLabel);
        form.add(Box.createVerticalStrut(24));
        form.add(loginBtn);
        form.add(Box.createVerticalStrut(20));
        form.add(sep);
        form.add(Box.createVerticalStrut(14));
        form.add(registerLabel);

        right.add(form);
        add(left,  BorderLayout.WEST);
        add(right, BorderLayout.CENTER);
    }

    // ── Connexion ─────────────────────────────────────────────────────────────
    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();
        try {
            UtilisateurDao dao = new UtilisateurDao();
            Utilisateur utilisateur = dao.authentifier(user, pass);
            if (utilisateur != null) {
                dispose();
                new MainWindow().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Identifiants incorrects. Veuillez réessayer.",
                        "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion : " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Mot de passe oublié — ÉTAPE 1 : saisir le login ──────────────────────
    private void handleForgotPassword() {
        JDialog step1 = new JDialog(this, "Mot de passe oublié", true);
        step1.setSize(420, 260);
        step1.setLocationRelativeTo(this);
        step1.setResizable(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(28, 36, 28, 36));

        JLabel title = new JLabel("🔒  Réinitialisation du mot de passe");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(ModernTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("<html>Saisissez votre identifiant. Un code à 6 chiffres<br>sera envoyé à votre adresse email.</html>");
        sub.setFont(ModernTheme.FONT_SMALL);
        sub.setForeground(ModernTheme.TEXT_SECONDARY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel loginLbl = new JLabel("Identifiant");
        loginLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        loginLbl.setForeground(ModernTheme.TEXT_SECONDARY);
        loginLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField loginField = ModernTheme.createTextField(20);
        loginField.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        btns.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton cancelBtn = ModernTheme.createSecondaryButton("Annuler");
        JButton sendBtn   = ModernTheme.createPrimaryButton("Envoyer le code");
        cancelBtn.addActionListener(e -> step1.dispose());

        sendBtn.addActionListener(e -> {
            String login = loginField.getText().trim();
            if (login.isEmpty()) {
                JOptionPane.showMessageDialog(step1, "Veuillez saisir votre identifiant.", "Champ vide", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                UtilisateurDao dao = new UtilisateurDao();
                Utilisateur u = dao.findByLogin(login);

                // Message générique pour la sécurité (ne pas révéler si le login existe)
                if (u == null || u.getEmail() == null || u.getEmail().isEmpty()) {
                    JOptionPane.showMessageDialog(step1,
                            "Si cet identifiant existe, un code a été envoyé à l'email associé.",
                            "Code envoyé", JOptionPane.INFORMATION_MESSAGE);
                    step1.dispose();
                    return;
                }

                // Générer et envoyer le code
                String code = ResetTokenManager.genererCode(login);
                new Thread(() -> {
                    try {
                        MailSender.envoyerCodeVerification(u.getEmail(), login, code);
                    } catch (Exception ex) {
                        System.err.println("[MailSender] Erreur envoi code : " + ex.getMessage());
                    }
                }).start();

                step1.dispose();
                // Ouvrir l'étape 2 : saisie du code
                showStep2(login, u.getEmail());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(step1, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        btns.add(cancelBtn);
        btns.add(sendBtn);

        content.add(title);
        content.add(Box.createVerticalStrut(8));
        content.add(sub);
        content.add(Box.createVerticalStrut(18));
        content.add(loginLbl);
        content.add(Box.createVerticalStrut(4));
        content.add(loginField);
        content.add(Box.createVerticalStrut(20));
        content.add(btns);

        step1.setContentPane(content);
        step1.setVisible(true);
    }

    // ── Mot de passe oublié — ÉTAPE 2 : saisir le code reçu par email ─────────
    private void showStep2(String login, String email) {
        JDialog step2 = new JDialog(this, "Vérification du code", true);
        step2.setSize(420, 320);
        step2.setLocationRelativeTo(this);
        step2.setResizable(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(28, 36, 28, 36));

        JLabel title = new JLabel("📧  Code de vérification");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(ModernTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("<html>Un code à 6 chiffres a été envoyé à<br><b>"
                + maskEmail(email) + "</b><br>Saisissez-le ci-dessous (valable 15 min).</html>");
        sub.setFont(ModernTheme.FONT_SMALL);
        sub.setForeground(ModernTheme.TEXT_SECONDARY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel codeLbl = new JLabel("Code reçu par email");
        codeLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        codeLbl.setForeground(ModernTheme.TEXT_SECONDARY);
        codeLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Champ code — grande police centrée
        JTextField codeField = new JTextField(10);
        codeField.setFont(new Font("Segoe UI", Font.BOLD, 28));
        codeField.setHorizontalAlignment(JTextField.CENTER);
        codeField.setBorder(BorderFactory.createCompoundBorder(
                new ModernTheme.RoundBorder(ModernTheme.PRIMARY, 8, 2),
                new EmptyBorder(6, 10, 6, 10)
        ));
        codeField.setAlignmentX(Component.LEFT_ALIGNMENT);
        codeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        JLabel errorLbl = new JLabel(" ");
        errorLbl.setFont(ModernTheme.FONT_SMALL);
        errorLbl.setForeground(ModernTheme.DANGER);
        errorLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        btns.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton cancelBtn  = ModernTheme.createSecondaryButton("Annuler");
        JButton verifyBtn  = ModernTheme.createPrimaryButton("Vérifier");
        cancelBtn.addActionListener(e -> step2.dispose());

        Runnable doVerify = () -> {
            String saisi = codeField.getText().trim();
            if (saisi.isEmpty()) {
                errorLbl.setText("Veuillez saisir le code.");
                return;
            }
            if (ResetTokenManager.verifierCode(login, saisi)) {
                ResetTokenManager.invalider(login);
                step2.dispose();
                // Étape 3 : nouveau mot de passe
                showStep3(login);
            } else {
                long restant = ResetTokenManager.secondesRestantes(login);
                if (restant == 0) {
                    errorLbl.setText("⏱️  Code expiré. Recommencez la procédure.");
                } else {
                    errorLbl.setText("❌  Code incorrect. " + restant + "s restantes.");
                }
                codeField.setText("");
                codeField.requestFocus();
            }
        };

        verifyBtn.addActionListener(e -> doVerify.run());
        codeField.addActionListener(e -> doVerify.run()); // Entrée pour valider

        btns.add(cancelBtn);
        btns.add(verifyBtn);

        content.add(title);
        content.add(Box.createVerticalStrut(8));
        content.add(sub);
        content.add(Box.createVerticalStrut(18));
        content.add(codeLbl);
        content.add(Box.createVerticalStrut(6));
        content.add(codeField);
        content.add(Box.createVerticalStrut(6));
        content.add(errorLbl);
        content.add(Box.createVerticalStrut(16));
        content.add(btns);

        step2.setContentPane(content);
        step2.setVisible(true);
    }

    // ── Mot de passe oublié — ÉTAPE 3 : nouveau mot de passe ─────────────────
    private void showStep3(String login) {
        JDialog step3 = new JDialog(this, "Nouveau mot de passe", true);
        step3.setSize(420, 320);
        step3.setLocationRelativeTo(this);
        step3.setResizable(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(28, 36, 28, 36));

        JLabel title = new JLabel("🔐  Nouveau mot de passe");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(ModernTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Code vérifié ✅ — Créez votre nouveau mot de passe.");
        sub.setFont(ModernTheme.FONT_SMALL);
        sub.setForeground(ModernTheme.TEXT_SECONDARY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel newLbl = new JLabel("Nouveau mot de passe");
        newLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        newLbl.setForeground(ModernTheme.TEXT_SECONDARY);
        newLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField newField = ModernTheme.createPasswordField(20);
        newField.setAlignmentX(Component.LEFT_ALIGNMENT);
        newField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JLabel confirmLbl = new JLabel("Confirmer le mot de passe");
        confirmLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        confirmLbl.setForeground(ModernTheme.TEXT_SECONDARY);
        confirmLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField confirmField = ModernTheme.createPasswordField(20);
        confirmField.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        btns.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton cancelBtn = ModernTheme.createSecondaryButton("Annuler");
        JButton saveBtn   = ModernTheme.createPrimaryButton("Enregistrer");
        cancelBtn.addActionListener(e -> step3.dispose());

        saveBtn.addActionListener(e -> {
            String newPass     = new String(newField.getPassword());
            String confirmPass = new String(confirmField.getPassword());
            if (newPass.isEmpty()) {
                JOptionPane.showMessageDialog(step3, "Veuillez saisir un mot de passe.", "Champ vide", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (newPass.length() < 6) {
                JOptionPane.showMessageDialog(step3, "Le mot de passe doit contenir au moins 6 caractères.", "Trop court", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(step3, "Les mots de passe ne correspondent pas !", "Erreur", JOptionPane.ERROR_MESSAGE);
                confirmField.setText("");
                return;
            }
            try {
                UtilisateurDao dao = new UtilisateurDao();
                Utilisateur u = dao.findByLogin(login);
                dao.updatePassword(u.getId(), newPass);
                step3.dispose();
                JOptionPane.showMessageDialog(this,
                        "✅  Mot de passe modifié avec succès !\nVous pouvez maintenant vous connecter.",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(step3, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        btns.add(cancelBtn);
        btns.add(saveBtn);

        content.add(title);
        content.add(Box.createVerticalStrut(6));
        content.add(sub);
        content.add(Box.createVerticalStrut(20));
        content.add(newLbl);
        content.add(Box.createVerticalStrut(6));
        content.add(newField);
        content.add(Box.createVerticalStrut(14));
        content.add(confirmLbl);
        content.add(Box.createVerticalStrut(6));
        content.add(confirmField);
        content.add(Box.createVerticalStrut(20));
        content.add(btns);

        step3.setContentPane(content);
        step3.setVisible(true);
    }

    private String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 2) return email;
        return email.substring(0, 2) + "***" + email.substring(at);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}