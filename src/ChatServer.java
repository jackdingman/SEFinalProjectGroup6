import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
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
    private final HashSet<String> connectedPlayers = new HashSet<>();
    private final HashSet<String> readyPlayers = new HashSet<>();

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

                if (command.startsWith("JOIN:")) {
                    String user = command.substring(5);
                    connectedPlayers.add(user);
                    log.append(user + " joined the waiting room\n");
                    sendToAllClients(new HashSet<>(connectedPlayers)); // update all clients
                }
                else if (command.startsWith("READY:")) {
                    String user = command.substring(6);
                    readyPlayers.add(user);
                    log.append(user + " is ready\n");

                    log.append("Connected players: " + connectedPlayers + "\n");
                    log.append("Ready players: " + readyPlayers + "\n");

                    // If there's only one player, start the game when they're ready
                    if (connectedPlayers.size() == 1 && readyPlayers.containsAll(connectedPlayers)) {
                        log.append("Only one player - starting game immediately\n");
                        sendToAllClients("START_GAME");
                    }
                    // Otherwise, wait for everyone to be ready
                    else if (readyPlayers.containsAll(connectedPlayers)) {
                        log.append("All players ready - starting game\n");
                        sendToAllClients("START_GAME");
                    }
                }
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
                } else if (command.equals("RESET_GAME")) {
                    world = new GameWorldState(1); // reset level
                    playerStates.clear(); // optional: reset all players
                    log.append("Game state reset by client.\n");
                    sendWorldUpdate();
                } else if (command.startsWith("BLOCK:")) {
                    String[] parts = command.split(":");
                    if (parts.length == 4) {
                        String blockId = parts[1];
                        int x = Integer.parseInt(parts[2]);
                        int y = Integer.parseInt(parts[3]);
                        world.updateBlockPosition(blockId, x, y);
                        sendWorldUpdate();  // No log entry
                    }
                    return;
                } else if (command.startsWith("BUTTON:")) {
                    String buttonId = command.substring(7);
                    world.activateButton(buttonId);
                    sendWorldUpdate();  // No log entry
                    return;
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