package views;

import dao.UserDAO;
import model.User;
import service.GenerateOTP;
import service.SendOTPService;
import service.UserService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Welcome extends JFrame {
    private JPanel panel;
    private JButton loginButton;
    private JButton signupButton;
    private JTextField emailField;
    private JTextField nameField;
    private JLabel emailLabel;
    private JLabel nameLabel;
    private JLabel otpLabel;

    public Welcome() {
        setTitle("Hidden vault System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(null);

        loginButton = new JButton("Login");
        signupButton = new JButton("Sign Up");

        emailLabel = new JLabel("Email:");
        nameLabel = new JLabel("Name:");
        otpLabel = new JLabel("OTP:");

        emailField = new JTextField();
        nameField = new JTextField();


        emailLabel.setBounds(50, 50, 80, 25);
        emailField.setBounds(150, 50, 165, 25);
        nameLabel.setBounds(50, 80, 80, 25);
        nameField.setBounds(150, 80, 165, 25);
        otpLabel.setBounds(50, 110, 80, 25);

        loginButton.setBounds(50, 140, 100, 25);
        signupButton.setBounds(200, 140, 100, 25);

        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(loginButton);
        panel.add(signupButton);

        add(panel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signUp();
            }
        });
    }

    private void login() {
        String email = emailField.getText();
        try {
            if (UserDAO.isExists(email)) {
                String genOTP = GenerateOTP.getOTP();
                SendOTPService.sendOTP(email, genOTP);
                JOptionPane.showMessageDialog(this, "OTP sent to email. Enter OTP to login.");
                String otp = JOptionPane.showInputDialog("Enter OTP:");
                if (otp.equals(genOTP)) {
                    new UserView(email).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Wrong OTP");
                }
            } else {
                JOptionPane.showMessageDialog(this, "User not found");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void signUp() {
        String name = nameField.getText();
        String email = emailField.getText();
        String genOTP = GenerateOTP.getOTP();
        SendOTPService.sendOTP(email, genOTP);
        JOptionPane.showMessageDialog(this, "OTP sent to email. Enter OTP to sign up.");
        String otp = JOptionPane.showInputDialog("Enter OTP:");
        if (otp.equals(genOTP)) {
            User user = new User(name, email);
            Integer response = UserService.saveUser(user);
            if (response == 1) {
                JOptionPane.showMessageDialog(this, "User registered");
            } else if (response == 0) {
                JOptionPane.showMessageDialog(this, "User already exists");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Wrong OTP");
        }
    }
}
