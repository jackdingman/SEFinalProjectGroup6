import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class GameWorldState implements Serializable {
    public int currentLevel;
    public HashSet<String> collectedCoinIds;
    public HashMap<String, int[]> blockPositions;  // blockId -> [x, y]
    public HashSet<String> activeButtonIds;        // buttonIds that are currently pressed

    public GameWorldState(int level) {
        this.currentLevel = level;
        this.collectedCoinIds = new HashSet<>();
        this.blockPositions = new HashMap<>();
        this.activeButtonIds = new HashSet<>();
    }

    public void collectCoin(String coinId) {
        collectedCoinIds.add(coinId);
    }

    public boolean isCollected(String coinId) {
        return collectedCoinIds.contains(coinId);
    }

    public void updateBlockPosition(String blockId, int x, int y) {
        blockPositions.put(blockId, new int[]{x, y});
    }

    public int[] getBlockPosition(String blockId) {
        return blockPositions.getOrDefault(blockId, new int[]{0, 0});
    }

    public void activateButton(String buttonId) {
        activeButtonIds.add(buttonId);
    }

    public void deactivateButton(String buttonId) {
        activeButtonIds.remove(buttonId);
    }

    public boolean isButtonActive(String buttonId) {
        return activeButtonIds.contains(buttonId);
    }

    public void advanceLevel() {
        currentLevel++;
        collectedCoinIds.clear();
        blockPositions.clear();
        activeButtonIds.clear();
    }
}
