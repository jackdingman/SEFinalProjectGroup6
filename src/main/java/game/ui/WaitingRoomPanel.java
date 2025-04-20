package game.ui;

import game.app.Game;
import game.multiplayer.ChatClient;

import java.awt.*;
import javax.swing.*;
import java.util.Set;


// Panel representing the waiting room where players cna join and mark themselves ready.
public class WaitingRoomPanel extends JPanel {

    // Username of the local player
    private final String username;

    // Client for communicating with the game server
    private ChatClient client;

    // Variable that indicate whether the player has signaled readiness
    private boolean ready = false;

    // Button for making the player ready
    private final JButton readyButton = new JButton("Ready");

    // Set of player names currently in the waiting room

    // Text area displaying the list of players
    private final JTextArea playerListArea = new JTextArea();


    // Constructs the waiting room panel and initializes the UI components
    public WaitingRoomPanel(String username, Game parent) {
        this.username = username;

        // Positioning for layout
        setLayout(null);
        setPreferredSize(new Dimension(1000, 600));
        setBackground(Color.LIGHT_GRAY);

        // Label prompting readiness
        JLabel waitingLabel = new JLabel("Waiting Room - Ready when you're set!");
        waitingLabel.setBounds(320, 50, 400, 30);
        waitingLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(waitingLabel);

        // Text area for player list
        playerListArea.setBounds(380, 100, 250, 200);
        playerListArea.setEditable(false);
        playerListArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(playerListArea);

        // Ready button with action listener
        readyButton.setBounds(400, 350, 200, 50);
        readyButton.addActionListener(e -> {
            if (!ready) {
                ready = true;
                readyButton.setText("Waiting...");
                readyButton.setEnabled(false);
                client.sendReady(username);
            }
        });
        add(readyButton);

        // Connect the chat client
        ChatClient temp = null;
        try {
            temp = new ChatClient("192.168.0.223", 8300, parent, this);
            temp.sendJoin(username);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        client = temp;
    }

    // Updates the displayed player list
    public void updatePlayerList(Set<String> names) {
        playerListArea.setText("");
        for (String name : names) {
            playerListArea.append(name + (name.equals(username) ? " (You)\n" : "\n"));
        }
    }

    // Transitions the UI from the waiting room to the game panel
    public void startGame() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            GamePanel gamePanel = new GamePanel(username);
            frame.setContentPane(gamePanel);
            frame.revalidate();
            frame.repaint();

            // Make sure the gamePanel has focus
            gamePanel.requestFocusInWindow();
        });
    }
}
