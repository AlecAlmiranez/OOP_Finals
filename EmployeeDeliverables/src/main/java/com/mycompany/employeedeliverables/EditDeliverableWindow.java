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
public class EditDeliverableWindow extends JFrame {
    private JTextField linkToFileField;
    private JTextField dateAndTimeField;
    private int employeeNum;
    private String originalFileName;
    private String originalDateAndTime;
    private Connection connection;
    private AdminDashboard adminDashboard;

    /**
     * 
     * @param employeeNum - From deliverables table
     * @param linkToFile - From deliverables table
     * @param dateAndTime - From deliverables table
     * @param connection - Inherit connection from admin dashboard
     * @param adminDashboard
     */
    public EditDeliverableWindow(int employeeNum, String linkToFile, String dateAndTime, Connection connection, AdminDashboard adminDashboard) {
        this.employeeNum = employeeNum;
        this.originalFileName = linkToFile;
        this.originalDateAndTime = dateAndTime;
        this.connection = connection;
        this.adminDashboard = adminDashboard;
        Color sage = new Color(114, 140, 105);
        
        setTitle("Edit Deliverable");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 10, 10));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        

        JLabel linkToFileLabel = new JLabel("Link to File: ");
        add(linkToFileLabel);
        linkToFileField = new JTextField(linkToFile);
        add(linkToFileField);
        
        JLabel dateAndTimeLabel = new JLabel ("Date and Time:");
        add(dateAndTimeLabel);
        dateAndTimeField = new JTextField(dateAndTime);
        add(dateAndTimeField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    updateDeliverable();
                    adminDashboard.loadDeliverablesData();
                    dispose();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        add(saveButton);
    }

    private void updateDeliverable() throws SQLException {
        String query = "UPDATE deliverables SET FileName = ?, DateandTime = ? WHERE EmployeeNum = ? AND FileName = ? AND DateandTime = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, linkToFileField.getText());
            statement.setString(2, dateAndTimeField.getText());
            statement.setInt(3, employeeNum);
            statement.setString(4, originalFileName);
            statement.setString(5, originalDateAndTime);
            statement.executeUpdate();
        }
    }

}
