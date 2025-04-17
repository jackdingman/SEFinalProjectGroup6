import java.io.IOException;

import ocsf.client.AbstractClient;

public class ChatClient extends AbstractClient {
    private GamePanel GamePanel;

    public ChatClient(String host, int port, GamePanel panel) throws Exception {
        super(host, port);
        this.GamePanel = panel;
        openConnection();
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        if (msg instanceof GameStateUpdate) {
            GameStateUpdate update = (GameStateUpdate) msg;
            GamePanel.updateOtherPlayers(update.players, update.worldState);
        }
    }

    public void sendPlayerUpdate(String username, int x, int y, int coinCount) {
        try {
            PlayerUpdate update = new PlayerUpdate(username, x, y, coinCount);
            sendToServer(update);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendCoinCollected(String coinId) {
        try {
            sendToServer("COLLECT:" + coinId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFlagReached() {
        try {
            sendToServer("FLAG_REACHED");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }

    


