import java.io.Serializable;

public class BlockUpdate implements Serializable {
    public int x, y;

    public BlockUpdate(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
