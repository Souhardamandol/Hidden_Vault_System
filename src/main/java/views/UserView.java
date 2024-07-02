package views;

import dao.DataDAO;
import model.Data;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class UserView extends JFrame {
    private String email;
    private JPanel panel;
    private JButton showHiddenFilesButton;
    private JButton hideFileButton;
    private JButton unhideFileButton;
    private JButton logoutButton;

    public UserView(String email) {
        this.email = email;
        setTitle("User Home");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(null);

        showHiddenFilesButton = new JButton("Show Hidden Files");
        hideFileButton = new JButton("Hide New File");
        unhideFileButton = new JButton("Unhide File");
        logoutButton = new JButton("Logout");

        showHiddenFilesButton.setBounds(50, 50, 300, 25);
        hideFileButton.setBounds(50, 100, 300, 25);
        unhideFileButton.setBounds(50, 150, 300, 25);
        logoutButton.setBounds(50, 200, 300, 25);

        panel.add(showHiddenFilesButton);
        panel.add(hideFileButton);
        panel.add(unhideFileButton);
        panel.add(logoutButton);

        add(panel);

        showHiddenFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHiddenFiles();
            }
        });

        hideFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideFile();
            }
        });

        unhideFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                unhideFile();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Welcome().setVisible(true);
                dispose();
            }
        });
    }

    private void showHiddenFiles() {
        try {
            List<Data> files = DataDAO.getAllFiles(email);
            StringBuilder sb = new StringBuilder("ID - File Name\n");
            for (Data file : files) {
                sb.append(file.getId()).append(" - ").append(file.getFileName()).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void hideFile() {
        String path = JOptionPane.showInputDialog("Enter the file path:");
        File file = new File(path);
        if (file.exists() && !file.isDirectory()) {
            Data data = new Data(0, file.getName(), path, email);
            try {
                DataDAO.hideFile(data);
                JOptionPane.showMessageDialog(this, "File hidden successfully");
            } catch (SQLException | IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error hiding file");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid file path");
        }
    }

    private void unhideFile() {
        try {
            List<Data> files = DataDAO.getAllFiles(email);
            StringBuilder sb = new StringBuilder("ID - File Name\n");
            for (Data file : files) {
                sb.append(file.getId()).append(" - ").append(file.getFileName()).append("\n");
            }
            String idStr = JOptionPane.showInputDialog(this, sb.toString() + "\nEnter the ID of the file to unhide:");
            int id = Integer.parseInt(idStr);
            DataDAO.unhide(id);
            JOptionPane.showMessageDialog(this, "File unhidden successfully");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error unhiding file");
        }
    }
}
