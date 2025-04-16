import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Level {
    private ArrayList<Platform> platforms;
    private ArrayList<Coin> coins;
    private Flag flag;
    private BufferedImage background;

    public Level(ArrayList<Platform> platforms, ArrayList<Coin> coins, Flag flag, BufferedImage background) {
        this.platforms = platforms;
        this.coins = coins;
        this.flag = flag;
        this.background = background;
    }

    public ArrayList<Platform> getPlatforms() {
        return platforms;
    }

    public ArrayList<Coin> getCoins() {
        return coins;
    }

    public Flag getFlag() {
        return flag;
    }

    public BufferedImage getBackground() {
        return background;
    }
}
