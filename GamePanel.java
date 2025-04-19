import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

public class GamePanel extends JPanel implements ActionListener {
    private ArrayList<Platform> platforms;
    private ArrayList<Coin> coins;
    private ArrayList<Button> buttons;
    private ArrayList<ToggleWall> toggleWalls;
    private ArrayList<PushableBlock> pushableBlocks;
    private final Player player;
    private BufferedImage background;
    private Flag flag;
    private int currentLevel = 1;

    private HashMap<String, PlayerUpdate> otherPlayers = new HashMap<>();
    private final ChatClient client;
    private final String username;
    private GameWorldState worldState = new GameWorldState(1);

    private boolean paused = false;
    private final Rectangle exitButtonBounds = new Rectangle();

    public void setPaused(boolean paused) {
        this.paused = paused;
        repaint();
    }

    public GamePanel(String username) {
        this.username = username;
        setPreferredSize(new Dimension(1000, 600));
        setBackground(Color.white);

        player = new Player(username, 100, 500);
        loadLevel(currentLevel);

        ChatClient tmp = null;
        try {
            tmp = new ChatClient("192.168.1.251", 8300, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        client = tmp;

        Timer timer = new Timer(16, this);
        timer.start();

        setFocusable(true);
        addKeyListener(player);
        setupPauseKeyBinding();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (paused && exitButtonBounds.contains(e.getPoint())) {
                    System.exit(0);
                }
            }
        });
    }

    private void setupPauseKeyBinding() {
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "togglePause");
        am.put("togglePause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paused = !paused;
                repaint();
                if (client != null) client.sendPause(paused);
            }
        });
    }

    private void loadLevel(int levelNum) {
        Level lvl = Levels.getLevel(levelNum);
        if (lvl == null) {
            currentLevel = 1;
            lvl = Levels.getLevel(currentLevel);
        }

        platforms = lvl.getPlatforms();
        coins = lvl.getCoins();
        flag = lvl.getFlag();
        background = lvl.getBackground();
        buttons = lvl.getButtons(); // if not already there
        toggleWalls = lvl.getToggleWalls(); // if not already there
        pushableBlocks = lvl.getBlocks(); 
        player.setPosition(100, 500);
    }


    public void updateOtherPlayers(HashMap<String, PlayerUpdate> players, GameWorldState state) {
        this.otherPlayers = players;
        if (this.worldState.currentLevel != state.currentLevel) {
            this.currentLevel = state.currentLevel;
            loadLevel(currentLevel);
        }
        this.worldState = state;
     // Update pushable block positions from server state
  
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





    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!paused) {
        	// Merge platforms + visible toggle walls
        	ArrayList<Platform> allPlatforms = new ArrayList<>(platforms);
        	for (ToggleWall wall : toggleWalls) {
        	    if (wall.isVisible()) {
        	        allPlatforms.add(new Platform(wall.getX(), wall.getY(), wall.getWidth(), wall.getHeight()));
        	    }
        	}

        	player.positionChange(allPlatforms, coins);

     

            for (Coin c : coins) {
                if (!worldState.collectedCoinIds.contains(c.getId())) {
                    c.update();
                    if (c.isCoinCollected(player.getX(), player.getY(), 30, 30)) {
                        if (client != null) client.sendCoinCollected(c.getId());
                        player.addCoin();
                    }
                }
            }

            flag.update();
            if (flag.isFlagReached(player.getX(), player.getY(), 30, 30) && client != null) {
                client.sendFlagReached();
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




            if (client != null) {
                client.sendPlayerUpdate(username, player.getX(), player.getY(), player.getCoinCount());
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        }

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

        flag.draw(g);
        player.draw(g);

        for (PlayerUpdate p : otherPlayers.values()) {
            if (!p.username.equals(username)) {
                g.setColor(Color.BLUE);
                g.fillRect(p.x, p.y, 30, 30);
                g.setColor(Color.BLACK);
                g.drawString(p.username, p.x, p.y - 5);
            }
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Coins: " + player.getCoinCount(), 20, 30);

        if (paused) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setFont(new Font("Arial", Font.BOLD, 48));
            String text = "PAUSED";
            FontMetrics fm = g2.getFontMetrics();
            int tx = (getWidth() - fm.stringWidth(text)) / 2;
            int ty = getHeight() / 2;
            g2.setColor(Color.WHITE);
            g2.drawString(text, tx, ty);

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
