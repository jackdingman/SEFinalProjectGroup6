package game.ui;

import game.entity.*;
import game.multiplayer.ChatClient;
import game.util.PushableBlock;
import game.util.ToggleWall;
import game.world.GameWorldState;
import game.level.*;
import game.entity.Button;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.awt.image.BufferedImage;


public class GamePanel extends JPanel implements ActionListener {
    // List of static platforms in the level
    private ArrayList<Platform> platforms;

    // List of coins collectible in the level
    private ArrayList<Coin> coins;

    // List of interactive buttons in the level
    private ArrayList<Button> buttons;

    // List of walls that can toggle visibility
    private ArrayList<ToggleWall> toggleWalls;

    // List of pushable blocks in the level
    private ArrayList<PushableBlock> pushableBlocks;

    // The local player instance
    private final Player player;

    // Background image for the level
    private BufferedImage background;

    // The goal flag for the level
    private Flag flag;

    // Index of the current level
    private int currentLevel = 1;

    // Timestamp when the level started
    private long levelStartTime;

    // Tracks statistics for this player and others
    private final PlayerStats stats = new PlayerStats();

    // Timer driving the game loop at ~60fps
    private Timer timer;

    // Multiplayer state: other players’ latest updates
    private HashMap<String, PlayerUpdate> otherPlayers = new HashMap<>();

    // Network client used to communicate with server
    private final ChatClient client;

    // Username of the local player
    private final String username;

    // Shared world state synchronized with server
    private GameWorldState worldState = new GameWorldState(1);

    // Whether the game is currently paused
    private boolean paused = false;

    // Bounds of the on-screen Exit button when paused
    private final Rectangle exitButtonBounds = new Rectangle();

    // Setter used by network code to toggle pause locally
    public void setPaused(boolean paused) {
        this.paused = paused;
        repaint();
    }

    // Main constructor
    public GamePanel(String username) {
        this.username = username;
        setPreferredSize(new Dimension(1000, 600)); // set panel size
        setBackground(Color.white);                 // set background color

        // Create player and load first level
        player = new Player(username, 100, 500);
        loadLevel(currentLevel);

        // Initialize network client for multiplayer
        ChatClient tmp = null;
        try {
            tmp = new ChatClient("192.168.0.223", 8300, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        client = tmp;

        // Start the game loop timer (calls actionPerformed)
        this.timer = new Timer(16, this);
        this.timer.start();

        // Enable keyboard input for movement and pause
        setFocusable(true);
        addKeyListener(player);
        setupPauseKeyBinding();

        // Listen for clicks on the Exit button when paused
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (paused && exitButtonBounds.contains(e.getPoint())) {
                    System.exit(0); // exit game
                }
            }
        });
    }

    // Configures the ESC key to toggle pause state
    private void setupPauseKeyBinding() {
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "togglePause");
        am.put("togglePause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paused = !paused;   // flip pause flag
                repaint();          // redraw overlay
                if (client != null) client.sendPause(paused); // notify server
            }
        });
    }

    // Loads level data (platforms, coins, etc.) for the given level number
    private void loadLevel(int levelNum) {
        Level lvl = Levels.getLevel(levelNum);
        if (lvl == null) {
            currentLevel = 1;
            lvl = Levels.getLevel(currentLevel);
        }
        assert lvl != null;
        platforms      = lvl.getPlatforms();
        coins          = lvl.getCoins();
        buttons        = lvl.getButtons();
        toggleWalls    = lvl.getToggleWalls();
        pushableBlocks = lvl.getBlocks();
        flag           = lvl.getFlag();
        background     = lvl.getBackground();
        player.setPosition(100, 500);  // reset player position
        levelStartTime = System.currentTimeMillis(); // record start time
    }

    // Called by ChatClient to synchronize remote players and world state
    // Called by ChatClient to synchronize remote players and world state
    public void updateOtherPlayers(HashMap<String, PlayerUpdate> players, GameWorldState state) {
        this.otherPlayers = players;

        // Advance level if remote state differs
        if (this.worldState.currentLevel != state.currentLevel) {
            this.currentLevel = state.currentLevel;
            stats.awardMedals(); // award medals for completed level
            // If final level passed, show Game Over screen
            if (currentLevel > 3) {
                if (timer != null) timer.stop();
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
                frame.setContentPane(new GameOverScreen(stats, username));
                frame.revalidate();
                return;

            }
            loadLevel(currentLevel); // load the new level
        }
        this.worldState = state; // store latest world state

        // Update pushable block positions from last state
        for (PushableBlock block : pushableBlocks) {
            int[] pos = worldState.getBlockPosition(block.getId());
            if (pos != null && !(pos[0] == 0 && pos[1] == 0)) {
                block.setPosition(pos[0], pos[1]);
            }
        }

        // Sync button activation state based on all players
        for (Button b : buttons) {
            boolean wasActive = b.isActivated();
            ArrayList<PlayerUpdate> allPlayers = new ArrayList<>(otherPlayers.values());
            allPlayers.add(new PlayerUpdate(username, player.getX(), player.getY(), player.getCoinCount()));
            b.update(allPlayers, pushableBlocks);
            boolean nowActive = b.isActivated();
            if (!wasActive && nowActive) {
                worldState.activateButton(b.getId());
            } else if (wasActive && !nowActive) {
                worldState.deactivateButton(b.getId());
            }
        }

        this.worldState = state; // store latest world state
    }

    // Initialize game loop that updates physics, input, networking, and triggers repaint
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!paused) {
            // Update local player movement and death handling
            if (player.getY() > 1200) {
                stats.recordDeath(username);
                player.setPosition(100, 500);
            }

            // Build combined platform list including visible toggle walls
            ArrayList<Platform> collidables = new ArrayList<>(platforms);
            for (ToggleWall wall : toggleWalls) {
                if (wall.isVisible()) {
                    collidables.add(
                            new Platform(wall.getX(), wall.getY(), wall.getWidth(), wall.getHeight())
                    );
                }
            }

            // One unified collision pass
            player.positionChange(collidables, coins); // second collision pass

            // Handle coin collection and notify server
            for (Coin c : coins) {
                if (!worldState.collectedCoinIds.contains(c.getId())) {
                    c.update();
                    if (c.isCoinCollected(player.getX(), player.getY(), 30, 30)) {
                        if (client != null) client.sendCoinCollected(c.getId());
                        player.addCoin();
                        stats.coinCollected(username);
                    }
                }
            }

            // Update buttons and notify server of activation changes
            for (Button b : buttons) {
                boolean wasActive = b.isActivated();
                ArrayList<PlayerUpdate> allPlayers = new ArrayList<>(otherPlayers.values());
                allPlayers.add(new PlayerUpdate(username, player.getX(), player.getY(), player.getCoinCount()));
                b.update(allPlayers, pushableBlocks);
                boolean nowActive = b.isActivated();
                if (!wasActive && nowActive) {
                    if (client != null) client.sendButtonActivated(b.getId());
                    worldState.activateButton(b.getId());
                } else if (wasActive && !nowActive) {
                    worldState.deactivateButton(b.getId());
                }
            }

            // Update and sync pushable blocks
            for (PushableBlock block : pushableBlocks) {
                int prevX = block.getBounds().x;
                int prevY = block.getBounds().y;

                // use the same collidables list built above:
                block.update(collidables, player);
                if (client != null
                        && (block.getBounds().x != prevX || block.getBounds().y != prevY)) {
                    client.sendBlockPosition(
                            block.getId(),
                            block.getBounds().x,
                            block.getBounds().y
                    );
                }
            }

            // Check flag reaching and notify server
            flag.update();
            if (flag.isFlagReached(player.getX(), player.getY(), 30, 30) && client != null) {
                client.sendFlagReached();
                stats.flagReached(username);
            }

            // Send local player update to server
            if (client != null) {
                client.sendPlayerUpdate(username, player.getX(), player.getY(), player.getCoinCount());
            }
        }
        repaint(); // always redraw frame
    }

    // Renders the game world, players, UI overlays, and pause menu
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw level background
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        }

        // Draw static and toggle platforms
        for (Platform p : platforms) p.draw(g);
        for (ToggleWall wall : toggleWalls) wall.draw(g);
        for (Button b : buttons) b.draw(g);

        // Draw coins that have not been collected
        for (Coin c : coins) {
            if (!worldState.collectedCoinIds.contains(c.getId())) {
                c.draw(g);
            }
        }

        // Update button states for all players
        ArrayList<PlayerUpdate> allPlayers = new ArrayList<>(otherPlayers.values());
        allPlayers.add(new PlayerUpdate(username, player.getX(), player.getY(), player.getCoinCount()));
        for (Button b : buttons) {
            b.update(allPlayers, pushableBlocks);
        }

        // Draw pushable blocks
        for (PushableBlock block : pushableBlocks) {
            block.draw(g);
        }

        // Draw goal flag
        flag.draw(g);

        // Draw local player
        player.draw(g);

        // Draw remote players as blue squares with names
        for (PlayerUpdate p : otherPlayers.values()) {
            if (!p.username.equals(username)) {
                g.setColor(Color.BLUE);
                g.fillRect(p.x, p.y, 30, 30);
                g.setColor(Color.BLACK);
                g.drawString(p.username, p.x, p.y - 5);
            }
        }

        // Draw HUD: coin count and elapsed time
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Coins: " + player.getCoinCount(), 20, 30);
        long elapsed = (System.currentTimeMillis() - levelStartTime) / 1000;
        g.drawString("Time: " + elapsed + "s", getWidth() - 100, 30);

        // Draw translucent pause overlay and Exit button if paused
        if (paused) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(0, 0, 0, 150)); // semi-transparent black
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Draw "PAUSED" text at center
            g2.setFont(new Font("Arial", Font.BOLD, 48));
            String text = "PAUSED";
            FontMetrics fm = g2.getFontMetrics();
            int tx = (getWidth() - fm.stringWidth(text)) / 2;
            int ty = getHeight() / 2;
            g2.setColor(Color.WHITE);
            g2.drawString(text, tx, ty);

            // Calculate Exit button bounds
            int btnW = 200, btnH = 50;
            int btnX = (getWidth() - btnW) / 2;
            int btnY = ty + 40;
            exitButtonBounds.setBounds(btnX, btnY, btnW, btnH);

            // Draw Exit button background and label
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(btnX, btnY, btnW, btnH);
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            FontMetrics fmBtn = g2.getFontMetrics();
            String btnText = "Exit";
            int bx = btnX + (btnW - fmBtn.stringWidth(btnText)) / 2;
            int by = btnY + ((btnH - fmBtn.getHeight()) / 2) + fmBtn.getAscent();
            g2.setColor(Color.WHITE);
            g2.drawString(btnText, bx, by);

            g2.dispose();
        }
    }

    // Provides access to the network client
    public ChatClient getClient() {
        return client;
    }
}
