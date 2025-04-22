import game.multiplayer.ChatClient;
import game.multiplayer.ChatServer;
import game.entity.PlayerUpdate;
import game.world.GameStateUpdate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

// Integration test for the ChatClient and ChatServer components.
public class ChatClientServerIntegrationTest {
    private ChatServer server;
    private JTextArea mockLogArea;
    private JLabel mockStatusLabel;
    private CountDownLatch messageLatch;
    private Object receivedMessage;

    @Before
    public void setUp() throws Exception {
        mockLogArea = Mockito.mock(JTextArea.class);
        mockStatusLabel = Mockito.mock(JLabel.class);

        // Create and start the server
        server = new ChatServer();
        server.setLog(mockLogArea);
        server.setStatus(mockStatusLabel);
        server.listen();

        Thread.sleep(500);
        assertTrue("Server should be running", server.isRunning());

        messageLatch = new CountDownLatch(1);
        receivedMessage = null;
    }

    @After
    public void tearDown() throws Exception {
        // Stop the server
        if (server != null && server.isRunning()) {
            server.stopListening();
            server.close();
        }
    }

    // Custom WaitingRoomPanel captures messages from the server
    private class TestWaitingRoomPanel extends game.ui.WaitingRoomPanel {
        public TestWaitingRoomPanel() {
            super("TestUser", null);
        }

        @Override
        public void updatePlayerList(Set<String> players) {
            receivedMessage = players;
            messageLatch.countDown();
        }

        @Override
        public void startGame() {
            receivedMessage = "START_GAME";
            messageLatch.countDown();
        }
    }

    @Test
    public void testClientServerJoinAndReady() throws Exception {
        TestWaitingRoomPanel testPanel = new TestWaitingRoomPanel();
        ChatClient client = new ChatClient("localhost", 8300, null, testPanel);

        try {
            client.sendJoin("TestUser");
            boolean messageReceived = messageLatch.await(5, TimeUnit.SECONDS);

            assertTrue("Should receive a response from server", messageReceived);
            assertTrue("Response should be a Set", receivedMessage instanceof Set);
            Set<String> players = (Set<String>) receivedMessage;
            assertTrue("Player set should contain TestUser", players.contains("TestUser"));

            // Reset the latch for the next message
            messageLatch = new CountDownLatch(1);

            client.sendReady("TestUser");
            messageReceived = messageLatch.await(5, TimeUnit.SECONDS);

            assertTrue("Should receive a response after ready message", messageReceived);

            // The server might send either START_GAME or an updated player list
            if (receivedMessage instanceof String) {
                assertEquals("String response should be START_GAME", "START_GAME", receivedMessage);
            } else if (receivedMessage instanceof Set) {
                Set<String> updatedPlayers = (Set<String>) receivedMessage;
                assertTrue("Player set should contain TestUser", updatedPlayers.contains("TestUser"));
            } else {
                fail("Unexpected response type: " + receivedMessage.getClass().getName());
            }
        } finally {
            client.closeConnection();
        }
    }

    @Test
    public void testPauseAndResume() throws Exception {
        game.ui.GamePanel mockGamePanel = Mockito.mock(game.ui.GamePanel.class);

        // Create a client connected to the server
        ChatClient client = new ChatClient("localhost", 8300, mockGamePanel);

        try {
            client.sendPause(true);
            Thread.sleep(500);

            Mockito.verify(mockGamePanel).setPaused(true);
            Mockito.reset(mockGamePanel);

            client.sendPause(false);
            Thread.sleep(500);

            Mockito.verify(mockGamePanel).setPaused(false);

        } finally {
            client.closeConnection();
        }
    }
}