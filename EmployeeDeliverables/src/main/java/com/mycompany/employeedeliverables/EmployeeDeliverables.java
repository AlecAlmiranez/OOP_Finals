package com.mycompany.employeedeliverables;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 *
 * @author Alec
 */
public class EmployeeDeliverables extends JFrame {

    /**
     * Launches GUI/Login GUI
     */
    public static void launchGUI() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    EmployeeDeliverables frame = new EmployeeDeliverables();
                    frame.initializeGUI(); // Call method to initialize GUI
                    frame.setVisible(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Method to initialize GUI components
    private void initializeGUI() throws SQLException {
        final String url = "jdbc:mysql://localhost:3306/db_employeedeliverables";
        final String user = "root";
        final String password = "";
        
        // Connect to DB
        final Connection connection = DriverManager.getConnection(url, user, password);

        System.out.println("Connection established successfully.");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("TaskForge");

        // Set up the main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        // Set up the welcome label
        JLabel welcomeLabel = new JLabel("TaskForge");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setForeground(Color.white);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 30));
        Color sage = new Color(114, 140, 105);
        welcomeLabel.setOpaque(true);
        welcomeLabel.setBackground(sage);
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Set up the center panel with BoxLayout
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(sage);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Create a panel for the employee number input
        JPanel employeePanel = new JPanel();
        employeePanel.setBackground(sage);
        employeePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JLabel employeeNumLabel = new JLabel("Employee Number:");
        employeeNumLabel.setForeground(Color.WHITE);
        JTextField employeeNumField = new JTextField(20);
        employeePanel.add(employeeNumLabel);
        employeePanel.add(employeeNumField);

        // Create a panel for the password input
        JPanel passwordPanel = new JPanel();
        passwordPanel.setBackground(sage);
        passwordPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JLabel employeePasswordFieldLabel = new JLabel("Password:");
        employeePasswordFieldLabel.setForeground(Color.WHITE);
        JPasswordField passwordField = new JPasswordField(20);
        passwordPanel.add(employeePasswordFieldLabel);
        passwordPanel.add(passwordField);

        // Add the input panels to the center panel
        centerPanel.add(employeePanel);
        centerPanel.add(passwordPanel);

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(sage);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton loginButton = new JButton("Login");
        buttonPanel.add(loginButton);

        // Account Recovery Button
        JButton recoverButton = new JButton("Recover Account");
        buttonPanel.add(recoverButton);

        // Add the button panel to the center panel
        centerPanel.add(buttonPanel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Get username and password input
                    String username = employeeNumField.getText().trim();
                    String password = new String(passwordField.getPassword());

                    // Query the database to check if input matches any employee record
                    PreparedStatement statement = connection.prepareStatement("SELECT Name FROM employee WHERE EmployeeNum = ? AND Password = ?");
                    statement.setString(1, username);
                    statement.setString(2, password);
                    ResultSet resultSet = statement.executeQuery();

                    // Query the database to check if input matches any admin record
                    PreparedStatement adminCheck = connection.prepareStatement("SELECT Name FROM admin WHERE AdminNum = ? AND Password = ?");
                    adminCheck.setString(1, username);
                    adminCheck.setString(2, password);
                    ResultSet adminResult = adminCheck.executeQuery();

                    // Display notification based on query result
                    if (resultSet.next()) {
                        String employeeName = resultSet.getString("Name"); // Gets the name of the employee in the record
                        JOptionPane.showMessageDialog(centerPanel, "Login successful.", "Notification", JOptionPane.INFORMATION_MESSAGE);
                        dispose(); // Close the login frame
                        new EmployeeDashboard(employeeName, username).setVisible(true);
                    } else if (adminResult.next()) {
                        String adminName = adminResult.getString("Name"); // Gets the name of the admin in the record
                        JOptionPane.showMessageDialog(centerPanel, "Admin login successful.", "Notification", JOptionPane.INFORMATION_MESSAGE);
                        dispose(); // Close the login frame
                        new AdminDashboard(adminName, username).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(centerPanel, "Invalid username or password.", "Notification", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        recoverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        new RecoverAccount().setVisible(true);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });
            }
        });

        pack();
        setLocationRelativeTo(null); // Center the frame on the screen
    }

    /**
     *
     * @param args
     * @throws SQLException
     */
    public static void main(String[] args) throws SQLException {
        launchGUI(); // Call launchGUI to start the GUI
    }
}
