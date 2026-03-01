package ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * ModernTheme — palette & helpers partagés pour toute l'application.
 */
public class ModernTheme {

    // ── Palette ──────────────────────────────────────────────────────────────
    public static final Color PRIMARY        = new Color(0x1A73E8);   // bleu Google-like
    public static final Color PRIMARY_DARK   = new Color(0x1557B0);
    public static final Color PRIMARY_LIGHT  = new Color(0xE8F0FE);
    public static final Color ACCENT         = new Color(0x34A853);   // vert succès
    public static final Color DANGER         = new Color(0xEA4335);   // rouge danger
    public static final Color WARNING        = new Color(0xFBBC05);   // jaune avertissement
    public static final Color BG             = new Color(0xF8F9FA);   // fond général
    public static final Color SIDEBAR_BG     = new Color(0x1E2A3A);   // sidebar sombre
    public static final Color SIDEBAR_HOVER  = new Color(0x2D3E52);
    public static final Color SIDEBAR_ACTIVE = new Color(0x1A73E8);
    public static final Color CARD_BG        = Color.WHITE;
    public static final Color BORDER_COLOR   = new Color(0xDEE2E6);
    public static final Color TEXT_PRIMARY   = new Color(0x212529);
    public static final Color TEXT_SECONDARY = new Color(0x6C757D);
    public static final Color TEXT_ON_DARK   = new Color(0xECEFF1);
    public static final Color TABLE_HEADER   = new Color(0xF1F3F4);
    public static final Color TABLE_ROW_ALT  = new Color(0xF8F9FA);
    public static final Color TABLE_SELECTED = new Color(0xE8F0FE);

    // ── Fonts ────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD,  12);

    // ── Factory : bouton principal ────────────────────────────────────────────
    public static JButton createPrimaryButton(String text) {
        return createButton(text, PRIMARY, Color.WHITE);
    }

    public static JButton createDangerButton(String text) {
        return createButton(text, DANGER, Color.WHITE);
    }

    public static JButton createSecondaryButton(String text) {
        return createButton(text, new Color(0xE9ECEF), TEXT_PRIMARY);
    }

    public static JButton createSuccessButton(String text) {
        return createButton(text, ACCENT, Color.WHITE);
    }

    private static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed()   ? bg.darker() :
                        getModel().isRollover()  ? bg.brighter() : bg;
                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 36));
        btn.setBorder(new EmptyBorder(6, 16, 6, 16));
        return btn;
    }

    // ── Factory : champ de texte arrondi ──────────────────────────────────────
    public static JTextField createTextField(int columns) {
        JTextField f = new JTextField(columns);
        styleTextField(f);
        return f;
    }

    public static JPasswordField createPasswordField(int columns) {
        JPasswordField f = new JPasswordField(columns);
        styleTextField(f);
        return f;
    }

    private static void styleTextField(JTextField f) {
        f.setFont(FONT_BODY);
        f.setForeground(TEXT_PRIMARY);
        f.setBackground(Color.WHITE);
        f.setPreferredSize(new Dimension(f.getPreferredSize().width, 38));
        f.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(BORDER_COLOR, 8, 1),
                new EmptyBorder(4, 10, 4, 10)
        ));
    }

    // ── Factory : label section ───────────────────────────────────────────────
    public static JLabel createSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SECTION);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    // ── Styliser une JTable ───────────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(TABLE_SELECTED);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setBackground(CARD_BG);
        table.setForeground(TEXT_PRIMARY);
        table.setFillsViewportHeight(true);

        // Lignes alternées
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (sel) {
                    c.setBackground(TABLE_SELECTED);
                    c.setForeground(TEXT_PRIMARY);
                } else {
                    c.setBackground(row % 2 == 0 ? CARD_BG : TABLE_ROW_ALT);
                    c.setForeground(TEXT_PRIMARY);
                }
                ((JLabel) c).setBorder(new EmptyBorder(0, 12, 0, 12));
                return c;
            }
        });

        // En-tête
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(TABLE_HEADER);
        header.setForeground(TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 44));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                lbl.setBackground(TABLE_HEADER);
                lbl.setForeground(TEXT_SECONDARY);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setBorder(new EmptyBorder(0, 12, 0, 12));
                return lbl;
            }
        });
    }

    // ── Styliser un JScrollPane ───────────────────────────────────────────────
    public static JScrollPane createStyledScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(new RoundBorder(BORDER_COLOR, 10, 1));
        sp.setBackground(CARD_BG);
        sp.getViewport().setBackground(CARD_BG);
        return sp;
    }

    // ── Bordure arrondie ──────────────────────────────────────────────────────
    public static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int radius, thickness;

        public RoundBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Float(x + thickness/2f, y + thickness/2f,
                    w - thickness, h - thickness, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) { return new Insets(thickness, thickness, thickness, thickness); }
    }

    // ── Panel carte (fond blanc, ombre légère, coins arrondis) ────────────────
    public static JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Ombre
                g2.setColor(new Color(0, 0, 0, 18));
                g2.fill(new RoundRectangle2D.Float(3, 4, getWidth()-4, getHeight()-4, 12, 12));
                // Fond
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-4, getHeight()-4, 12, 12));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        return card;
    }
}