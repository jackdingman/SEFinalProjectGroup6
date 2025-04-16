import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Levels {

    public static Level getLevel(int levelNum) {
        ArrayList<Platform> platforms = new ArrayList<>();
        ArrayList<Coin> coins = new ArrayList<>();
        Flag flag = null;
        BufferedImage background = null;

        if (levelNum == 1) {
            platforms.add(new Platform(50, 550, 200, 20));
            platforms.add(new Platform(300, 500, 150, 20));
            platforms.add(new Platform(600, 350, 100, 20));

            coins.add(new Coin(200, 520));
            coins.add(new Coin(350, 420));
            coins.add(new Coin(620, 320));

            flag = new Flag(680, 280);

            try {
                background = ImageIO.read(Levels.class.getResource("/textureAssets/lvl1background.jpg"));
            } catch (IOException e) {
                System.err.println("Failed to load background for level 1");
                e.printStackTrace();
            }

        } else if (levelNum == 2) {
            platforms.add(new Platform(50, 550, 300, 20));
            platforms.add(new Platform(400, 450, 200, 20));
            platforms.add(new Platform(700, 350, 100, 20));

            coins.add(new Coin(100, 520));
            coins.add(new Coin(450, 420));
            coins.add(new Coin(720, 320));

            flag = new Flag(750, 290);

            try {
                background = ImageIO.read(Levels.class.getResource("/textureAssets/lvl1background.jpg"));
            } catch (IOException e) {
                System.err.println("Failed to load background for level 2");
                e.printStackTrace();
            }
        }

        else if (levelNum == 3) {
            platforms.add(new Platform(50, 550, 300, 20));
            platforms.add(new Platform(400, 450, 200, 20));
            platforms.add(new Platform(700, 350, 300, 20));

            coins.add(new Coin(100, 520));
            coins.add(new Coin(450, 420));
            coins.add(new Coin(720, 320));

            flag = new Flag(750, 290);

            try {
                background = ImageIO.read(Levels.class.getResource("/textureAssets/lvl1background.jpg"));
            } catch (IOException e) {
                System.err.println("Failed to load background for level 2");
                e.printStackTrace();
            }
        }

        if (flag != null && background != null) {
            return new Level(platforms, coins, flag, background);
        } else {
            return null;
        }
    }
}
