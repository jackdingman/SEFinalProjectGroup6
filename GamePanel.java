import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GamePanel extends JPanel implements ActionListener {
    private Timer timer; // Game loop timer
    private ArrayList<Platform> platforms; // Platforms in the level
    private Player player;

    public GamePanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.white);

        // Create the player
        player = new Player("Player1", 100, 500);

        // Create platforms
        platforms = new ArrayList<>();
        platforms.add(new Platform(50, 550, 200, 20)); // Ground
        platforms.add(new Platform(300, 450, 150, 20)); // Floating platform
        platforms.add(new Platform(600, 350, 100, 20)); // Another platform

        // Start game loop
        timer = new Timer(16, this); // Approx. 60 FPS
        timer.start();

        // Listen for keyboard input
        addKeyListener(player);
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw player
        player.draw(g);

        // Draw platforms
        g.setColor(Color.black);
        for (Platform p : platforms) {
            p.draw(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        player.positionChange(platforms); // Update player movement with collision
        repaint(); // Redraw the scene
    }
}
