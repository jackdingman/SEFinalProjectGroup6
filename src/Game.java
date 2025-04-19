import javax.swing.*;

public class    Game extends JFrame {
    public Game(String username) {
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(new GamePanel(username));  // Pass the username
    }

    public static void main(String[] args) {
        // Start with WelcomePage
        SwingUtilities.invokeLater(() -> {
            WelcomePage welcome = new WelcomePage();
            welcome.setVisible(true);
        });
    }
}
