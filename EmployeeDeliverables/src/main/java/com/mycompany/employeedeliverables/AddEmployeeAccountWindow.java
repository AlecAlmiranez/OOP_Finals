package com.mycompany.employeedeliverables;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Alec
 */
public class AddEmployeeAccountWindow extends JFrame {
    private JTextField nameField;
    private JTextField employeeNumberField;
    private JTextField passwordField;
    private JTextField emailField;
    private JTextField recoveryField;
    private Connection connection;
    private AdminDashboard adminDashboard;

    /**
     * Popup window for when adding a new employee
     * Inherits connection from admin dashboard
     * @param connection
     * @param adminDashboard
     */
    public AddEmployeeAccountWindow(Connection connection, AdminDashboard adminDashboard) {
        this.connection = connection;
        this.adminDashboard = adminDashboard;

        setTitle("Add Employee Account");
        setSize(300, 250);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 10, 10));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Employee Number:"));
        employeeNumberField = new JTextField();
        add(employeeNumberField);

        add(new JLabel("Password:"));
        passwordField = new JTextField();
        add(passwordField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Recovery:"));
        recoveryField = new JTextField();
        add(recoveryField);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    addEmployeeAccount();
                    adminDashboard.loadEmployeeData(); // Reload employee data in AdminDashboard
                    dispose(); // Close the window after adding
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(AddEmployeeAccountWindow.this, "Failed to add employee account.");
                }
            }
        });
        add(addButton);
    }

    private void addEmployeeAccount() throws SQLException {
        String query = "INSERT INTO employee (EmployeeNum, Name, Password, Email, Recovery) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, Integer.parseInt(employeeNumberField.getText()));
            statement.setString(2, nameField.getText());
            statement.setString(3, passwordField.getText());
            statement.setString(4, emailField.getText());
            statement.setInt(5, Integer.parseInt(recoveryField.getText()));
            statement.executeUpdate();
        }
    }
}
