package game.entity;

import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Coin {
    // Diameter of the coin in pixels
    private final int coinSize = 25;

    // X and Y position of the coin
    private int x, y;

    // Tracks whether the coin has been collected
    private boolean isCollected;

    // Array of animation frames for the spinning coin
    private BufferedImage[] coinAngles;

    // Counter to manage frame delay
    private int coinAngleCounter = 0;

    // Number of updates to wait before advancing frame
    private int delay = 3;

    // Index of the current frame in coinAngles
    private int currentPic = 0;

    // Unique identifier for multiplayer synchronization
    private String id;

    // Constructor to set position, ID, and load animation frames
    public Coin(int x, int y, String id) {
        this.x = x;
        this.y = y;
        this.id = id;

        coinAngles = new BufferedImage[3]; // Three-frame coin animation

        // Load each coin frame image
        for (int i = 0; i < coinAngles.length; i++) {
            try {
                coinAngles[i] = ImageIO.read(getClass().getResource("/textureAssets/coinAngle" + (i + 1) + ".png"));
            } catch (IOException e) {
                System.err.println("Failed to load platform texture");
                e.printStackTrace();
            }
        }
    }

    // Draws the current coin frame if not yet collected
    public void draw(Graphics g) {
        if (!isCollected && coinAngles[currentPic] != null) {
            g.drawImage(coinAngles[currentPic], x, y, coinSize, coinSize, null);
        }
    }

    // Advances the coin animation frame counter if not collected
    public void update() {
        if (!isCollected) {
            coinAngleCounter++;
            if (coinAngleCounter >= delay) {
                coinAngleCounter = 0;
                currentPic = (currentPic + 1) % coinAngles.length;
            }
        }
    }

    // Checks collision with player and marks coin as collected
    public boolean isCoinCollected(int playerX, int playerY, int playerWidth, int playerHeight) {
        Rectangle coinEntity = new Rectangle(x, y, coinSize, coinSize);
        Rectangle playerEntity = new Rectangle(playerX, playerY, playerWidth, playerHeight);

        if (!isCollected && playerEntity.intersects(coinEntity)) {
            isCollected = true;
            return true;
        }
        return false;
    }

    // Returns whether the coin has been collected
    public boolean collect() {
        return isCollected;
    }

    // Returns the unique ID of the coin
    public String getId() {
        return id;
    }

    // Returns the X position of the coin
    public int getX() {
        return x;
    }

    // Returns the Y position of the coin
    public int getY() {
        return y;
    }
}