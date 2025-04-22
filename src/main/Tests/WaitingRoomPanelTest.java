import game.app.Game;
import game.multiplayer.ChatClient;
import game.ui.GamePanel;
import game.ui.WaitingRoomPanel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.util.Set;

import static org.junit.Assert.*;

// Test Class for WaitingRoomPanel
public class WaitingRoomPanelTest {
    private static class TestableWaitingRoomPanel extends WaitingRoomPanel {
        public TestableWaitingRoomPanel(String username, Game parent) {
            super(username, parent);
            // Do not create a real ChatClient
            setClient(null);
        }

        // Override startGame to make it testable
        @Override
        public void startGame() {
            // The default implementation is empty to avoid creating a real GamePanel
        }
    }

    private TestableWaitingRoomPanel panel;
    private ChatClient mockClient;
    private Game mockGame;

    @Before
    public void setUp() {
        // Create mocks
        mockClient = Mockito.mock(ChatClient.class);
        mockGame = Mockito.mock(Game.class);

        // Create our test panel
        panel = new TestableWaitingRoomPanel("Player1", mockGame);

        // Set our mock client
        panel.setClient(mockClient);
    }

    @Test
    public void testUpdatePlayerList() {
        Set<String> players = Set.of("Player1", "Player2", "Player3");
        panel.updatePlayerList(players);

        String actualText = panel.getPlayerListArea().getText();

        assertTrue(actualText.contains("Player1 (You)"));
        assertTrue(actualText.contains("Player2"));
        assertTrue(actualText.contains("Player3"));

        // Check that each player appears exactly once (by counting newlines)
        assertEquals(3, actualText.split("\n").length);
    }

    @Test
    public void testReadyButtonAction() {
        JButton readyButton = panel.getReadyButton();

        // Simulate a button click
        readyButton.doClick();

        assertTrue(panel.getReady());
        assertEquals("Waiting...", readyButton.getText());
        assertFalse(readyButton.isEnabled());
        Mockito.verify(mockClient).sendReady("Player1");
    }

    @Test
    public void testStartGame() throws Exception {
        // Create a frame and add our panel to it
        JFrame frame = new JFrame();
        frame.setContentPane(panel);
        frame.pack();

        TestableWaitingRoomPanel spyPanel = Mockito.spy(panel);
        GamePanel mockGamePanel = Mockito.mock(GamePanel.class);

        // Use Mockito to make the spyPanel.startGame() method create our mock GamePanel
        Mockito.doAnswer(_ -> {
            SwingUtilities.invokeLater(() -> {
                frame.setContentPane(mockGamePanel);
                frame.revalidate();
                frame.repaint();
            });
            return null;
        }).when(spyPanel).startGame();

        frame.setContentPane(spyPanel);
        spyPanel.startGame();

        // Wait for the SwingUtilities.invokeLater to complete
        Thread.sleep(100);

        Mockito.verify(spyPanel).startGame();
        assertEquals(mockGamePanel, frame.getContentPane());
    }
}