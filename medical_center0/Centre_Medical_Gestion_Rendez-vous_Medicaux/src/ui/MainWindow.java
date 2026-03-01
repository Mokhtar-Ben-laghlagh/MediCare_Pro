package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class MainWindow extends JFrame {

    private JPanel contentPanel;
    private JPanel activeSideBtn = null;

    public MainWindow() {
        setTitle("MediCare Pro — Gestion des Rendez-Vous Médicaux");
        setSize(1100, 680);
        setMinimumSize(new Dimension(900, 580));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ModernTheme.BG);

        // ── Sidebar ─────────────────────────────────────────────────────────
        JPanel sidebar = buildSidebar();
        add(sidebar, BorderLayout.WEST);

        // ── Zone de contenu ──────────────────────────────────────────────────
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(ModernTheme.BG);
        contentPanel.setBorder(new EmptyBorder(24, 24, 24, 24));
        add(contentPanel, BorderLayout.CENTER);

        // Page d'accueil
        showWelcome();
    }

    // ── Construction de la sidebar ───────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(ModernTheme.SIDEBAR_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 22));
        logoPanel.setOpaque(false);
        JLabel logo = new JLabel("🏥  MediCare Pro");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        logo.setForeground(Color.WHITE);
        logoPanel.add(logo);
        sidebar.add(logoPanel);

        // Séparateur
        sidebar.add(createSidebarSeparator());

        // Section label
        sidebar.add(createSidebarSection("NAVIGATION"));

        // Boutons de navigation
        JPanel btnDashboard = createSidebarButton("🏠", "Tableau de bord", () -> showWelcome());
        JPanel btnPatients  = createSidebarButton("👤", "Patients",         () -> showPanel(new PatientForm()));
        JPanel btnMedecins  = createSidebarButton("👨‍⚕️", "Médecins",       () -> showPanel(new MedecinForm()));
        JPanel btnRdv       = createSidebarButton("📅", "Rendez-vous",      () -> showPanel(new RdvForm()));
        JPanel btnStats     = createSidebarButton("📊", "Statistiques",     () -> StatsWindow.showStats());

        sidebar.add(btnDashboard);
        sidebar.add(btnPatients);
        sidebar.add(btnMedecins);
        sidebar.add(btnRdv);
        sidebar.add(createSidebarSeparator());
        sidebar.add(createSidebarSection("RAPPORTS"));
        sidebar.add(btnStats);

        sidebar.add(Box.createVerticalGlue());

        // Pied de sidebar
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        footer.setOpaque(false);
        JLabel version = new JLabel("v1.0.0  •  Centre Médical");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        version.setForeground(new Color(255, 255, 255, 80));
        footer.add(version);
        sidebar.add(footer);

        // Activer Tableau de bord par défaut
        setActiveButton(btnDashboard);

        return sidebar;
    }

    private JPanel createSidebarButton(String icon, String label, Runnable action) {
        JPanel btn = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(220, 46));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        JLabel textLbl = new JLabel(label);
        textLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textLbl.setForeground(new Color(0xB0BEC5));

        btn.add(iconLbl);
        btn.add(textLbl);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                setActiveButton(btn);
                action.run();
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != activeSideBtn) {
                    btn.setOpaque(true);
                    btn.setBackground(ModernTheme.SIDEBAR_HOVER);
                    btn.repaint();
                }
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != activeSideBtn) {
                    btn.setOpaque(false);
                    btn.repaint();
                }
            }
        });

        return btn;
    }

    private void setActiveButton(JPanel btn) {
        if (activeSideBtn != null) {
            activeSideBtn.setOpaque(false);
            activeSideBtn.setBackground(null);
            // Remettre couleur texte normal
            for (Component c : activeSideBtn.getComponents()) {
                if (c instanceof JLabel) ((JLabel) c).setForeground(new Color(0xB0BEC5));
            }
        }
        activeSideBtn = btn;
        btn.setOpaque(true);
        btn.setBackground(ModernTheme.SIDEBAR_ACTIVE);
        for (Component c : btn.getComponents()) {
            if (c instanceof JLabel) ((JLabel) c).setForeground(Color.WHITE);
        }
        btn.repaint();
    }

    private Component createSidebarSeparator() {
        JPanel sep = new JPanel();
        sep.setMaximumSize(new Dimension(220, 1));
        sep.setPreferredSize(new Dimension(220, 1));
        sep.setBackground(new Color(255, 255, 255, 25));
        sep.setOpaque(true);
        return sep;
    }

    private Component createSidebarSection(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(255, 255, 255, 80));
        lbl.setBorder(new EmptyBorder(14, 20, 4, 0));
        return lbl;
    }

    // ── Affichage des panels ─────────────────────────────────────────────────
    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showWelcome() {
        contentPanel.removeAll();
        contentPanel.add(buildWelcomePanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel buildWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Titre de bienvenue
        JLabel title = new JLabel("Tableau de bord");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(ModernTheme.TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(title, gbc);

        JLabel sub = new JLabel("Bienvenue dans votre système de gestion des rendez-vous");
        sub.setFont(ModernTheme.FONT_BODY);
        sub.setForeground(ModernTheme.TEXT_SECONDARY);
        gbc.gridy = 1;
        panel.add(sub, gbc);

        // Cartes de stats rapides
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; panel.add(buildStatCard("👤", "Patients",     "Gérer les patients",     ModernTheme.PRIMARY), gbc);
        gbc.gridx = 1; panel.add(buildStatCard("👨‍⚕️", "Médecins",   "Gérer les médecins",     new Color(0x8E44AD)), gbc);
        gbc.gridx = 2; panel.add(buildStatCard("📅", "Rendez-vous",  "Planifier & suivre",     ModernTheme.ACCENT), gbc);

        return panel;
    }

    private JPanel buildStatCard(String icon, String title, String desc, Color accent) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fill(new RoundRectangle2D.Float(3, 4, getWidth()-5, getHeight()-5, 14, 14));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-4, getHeight()-4, 14, 14));
                // Bande colorée en haut
                g2.setColor(accent);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-4, 6, 14, 14));
                g2.fillRect(0, 3, getWidth()-4, 6);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(22, 20, 20, 20));
        card.setPreferredSize(new Dimension(200, 140));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(ModernTheme.TEXT_PRIMARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLbl = new JLabel(desc);
        descLbl.setFont(ModernTheme.FONT_SMALL);
        descLbl.setForeground(ModernTheme.TEXT_SECONDARY);
        descLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(iconLbl);
        card.add(Box.createVerticalStrut(10));
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(descLbl);

        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}