import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class ChatServer extends AbstractServer {
    private JTextArea log;
    private JLabel status;
    private boolean running = false;
    private Database database;

    private final HashMap<String, PlayerUpdate> playerStates = new HashMap<>();
    private GameWorldState world = new GameWorldState(1);

    // Constructor
    public ChatServer() {
        super(8300);
        this.setTimeout(500);
    }

    public boolean isRunning() {
        return running;
    }

    // Link GUI components to update log and status from server events
    public void setLog(JTextArea log) {
        this.log = log;
    }

    public void setStatus(JLabel status) {
        this.status = status;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    // Triggered when server starts listening for client connections
    @Override
    public void serverStarted() {
        running = true;
        status.setText("Listening");
        status.setForeground(Color.GREEN);
        log.append("Server started\n");
    }

    // Triggered when server stops listening for new clients
    @Override
    public void serverStopped() {
        status.setText("Stopped");
        status.setForeground(Color.RED);
        log.append("Server stopped accepting new clients - press Listen to start accepting new clients\n");
    }

    // Triggered when server is completely shut down
    @Override
    public void serverClosed() {
        running = false;
        status.setText("Close");
        status.setForeground(Color.RED);
        log.append("Server and all current clients are closed - press Listen to restart\n");
    }

    // Triggers when a client connects
    @Override
    public void clientConnected(ConnectionToClient client) {
        log.append("Client " + client.getId() + " connected\n");
    }

    // Main method that handles any message received from a client
    @Override
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        try {
            // Login attempt
            if (msg instanceof LoginData) {
                LoginData data = (LoginData) msg;
                Object result;
                if (database.verifyAccount(data.getUsername(), data.getPassword())) {
                    result = "LoginSuccessful";
                    log.append("Client " + client.getId() + " logged in as " + data.getUsername() + "\n");
                } else {
                    result = new Error("The username and password are incorrect.", "Login");
                    log.append("Client " + client.getId() + " failed to log in\n");
                }
                client.sendToClient(result);
                return;
            }
            // Player position + coin count
            else if (msg instanceof PlayerUpdate) {
                PlayerUpdate update = (PlayerUpdate) msg;
                playerStates.put(update.username, update);
                sendWorldUpdate();
                return;
            }

            // Handles string-based commands
            else if (msg instanceof String) {
                String command = (String) msg;

                // Pause and resume sync
                if (command.equals("PAUSE") || command.equals("RESUME")) {
                    sendToAllClients(command);
                    log.append("Broadcasted " + command + "\n");
                    return;
                }
                // coin / flag
                if (command.startsWith("COLLECT:")) {
                    String coinId = command.substring(8);
                    world.collectCoin(coinId);
                    log.append("Coin collected: " + coinId + "\n");
                    sendWorldUpdate();
                } else if (command.equals("FLAG_REACHED")) {
                    world.advanceLevel();
                    log.append("Flag reached. Advancing to level " + world.currentLevel + "\n");
                    sendWorldUpdate();
                }
                return;
            }
        } catch (IOException e) {
            log.append("Error: " + e.getMessage() + "\n");
        }
    }

    // Builds and broadcasts the current full game state to all clients
    private void sendWorldUpdate() throws IOException {
        GameStateUpdate update = new GameStateUpdate(new HashMap<>(playerStates), world);
        sendToAllClients(update);
    }

    // Triggeredf if an error occurs while listening for clients
    @Override
    public void listeningException(Throwable exception) {
        running = false;
        status.setText("Exception occurred while listening");
        status.setForeground(Color.RED);
        log.append("Listening exception: " + exception.getMessage() + "\n");
        log.append("Press Listen to restart server\n");
    }
}