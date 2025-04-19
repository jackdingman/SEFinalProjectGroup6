import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Level {
    private ArrayList<Platform> platforms;
    private ArrayList<Coin> coins;
    private ArrayList<Button> buttons;
    private ArrayList<PushableBlock> blocks;
    private ArrayList<ToggleWall> toggleWalls;
    private Flag flag;
    private BufferedImage background;

    public Level(ArrayList<Platform> platforms, ArrayList<Coin> coins, ArrayList<Button> buttons,
                 ArrayList<PushableBlock> blocks, ArrayList<ToggleWall> toggleWalls,
                 Flag flag, BufferedImage background) {
        this.platforms = platforms;
        this.coins = coins;
        this.buttons = buttons;
        this.blocks = blocks;
        this.toggleWalls = toggleWalls;
        this.flag = flag;
        this.background = background;
    }

    public ArrayList<Platform> getPlatforms() { return platforms; }
    public ArrayList<Coin> getCoins() { return coins; }
    public ArrayList<Button> getButtons() { return buttons; }
    public ArrayList<PushableBlock> getBlocks() { return blocks; }
    public ArrayList<ToggleWall> getToggleWalls() { return toggleWalls; }
    public Flag getFlag() { return flag; }
    public BufferedImage getBackground() { return background; }
}
