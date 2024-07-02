import views.Welcome;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Launch the Swing application
        SwingUtilities.invokeLater(() -> {
            new Welcome().setVisible(true);
        });
    }
}
