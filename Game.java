import javax.swing.*;

public class Game extends JFrame {
    public Game(String username) {
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(new GamePanel(username));  // Pass the username
    }

    public static void main(String[] args) {
        String username = JOptionPane.showInputDialog("Enter your username:");
        if (username != null && !username.trim().isEmpty()) {
            Game game = new Game(username);
            game.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Username is required.");
        }
    }
}
