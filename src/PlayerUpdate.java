import java.io.Serializable;

// Serializable data transfer object for sending player state updates over the network
public class PlayerUpdate implements Serializable {
    // Username of the player
    public String username;

    // Current X-coordinate of the player
    public int x;

    // Current Y-coordinate of the player
    public int y;

    // Number of coins the player has collected
    public int coinCount;

    // Constructor
    public PlayerUpdate(String username, int x, int y, int coinCount) {
        this.username = username;
        this.x = x;
        this.y = y;
        this.coinCount = coinCount;
    }
}
