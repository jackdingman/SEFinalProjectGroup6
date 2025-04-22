package game.entity;

import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Flag {
    // Width of each flag frame in pixels
    private final int flagWidth = 40;

    // Height of each flag frame in pixels
    private final int flagHeight = 60;

    // X position of the flag
    private int x, y;

    // Tracks whether the flag has been reached by a player
    private boolean isReached;

    // Array of animation frames for the flag
    private BufferedImage[] flagFrames;

    // Counter to manage frame delay
    private int flagFrameCounter = 0;

    // Number of updates to wait before advancing frame
    private int delay = 3;

    // Index of the current frame in flagFrames
    private int currentPic = 0;

    // Constructor to set position and load animation frames
    public Flag(int x, int y) {
        this.x = x;
        this.y = y;

        flagFrames = new BufferedImage[3]; // Three-frame flag animation

        // Load each flag frame image
        for (int i = 0; i < flagFrames.length; i++) {
            try {
                flagFrames[i] = ImageIO.read(getClass().getResource("/textureAssets/flag" + (i + 1) + ".png"));
            } catch (IOException e) {
                System.err.println("Failed to load flag texture");
                e.printStackTrace();
            }
        }
    }

    // Draws the current flag frame if not yet reached
    public void draw(Graphics g) {
        if (!isReached && flagFrames[currentPic] != null) {
            g.drawImage(flagFrames[currentPic], x, y, flagWidth, flagHeight, null);
        }
    }

    // Advances the animation frame counter if not reached
    public void update() {
        if (!isReached) {
            flagFrameCounter++;
            if (flagFrameCounter >= delay) {
                flagFrameCounter = 0;
                currentPic = (currentPic + 1) % flagFrames.length;
            }
        }
    }

    // Checks collision with player and marks flag as reached
    public boolean isFlagReached(int playerX, int playerY, int playerWidth, int playerHeight) {
        Rectangle flagEntity = new Rectangle(x, y, flagWidth, flagHeight);
        Rectangle playerEntity = new Rectangle(playerX, playerY, playerWidth, playerHeight);

        if (!isReached && playerEntity.intersects(flagEntity)) {
            isReached = true;
            return true;
        }
        return false;
    }

    // Returns whether the flag has been reached
    public boolean isReached() {
        return isReached;
    }
}
