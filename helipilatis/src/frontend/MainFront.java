package frontend;

import javax.swing.SwingUtilities;

public class MainFront {

    private static void createAndShowGUI() {
        // Create and display the HomeScreen
        new HomeScreen().setVisible(true);
    }

    public static void main(String[] args) {
        // Schedule a job for the event dispatch thread:
        // creating and showing this application's GUI
        SwingUtilities.invokeLater(MainFront::createAndShowGUI);
    }
}