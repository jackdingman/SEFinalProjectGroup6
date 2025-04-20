import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

public class GamePanel extends JPanel implements ActionListener {
    // Game objects and variables
    private ArrayList<Platform> platforms;
    private ArrayList<Coin> coins;
    private ArrayList<Button> buttons;
    private ArrayList<ToggleWall> toggleWalls;
    private ArrayList<PushableBlock> pushableBlocks;
    private final Player player;
    private BufferedImage background;
    private Flag flag;
    private int currentLevel = 1;
    private long levelStartTime;

    //Tracks all players stats
    private final PlayerStats stats = new PlayerStats();

    //Changes Timer Logic (passable)
    private Timer timer;


    // Multiplayer objects and variables
    private HashMap<String, PlayerUpdate> otherPlayers = new HashMap<>();
    private final ChatClient client;
    private final String username;
    private GameWorldState worldState = new GameWorldState(1);

    // pause state flag
    private boolean paused = false;

    // bounds of the "Exit" button
    private final Rectangle exitButtonBounds = new Rectangle();


    // Public setter so ChatClient can toggle pause
    public void setPaused(boolean paused) {
        this.paused = paused;
        repaint();
    }

    // Constructor
    public GamePanel(String username) {
        this.username = username;
        setPreferredSize(new Dimension(1000, 600));
        setBackground(Color.white);

        // Initialize player + level
        player = new Player(username, 100, 500);
        loadLevel(currentLevel);

        // Initialize the network client
        ChatClient tmp = null;
        try {
            tmp = new ChatClient("192.168.0.223", 8300, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        client = tmp;

        // start game loop
        this.timer = new Timer(16, this);
        this.timer.start();


        // Enable key and mouse inputs
        setFocusable(true);
        addKeyListener(player);
        setupPauseKeyBinding();

        // Handles clicks on "Exit" button
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (paused && exitButtonBounds.contains(e.getPoint())) {
                    System.exit(0);
                }
            }
        });
    }

    // Sets up the ESC key to toggle pause
    private void setupPauseKeyBinding() {
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "togglePause");
        am.put("togglePause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paused = !paused;
                repaint();  // Updates the screen
                if (client != null) client.sendPause(paused); // Informs the server
            }
        });
    }

    // Loads the platforms, coins, flags, background for the given level
    private void loadLevel(int levelNum) {
        Level lvl = Levels.getLevel(levelNum);
        if (lvl == null) {
            currentLevel = 1;
            lvl = Levels.getLevel(currentLevel);
        }
        assert lvl != null;
        platforms  = lvl.getPlatforms();
        coins      = lvl.getCoins();
        buttons = lvl.getButtons(); // if not already there
        toggleWalls = lvl.getToggleWalls(); // if not already there
        pushableBlocks = lvl.getBlocks();
        flag       = lvl.getFlag();
        background = lvl.getBackground();
        player.setPosition(100, 500);

        // Initializes timer in the top right
        levelStartTime = System.currentTimeMillis();

    }

    // Called by the ChatClient to sync multiplayer state
    public void updateOtherPlayers(HashMap<String, PlayerUpdate> players, GameWorldState state) {
        this.otherPlayers = players;
        if (this.worldState.currentLevel != state.currentLevel) {
            this.currentLevel = state.currentLevel;

            stats.awardMedals();

            for (PushableBlock block : pushableBlocks) {
                int[] pos = worldState.getBlockPosition(block.getId());
                if (pos != null && !(pos[0] == 0 && pos[1] == 0)) {
                    block.setPosition(pos[0], pos[1]);
                }
            }

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

            //Displays Game Over Panel when final level is reached
            if (currentLevel > 3) { // Game Over after final level
                if (timer != null) timer.stop(); // stops game updates
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
                frame.setContentPane(new GameOverScreen(stats, username));
                frame.revalidate();
                return;
            }

            loadLevel(currentLevel);
        }
        this.worldState = state;
    }

    // This is called ever frame by the Timer
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!paused) {
            // normal player position
            player.positionChange(platforms, coins);

            if (player.getY() > 1200) {
                stats.recordDeath(username); // records player's falls as deaths
                player.setPosition(100, 500);
            }

            // Merge platforms + visible toggle walls
            ArrayList<Platform> allPlatforms = new ArrayList<>(platforms);
            for (ToggleWall wall : toggleWalls) {
                if (wall.isVisible()) {
                    allPlatforms.add(new Platform(wall.getX(), wall.getY(), wall.getWidth(), wall.getHeight()));
                }
            }

            player.positionChange(allPlatforms, coins);

            // Update and check coin collisions
            for (Coin c : coins) {
                if (!worldState.collectedCoinIds.contains(c.getId())) {
                    c.update();
                    if (c.isCoinCollected(player.getX(), player.getY(), 30, 30)) {
                        if (client != null) client.sendCoinCollected(c.getId());
                        player.addCoin();

                        // Adds coin collected to tracked stats
                        stats.coinCollected(username);

                    }
                }
            }

            for (Button b : buttons) {
                boolean wasActive = b.isActivated();

                // Build a full list of all players (including local + remote)
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

            for (PushableBlock block : pushableBlocks) {
                int prevX = block.getBounds().x;
                int prevY = block.getBounds().y;

                block.update(platforms, player);

                if (client != null && (block.getBounds().x != prevX || block.getBounds().y != prevY)) {
                    client.sendBlockPosition(block.getId(), block.getBounds().x, block.getBounds().y);
                }
            }

            // Check for reaching flag
            flag.update();
            if (flag.isFlagReached(player.getX(), player.getY(), 30, 30) && client != null) {
                client.sendFlagReached();

                // Adds fastest to flag to tracked stats
                stats.flagReached(username);

            }

            // Send player update to server
            if (client != null) {
                client.sendPlayerUpdate(username, player.getX(), player.getY(), player.getCoinCount());
            }
        }
        // always repaint (so overlay is responsive)
        repaint();
    }

    // Draw game and overlays
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        }

        // Draw platforms and coins
        for (Platform p : platforms) p.draw(g);
        for (ToggleWall wall : toggleWalls) wall.draw(g);
        for (Button b : buttons) b.draw(g);

        for (Coin c : coins) {
            if (!worldState.collectedCoinIds.contains(c.getId())) {
                c.draw(g);
            }
        }

        ArrayList<PlayerUpdate> allPlayers = new ArrayList<>(otherPlayers.values());
        allPlayers.add(new PlayerUpdate(username, player.getX(), player.getY(), player.getCoinCount()));

        for (Button b : buttons) {
            b.update(allPlayers, pushableBlocks);
        }

        for (PushableBlock block : pushableBlocks) {
            block.update(platforms, player);
            block.draw(g);
        }

        // Draw flag and players
        flag.draw(g);
        player.draw(g);

        // Draw other players for multiplayer
        for (PlayerUpdate p : otherPlayers.values()) {
            if (!p.username.equals(username)) {
                g.setColor(Color.BLUE);
                g.fillRect(p.x, p.y, 30, 30);
                g.setColor(Color.BLACK);
                g.drawString(p.username, p.x, p.y - 5);
            }
        }

        // Draw coin count
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Coins: " + player.getCoinCount(), 20, 30);

        // Draws a timer on the Top Right
        long elapsed = (System.currentTimeMillis() - levelStartTime) / 1000;
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Time: " + elapsed + "s", getWidth() - 100, 30);


        // Draw pause overlay & Exit button
        if (paused) {
            Graphics2D g2 = (Graphics2D) g.create();
            // translucent background
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, getWidth(), getHeight());

            // "PAUSED" text
            g2.setFont(new Font("Arial", Font.BOLD, 48));
            String text = "PAUSED";
            FontMetrics fm = g2.getFontMetrics();
            int tx = (getWidth() - fm.stringWidth(text)) / 2;
            int ty = getHeight() / 2;
            g2.setColor(Color.WHITE);
            g2.drawString(text, tx, ty);

            // Exit button
            int btnW = 200, btnH = 50;
            int btnX = (getWidth() - btnW) / 2;
            int btnY = ty + 40;
            exitButtonBounds.setBounds(btnX, btnY, btnW, btnH);

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

    public ChatClient getClient() {
        return client;
    }

}