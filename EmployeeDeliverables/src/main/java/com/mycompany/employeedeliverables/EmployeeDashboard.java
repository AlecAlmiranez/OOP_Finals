package com.mycompany.employeedeliverables;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alec
 */
public class EmployeeDashboard extends EmployeeDeliverables {
    private File selectedFile;

    /**
     * Uses the employeeName from the record to create a new jframe with the passed variables
     * @param employeeName
     * @param username
     * @throws SQLException
     */
    public EmployeeDashboard(String employeeName, String username) throws SQLException {
        final String url = "jdbc:mysql://localhost:3306/db_employeedeliverables";
        final String user = "root";
        final String password = "";
        
        // Connect to DB
        final Connection connection = DriverManager.getConnection(url, user, password);
        setTitle("Employee Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome, " + employeeName);
        welcomeLabel.setForeground(Color.white);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 30));

        // Text area for user input
        JLabel inputLabel = new JLabel("Enter file description here:");
        inputLabel.setForeground(Color.white);
        JTextArea inputArea = new JTextArea(5, 50); // 5 rows, 50 columns
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);

        // File chooser button
        JButton chooseFileButton = new JButton("Choose PDF to upload");
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    JOptionPane.showMessageDialog(null, "Selected file: " + selectedFile.getName());
                }
            }
        });

        // Submit button
        JButton submitButton = new JButton("Submit"); 
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String linkToFile = inputArea.getText();
                if (selectedFile != null || !linkToFile.isEmpty()) {
                    try (FileInputStream fis = selectedFile != null ? new FileInputStream(selectedFile) : null) {
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                        String query = "INSERT INTO deliverables (EmployeeNum, LinkToFile, FileName, FileData, DateAndTime) VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement insertNewSubmission = connection.prepareStatement(query);
                        insertNewSubmission.setString(1, username); // Assuming username is EmployeeNum
                        insertNewSubmission.setString(2, linkToFile.isEmpty() ? null : linkToFile);
                        insertNewSubmission.setString(3, selectedFile != null ? selectedFile.getName() : null);
                        if (selectedFile != null) {
                            insertNewSubmission.setBinaryStream(4, fis, (int) selectedFile.length());
                        } else {
                            insertNewSubmission.setNull(4, java.sql.Types.BLOB);
                        }
                        insertNewSubmission.setTimestamp(5, timestamp);

                        insertNewSubmission.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Submission successful!");

                    } catch (SQLException | IOException ex) {
                        Logger.getLogger(EmployeeDashboard.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a description or choose a PDF file to upload.");
                }
            }
        });

        // Logout button
        JButton logoutButton = new JButton("Logout"); 
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EmployeeDashboard.this.dispose();
                EmployeeDeliverables.launchGUI();
            }
        });

        // Center panel with vertical layout for labels and input
        JPanel centerPanel = new JPanel(new GridLayout(0, 1, 50, 1));
        Color sage = new Color(114, 140, 105);
        centerPanel.setBackground(sage);
        centerPanel.add(welcomeLabel);
        centerPanel.add(inputLabel);
        centerPanel.add(new JScrollPane(inputArea));
        centerPanel.add(chooseFileButton);
        centerPanel.add(submitButton);
        centerPanel.add(logoutButton);
        
        // Padding
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(centerPanel, BorderLayout.CENTER);
        
        setSize(600, 400); 
        setLocationRelativeTo(null);
    }

    /**
     * Main Method used for debugging purposes
     * @param args
     */
    public static void main(String[] args) {
        // Debugging
        SwingUtilities.invokeLater(() -> {
            try {
                new EmployeeDashboard("Employee", "EmployeeNum").setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(EmployeeDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}
