import java.util.*;

// Tracks in-game statistics for each player
public class PlayerStats {

    // Username of the first player to touch the levels flag
    private String firstToFlag = null;

    // Number of coins collected by each player
    private final Map<String, Integer> coinsCollected = new HashMap<>();

    // Number of deaths recorded for each player
    private final Map<String, Integer> deaths = new HashMap<>();

    // Number of levels medals each player has earned
    private final Map<String, Integer> medals = new HashMap<>();

    // Sets the first player to touch the flag's username
    public void flagReached(String player) {
        if (firstToFlag == null) firstToFlag = player;
    }

    // Tracks coins collected
    public void coinCollected(String player) {
        coinsCollected.merge(player, 1, Integer::sum);
    }

    // Tracks player's deaths
    public void recordDeath(String player) {
        deaths.merge(player, 1, Integer::sum);
    }

    // Awards Least death and Most coin medals to players for each level
    public void awardMedals() {
        // Least deaths
        String leastDeaths = deaths.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);
        if (leastDeaths != null) medals.merge(leastDeaths, 1, Integer::sum);

        // Most coins
        String mostCoins = coinsCollected.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);
        if (mostCoins != null) medals.merge(mostCoins, 1, Integer::sum);
    }

    // Checks who have the most medals
    public String getMVP() {
        return medals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("N/A");
    }

    // Return immutable view of coins collected
    public Map<String, Integer> getCoinsCollected() {
        return coinsCollected;
    }

    // Return immutable view of death counts
    public Map<String, Integer> getDeaths() {
        return deaths;
    }

    // Return immutable view of medals count
    public Map<String, Integer> getMedals() {
        return medals;
    }

    // Return the username of the first player who reached the flag
    public String getFirstToFlag() {
        return firstToFlag;
    }
}