import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Flag {
    private final int flagWidth = 40;
    private final int flagHeight = 60;
    private int x, y;
    private boolean isReached; // tracker for if the flag has been reached
    private BufferedImage[] flagFrames;

    private int flagFrameCounter = 0;
    private int delay = 3;
    private int currentPic = 0;

    public Flag(int x, int y) {
        this.x = x;
        this.y = y;

        flagFrames = new BufferedImage[3];

        for (int i = 0; i < flagFrames.length; i++) {
            try {
                flagFrames[i] = ImageIO.read(getClass().getResource("/textureAssets/flag" + (i + 1) + ".png"));
            } catch (IOException e) {
                System.err.println("Failed to load flag texture");
                e.printStackTrace();
            }
        }
    }

    public void draw(Graphics g) {
        if (!isReached && flagFrames[currentPic] != null) {
            g.drawImage(flagFrames[currentPic], x, y, flagWidth, flagHeight, null);
        }
    }

    public void update() {
        if (!isReached) {
            flagFrameCounter++;
            if (flagFrameCounter >= delay) {
                flagFrameCounter = 0;
                currentPic = (currentPic + 1) % flagFrames.length;
            }
        }
    }

    public boolean isFlagReached(int playerX, int playerY, int playerWidth, int playerHeight) {
        Rectangle flagEntity = new Rectangle(x, y, flagWidth, flagHeight);
        Rectangle playerEntity = new Rectangle(playerX, playerY, playerWidth, playerHeight);

        if (!isReached && playerEntity.intersects(flagEntity)) {
            isReached = true;
            return true;
        }
        return false;
    }

    public boolean isReached() {
        return isReached;
    }
}
