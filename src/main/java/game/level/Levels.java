package game.level;

import game.entity.Coin;
import game.entity.Flag;
import game.entity.Button;
import game.entity.Platform;
import game.util.PushableBlock;
import game.util.ToggleWall;

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
            platforms.add(new Platform(300, 500, 250, 20));
            platforms.add(new Platform(600, 350, 100, 20));

            coins.add(new Coin(200, 520, "lvl1_coin0"));
            coins.add(new Coin(350, 420, "lvl1_coin1"));
            coins.add(new Coin(620, 320, "lvl1_coin2"));



            flag = new Flag(680, 280);

            try {
                background = ImageIO.read(Levels.class.getResource("/textureAssets/lvl1background.jpg"));
            } catch (IOException e) {
                System.err.println("Failed to load background for level 1");
                e.printStackTrace();
            }

        } else if (levelNum == 2) {
            platforms.add(new Platform(50, 550, 300, 20));

            platforms.add(new Platform(700, 350, 100, 20));

            coins.add(new Coin(100, 520, "lvl2_coin0"));
            coins.add(new Coin(450, 420, "lvl2_coin1"));
            coins.add(new Coin(720, 320, "lvl2_coin2"));

            ToggleWall wall = new ToggleWall(400, 450, 200, 20);
            toggleWalls.add(wall);
            buttons.add(new Button("btn2", 220, 540,
                    () -> wall.setVisible(true),
                    () -> wall.setVisible(false)
            ));

            blocks.add(new PushableBlock("block2", 75, 510));

            flag = new Flag(750, 290);

            try {
                background = ImageIO.read(Levels.class.getResource("/textureAssets/lvl1background.jpg"));
            } catch (IOException e) {
                System.err.println("Failed to load background for level 2");
                e.printStackTrace();
            }

        } else if (levelNum == 3) {
            platforms.add(new Platform(50, 550, 400, 20));
            platforms.add(new Platform(400, 450, 200, 20));
            platforms.add(new Platform(700, 350, 300, 20));

            coins.add(new Coin(100, 520, "lvl3_coin0"));
            coins.add(new Coin(450, 420, "lvl3_coin1"));
            coins.add(new Coin(720, 320, "lvl3_coin2"));

            ToggleWall wall1 = new ToggleWall(200, 250, 100, 20);
            ToggleWall wall2 = new ToggleWall(500, 300, 100, 20);
            toggleWalls.add(wall1);
            toggleWalls.add(wall2);

            buttons.add(new Button("btn3", 125, 530,
                    () -> {
                        wall1.setVisible(true);
                        wall2.setVisible(true);
                    },
                    () -> {
                        wall1.setVisible(false);
                        wall2.setVisible(false);
                    }
            ));

            blocks.add(new PushableBlock("block3", 420, 420));
            flag = new Flag(170, 220);

            try {
                background = ImageIO.read(Levels.class.getResource("/textureAssets/lvl1background.jpg"));
            } catch (IOException e) {
                System.err.println("Failed to load background for level 3");
                e.printStackTrace();
            }
        } else if (levelNum == 4) {
            platforms.add(new Platform(50, 550, 200, 20)); // Starting platform
            platforms.add(new Platform(300, 550, 200, 20)); // Left button platform
            platforms.add(new Platform(850, 550, 200, 20)); // Right button platform
            platforms.add(new Platform(550, 450, 100, 20)); // Final flag platform

            coins.add(new Coin(320, 520, "lvl4_coin0"));
            coins.add(new Coin(870, 520, "lvl4_coin1"));

            ToggleWall bridge1 = new ToggleWall(500, 530, 80, 20);
            ToggleWall bridge2 = new ToggleWall(700, 530, 80, 20);
            toggleWalls.add(bridge1);
            toggleWalls.add(bridge2);

            // Buttons on both ends
            buttons.add(new Button("btn4a", 310, 540,
                    () -> {
                        bridge1.setVisible(true);
                        bridge2.setVisible(true);
                    },
                    () -> {
                        bridge1.setVisible(false);
                        bridge2.setVisible(false);
                    }
            ));
            buttons.add(new Button("btn4b", 880, 540,
                    () -> {
                        bridge1.setVisible(true);
                        bridge2.setVisible(true);
                    },
                    () -> {
                        bridge1.setVisible(false);
                        bridge2.setVisible(false);
                    }
            ));

            flag = new Flag(570, 420);

            try {
                background = ImageIO.read(Levels.class.getResource("/textureAssets/lvl1background.jpg"));
            } catch (IOException e) {
                System.err.println("Failed to load background for level 4");
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