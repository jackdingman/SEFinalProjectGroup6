import java.io.IOException;
import ocsf.client.AbstractClient;

public class ChatClient extends AbstractClient {

    // Reference to the game panel
    private GamePanel gamePanel;

    // Constructor
    public ChatClient(String host, int port, GamePanel panel) throws Exception {
        super(host, port);
        this.gamePanel = panel;
        openConnection();
    }

    // Called automatically when a message is received from server - Handles pauses and multiplayer state updates
    @Override
    protected void handleMessageFromServer(Object msg) {
        // Pause/resume commands
        if (msg instanceof String) {
            String cmd = (String) msg;
            if (cmd.equals("PAUSE")) {
                gamePanel.setPaused(true);
                return;
            } else if (cmd.equals("RESUME")) {
                gamePanel.setPaused(false);
                return;
            }
        }

        // Game state updates
        if (msg instanceof GameStateUpdate) {
            GameStateUpdate update = (GameStateUpdate) msg;
            gamePanel.updateOtherPlayers(update.players, update.worldState);
        }
    }

    // Sends current players position and coin count to server
    public void sendPlayerUpdate(String username, int x, int y, int coinCount) {
        try {
            PlayerUpdate update = new PlayerUpdate(username, x, y, coinCount);
            sendToServer(update);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Sends a message to the server indicating a coin was collected
    public void sendCoinCollected(String coinId) {
        try {
            sendToServer("COLLECT:" + coinId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sends a message to the server indicating the player reached the flag
    public void sendFlagReached() {
        try {
            sendToServer("FLAG_REACHED");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sends a message or resume command to the server, depending on the current state
    public void sendPause(boolean paused) {
        try {
            sendToServer(paused ? "PAUSE" : "RESUME");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendBlockPosition(String blockId, int x, int y) {
        try {
            sendToServer("BLOCK:" + blockId + ":" + x + ":" + y);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendButtonActivated(String buttonId) {
        try {
            sendToServer("BUTTON:" + buttonId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
