import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class GameWorldState implements Serializable {
    // Current level index in the game
    public int currentLevel;

    // IDs of coins that have been collected
    public HashSet<String> collectedCoinIds;

    // Mapping from block ID to its [x, y] position
    public HashMap<String, int[]> blockPositions;

    // IDs of buttons that are currently pressed/active
    public HashSet<String> activeButtonIds;

    // Constructor
    public GameWorldState(int level) {
        this.currentLevel = level;
        this.collectedCoinIds = new HashSet<>();
        this.blockPositions = new HashMap<>();
        this.activeButtonIds = new HashSet<>();
    }

    // Mark a coin as collected
    public void collectCoin(String coinId) {
        collectedCoinIds.add(coinId);
    }

    // Check if a coin has already been collected
    public boolean isCollected(String coinId) {
        return collectedCoinIds.contains(coinId);
    }

    // Update the stored position of a pushable block
    public void updateBlockPosition(String blockId, int x, int y) {
        blockPositions.put(blockId, new int[]{x, y});
    }

    // Retrieve the last known position of a block (default to [0,0])
    public int[] getBlockPosition(String blockId) {
        return blockPositions.getOrDefault(blockId, new int[]{0, 0});
    }

    // Mark a button as active/pressed
    public void activateButton(String buttonId) {
        activeButtonIds.add(buttonId);
    }

    // Mark a button as inactive/released
    public void deactivateButton(String buttonId) {
        activeButtonIds.remove(buttonId);
    }

    // Check if a button is currently active
    public boolean isButtonActive(String buttonId) {
        return activeButtonIds.contains(buttonId);
    }

    // Advance to the next level and reset transient state
    public void advanceLevel() {
        currentLevel++;
        collectedCoinIds.clear();
        blockPositions.clear();
        activeButtonIds.clear();
    }
}
