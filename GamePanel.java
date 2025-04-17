import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

public class GamePanel extends JPanel implements ActionListener {
    private Timer timer;
    private ArrayList<Platform> platforms;
    private ArrayList<Coin> coins;
    private Player player;
    private BufferedImage background;
    private Flag flag;
    private int currentLevel = 1;

    // Multiplayer
    private HashMap<String, PlayerUpdate> otherPlayers = new HashMap<>();
    private ChatClient client;
    private String username;
    private GameWorldState worldState = new GameWorldState(1);

    public GamePanel(String username) {
        this.username = username;
        setPreferredSize(new Dimension(1000, 600));
        setBackground(Color.white);

        player = new Player(username, 100, 500);
        loadLevel(currentLevel);

        try {
        	// Right now this is used to determine the ip address so other computers could join change this to make it work on your own network I think
        	client = new ChatClient("192.168.1.251", 8300, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        timer = new Timer(16, this); // ~60 FPS
        timer.start();

        addKeyListener(player);
        setFocusable(true);
    }

    private void loadLevel(int levelNum) {
        Level lvl = Levels.getLevel(levelNum);
        if (lvl == null) {
            System.out.println("All levels complete. Restarting...");
            currentLevel = 1;
            lvl = Levels.getLevel(currentLevel);
        }

        platforms = lvl.getPlatforms();
        coins = lvl.getCoins();
        flag = lvl.getFlag();
        background = lvl.getBackground();
        player.setPosition(100, 500);
    }

    public void updateOtherPlayers(HashMap<String, PlayerUpdate> players, GameWorldState state) {
        this.otherPlayers = players;

        // Reload level if changed
        if (this.worldState.currentLevel != state.currentLevel) {
            this.currentLevel = state.currentLevel;
            loadLevel(currentLevel);
        }

        this.worldState = state;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        }

        for (Platform p : platforms) {
            p.draw(g);
        }

        for (Coin c : coins) {
            if (!worldState.collectedCoinIds.contains(c.getId())) {
                c.draw(g);
            }
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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        player.positionChange(platforms, coins);

        for (Coin c : coins) {
            if (!worldState.collectedCoinIds.contains(c.getId())) {
                c.update();
                if (c.isCoinCollected(player.getX(), player.getY(), 30, 30)) {
                    client.sendCoinCollected(c.getId());
                    player.addCoin();
                }
            }
        }

        flag.update();

        if (flag.isFlagReached(player.getX(), player.getY(), 30, 30)) {
            client.sendFlagReached();
        }

        if (client != null) {
            client.sendPlayerUpdate(username, player.getX(), player.getY(), player.getCoinCount());
        }

        repaint();
    }
}
