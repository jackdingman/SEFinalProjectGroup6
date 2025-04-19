import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class WaitingRoomPanel extends JPanel {
    private final String username;
    private ChatClient client;
    private boolean ready = false;
    private final JButton readyButton = new JButton("Ready");
    private final Set<String> playerNames = new HashSet<>();
    private final JTextArea playerListArea = new JTextArea();

    public WaitingRoomPanel(String username, Game parent) {
        this.username = username;
        setLayout(null);
        setPreferredSize(new Dimension(1000, 600));
        setBackground(Color.LIGHT_GRAY);

        JLabel waitingLabel = new JLabel("Waiting Room - Ready when you're set!");
        waitingLabel.setBounds(320, 50, 400, 30);
        waitingLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(waitingLabel);

        playerListArea.setBounds(380, 100, 250, 200);
        playerListArea.setEditable(false);
        playerListArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(playerListArea);

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

        // Connect client
        ChatClient temp = null;
        try {
            temp = new ChatClient("192.168.0.223", 8300, parent, this);
            temp.sendJoin(username); // register player with server
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        client = temp;
    }

    public void updatePlayerList(Set<String> names) {
        playerListArea.setText("");
        for (String name : names) {
            playerListArea.append(name + (name.equals(username) ? " (You)\n" : "\n"));
        }
    }

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
