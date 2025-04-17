import java.io.Serializable;
import java.util.HashSet;

public class GameWorldState implements Serializable {
    public int currentLevel;
    public HashSet<String> collectedCoinIds;

    public GameWorldState(int level) {
        this.currentLevel = level;
        this.collectedCoinIds = new HashSet<>();
    }

    public void collectCoin(String coinId) {
        collectedCoinIds.add(coinId);
    }

    public boolean isCollected(String coinId) {
        return collectedCoinIds.contains(coinId);
    }

    public void advanceLevel() {
        currentLevel++;
        collectedCoinIds.clear();
    }
}
