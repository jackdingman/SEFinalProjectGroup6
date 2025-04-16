import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GamePanel extends JPanel implements ActionListener {
    private Timer timer;
    private ArrayList<Platform> platforms;
    private ArrayList<Coin> coins;
    private Player player;
    private BufferedImage background;
    private Flag flag;
    private int currentLevel = 1;

    public GamePanel() {
        setPreferredSize(new Dimension(1000, 600));
        setBackground(Color.white);

        player = new Player("Player1", 100, 500);
        loadLevel(currentLevel);

        timer = new Timer(16, this);
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
            c.draw(g);
        }

        flag.draw(g);
        player.draw(g);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Coins: " + player.getCoinCount(), 20, 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        player.positionChange(platforms, coins);

        for (Coin c : coins) {
            c.update();
            if (c.isCoinCollected(player.getX(), player.getY(), 30, 30)) {
                player.addCoin();
            }
        }

        flag.update();

        if (flag.isFlagReached(player.getX(), player.getY(), 30, 30)) {
            currentLevel++;
            loadLevel(currentLevel);
        }

        repaint();
    }
}
