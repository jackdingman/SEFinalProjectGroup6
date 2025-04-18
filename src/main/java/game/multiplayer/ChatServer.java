package game.multiplayer;

import java.awt.*;
import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;

import main.java.login.util.LoginData;
import game.database.Database;
import game.entity.PlayerUpdate;
import game.world.GameStateUpdate;
import game.world.GameWorldState;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class ChatServer extends AbstractServer {
    private JTextArea log;
    private JLabel status;
    private boolean running = false;
    private Database database;

    private final HashMap<String, PlayerUpdate> playerStates = new HashMap<>();
    private GameWorldState world = new GameWorldState(1);

    public ChatServer() {
        super(8300);  // default port
        this.setTimeout(500);
    }

    public boolean isRunning() {
        return running;
    }

    public void setLog(JTextArea log) {
        this.log = log;
    }

    public void setStatus(JLabel status) {
        this.status = status;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    @Override
    public void serverStarted() {
        running = true;
        status.setText("Listening");
        status.setForeground(Color.GREEN);
        log.append("Server started\n");
    }

    @Override
    public void serverStopped() {
        status.setText("Stopped");
        status.setForeground(Color.RED);
        log.append("Server stopped accepting new clients - press Listen to start accepting new clients\n");
    }

    @Override
    public void serverClosed() {
        running = false;
        status.setText("Close");
        status.setForeground(Color.RED);
        log.append("Server and all current clients are closed - press Listen to restart\n");
    }

    @Override
    public void clientConnected(ConnectionToClient client) {
        log.append("Client " + client.getId() + " connected\n");
    }

    @Override
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        try {
            if (msg instanceof LoginData) {
                LoginData data = (LoginData) msg;
                Object result;
                if (database.verifyAccount(data.getUsername(), data.getPassword())) {
                    result = "LoginSuccessful";
                    log.append("Client " + client.getId() + " logged in as " + data.getUsername() + "\n");
                } else {
                    result = new Error("The username and password are incorrect.");
                    log.append("Client " + client.getId() + " failed to log in\n");
                }
                client.sendToClient(result);
            }

            // main.java.game.entity.Player position + coin count update
            else if (msg instanceof PlayerUpdate) {
                PlayerUpdate update = (PlayerUpdate) msg;
                playerStates.put(update.username, update);
                sendWorldUpdate();
            }

            // Handle coin collection and level transition via string commands
            else if (msg instanceof String) {
                String command = (String) msg;

                if (command.startsWith("COLLECT:")) {
                    String coinId = command.substring(8);
                    world.collectCoin(coinId);
                    log.append("main.java.game.entity.Coin collected: " + coinId + "\n");
                    sendWorldUpdate();
                }

                else if (command.equals("FLAG_REACHED")) {
                    world.advanceLevel();
                    log.append("main.java.game.entity.Flag reached. Advancing to level " + world.currentLevel + "\n");
                    sendWorldUpdate();
                }
            }

        } catch (IOException e) {
            log.append("Error: " + e.getMessage() + "\n");
        }
    }

    // Helper to broadcast latest state to all clients
    private void sendWorldUpdate() throws IOException {
        GameStateUpdate update = new GameStateUpdate(new HashMap<>(playerStates), world);
		sendToAllClients(update);
    }

    @Override
    public void listeningException(Throwable exception) {
        running = false;
        status.setText("Exception occurred while listening");
        status.setForeground(Color.RED);
        log.append("Listening exception: " + exception.getMessage() + "\n");
        log.append("Press Listen to restart server\n");
    }
}
