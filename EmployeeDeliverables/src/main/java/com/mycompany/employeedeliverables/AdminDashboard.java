package com.mycompany.employeedeliverables;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alec
 */
public class AdminDashboard extends JFrame {

    private Connection connection;
    private DefaultTableModel tableModel;
    private JTable dataDisplayTable;
    private JButton addEmployeeButton;
    private boolean ifDeliverables = false;

    /**
     * Opens the AdminDashboard using the admin name in the record
     * @param adminName
     * @param username
     * @throws SQLException
     */
    public AdminDashboard(String adminName, String username) throws SQLException {
        final String url = "jdbc:mysql://localhost:3306/db_employeedeliverables";
        final String user = "root";
        final String password = "";

        // Establish connection
        connection = DriverManager.getConnection(url, user, password);

        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Welcome label for the admin
        JLabel welcomeLabel = new JLabel("Welcome, " + adminName);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setForeground(Color.white);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 30));

        JPanel topPanel = new JPanel(new GridLayout(0, 1, 50, 1));
        topPanel.add(welcomeLabel);
        topPanel.setBackground(new Color(114, 140, 105));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(topPanel, BorderLayout.NORTH);

        // Table to display data
        String[] columnNames = {"Employee Number", "Link To File", "Date and Time", "File Name"};
        tableModel = new DefaultTableModel(columnNames, 0);
        dataDisplayTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // cells are not editable
            }
        };
        dataDisplayTable.getTableHeader().setBackground(new Color(150, 180, 135));
        dataDisplayTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        dataDisplayTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(dataDisplayTable);
        add(scrollPane, BorderLayout.CENTER);
        // Button panel for Edit, Add Employee, Delete, Refresh, Logout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Edit button
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = dataDisplayTable.getSelectedRow();
                if (selectedRow != -1) { // Selected Row
                    if (ifDeliverables) {
                        int employeeNum = (int) dataDisplayTable.getValueAt(selectedRow, 0);
                        String linkToFile = (String) dataDisplayTable.getValueAt(selectedRow, 1);
                        String dateAndTime = (String) dataDisplayTable.getValueAt(selectedRow, 2);
                        new EditDeliverableWindow(employeeNum, linkToFile, dateAndTime, connection, AdminDashboard.this).setVisible(true); // Opens edit window of selected deliverable
                    } else {
                        String employeeNum = (String) dataDisplayTable.getValueAt(selectedRow, 0);
                        try {
                            new EditEmployeeWindow(employeeNum).setVisible(true); // Open the edit window with the selected employee number
                        } catch (SQLException ex) {
                            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Please select an item to edit.");
                }
            }
        });
        buttonPanel.add(editButton);

        // Add Employee button
        addEmployeeButton = new JButton("Add Employee");
        addEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddEmployeeAccountWindow addEmployeeWindow = new AddEmployeeAccountWindow(connection, AdminDashboard.this);
                addEmployeeWindow.setVisible(true);
            }
        });
        buttonPanel.add(addEmployeeButton);

        // Delete Button
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = dataDisplayTable.getSelectedRow();
                if (selectedRow != -1) {
                    int confirm = JOptionPane.showConfirmDialog(AdminDashboard.this, "Are you sure you want to delete the selected item?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            if (ifDeliverables) {
                                int employeeNum = (int) dataDisplayTable.getValueAt(selectedRow, 0);
                                String linkToFile = (String) dataDisplayTable.getValueAt(selectedRow, 1);
                                String dateAndTime = (String) dataDisplayTable.getValueAt(selectedRow, 2);
                                deleteDeliverable(employeeNum, linkToFile, dateAndTime);
                                loadDeliverablesData(); // Reload deliverables data in AdminDashboard
                            } else {
                                String employeeNum = (String) dataDisplayTable.getValueAt(selectedRow, 0);
                                deleteEmployeeAccount(employeeNum);
                                loadEmployeeData(); // Reload employee data in AdminDashboard
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(AdminDashboard.this, "Failed to delete the selected item.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Please select an item to delete.");
                }
            }
        });
        buttonPanel.add(deleteButton);

        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ifDeliverables) {
                    loadDeliverablesData(); // For Refreshing Deliverables
                } else {
                    loadEmployeeData(); // Refresh EmployeeData
                }
            }
        });
        buttonPanel.add(refreshButton);

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminDashboard.this.dispose();
                EmployeeDeliverables.launchGUI();
            }
        });
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.SOUTH);
        
        // Download button
        JButton downloadButton = new JButton("Download");
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = dataDisplayTable.getSelectedRow();
                if (selectedRow != -1) {
                    String fileName = (String) dataDisplayTable.getValueAt(selectedRow, 3); // Get file name
                    int employeeNum = (int) dataDisplayTable.getValueAt(selectedRow, 0);
                    Blob fileData = retrieveFileData(employeeNum, fileName);
                    System.out.println(fileData);
                    try {
                        downloadFile(fileName, fileData);
                    } catch (SQLException | IOException ex) {
                        Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Please select a row to download.");
                }
            }
        });
        buttonPanel.add(downloadButton);

        add(buttonPanel, BorderLayout.SOUTH);

        
        // Menubar
        JMenuBar menuBar = new JMenuBar();
        JMenu viewMenu = new JMenu("View");
        JMenuItem employeesMenuItem = new JMenuItem("Employees");
        JMenuItem deliverablesMenuItem = new JMenuItem("Deliverables");

        // Add action listeners for menu items
        employeesMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    switchToEmployeesView();
                    ifDeliverables = false;
                    addEmployeeButton.setVisible(true);

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        deliverablesMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    switchToDeliverablesView();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Add menu items to the menu
        viewMenu.add(employeesMenuItem);
        viewMenu.add(deliverablesMenuItem);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);

        // Load employee data initially
        switchToEmployeesView();

        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    void loadEmployeeData() {
        String query = "SELECT EmployeeNum, Name, Password, Email, Recovery FROM employee";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            // Clear existing rows from the table
            tableModel.setRowCount(0);

            // Reload Data (For when there are changes)
            while (resultSet.next()) {
                String employeeNum = resultSet.getString("EmployeeNum");
                String name = resultSet.getString("Name");
                String password = resultSet.getString("Password");
                String email = resultSet.getString("Email");
                String recovery = resultSet.getString("Recovery");
                tableModel.addRow(new Object[]{employeeNum, name, password, email, recovery});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    void switchToEmployeesView() throws SQLException {
        tableModel.setColumnIdentifiers(new String[]{"Employee Number", "Name", "Password", "Email", "Recovery"});
        loadEmployeeData();
    }

    void switchToDeliverablesView() throws SQLException {
        tableModel.setColumnIdentifiers(new String[]{"EmployeeNum", "Description", "DateAndTime", "FileName","FileData"});
        loadDeliverablesData();
        ifDeliverables = true;
        addEmployeeButton.setVisible(false);
    }

    void loadDeliverablesData() {
        String query = "SELECT EmployeeNum, linkToFile, DateAndTime, FileName FROM deliverables";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            // Clear existing rows from the table
            tableModel.setRowCount(0);

            // Reload Data
            while (resultSet.next()) {
                int employeeNum = resultSet.getInt("EmployeeNum");
                String linkToFile = resultSet.getString("linkToFile");
                String dateAndTime = resultSet.getString("DateandTime");
                String fileName = resultSet.getString("FileName");
                
                String fileDataSummary = "File Attached"; // You can adjust this based on your actual logic

                tableModel.addRow(new Object[]{employeeNum, linkToFile, dateAndTime, fileName, fileDataSummary});
            }
        } catch (SQLException ex) {
        }
    }

    Blob retrieveFileData(int employeeNum, String fileName) {
         Blob fileData = null;
         String query = "SELECT FileData FROM deliverables WHERE EmployeeNum = ? AND FileName = ?";
         try (PreparedStatement statement = connection.prepareStatement(query)) {
             statement.setInt(1, employeeNum);
             statement.setString(2, fileName);
             System.out.println(employeeNum);
             System.out.println(fileName);

             try (ResultSet resultSet = statement.executeQuery()) {
                 if (resultSet.next()) {
                     fileData = resultSet.getBlob("FileData");
                 } else {
                     System.err.println("No matching record found for employeeNum: " + employeeNum + ", fileName: " + fileName);
                 }
             }
         } catch (SQLException ex) {
             ex.printStackTrace();
             System.err.println("Error executing query: " + ex.getMessage());
         }
         return fileData;
        }



    
    private void deleteEmployeeAccount(String employeeNum) throws SQLException {
        String deleteQuery = "DELETE FROM employee WHERE EmployeeNum = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setString(1, employeeNum);
            statement.executeUpdate();
        }
    }
    
    private void downloadFile(String fileName, Blob fileData) throws SQLException, IOException {
        // Create a file chooser to save the downloaded file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(fileName));
        int userSelection = fileChooser.showSaveDialog(AdminDashboard.this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            try (InputStream inputStream = fileData.getBinaryStream();
                 FileOutputStream outputStream = new FileOutputStream(fileToSave)) {

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                JOptionPane.showMessageDialog(AdminDashboard.this, "File downloaded successfully: " + fileToSave.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(AdminDashboard.this, "Failed to download the file: " + fileName);
            }
        }
    }
    
    private void deleteDeliverable(int employeeNum, String linkToFile, String dateAndTime) throws SQLException {
        String deleteQuery = "DELETE FROM deliverables WHERE EmployeeNum = ? AND LinkToFile = ? AND DateandTime = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setInt(1, employeeNum);
            statement.setString(2, linkToFile);
            statement.setString(3, dateAndTime);
            statement.executeUpdate();
        }
    }

    /**
     * Main method used for debugging the system
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new AdminDashboard("Admin", "AdminNum").setVisible(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }
}