package game.entity;

import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

// Represents a static platform in the game world
public class Platform {

    // X, Y Coordinate of the platform
    private int x, y;

    // Width and Height of the platform
    private int width, height;

    // Text image for the platform
    private BufferedImage texture;

    // Constructor
    public Platform(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        try {
            texture = ImageIO.read(getClass().getResource("/textureAssets/generatedGrassPlatform.png")); // adjust path as needed
        } catch (IOException e) {
            System.err.println("Failed to load platform texture");
            e.printStackTrace();
        }
    }

    // Draws the platform if texture is available
    public void draw(Graphics g) {
        if (texture != null) {
            g.drawImage(texture, x, y, width, height, null);
        } else {
            g.setColor(Color.black);
            g.fillRect(x, y, width, height);
        }
    }

    // return the platform’s x‑coordinate
    public int getX() {
        return x;
    }

    // return the platform’s y‑coordinate
    public int getY() {
        return y;
    }

    // return the platform’s width
    public int getWidth() {
        return width;
    }

    // return the platform’s height
    public int getHeight() {
        return height;
    }
}

