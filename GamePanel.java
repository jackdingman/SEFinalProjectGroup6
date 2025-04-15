import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class GamePanel extends JPanel implements ActionListener {
    private Timer timer; // Game loop timer
    private ArrayList<Platform> platforms; // Platforms in the level
    private ArrayList<Coin> coins; //list of the available coins on the level
    private Player player;
    private BufferedImage background;


    public GamePanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.white);

        // Create the player
        player = new Player("Player1", 100, 500);

        // Create platforms
        platforms = new ArrayList<>();

        platforms.add(new Platform(50, 550, 200, 20)); // Ground
        platforms.add(new Platform(300, 500, 150, 20)); // Floating platform
        platforms.add(new Platform(600, 350, 100, 20)); // Another platform

        coins = new ArrayList<>();

        // Add some coins to the level
        coins.add(new Coin(200, 520));
        coins.add(new Coin(350, 420));
        coins.add(new Coin(620, 320));


        // Start game loop
        timer = new Timer(16, this); // Approx. 60 FPS
        timer.start();



        // Listen for keyboard input
        addKeyListener(player);
        setFocusable(true);

        try {
            background = ImageIO.read(getClass().getResource("/textureAssets/lvl1background.jpg")); // or backgroundX.jpg
        } catch (IOException e) {
            System.err.println("Fail");
            e.printStackTrace();
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw background
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        }

        // Draw platforms
        for (Platform p : platforms) {
            p.draw(g);
        }
        for (Coin c : coins) {
            c.draw(g);
        }

        // Draw player
        player.draw(g);

        g.setColor(Color.BLACK); // Text color
        g.setFont(new Font("Arial", Font.BOLD, 18)); // Font style and size
        g.drawString("Coins: " + player.getCoinCount(), 20, 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        player.positionChange(platforms, coins); // Update player movement with collision

        // Update and check coins
        for (Coin c : coins) {
            c.update(); // coin animation
            if (c.isCoinCollected(player.getX(), player.getY(), 30, 30)) {
                player.addCoin(); //adds a coin to the player's count
            }
        }
        repaint(); // Redraw the scene
    }
}
