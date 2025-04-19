import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Levels {

    public static Level getLevel(int levelNum) {
        ArrayList<Platform> platforms = new ArrayList<>();
        ArrayList<Coin> coins = new ArrayList<>();
        ArrayList<Button> buttons = new ArrayList<>();
        ArrayList<ToggleWall> toggleWalls = new ArrayList<>();
        ArrayList<PushableBlock> blocks = new ArrayList<>();
        Flag flag = null;
        BufferedImage background = null;

        if (levelNum == 1) {
            platforms.add(new Platform(50, 550, 200, 20));
            platforms.add(new Platform(300, 500, 150, 20));
            platforms.add(new Platform(600, 350, 100, 20));

            coins.add(new Coin(200, 520, "lvl1_coin0"));
            coins.add(new Coin(350, 420, "lvl1_coin1"));
            coins.add(new Coin(620, 320, "lvl1_coin2"));

            ToggleWall wall = new ToggleWall(700, 300, 100, 20);
            toggleWalls.add(wall);

            buttons.add(new Button("btn1", 100, 540,
                () -> wall.setVisible(true),
                () -> wall.setVisible(false)
            ));

            blocks.add(new PushableBlock("block1", 150, 500));
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

            coins.add(new Coin(100, 520, "lvl2_coin0"));
            coins.add(new Coin(450, 420, "lvl2_coin1"));
            coins.add(new Coin(720, 320, "lvl2_coin2"));

            ToggleWall wall = new ToggleWall(800, 320, 100, 20);
            toggleWalls.add(wall);
            buttons.add(new Button("btn2", 470, 440,
            	    () -> wall.setVisible(true),
            	    () -> wall.setVisible(false)
            	));

            	blocks.add(new PushableBlock("block2", 420, 420));

            flag = new Flag(750, 290);

            try {
                background = ImageIO.read(Levels.class.getResource("/textureAssets/lvl1background.jpg"));
            } catch (IOException e) {
                System.err.println("Failed to load background for level 2");
                e.printStackTrace();
            }

        } else if (levelNum == 3) {
            platforms.add(new Platform(50, 550, 300, 20));
            platforms.add(new Platform(400, 450, 200, 20));
            platforms.add(new Platform(700, 350, 300, 20));

            coins.add(new Coin(100, 520, "lvl3_coin0"));
            coins.add(new Coin(450, 420, "lvl3_coin1"));
            coins.add(new Coin(720, 320, "lvl3_coin2"));

            ToggleWall wall1 = new ToggleWall(600, 300, 80, 20);
            ToggleWall wall2 = new ToggleWall(800, 300, 80, 20);
            toggleWalls.add(wall1);
            toggleWalls.add(wall2);

            buttons.add(new Button("btn3", 500, 500,
                () -> { wall1.setVisible(true); wall2.setVisible(true); },
                () -> { wall1.setVisible(false); wall2.setVisible(false); }
            ));

            blocks.add(new PushableBlock("block3", 400, 500));
            flag = new Flag(750, 290);

            try {
                background = ImageIO.read(Levels.class.getResource("/textureAssets/lvl1background.jpg"));
            } catch (IOException e) {
                System.err.println("Failed to load background for level 3");
                e.printStackTrace();
            }
        }

        if (flag != null && background != null) {
            return new Level(platforms, coins, buttons, blocks, toggleWalls, flag, background);
        } else {
            return null;
        }
    }
}
