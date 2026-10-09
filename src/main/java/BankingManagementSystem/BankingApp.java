package BankingManagementSystem;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class BankingApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Use Swing's default look and feel when the system theme is unavailable.
            }
            new BankingUI().setVisible(true);
        });
    }
}
