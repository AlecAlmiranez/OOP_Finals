package com.mycompany.employeedeliverables;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 *
 * @author Alec
 */
public class EditEmployeeWindow extends JFrame {
    private final Connection connection;
    private JTextField nameField = new JTextField(20);
    private JTextField passwordField = new JTextField(20);
    private JTextField emailField = new JTextField(20);
    private JTextField recoveryField = new JTextField(20);

    /**
     * 
     * @param employeeNum - from employee table
     * @throws SQLException
     */
    public EditEmployeeWindow(String employeeNum) throws SQLException {
        final String url = "jdbc:mysql://localhost:3306/db_employeedeliverables";
        final String user = "root";
        final String password = "";

        // Connect to DB
        connection = DriverManager.getConnection(url, user, password);

        setTitle("Edit Employee");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel for input fields
        JPanel inputPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Labels and text fields for employee details
        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField(20);
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Password:"));
        passwordField = new JTextField(20);
        inputPanel.add(passwordField);

        inputPanel.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        inputPanel.add(emailField);

        inputPanel.add(new JLabel("Recovery:"));
        recoveryField = new JTextField(20);
        inputPanel.add(recoveryField);

        add(inputPanel, BorderLayout.CENTER);

        // Button panel for Save button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    saveEmployeeDetails(employeeNum);
                    JOptionPane.showMessageDialog(EditEmployeeWindow.this, "Employee details saved successfully.");
                    EditEmployeeWindow.this.dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(EditEmployeeWindow.this, "Error occurred while saving employee details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load employee details
        loadEmployeeDetails(employeeNum);

        setSize(400, 300);
        setLocationRelativeTo(null);
    }

    private void loadEmployeeDetails(String employeeNum) throws SQLException {
        String query = "SELECT Name, Password, Email, Recovery FROM employee WHERE EmployeeNum = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, employeeNum);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("Name");
                String password = resultSet.getString("Password");
                String email = resultSet.getString("Email");
                String recovery = resultSet.getString("Recovery");
                // Set the loaded details in the text fields
                nameField.setText(name);
                passwordField.setText(password);
                emailField.setText(email);
                recoveryField.setText(recovery);
            }
        }
    }

    private void saveEmployeeDetails(String employeeNum) throws SQLException {
        String name = nameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String recovery = recoveryField.getText();

        String query = "UPDATE employee SET Name=?, Password=?, Email=?, Recovery=? WHERE EmployeeNum=?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, password);
            statement.setString(3, email);
            statement.setString(4, recovery);
            statement.setString(5, employeeNum);
            statement.executeUpdate();
        }
    }

    /**
     * Main method used for debugging
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new EditEmployeeWindow("123").setVisible(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }
}
