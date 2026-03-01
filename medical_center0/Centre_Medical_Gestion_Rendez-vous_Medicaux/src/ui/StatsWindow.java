package ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import service.RendezVousService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;

public class StatsWindow extends JFrame {

    private final RendezVousService rdvService = new RendezVousService();

    private JPanel chartsPanel;
    private JLabel recetteTotaleLabel;

    public StatsWindow() {
        setTitle("MediCare Pro — Statistiques & Recettes");
        setSize(980, 680);
        setMinimumSize(new Dimension(800, 550));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ModernTheme.BG);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);

        chargerTout();
    }

    // ── En-tête ───────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernTheme.SIDEBAR_BG);
        header.setBorder(new EmptyBorder(14, 24, 14, 24));

        JLabel title = new JLabel("📊  Statistiques & Recettes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        return header;
    }

    // ── Corps principal ───────────────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setBackground(ModernTheme.BG);
        body.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Carte recette totale
        recetteTotaleLabel = new JLabel("💰  Recettes réalisées : calcul...");
        recetteTotaleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        recetteTotaleLabel.setForeground(Color.WHITE);

        JPanel recetteCard = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ModernTheme.ACCENT.darker(), getWidth(), 0, ModernTheme.ACCENT);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
            }
        };
        recetteCard.setOpaque(false);
        recetteCard.setPreferredSize(new Dimension(0, 60));
        recetteCard.add(recetteTotaleLabel);

        // Zone graphiques (côte à côte)
        chartsPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        chartsPanel.setBackground(ModernTheme.BG);

        body.add(recetteCard, BorderLayout.NORTH);
        body.add(chartsPanel, BorderLayout.CENTER);
        return body;
    }

    // ── Chargement complet ────────────────────────────────────────────────────
    private void chargerTout() {
        chartsPanel.removeAll();

        // 1. Graphique consultations par mois
        chartsPanel.add(buildBarChart());

        // 2. Tableau recettes par médecin
        chartsPanel.add(buildRecettesTable());

        // 3. Recette totale
        try {
            double total = rdvService.calculerRecetteTotale();
            recetteTotaleLabel.setText(String.format("💰  Recettes réalisées — toutes périodes : %.2f DH", total));
        } catch (Exception e) {
            recetteTotaleLabel.setText("Erreur calcul recettes : " + e.getMessage());
        }

        chartsPanel.revalidate();
        chartsPanel.repaint();
    }

    // ── Graphique barres : consultations par mois ─────────────────────────────
    private JPanel buildBarChart() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                new ModernTheme.RoundBorder(ModernTheme.BORDER_COLOR, 10, 1),
                new EmptyBorder(16, 16, 16, 16)));

        JLabel lbl = new JLabel("📈  Consultations par mois");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(ModernTheme.TEXT_PRIMARY);
        wrapper.add(lbl, BorderLayout.NORTH);

        try {
            Map<String, Integer> data = rdvService.consultationsParMois();
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (Map.Entry<String, Integer> e : data.entrySet())
                dataset.addValue(e.getValue(), "Consultations", e.getKey());

            JFreeChart chart = ChartFactory.createBarChart(
                    null, "Mois", "Nb RDV", dataset, PlotOrientation.VERTICAL, false, true, false);
            chart.setBackgroundPaint(Color.WHITE);
            chart.setBorderVisible(false);

            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(ModernTheme.BORDER_COLOR);
            plot.setOutlineVisible(false);
            plot.setDomainGridlinesVisible(false);

            BarRenderer renderer = new BarRenderer() {
                @Override public Paint getItemPaint(int row, int col) { return ModernTheme.PRIMARY; }
            };
            renderer.setMaximumBarWidth(0.07);
            renderer.setBarPainter(new StandardBarPainter());
            renderer.setShadowVisible(false);
            plot.setRenderer(renderer);

            CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
            domainAxis.setTickLabelPaint(ModernTheme.TEXT_SECONDARY);

            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

            ChartPanel cp = new ChartPanel(chart);
            cp.setBackground(Color.WHITE);
            wrapper.add(cp, BorderLayout.CENTER);

        } catch (Exception e) {
            wrapper.add(new JLabel("Erreur : " + e.getMessage(), SwingConstants.CENTER));
        }
        return wrapper;
    }

    // ── Tableau recettes par médecin ──────────────────────────────────────────
    private JPanel buildRecettesTable() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                new ModernTheme.RoundBorder(ModernTheme.BORDER_COLOR, 10, 1),
                new EmptyBorder(16, 16, 16, 16)));

        JLabel lbl = new JLabel("💰  Recettes par médecin (actes réalisés)");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(ModernTheme.TEXT_PRIMARY);
        wrapper.add(lbl, BorderLayout.NORTH);

        try {
            Map<String, Double> data = rdvService.recettesParMedecin();

            String[] cols = {"Médecin", "Recette (DH)"};
            Object[][] rows = new Object[data.size()][2];
            int i = 0;
            double grandTotal = 0;
            for (Map.Entry<String, Double> e : data.entrySet()) {
                rows[i][0] = e.getKey();
                rows[i][1] = String.format("%.2f DH", e.getValue());
                grandTotal += e.getValue();
                i++;
            }

            JTable t = new JTable(rows, cols) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            ModernTheme.styleTable(t);
            t.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
                { setHorizontalAlignment(SwingConstants.RIGHT); }
            });

            JPanel tableWrapper = new JPanel(new BorderLayout());
            tableWrapper.add(ModernTheme.createStyledScrollPane(t), BorderLayout.CENTER);

            JLabel totalLbl = new JLabel(String.format("TOTAL : %.2f DH", grandTotal));
            totalLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            totalLbl.setForeground(ModernTheme.ACCENT);
            totalLbl.setHorizontalAlignment(SwingConstants.RIGHT);
            totalLbl.setBorder(new EmptyBorder(10, 0, 0, 4));
            tableWrapper.add(totalLbl, BorderLayout.SOUTH);

            wrapper.add(tableWrapper, BorderLayout.CENTER);

        } catch (Exception e) {
            wrapper.add(new JLabel("Erreur : " + e.getMessage(), SwingConstants.CENTER));
        }
        return wrapper;
    }

    public static void showStats() {
        SwingUtilities.invokeLater(() -> new StatsWindow().setVisible(true));
    }
}