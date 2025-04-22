package game.multiplayer;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JLabel;
import java.io.IOException;
import javax.swing.JTextArea;

import game.entity.PlayerUpdate;
import game.world.GameStateUpdate;
import game.world.GameWorldState;
import login.util.LoginData;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class ChatServer extends AbstractServer {

    // Tracks the total coins collected by each player
    private final HashMap<String, Integer> serverCoins = new HashMap<>();

    // Tracks the total deaths recorded for each player
    private final HashMap<String, Integer> serverDeaths = new HashMap<>();

    // Tracks the total medals awarded to each player
    private final HashMap<String, Integer> serverMedals = new HashMap<>();

    // Maps each level number to the first player who reached the flag on that level
    private final HashMap<Integer, String> levelFirstFlags = new HashMap<>();

    // Reference to GUI log area for server messages
    private JTextArea log;

    // Reference to GUI status label
    private JLabel status;

    // Flag indicating whether server is currently running
    private boolean running = false;

    // Tracks each player's latest update
    private final HashMap<String, PlayerUpdate> playerStates = new HashMap<>();

    // Set of players who have joined the waiting room
    private final HashSet<String> connectedPlayers = new HashSet<>();

    // Set of players who have marked ready
    private final HashSet<String> readyPlayers = new HashSet<>();

    // Shared game world state
    private GameWorldState world = new GameWorldState(1);

    // Constructor
    public ChatServer() {
        super(8300);
        this.setTimeout(500);
    }

    // Returns whether server is accepting connections
    public boolean isRunning() {
        return running;
    }

    // Link GUI components to update log and status from server events
    public void setLog(JTextArea log) {
        this.log = log;
    }

    // Injects the GUI status label
    public void setStatus(JLabel status) {
        this.status = status;
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
                return;
            }
            // Player position + coin count
            else if (msg instanceof PlayerUpdate) {
                PlayerUpdate update = (PlayerUpdate) msg;
                playerStates.put(update.username, update);
                // Update coin count from player updates
                serverCoins.put(update.username, update.coinCount);
                sendWorldUpdate();
                return;
            }

            // Handles string-based commands
            else if (msg instanceof String) {
                String command = (String) msg;
                // log.append("<<< Server got raw command: '" + command + "'\n");

                // Player joins waiting room
                if (command.startsWith("JOIN:")) {
                    String user = command.substring(5);
                    connectedPlayers.add(user);
                    log.append(user + " joined the waiting room\n");
                    sendToAllClients(new HashSet<>(connectedPlayers)); // update all clients
                }
                // Player signals ready
                else if (command.startsWith("READY:")) {
                    String user = command.substring(6);
                    readyPlayers.add(user);
                    log.append(user + " is ready\n");

                    // If there's only one player, start immediately when ready
                    if (connectedPlayers.size() == 1 && readyPlayers.containsAll(connectedPlayers)) {
                        log.append("Starting single-player game\n");
                        sendToAllClients("START_GAME");
                    }
                    // Wait for everyone to be ready in multiplayer
                    else if (readyPlayers.containsAll(connectedPlayers)) {
                        log.append("All players ready - starting game\n");
                        sendToAllClients("START_GAME");
                    }
                }
                // Pause/resume broadcast
                else if (command.equals("PAUSE") || command.equals("RESUME")) {
                    sendToAllClients(command);
                    log.append("Broadcasted " + command + "\n");
                }
                // Coin collection event
                else if (command.startsWith("COLLECT:")) {
                    String coinId = command.substring(8);
                    world.collectCoin(coinId);
                    log.append("Coin collected: " + coinId + "\n");
                    sendWorldUpdate();
                }
                // Flag reached event: advance level
                else if (command.equals("FLAG_REACHED")) {
                    int completedLevel = world.currentLevel;
                    world.advanceLevel();
                    log.append("Flag reached. Advancing to level " + world.currentLevel + "\n");

                    // Award medal to first player who reached this level's flag
                    String firstPlayer = levelFirstFlags.get(completedLevel);
                    if (firstPlayer != null) {
                        serverMedals.merge(firstPlayer, 1, Integer::sum);
                        log.append("Awarded level completion medal to " + firstPlayer + "\n");
                    }

                    // Send final stats if game completed
                    if (world.currentLevel > 3) {
                        HashMap<String, Object> statsData = new HashMap<>();
                        statsData.put("coins", new HashMap<>(serverCoins));
                        statsData.put("deaths", new HashMap<>(serverDeaths));
                        statsData.put("medals", new HashMap<>(serverMedals));
                        sendToAllClients(statsData);
                        log.append("Sent final stats to all clients\n");
                    }

                    sendWorldUpdate();
                }
                // Reset game to level 1
                else if (command.equals("RESET_GAME")) {
                    world = new GameWorldState(1);
                    playerStates.clear();
                    serverCoins.clear();
                    serverDeaths.clear();
                    serverMedals.clear();
                    levelFirstFlags.clear();
                    log.append("Game state fully reset\n");
                    sendWorldUpdate();
                }
                // Block position update
                else if (command.startsWith("BLOCK:")) {
                    String[] parts = command.split(":");
                    if (parts.length == 4) {
                        String blockId = parts[1];
                        int x = Integer.parseInt(parts[2]);
                        int y = Integer.parseInt(parts[3]);
                        world.updateBlockPosition(blockId, x, y);
                        sendWorldUpdate();
                    }
                }
                // Button activation event
                else if (command.startsWith("BUTTON:")) {
                    String buttonId = command.substring(7);
                    world.activateButton(buttonId);
                    sendWorldUpdate();
                }
                // Player death notification
                else if (command.startsWith("DEATH:")) {
                    String user = command.substring("DEATH:".length());
                    serverDeaths.merge(user, 1, Integer::sum);
                    log.append("Recorded death for " + user + "\n");
                }
                // Medal award notification
                else if (command.startsWith("MEDAL:")) {
                    String username = command.substring(6);

                    serverMedals.merge(username, 1, Integer::sum);
                    log.append("Awarded medal to " + username + "\n");
                }
                // First player to reach flag in a level
                else if (command.startsWith("FLAG_FIRST:")) {
                    String[] parts = command.split(":");
                    if (parts.length == 2) {
                        String username = parts[1];
                        // Only record first player per level
                        if (!levelFirstFlags.containsKey(world.currentLevel)) {
                            levelFirstFlags.put(world.currentLevel, username);
                            log.append(username + " first to reach flag in level "
                                    + world.currentLevel + "\n");
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.append("Error: " + e.getMessage() + "\n");
        } catch (NumberFormatException e) {
            log.append("Invalid number format in command\n");
        } catch (Exception e) {
            log.append("Unexpected error: " + e.getMessage() + "\n");
        }
    }

    // Builds and broadcasts the current full game state to all clients
    private void sendWorldUpdate() throws IOException {
        GameStateUpdate update = new GameStateUpdate(new HashMap<>(playerStates), world);
        sendToAllClients(update);
    }

    // Triggered if an error occurs while listening for clients
    @Override
    public void listeningException(Throwable exception) {
        running = false;
        status.setText("Exception occurred while listening");
        status.setForeground(Color.RED);
        log.append("Listening exception: " + exception.getMessage() + "\n");
        log.append("Press Listen to restart server\n");
    }
}