package ui;

import dao.UtilisateurDao;
import model.Utilisateur;
import util.MailSender;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Formulaire d'inscription — login + email + mot de passe.
 * Envoie un email de bienvenue via Gmail après création du compte.
 */
public class RegisterForm extends JFrame {

    private JTextField     loginField;
    private JTextField     emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;

    public RegisterForm() {
        setTitle("Inscription — MediCare Pro");
        setSize(900, 580);
        setMinimumSize(new Dimension(800, 520));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ── Panneau gauche ──────────────────────────────────────────────────
        JPanel left = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0x1557B0), 0, getHeight(), new Color(0x34A853));
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

        JLabel tagline = new JLabel("<html><div style='text-align:center'>Rejoignez notre système<br>de gestion médicale</div></html>");
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

        // ── Panneau droit (formulaire) ──────────────────────────────────────
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(ModernTheme.BG);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                new ModernTheme.RoundBorder(ModernTheme.BORDER_COLOR, 12, 1),
                new EmptyBorder(40, 48, 36, 48)
        ));
        form.setMaximumSize(new Dimension(380, 540));

        // Titre
        JLabel title = new JLabel("Créer un compte");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(ModernTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Remplissez les informations ci-dessous.");
        sub.setFont(ModernTheme.FONT_SMALL);
        sub.setForeground(ModernTheme.TEXT_SECONDARY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Champs ──────────────────────────────────────────────────────────
        JLabel loginLbl = makeLabel("Identifiant (login)");
        loginField = ModernTheme.createTextField(20);
        loginField.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JLabel emailLbl = makeLabel("Adresse email");
        emailField = ModernTheme.createTextField(20);
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JLabel passLbl = makeLabel("Mot de passe");
        passwordField = ModernTheme.createPasswordField(20);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        // Indicateur de force du mot de passe
        JLabel strengthLbl = new JLabel(" ");
        strengthLbl.setFont(ModernTheme.FONT_SMALL);
        strengthLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
            private void update() {
                String p = new String(passwordField.getPassword());
                if (p.isEmpty()) {
                    strengthLbl.setText(" ");
                } else if (p.length() < 6) {
                    strengthLbl.setText("⚠️  Trop court (min. 6 caractères)");
                    strengthLbl.setForeground(ModernTheme.DANGER);
                } else if (p.length() < 10 || !p.matches(".*[A-Z].*") || !p.matches(".*[0-9].*")) {
                    strengthLbl.setText("🟡  Moyen");
                    strengthLbl.setForeground(ModernTheme.WARNING);
                } else {
                    strengthLbl.setText("✅  Fort");
                    strengthLbl.setForeground(ModernTheme.ACCENT);
                }
            }
        });

        JLabel confirmLbl = makeLabel("Confirmer le mot de passe");
        confirmField = ModernTheme.createPasswordField(20);
        confirmField.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        confirmField.addActionListener(e -> handleRegister());

        // ── Bouton inscription ───────────────────────────────────────────────
        JButton registerBtn = new JButton("Créer mon compte") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed() ? ModernTheme.ACCENT.darker()
                        : getModel().isRollover() ? ModernTheme.ACCENT.brighter()
                        : ModernTheme.ACCENT;
                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setFocusPainted(false);
        registerBtn.setOpaque(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        registerBtn.addActionListener(e -> handleRegister());

        // ── Lien retour connexion ────────────────────────────────────────────
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(ModernTheme.BORDER_COLOR);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel loginLink = new JLabel("<html>Déjà un compte ? <u style='color:#1A73E8;'>Se connecter</u></html>");
        loginLink.setFont(ModernTheme.FONT_SMALL);
        loginLink.setForeground(ModernTheme.TEXT_SECONDARY);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                new LoginForm().setVisible(true);
            }
        });

        // Assemblage
        form.add(title);
        form.add(Box.createVerticalStrut(4));
        form.add(sub);
        form.add(Box.createVerticalStrut(24));
        form.add(loginLbl);
        form.add(Box.createVerticalStrut(6));
        form.add(loginField);
        form.add(Box.createVerticalStrut(14));
        form.add(emailLbl);
        form.add(Box.createVerticalStrut(6));
        form.add(emailField);
        form.add(Box.createVerticalStrut(14));
        form.add(passLbl);
        form.add(Box.createVerticalStrut(6));
        form.add(passwordField);
        form.add(Box.createVerticalStrut(4));
        form.add(strengthLbl);
        form.add(Box.createVerticalStrut(10));
        form.add(confirmLbl);
        form.add(Box.createVerticalStrut(6));
        form.add(confirmField);
        form.add(Box.createVerticalStrut(22));
        form.add(registerBtn);
        form.add(Box.createVerticalStrut(18));
        form.add(sep);
        form.add(Box.createVerticalStrut(14));
        form.add(loginLink);

        right.add(form);
        add(left,  BorderLayout.WEST);
        add(right, BorderLayout.CENTER);
    }

    // ── Logique d'inscription ─────────────────────────────────────────────────
    private void handleRegister() {
        String login   = loginField.getText().trim();
        String email   = emailField.getText().trim();
        String pass    = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (login.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            showWarning("Veuillez remplir tous les champs.");
            return;
        }
        if (!email.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            showWarning("Adresse email invalide.");
            emailField.requestFocus();
            return;
        }
        if (pass.length() < 6) {
            showWarning("Le mot de passe doit contenir au moins 6 caractères.");
            passwordField.requestFocus();
            return;
        }
        if (!pass.equals(confirm)) {
            showError("Les mots de passe ne correspondent pas !");
            confirmField.setText("");
            confirmField.requestFocus();
            return;
        }

        try {
            UtilisateurDao dao = new UtilisateurDao();

            if (dao.findByLogin(login) != null) {
                showError("Cet identifiant est déjà utilisé. Choisissez-en un autre.");
                loginField.requestFocus();
                return;
            }
            if (dao.findByEmail(email) != null) {
                showError("Cette adresse email est déjà associée à un compte.");
                emailField.requestFocus();
                return;
            }

            // Enregistrement en base
            dao.inscrire(new Utilisateur(login, pass, email));

            // Email de bienvenue dans un thread séparé (ne bloque pas l'UI)
            new Thread(() -> {
                try {
                    MailSender.envoyerBienvenue(email, login);
                } catch (Exception ex) {
                    System.err.println("[MailSender] Bienvenue non envoyé : " + ex.getMessage());
                }
            }).start();

            JOptionPane.showMessageDialog(this,
                    "✅  Compte créé avec succès !\nUn email de bienvenue a été envoyé à " + email + ".",
                    "Inscription réussie", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new LoginForm().setVisible(true);

        } catch (Exception ex) {
            showError("Erreur lors de l'inscription : " + ex.getMessage());
        }
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(ModernTheme.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void showError(String msg)   { JOptionPane.showMessageDialog(this, msg, "Erreur",    JOptionPane.ERROR_MESSAGE); }
    private void showWarning(String msg) { JOptionPane.showMessageDialog(this, msg, "Attention", JOptionPane.WARNING_MESSAGE); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterForm().setVisible(true));
    }
}