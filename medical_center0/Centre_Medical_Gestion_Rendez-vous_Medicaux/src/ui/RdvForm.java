package ui;

import service.*;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class RdvForm extends JPanel {

    private final RendezVousService rdvService = new RendezVousService();
    private final PatientService    pService   = new PatientService();
    private final MedecinService    mService   = new MedecinService();
    private JTable            table;
    private DefaultTableModel model;

    // ── Filtres ───────────────────────────────────────────────
    private JComboBox<String> specCombo;
    private JTextField        dateDebutField;
    private JTextField        dateFinField;

    public RdvForm() {
        setLayout(new BorderLayout(0, 8));
        setBackground(ModernTheme.BG);

        JPanel north = new JPanel(new BorderLayout(0, 8));
        north.setOpaque(false);
        north.add(buildHeader(),  BorderLayout.NORTH);
        north.add(buildFilters(), BorderLayout.SOUTH);

        add(north,             BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonBar(),  BorderLayout.SOUTH);

        loadRDV(null, null, null);
    }

    // ── En-tête ───────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 4, 0));

        JLabel title = new JLabel("📅  Gestion des Rendez-vous");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(ModernTheme.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        JButton statsBtn = ModernTheme.createSuccessButton("📊  Voir Statistiques");
        statsBtn.addActionListener(e -> StatsWindow.showStats());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(statsBtn);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    // ── Barre de filtres ──────────────────────────────────────
    private JPanel buildFilters() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                new ModernTheme.RoundBorder(ModernTheme.BORDER_COLOR, 8, 1),
                new EmptyBorder(2, 10, 2, 10)));

        // Spécialité
        bar.add(filterLabel("Spécialité :"));
        specCombo = new JComboBox<>(new String[]{"Toutes"});
        specCombo.setFont(ModernTheme.FONT_BODY);
        specCombo.setPreferredSize(new Dimension(155, 30));
        bar.add(specCombo);
        chargerSpecialites();

        // Séparateur visuel
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 24));
        sep.setForeground(ModernTheme.BORDER_COLOR);
        bar.add(sep);

        // Période
        bar.add(filterLabel("Du :"));
        dateDebutField = filterDateField("yyyy-MM-dd");
        bar.add(dateDebutField);

        bar.add(filterLabel("Au :"));
        dateFinField = filterDateField("yyyy-MM-dd");
        bar.add(dateFinField);

        // Boutons
        JButton filterBtn = ModernTheme.createPrimaryButton("🔍 Filtrer");
        filterBtn.setPreferredSize(new Dimension(100, 30));
        filterBtn.addActionListener(e -> appliquerFiltres());
        bar.add(filterBtn);

        JButton resetBtn = ModernTheme.createSecondaryButton("✕ Reset");
        resetBtn.setPreferredSize(new Dimension(80, 30));
        resetBtn.addActionListener(e -> {
            specCombo.setSelectedIndex(0);
            dateDebutField.setText("");
            dateFinField.setText("");
            loadRDV(null, null, null);
        });
        bar.add(resetBtn);

        return bar;
    }

    // ── Tableau ───────────────────────────────────────────────
    private JPanel buildTablePanel() {
        String[] columns = {"ID", "Patient", "Médecin", "Spécialité", "Date & Heure", "Acte médical", "Tarif (MAD)", "Statut"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        ModernTheme.styleTable(table);

        // Colonne tarif alignée à droite
        table.getColumnModel().getColumn(6).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            { setHorizontalAlignment(SwingConstants.RIGHT); }
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (v != null) setText(String.format("%.2f DH", ((Number) v).doubleValue()));
                c.setBackground(sel ? ModernTheme.TABLE_SELECTED : (row % 2 == 0 ? Color.WHITE : ModernTheme.TABLE_ROW_ALT));
                c.setForeground(ModernTheme.TEXT_PRIMARY);
                ((JLabel) c).setBorder(new EmptyBorder(0, 12, 0, 16));
                return c;
            }
        });

        // Colonne statut colorée
        table.getColumnModel().getColumn(7).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                String statut = v != null ? v.toString() : "";
                Color bg = sel ? ModernTheme.TABLE_SELECTED :
                        statut.contains("Réalisé") ? new Color(0xE6F4EA) :
                                statut.contains("Annulé")  ? new Color(0xFCE8E6) :
                                        new Color(0xE8F0FE);
                c.setBackground(bg);
                ((JLabel) c).setBorder(new EmptyBorder(0, 12, 0, 12));
                return c;
            }
        });

        // Largeurs colonnes
        int[] widths = {40, 120, 120, 110, 130, 150, 100, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(ModernTheme.createStyledScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ── Boutons CRUD ──────────────────────────────────────────
    private JPanel buildButtonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        bar.setOpaque(false);

        JButton addBtn     = ModernTheme.createPrimaryButton("＋  Planifier RDV");
        JButton updateBtn  = ModernTheme.createSecondaryButton("✎  Modifier");
        JButton deleteBtn  = ModernTheme.createDangerButton("🗑  Supprimer");
        JButton annulerBtn = ModernTheme.createDangerButton("❌  Annuler RDV");
        JButton realiseBtn = ModernTheme.createSuccessButton("✅  Marquer Réalisé");

        bar.add(realiseBtn);
        bar.add(annulerBtn);
        bar.add(updateBtn);
        bar.add(deleteBtn);
        bar.add(addBtn);

        addBtn.addActionListener(e     -> planifierRDV());
        updateBtn.addActionListener(e  -> modifierRDV());
        deleteBtn.addActionListener(e  -> supprimerRDV());
        annulerBtn.addActionListener(e -> marquerAnnule());
        realiseBtn.addActionListener(e -> marquerRealise());

        return bar;
    }

    // ── Chargement avec filtres ───────────────────────────────
    private void loadRDV(String specialite, Date debut, Date fin) {
        try {
            model.setRowCount(0);
            List<RendezVous> rdvs = rdvService.filtrer(specialite, debut, fin);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH:mm");
            for (RendezVous r : rdvs) {
                model.addRow(new Object[]{
                        r.getId(),
                        r.getPatient() != null ? r.getPatient().getNom()        : "Inconnu",
                        r.getMedecin() != null ? r.getMedecin().getNom()         : "Inconnu",
                        r.getMedecin() != null ? r.getMedecin().getSpecialite()  : "—",
                        sdf.format(r.getDate()),
                        r.getActe(),
                        r.getTarif(),
                        r.getStatutLabel()
                });
            }
        } catch (Exception ex) {
            showError("Erreur chargement RDV : " + ex.getMessage());
        }
    }

    private void appliquerFiltres() {
        String spec = (String) specCombo.getSelectedItem();
        Date debut  = parseDate(dateDebutField.getText());
        Date fin    = parseDate(dateFinField.getText());
        loadRDV(spec, debut, fin);
    }

    private void chargerSpecialites() {
        try {
            List<String> specs = rdvService.getSpecialitesDisponibles();
            specCombo.removeAllItems();
            specs.forEach(specCombo::addItem);
        } catch (Exception ignored) {}
    }

    // ── Planifier ─────────────────────────────────────────────
    private void planifierRDV() {
        JDialog dialog = buildFormDialog("Planifier un rendez-vous", 420, 400);
        JTextField pidF   = ModernTheme.createTextField(20);
        JTextField midF   = ModernTheme.createTextField(20);
        JTextField dateF  = ModernTheme.createTextField(20);
        JTextField acteF  = ModernTheme.createTextField(20);
        JTextField tarifF = ModernTheme.createTextField(20);
        dateF.setText("2024-01-15 09:00");

        fillDialog(dialog,
                new String[]{"ID Patient", "ID Médecin", "Date (yyyy-MM-dd HH:mm)", "Acte médical", "Tarif"},
                new JTextField[]{pidF, midF, dateF, acteF, tarifF}, () -> {
                    try {
                        Patient p = pService.getPatient(Integer.parseInt(pidF.getText().trim()));
                        Medecin m = mService.getMedecin(Integer.parseInt(midF.getText().trim()));
                        if (p == null || m == null) { showError("Patient ou Médecin introuvable."); return; }
                        Date date  = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateF.getText().trim());
                        double tarif = Double.parseDouble(tarifF.getText().trim());
                        rdvService.planifierRDV(p, m, date, acteF.getText().trim(), tarif);
                        chargerSpecialites();
                        appliquerFiltres();
                        dialog.dispose();
                        showSuccess("Rendez-vous planifié avec succès !");
                    } catch (Exception ex) { showError("Erreur : " + ex.getMessage()); }
                });
        dialog.setVisible(true);
    }

    // ── Modifier ──────────────────────────────────────────────
    private void modifierRDV() {
        int row = table.getSelectedRow();
        if (row == -1) { showWarning("Sélectionnez un rendez-vous dans le tableau."); return; }

        JDialog dialog = buildFormDialog("Modifier le rendez-vous", 420, 400);
        JTextField pidF   = ModernTheme.createTextField(20);
        JTextField midF   = ModernTheme.createTextField(20);
        JTextField dateF  = ModernTheme.createTextField(20);
        JTextField acteF  = ModernTheme.createTextField(20);
        JTextField tarifF = ModernTheme.createTextField(20);
        pidF.setText(model.getValueAt(row, 1).toString());
        midF.setText(model.getValueAt(row, 2).toString());
        dateF.setText(model.getValueAt(row, 4).toString().replace("  ", " "));
        acteF.setText(model.getValueAt(row, 5).toString());
        tarifF.setText(model.getValueAt(row, 6).toString());
        int id = (int) model.getValueAt(row, 0);
        fillDialog(dialog,
                new String[]{"ID Patient", "ID Médecin", "Date (yyyy-MM-dd HH:mm)", "Acte médical", "Tarif"},
                new JTextField[]{pidF, midF, dateF, acteF, tarifF}, () -> {
                    try {
                        Patient p = pService.getPatient(Integer.parseInt(pidF.getText().trim()));
                        Medecin m = mService.getMedecin(Integer.parseInt(midF.getText().trim()));
                        if (p == null || m == null) { showError("Patient ou Médecin introuvable."); return; }
                        Date date  = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateF.getText().trim());
                        double tarif = Double.parseDouble(tarifF.getText().trim());
                        rdvService.update(new RendezVous(id, p, m, date, acteF.getText().trim(), tarif));
                        appliquerFiltres();
                        dialog.dispose();
                        showSuccess("Rendez-vous modifié avec succès !");
                    } catch (Exception ex) { showError("Erreur : " + ex.getMessage()); }
                });
        dialog.setVisible(true);
    }

    // ── Supprimer ─────────────────────────────────────────────
    private void supprimerRDV() {
        int row = table.getSelectedRow();
        if (row == -1) { showWarning("Sélectionnez un rendez-vous dans le tableau."); return; }
        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Annuler le rendez-vous ID " + id + " ?\nCette action est irréversible.",
                "Confirmer l'annulation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try { rdvService.delete(id); appliquerFiltres(); showSuccess("Rendez-vous annulé."); }
            catch (Exception ex) { showError("Erreur : " + ex.getMessage()); }
        }
    }

    // ── Marquer Réalisé ───────────────────────────────────────
    private void marquerRealise() {
        int row = table.getSelectedRow();
        if (row == -1) { showWarning("Sélectionnez un rendez-vous dans le tableau."); return; }
        int id = (int) model.getValueAt(row, 0);
        try {
            rdvService.marquerRealise(id);
            appliquerFiltres();
            showSuccess("Rendez-vous marqué comme réalisé !");
        } catch (Exception ex) { showError("Erreur : " + ex.getMessage()); }
    }

    // ── Marquer Annulé ────────────────────────────────────────
    private void marquerAnnule() {
        int row = table.getSelectedRow();
        if (row == -1) { showWarning("Sélectionnez un rendez-vous dans le tableau."); return; }
        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Marquer le rendez-vous ID " + id + " comme annulé ?",
                "Confirmer l'annulation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                rdvService.marquerAnnule(id);
                appliquerFiltres();
                showSuccess("Rendez-vous marqué comme annulé !");
            } catch (Exception ex) { showError("Erreur : " + ex.getMessage()); }
        }
    }

    // ── Helpers ───────────────────────────────────────────────
    private JDialog buildFormDialog(String title, int w, int h) {
        Window win = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = (win instanceof Frame)
                ? new JDialog((Frame) win, title, true)
                : new JDialog((Dialog) win, title, true);
        dialog.setSize(w, h);
        dialog.setLocationRelativeTo(this);
        return dialog;
    }

    private void fillDialog(JDialog dialog, String[] labels, JTextField[] fields, Runnable onSave) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(24, 32, 24, 32));

        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(ModernTheme.TEXT_SECONDARY);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            fields[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            fields[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            content.add(lbl);
            content.add(Box.createVerticalStrut(4));
            content.add(fields[i]);
            content.add(Box.createVerticalStrut(10));
        }

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        btns.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton cancel = ModernTheme.createSecondaryButton("Annuler");
        JButton save   = ModernTheme.createPrimaryButton("Enregistrer");
        cancel.addActionListener(e -> dialog.dispose());
        save.addActionListener(e -> onSave.run());
        btns.add(cancel);
        btns.add(save);
        content.add(Box.createVerticalStrut(8));
        content.add(btns);
        dialog.setContentPane(content);
    }

    private JLabel filterLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(ModernTheme.TEXT_SECONDARY);
        return l;
    }

    private JTextField filterDateField(String placeholder) {
        JTextField f = ModernTheme.createTextField(10);
        f.setText(placeholder);
        f.setPreferredSize(new Dimension(100, 30));
        return f;
    }

    private Date parseDate(String text) {
        if (text == null || text.isBlank() || text.equals("yyyy-MM-dd")) return null;
        try { return new SimpleDateFormat("yyyy-MM-dd").parse(text.trim()); }
        catch (Exception e) { return null; }
    }

    private void showError(String msg)   { JOptionPane.showMessageDialog(this, msg, "Erreur",    JOptionPane.ERROR_MESSAGE); }
    private void showSuccess(String msg) { JOptionPane.showMessageDialog(this, msg, "Succès",    JOptionPane.INFORMATION_MESSAGE); }
    private void showWarning(String msg) { JOptionPane.showMessageDialog(this, msg, "Attention", JOptionPane.WARNING_MESSAGE); }
}