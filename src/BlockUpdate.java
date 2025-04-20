import java.io.Serializable;

public class BlockUpdate implements Serializable {
    // X position of the block after update
    public int x, y;

    // Constructor sets new block position
    public BlockUpdate(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
