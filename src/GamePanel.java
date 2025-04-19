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
    private final Player player;
    private BufferedImage background;
    private Flag flag;
    private int currentLevel = 1;

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
        Timer timer = new Timer(16, this);
        timer.start();

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
        flag       = lvl.getFlag();
        background = lvl.getBackground();
        player.setPosition(100, 500);
    }

    // Called by the ChatClient to sync multiplayer state
    public void updateOtherPlayers(HashMap<String, PlayerUpdate> players, GameWorldState state) {
        this.otherPlayers = players;
        if (this.worldState.currentLevel != state.currentLevel) {
            this.currentLevel = state.currentLevel;
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

            // Update and check coin collisions
            for (Coin c : coins) {
                if (!worldState.collectedCoinIds.contains(c.getId())) {
                    c.update();
                    if (c.isCoinCollected(player.getX(), player.getY(), 30, 30)) {
                        if (client != null) client.sendCoinCollected(c.getId());
                        player.addCoin();
                    }
                }
            }
            // Check for reaching flag
            flag.update();
            if (flag.isFlagReached(player.getX(), player.getY(), 30, 30) && client != null) {
                client.sendFlagReached();
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
        for (Coin c : coins) {
            if (!worldState.collectedCoinIds.contains(c.getId())) {
                c.draw(g);
            }
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
}
