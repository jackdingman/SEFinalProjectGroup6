import java.io.Serializable;

public class PlayerUpdate implements Serializable {
    public String username;
    public int x;
    public int y;
    public int coinCount;

    public PlayerUpdate(String username, int x, int y, int coinCount) {
        this.username = username;
        this.x = x;
        this.y = y;
        this.coinCount = coinCount;
    }
}
