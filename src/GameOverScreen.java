import javax.swing.*;
import java.awt.*;

/**
  Displays the Game Over screen with all player stats.
 */
public class GameOverScreen extends JPanel {
    public GameOverScreen(PlayerStats stats, String username) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Title label
        JLabel title = new JLabel("Game Over", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        // Text area to show player stats
        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        statsArea.setBackground(Color.DARK_GRAY);
        statsArea.setForeground(Color.WHITE);

        // Build stats text
        StringBuilder sb = new StringBuilder("== Final Stats ==\n\n");
        for (String player : stats.getCoinsCollected().keySet()) {
            sb.append(String.format("Player: %-10s | Coins: %2d | Deaths: %2d | Medals: %2d\n",
                    player,
                    stats.getCoinsCollected().getOrDefault(player, 0),
                    stats.getDeaths().getOrDefault(player, 0),
                    stats.getMedals().getOrDefault(player, 0)
            ));
        }

        // Adds the MVP line
        sb.append("\nMVP: ").append(stats.getMVP());
        statsArea.setText(sb.toString());

        // Add scrollable stats area
        add(new JScrollPane(statsArea), BorderLayout.CENTER);

        //Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);

        // Restart button: loads GamePanel again with the same username
        JButton restartBtn = new JButton("Restart Game");
        restartBtn.setFont(new Font("Arial", Font.BOLD, 18));
        restartBtn.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            GamePanel newPanel = new GamePanel(username);

            // Tell server to reset level
            if (newPanel.getClient() != null) {
                newPanel.getClient().sendResetCommand(); // Reset's the server for a new game
            }

            frame.setContentPane(newPanel);
            frame.revalidate();
            frame.repaint();
            newPanel.requestFocusInWindow();
        });



        // Exit button: closes the application
        JButton exitBtn = new JButton("Exit Game");
        exitBtn.setFont(new Font("Arial", Font.BOLD, 18));
        exitBtn.addActionListener(e -> System.exit(0));

        buttonPanel.add(restartBtn);
        buttonPanel.add(exitBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
