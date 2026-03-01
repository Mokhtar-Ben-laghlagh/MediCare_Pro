package ui;

import dao.UtilisateurDao;
import util.ResetTokenManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Fenêtre de réinitialisation du mot de passe.
 * S'ouvre automatiquement quand l'utilisateur clique sur le bouton dans l'email.
 */
public class ResetPasswordWindow extends JFrame {

    private final String token;

    public ResetPasswordWindow(String token) {
        this.token = token;

        setTitle("Réinitialisation du mot de passe");
        setSize(480, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(ModernTheme.BG);
        setLayout(new BorderLayout());

        // ── En-tête ──────────────────────────────────────────────────────────
        JPanel header = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ModernTheme.PRIMARY_DARK, getWidth(), 0, ModernTheme.PRIMARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(0, 80));
        header.setLayout(new GridBagLayout());

        JLabel headerTitle = new JLabel("🔐  Nouveau mot de passe");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerTitle.setForeground(Color.WHITE);
        header.add(headerTitle);
        add(header, BorderLayout.NORTH);

        // ── Formulaire ───────────────────────────────────────────────────────
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(32, 40, 32, 40));

        // Vérification token
        if (!ResetTokenManager.estValide(token)) {
            showExpired(form);
        } else {
            showForm(form);
        }

        add(form, BorderLayout.CENTER);
        setVisible(true);
    }

    // ── Formulaire principal ──────────────────────────────────────────────────
    private void showForm(JPanel form) {
        String login = ResetTokenManager.getLogin(token);

        JLabel info = new JLabel("Compte : " + login);
        info.setFont(new Font("Segoe UI", Font.BOLD, 13));
        info.setForeground(ModernTheme.PRIMARY);
        info.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Ce lien expire dans 15 minutes après réception de l'email.");
        sub.setFont(ModernTheme.FONT_SMALL);
        sub.setForeground(ModernTheme.TEXT_SECONDARY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Nouveau mot de passe
        JLabel newLbl = new JLabel("Nouveau mot de passe");
        newLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        newLbl.setForeground(ModernTheme.TEXT_SECONDARY);
        newLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField newField = ModernTheme.createPasswordField(20);
        newField.setAlignmentX(Component.LEFT_ALIGNMENT);
        newField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        // Indicateur de force
        JLabel strengthLbl = new JLabel(" ");
        strengthLbl.setFont(ModernTheme.FONT_SMALL);
        strengthLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        newField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { updateStrength(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { updateStrength(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateStrength(); }
            private void updateStrength() {
                String p = new String(newField.getPassword());
                if (p.length() < 6) {
                    strengthLbl.setText("⚠️  Trop court");
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

        // Confirmer
        JLabel confirmLbl = new JLabel("Confirmer le mot de passe");
        confirmLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        confirmLbl.setForeground(ModernTheme.TEXT_SECONDARY);
        confirmLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField confirmField = ModernTheme.createPasswordField(20);
        confirmField.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        // Bouton enregistrer
        JButton saveBtn = ModernTheme.createPrimaryButton("Enregistrer le mot de passe");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        saveBtn.addActionListener(e -> {
            String newPass     = new String(newField.getPassword());
            String confirmPass = new String(confirmField.getPassword());

            if (newPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez saisir un mot de passe.", "Champ vide", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (newPass.length() < 6) {
                JOptionPane.showMessageDialog(this, "Le mot de passe doit contenir au moins 6 caractères.", "Trop court", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "Les mots de passe ne correspondent pas !", "Erreur", JOptionPane.ERROR_MESSAGE);
                confirmField.setText("");
                return;
            }
            try {
                UtilisateurDao dao = new UtilisateurDao();
                dao.updatePassword(dao.findByLogin(login).getId(), newPass);
                ResetTokenManager.invalider(token); // Token utilisé → invalidé
                JOptionPane.showMessageDialog(this, "✅  Mot de passe modifié avec succès !\nVous pouvez vous connecter.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        form.add(info);
        form.add(Box.createVerticalStrut(4));
        form.add(sub);
        form.add(Box.createVerticalStrut(20));
        form.add(newLbl);
        form.add(Box.createVerticalStrut(4));
        form.add(newField);
        form.add(Box.createVerticalStrut(4));
        form.add(strengthLbl);
        form.add(Box.createVerticalStrut(12));
        form.add(confirmLbl);
        form.add(Box.createVerticalStrut(4));
        form.add(confirmField);
        form.add(Box.createVerticalStrut(24));
        form.add(saveBtn);
    }

    // ── Token expiré ──────────────────────────────────────────────────────────
    private void showExpired(JPanel form) {
        JLabel icon = new JLabel("⏱️", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel("Ce lien a expiré ou est invalide.");
        msg.setFont(new Font("Segoe UI", Font.BOLD, 15));
        msg.setForeground(ModernTheme.DANGER);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Veuillez refaire une demande de réinitialisation.");
        sub.setFont(ModernTheme.FONT_BODY);
        sub.setForeground(ModernTheme.TEXT_SECONDARY);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        form.add(Box.createVerticalGlue());
        form.add(icon);
        form.add(Box.createVerticalStrut(16));
        form.add(msg);
        form.add(Box.createVerticalStrut(8));
        form.add(sub);
        form.add(Box.createVerticalGlue());
    }

    // ── Point d'entrée (appelé depuis le lien de l'email) ─────────────────────
    public static void main(String[] args) {
        // Simuler un clic sur le bouton de l'email avec un token de test
        if (args.length > 0) {
            SwingUtilities.invokeLater(() -> new ResetPasswordWindow(args[0]));
        } else {
            System.out.println("Usage: java ResetPasswordWindow <token>");
        }
    }
}