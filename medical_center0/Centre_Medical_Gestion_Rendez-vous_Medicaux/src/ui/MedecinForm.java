package ui;

import service.MedecinService;
import model.Medecin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MedecinForm extends JPanel {

    private final MedecinService service = new MedecinService();
    private JTable table;
    private DefaultTableModel model;

    public MedecinForm() {
        setLayout(new BorderLayout());
        setBackground(ModernTheme.BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonBar(), BorderLayout.SOUTH);

        loadMedecins();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel title = new JLabel("👨‍⚕️  Gestion des Médecins");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(ModernTheme.TEXT_PRIMARY);

        header.add(title, BorderLayout.WEST);
        return header;
    }

    private JPanel buildTablePanel() {
        String[] columns = {"ID", "Nom complet", "Spécialité", "Téléphone"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        ModernTheme.styleTable(table);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(ModernTheme.createStyledScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildButtonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        bar.setOpaque(false);

        JButton refreshBtn = ModernTheme.createSecondaryButton("⟳  Rafraîchir");
        JButton addBtn     = ModernTheme.createPrimaryButton("＋  Ajouter");
        JButton updateBtn  = ModernTheme.createSecondaryButton("✎  Modifier");
        JButton deleteBtn  = ModernTheme.createDangerButton("🗑  Supprimer");

        bar.add(refreshBtn);
        bar.add(updateBtn);
        bar.add(deleteBtn);
        bar.add(addBtn);

        refreshBtn.addActionListener(e -> loadMedecins());
        addBtn.addActionListener(e -> ajouterMedecin());
        updateBtn.addActionListener(e -> modifierMedecin());
        deleteBtn.addActionListener(e -> supprimerMedecin());

        return bar;
    }

    private void loadMedecins() {
        try {
            model.setRowCount(0);
            List<Medecin> medecins = service.getAllMedecins();
            for (Medecin m : medecins)
                model.addRow(new Object[]{m.getId(), m.getNom(), m.getSpecialite(), m.getTelephone()});
        } catch (Exception ex) {
            showError("Erreur chargement : " + ex.getMessage());
        }
    }

    private void ajouterMedecin() {
        JDialog dialog = buildFormDialog("Ajouter un médecin");
        JTextField nomF    = ModernTheme.createTextField(20);
        JTextField specF   = ModernTheme.createTextField(20);
        JTextField telF    = ModernTheme.createTextField(20);
        fillDialog(dialog, new String[]{"Nom complet", "Spécialité", "Téléphone"}, new JTextField[]{nomF, specF, telF}, () -> {
            try {
                Medecin m = new Medecin(nomF.getText().trim(), specF.getText().trim(), telF.getText().trim());
                service.createMedecin(m);
                loadMedecins();
                dialog.dispose();
                showSuccess("Médecin ajouté avec succès !");
            } catch (Exception ex) { showError("Erreur : " + ex.getMessage()); }
        });
        dialog.setVisible(true);
    }

    private void modifierMedecin() {
        int row = table.getSelectedRow();
        if (row == -1) { showWarning("Sélectionnez un médecin dans le tableau."); return; }

        JDialog dialog = buildFormDialog("Modifier le médecin");
        JTextField nomF  = ModernTheme.createTextField(20);
        JTextField specF = ModernTheme.createTextField(20);
        JTextField telF  = ModernTheme.createTextField(20);
        nomF.setText(model.getValueAt(row, 1).toString());
        specF.setText(model.getValueAt(row, 2).toString());
        telF.setText(model.getValueAt(row, 3).toString());

        int id = (int) model.getValueAt(row, 0);
        fillDialog(dialog, new String[]{"Nom complet", "Spécialité", "Téléphone"}, new JTextField[]{nomF, specF, telF}, () -> {
            try {
                service.updateMedecin(new Medecin(id, nomF.getText().trim(), specF.getText().trim(), telF.getText().trim()));
                loadMedecins();
                dialog.dispose();
                showSuccess("Médecin modifié avec succès !");
            } catch (Exception ex) { showError("Erreur : " + ex.getMessage()); }
        });
        dialog.setVisible(true);
    }

    private void supprimerMedecin() {
        int row = table.getSelectedRow();
        if (row == -1) { showWarning("Sélectionnez un médecin dans le tableau."); return; }
        int id  = (int) model.getValueAt(row, 0);
        String nom = model.getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer le médecin « " + nom + " » (ID " + id + ") ?",
                "Confirmer la suppression", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try { service.deleteMedecin(id); loadMedecins(); showSuccess("Médecin supprimé."); }
            catch (Exception ex) { showError("Erreur : " + ex.getMessage()); }
        }
    }

    // ── Helpers UI ────────────────────────────────────────────────────────────
    private JDialog buildFormDialog(String title) {
        Window win = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = (win instanceof Frame)
                ? new JDialog((Frame) win, title, true)
                : new JDialog((Dialog) win, title, true);
        dialog.setSize(380, 310);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(ModernTheme.BG);
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
            content.add(Box.createVerticalStrut(12));
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

    private void showError(String msg)   { JOptionPane.showMessageDialog(this, msg, "Erreur",       JOptionPane.ERROR_MESSAGE); }
    private void showSuccess(String msg) { JOptionPane.showMessageDialog(this, msg, "Succès",        JOptionPane.INFORMATION_MESSAGE); }
    private void showWarning(String msg) { JOptionPane.showMessageDialog(this, msg, "Attention",     JOptionPane.WARNING_MESSAGE); }
}