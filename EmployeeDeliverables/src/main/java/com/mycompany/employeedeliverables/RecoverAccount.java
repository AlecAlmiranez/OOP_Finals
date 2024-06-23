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
public class RecoverAccount extends JFrame {
    private Connection connection;
    private JTextField employeeNumField;
    private JTextField recoveryNumberField;
    private JPasswordField newPasswordField;
    private JTextArea accountDetailsArea;
    private JPanel panel;
    
    /**
     * Creates a new jframe for account recovery
     * @throws SQLException
     */
    public RecoverAccount() throws SQLException {
        final String url = "jdbc:mysql://localhost:3306/db_employeedeliverables";
        final String user = "root";
        final String password = "";

        // Connection communicating with DB
        connection = DriverManager.getConnection(url, user, password);

        // Set up the window
        setTitle("Account Recovery");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create UI components
        JLabel instructionLabel = new JLabel("Enter your employee number and recovery number:");
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 20));

        employeeNumField = new JTextField(20);
        recoveryNumberField = new JTextField(20);
        newPasswordField = new JPasswordField(20);
        newPasswordField.setEnabled(false); // Initially disabled
        JButton recoverButton = new JButton("Recover Account");
        JButton updatePasswordButton = new JButton("Update Password");
        updatePasswordButton.setEnabled(false); // Initially disabled

        // Add action listener to the recover button
        recoverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String employeeNum = employeeNumField.getText().trim();
                String recoveryNumber = recoveryNumberField.getText().trim();
                if (!employeeNum.isEmpty() && !recoveryNumber.isEmpty()) {
                    try {
                        if (verifyAccount(employeeNum, recoveryNumber)) {
                            newPasswordField.setEnabled(true);
                            updatePasswordButton.setEnabled(true);
                            panel.add(new JLabel("New Password: "));
                            panel.add(newPasswordField);
        
                        } else {
                            JOptionPane.showMessageDialog(RecoverAccount.this, "Invalid employee number or recovery number.");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(RecoverAccount.this, "Error verifying account.");
                    }
                } else {
                    JOptionPane.showMessageDialog(RecoverAccount.this, "Please enter both employee number and recovery number.");
                }
            }
        });

        // Add action listener to the update password button
        updatePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String employeeNum = employeeNumField.getText().trim();
                String newPassword = new String(newPasswordField.getPassword()).trim();
                if (!newPassword.isEmpty()) {
                    try {
                        updatePassword(employeeNum, newPassword);
                        JOptionPane.showMessageDialog(RecoverAccount.this, "Password updated successfully.");
                        dispose();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(RecoverAccount.this, "Error updating password.");
                    }
                } else {
                    JOptionPane.showMessageDialog(RecoverAccount.this, "Please enter a new password.");
                }
            }
        });

        accountDetailsArea = new JTextArea(5, 30);
        accountDetailsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(accountDetailsArea);

        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        add(panel, BorderLayout.CENTER);
        panel.add(new JLabel("Employee Number:"));
        panel.add(employeeNumField);
        panel.add(new JLabel("Recovery Number:"));
        panel.add(recoveryNumberField);
        panel.add(accountDetailsArea);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        buttonPanel.add(recoverButton);
        buttonPanel.add(updatePasswordButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        
        setSize(400, 300);
        setLocationRelativeTo(null);
    }

    private boolean verifyAccount(String employeeNum, String recoveryNumber) throws SQLException {
        String query = "SELECT EmployeeNum, Name, Email FROM employee WHERE EmployeeNum = ? AND Recovery = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, employeeNum);
            statement.setString(2, recoveryNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("Name");
                    String email = resultSet.getString("Email");

                    accountDetailsArea.setText("Account Details:\n");
                    accountDetailsArea.append("Employee Number: " + employeeNum + "\n");
                    accountDetailsArea.append("Name: " + name + "\n");
                    accountDetailsArea.append("Email: " + email + "\n");
                    return true;
                } else {
                    accountDetailsArea.setText("No account found with the provided employee number and recovery number.");
                    return false;
                }
            }
        }
    }

    private void updatePassword(String employeeNum, String newPassword) throws SQLException {
        String updateQuery = "UPDATE employee SET Password = ? WHERE EmployeeNum = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, newPassword);
            statement.setString(2, employeeNum);
            statement.executeUpdate();
        }
    }

    /**
     * Main method used for debugging
     * @param args
     */
    public static void main(String[] args) { // Debugging
        SwingUtilities.invokeLater(() -> {
            try {
                new RecoverAccount().setVisible(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }
}
