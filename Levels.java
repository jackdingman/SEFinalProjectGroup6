import java.util.ArrayList;

public class Levels {
    private int level;
    private ArrayList<Platform> platforms;
    private ArrayList<Coin> coins;

    public Levels(int level, ArrayList<Platform> platforms, ArrayList<Coin> coins) {
        this.level = level;
        this.platforms = platforms;
        this.coins = coins;

    }
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;

    }
}

