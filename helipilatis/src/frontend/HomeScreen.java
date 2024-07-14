package frontend;

import javax.swing.*;
import java.awt.*;

public class HomeScreen extends JFrame {
    public HomeScreen() {
        setTitle("Pilates Appointment System - Home");

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        JButton loginAdminButton = new JButton("Login as Admin");

        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginAdminButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginButton.addActionListener(e -> System.out.println("Login button clicked"));
        registerButton.addActionListener(e -> System.out.println("Register button clicked"));
        loginAdminButton.addActionListener(e -> System.out.println("Login as Admin button clicked"));

        add(loginButton);
        // Add space between login and register button
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(registerButton);
        // Add space between register and login admin button
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(loginAdminButton);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HomeScreen().setVisible(true));
    }
}