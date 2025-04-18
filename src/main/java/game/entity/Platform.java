package game.entity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Platform {
    private int x, y, width, height;
    private BufferedImage texture;

    public Platform(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        try {
            texture = ImageIO.read(
                    getClass().getResource("/textureAssets/generatedGrassPlatform.png")
            );
        } catch (IOException e) {
            System.err.println("Failed to load platform texture: generatedGrassPlatform.png");
            e.printStackTrace();
        }
    }

    public void draw(Graphics g) {
        if (texture != null) {
            g.drawImage(texture, x, y, width, height, null);
        } else {
            g.setColor(Color.black);
            g.fillRect(x, y, width, height);
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}

