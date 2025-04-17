import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static java.lang.Thread.sleep;

public class Coin {
    private final int coinSize = 25; //pixel diameter of the coin
    private int x, y;
    private boolean isCollected; //tracker for if the coin has been collected.
    private BufferedImage coinTexture; // jpg for what a coin looks like. May implement turning animation
    private BufferedImage[] coinAngles;

    private int coinAngleCounter = 0;
    private int delay = 3;
    private int currentPic = 0;

    // ✅ Added ID for multiplayer sync
    private String id;

    // ✅ Modified constructor to accept ID
    public Coin(int x, int y, String id) {
        this.x = x;
        this.y = y;
        this.id = id;

        coinAngles = new BufferedImage[3];

        for (int i = 0; i < coinAngles.length; i++) {
            try {
                coinAngles[i] = ImageIO.read(getClass().getResource("/textureAssets/coinAngle"+(i+1)+".png")); // adjust path as needed
                //coinTexture = ImageIO.read(getClass().getResource("/textureAssets/coinAngle1.png")); // adjust path as needed
            } catch (IOException e) {
                System.err.println("Failed to load platform texture");
                e.printStackTrace();
            }
        }
    }

    public void draw(Graphics g){
        if (!isCollected && coinAngles[currentPic] != null) {
            g.drawImage(coinAngles[currentPic], x, y, coinSize, coinSize, null);
        }
    }

    public void update() {
        if (!isCollected) {
            coinAngleCounter++;
            if (coinAngleCounter >= delay) {
                coinAngleCounter = 0;
                currentPic = (currentPic + 1) % coinAngles.length;
            }
        }
    }

    public boolean isCoinCollected(int playerX, int playerY, int playerWidth, int playerHeight) {
        Rectangle coinEntity = new Rectangle(x, y, coinSize, coinSize); // size and position on window
        Rectangle playerEntity = new Rectangle(playerX, playerY, playerWidth, playerHeight); // size and position on window

        if(!isCollected && playerEntity.intersects(coinEntity)) { // collision checker
            isCollected = true;
            return true;
        }
        return false; // return false if no collision
    }

    public boolean collect(){
        return isCollected;
    }

    // ✅ New methods for multiplayer tracking

    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
