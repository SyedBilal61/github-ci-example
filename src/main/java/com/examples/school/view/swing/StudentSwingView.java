package com.examples.school.view.swing;

import com.examples.school.model.Student;
import com.examples.school.view.StudentView;
import com.examples.school.controller.SchoolController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class StudentSwingView extends JFrame implements StudentView {

    private JPanel contentPane;
    private JTextField txtId;
    private JTextField txtName;
    private JList<StudentItem> listStudents;
    private DefaultListModel<StudentItem> listStudentsModel;
    private JButton btnAdd;
    private JButton btnDelete;
    private JLabel lblErrorMessage;
    private SchoolController schoolController; // Added this reference
    private static final long serialVersionUID = 1L;

    public StudentSwingView() {
        setTitle("Student View");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{0, 0, 0};
        gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
        gbl_contentPane.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
        gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
        contentPane.setLayout(gbl_contentPane);

        // ID label
        JLabel lblId = new JLabel("id");
        GridBagConstraints gbc_lblId = new GridBagConstraints();
        gbc_lblId.insets = new Insets(0, 0, 5, 5);
        gbc_lblId.anchor = GridBagConstraints.EAST;
        gbc_lblId.gridx = 0;
        gbc_lblId.gridy = 0;
        contentPane.add(lblId, gbc_lblId);

        // ID text field
        txtId = new JTextField();
        txtId.setName("idTextBox");
        GridBagConstraints gbc_txtId = new GridBagConstraints();
        gbc_txtId.insets = new Insets(0, 0, 5, 0);
        gbc_txtId.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtId.gridx = 1;
        gbc_txtId.gridy = 0;
        contentPane.add(txtId, gbc_txtId);
        txtId.setColumns(10);

        // Name label
        JLabel lblName = new JLabel("name");
        GridBagConstraints gbc_lblName = new GridBagConstraints();
        gbc_lblName.anchor = GridBagConstraints.EAST;
        gbc_lblName.insets = new Insets(0, 0, 5, 5);
        gbc_lblName.gridx = 0;
        gbc_lblName.gridy = 1;
        contentPane.add(lblName, gbc_lblName);

        // Name text field
        txtName = new JTextField();
        txtName.setName("nameTextBox");
        GridBagConstraints gbc_txtName = new GridBagConstraints();
        gbc_txtName.insets = new Insets(0, 0, 5, 0);
        gbc_txtName.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtName.gridx = 1;
        gbc_txtName.gridy = 1;
        contentPane.add(txtName, gbc_txtName);
        txtName.setColumns(10);

        // Add button
        btnAdd = new JButton("Add");
        btnAdd.setName("addButton");
        btnAdd.setEnabled(false);
        GridBagConstraints gbc_btnAdd = new GridBagConstraints();
        gbc_btnAdd.insets = new Insets(0, 0, 5, 0);
        gbc_btnAdd.gridwidth = 2;
        gbc_btnAdd.gridx = 0;
        gbc_btnAdd.gridy = 2;
        contentPane.add(btnAdd, gbc_btnAdd);

        // Enable Add button only if both fields are not empty
        KeyAdapter btnAddEnabler = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                btnAdd.setEnabled(!txtId.getText().trim().isEmpty() &&
                        !txtName.getText().trim().isEmpty());
            }
        };
        txtId.addKeyListener(btnAddEnabler);
        txtName.addKeyListener(btnAddEnabler);

        // Student list
        listStudentsModel = new DefaultListModel<>();
        listStudents = new JList<>(listStudentsModel);
        listStudents.setName("studentList");
        listStudents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(listStudents);
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridwidth = 2;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 3;
        contentPane.add(scrollPane, gbc_scrollPane);

        // Delete button
        btnDelete = new JButton("Delete Selected");
        btnDelete.setName("deleteButton");
        btnDelete.setEnabled(false);
        GridBagConstraints gbc_btnDelete = new GridBagConstraints();
        gbc_btnDelete.insets = new Insets(0, 0, 5, 0);
        gbc_btnDelete.gridwidth = 2;
        gbc_btnDelete.gridx = 0;
        gbc_btnDelete.gridy = 4;
        contentPane.add(btnDelete, gbc_btnDelete);

        // Enable Delete button only when a student is selected
        listStudents.addListSelectionListener(e -> {
            btnDelete.setEnabled(!listStudents.isSelectionEmpty());
        });

        // Error label
        lblErrorMessage = new JLabel("");
        lblErrorMessage.setName("errorMessageLabel");
        GridBagConstraints gbc_lblError = new GridBagConstraints();
        gbc_lblError.gridwidth = 2;
        gbc_lblError.insets = new Insets(0, 0, 0, 5);
        gbc_lblError.gridx = 0;
        gbc_lblError.gridy = 5;
        contentPane.add(lblErrorMessage, gbc_lblError);
    }

    public void showError(String message, Student student) {
        lblErrorMessage.setText(message + ": " + student.getName());
    }

    @Override
    public void showAllStudents(List<Student> students) {
        listStudentsModel.clear();
        for (Student s : students) {
            listStudentsModel.addElement(new StudentItem(s.getId(), s.getName()));
        }
    }

    // Allow controller injection from SchoolSwingApp
    public void setSchoolController(SchoolController controller) {
        this.schoolController = controller;
    }

    // Getters for UI components
    public JTextField getTxtId() { return txtId; }
    public JTextField getTxtName() { return txtName; }
    public JButton getBtnAdd() { return btnAdd; }
    public JButton getBtnDelete() { return btnDelete; }
    public JList<StudentItem> getListStudents() { return listStudents; }

    // Inner class used only for the JList display
    private static class StudentItem {
        private String id;
        private String name;

        public StudentItem(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return id + " " + name;
        }
    }
}