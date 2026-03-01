package ui;

import service.PatientService;
import model.Patient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientForm extends JPanel {

    private final PatientService service = new PatientService();
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;

    public PatientForm() {
        setLayout(new BorderLayout());
        setBackground(ModernTheme.BG);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonBar(), BorderLayout.SOUTH);

        loadPatients();
    }

    // ── En-tête ──────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        JLabel title = new JLabel("👤  Gestion des Patients");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(ModernTheme.TEXT_PRIMARY);
        left.add(title);

        // Barre de recherche
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        searchField = ModernTheme.createTextField(18);
        searchField.putClientProperty("JTextField.placeholderText", "Rechercher un patient...");
        right.add(new JLabel("🔍 "));
        right.add(searchField);

        header.add(left,  BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // ── Tableau ──────────────────────────────────────────────────────────────
    private JPanel buildTablePanel() {
        String[] columns = {"ID", "Nom complet", "Âge", "Ville"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        ModernTheme.styleTable(table);

        JScrollPane sp = ModernTheme.createStyledScrollPane(table);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    // ── Barre de boutons ─────────────────────────────────────────────────────
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

        refreshBtn.addActionListener(e -> loadPatients());
        addBtn.addActionListener(e -> ajouterPatient());
        updateBtn.addActionListener(e -> modifierPatient());
        deleteBtn.addActionListener(e -> supprimerPatient());

        return bar;
    }

    // ── Données ──────────────────────────────────────────────────────────────
    private void loadPatients() {
        try {
            model.setRowCount(0);
            List<Patient> patients = service.getAllPatients();
            for (Patient p : patients) {
                model.addRow(new Object[]{p.getId(), p.getNom(), p.getAge(), p.getVille()});
            }
        } catch (Exception ex) {
            showError("Erreur chargement : " + ex.getMessage());
        }
    }

    private void ajouterPatient() {
        JDialog dialog = buildFormDialog("Ajouter un patient");
        JTextField nomF   = ModernTheme.createTextField(20);
        JTextField ageF   = ModernTheme.createTextField(20);
        JTextField villeF = ModernTheme.createTextField(20);
        fillDialog(dialog, new String[]{"Nom complet", "Âge", "Ville"}, new JTextField[]{nomF, ageF, villeF}, () -> {
            try {
                int age = Integer.parseInt(ageF.getText().trim());
                Patient p = new Patient(nomF.getText().trim(), age, villeF.getText().trim());
                service.createPatient(p);
                loadPatients();
                dialog.dispose();
                showSuccess("Patient ajouté avec succès !");
            } catch (Exception ex) { showError("Erreur : " + ex.getMessage()); }
        });
        dialog.setVisible(true);
    }

    private void modifierPatient() {
        int row = table.getSelectedRow();
        if (row == -1) { showWarning("Sélectionnez un patient dans le tableau."); return; }

        JDialog dialog = buildFormDialog("Modifier le patient");
        JTextField nomF   = ModernTheme.createTextField(20);
        JTextField ageF   = ModernTheme.createTextField(20);
        JTextField villeF = ModernTheme.createTextField(20);
        nomF.setText(model.getValueAt(row, 1).toString());
        ageF.setText(model.getValueAt(row, 2).toString());
        villeF.setText(model.getValueAt(row, 3).toString());

        int id = (int) model.getValueAt(row, 0);
        fillDialog(dialog, new String[]{"Nom complet", "Âge", "Ville"}, new JTextField[]{nomF, ageF, villeF}, () -> {
            try {
                int age = Integer.parseInt(ageF.getText().trim());
                service.updatePatient(new Patient(id, nomF.getText().trim(), age, villeF.getText().trim()));
                loadPatients();
                dialog.dispose();
                showSuccess("Patient modifié avec succès !");
            } catch (Exception ex) { showError("Erreur : " + ex.getMessage()); }
        });
        dialog.setVisible(true);
    }

    private void supprimerPatient() {
        int row = table.getSelectedRow();
        if (row == -1) { showWarning("Sélectionnez un patient dans le tableau."); return; }
        int id = (int) model.getValueAt(row, 0);
        String nom = model.getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer le patient « " + nom + " » (ID " + id + ") ?",
                "Confirmer la suppression", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try { service.deletePatient(id); loadPatients(); showSuccess("Patient supprimé."); }
            catch (Exception ex) { showError("Erreur : " + ex.getMessage()); }
        }
    }

    // ── Helpers UI ───────────────────────────────────────────────────────────
    private JDialog buildFormDialog(String title) {
        Window win = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = (win instanceof Frame)
                ? new JDialog((Frame) win, title, true)
                : new JDialog((Dialog) win, title, true);
        dialog.setSize(380, 300);
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

    private void showError(String msg)   { JOptionPane.showMessageDialog(this, msg, "Erreur",         JOptionPane.ERROR_MESSAGE); }
    private void showSuccess(String msg) { JOptionPane.showMessageDialog(this, msg, "Succès",          JOptionPane.INFORMATION_MESSAGE); }
    private void showWarning(String msg) { JOptionPane.showMessageDialog(this, msg, "Avertissement",   JOptionPane.WARNING_MESSAGE); }
}