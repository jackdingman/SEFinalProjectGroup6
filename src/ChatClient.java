import java.util.Set;
import java.io.IOException;
import ocsf.client.AbstractClient;

public class ChatClient extends AbstractClient {
    // Reference to the waiting room UI panel
    private final WaitingRoomPanel waitingPanel;

    // Reference to the main game UI panel
    private GamePanel gamePanel;

    // Constructor used by waiting room
    public ChatClient(String host, int port, Game gameFrame, WaitingRoomPanel waitingPanel) throws Exception {
        super(host, port);
        this.waitingPanel = waitingPanel;
        this.gamePanel = null;
        openConnection();
    }

    // Constructor used by game panel after waiting room finishes
    public ChatClient(String host, int port, GamePanel panel) throws Exception {
        super(host, port);
        this.gamePanel = panel;
        this.waitingPanel = null;
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
            } else if (cmd.equals("START_GAME")) {
                if (waitingPanel != null) waitingPanel.startGame();
                return;
            }
        }

        // Update waiting room players
        if (msg instanceof java.util.Set<?> set && waitingPanel != null) {
            Set<String> players = (Set<String>) set;
            waitingPanel.updatePlayerList(players);
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

    // Join the waiting room with a username
    public void sendJoin(String username) {
        try {
            sendToServer("JOIN:" + username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Signals a player is ready
    public void sendReady(String username) {
        try {
            sendToServer("READY:" + username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sends a message to the server to restart the game
    public void sendResetCommand() {
        try {
            sendToServer("RESET_GAME");
        } catch (IOException e) {
            System.err.println("Failed to send reset command: " + e.getMessage());
        }
    }

    // Update block position on server for synchronization
    public void sendBlockPosition(String blockId, int x, int y) {
        try {
            sendToServer("BLOCK:" + blockId + ":" + x + ":" + y);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Notify server that a button was activated
    public void sendButtonActivated(String buttonId) {
        try {
            sendToServer("BUTTON:" + buttonId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}