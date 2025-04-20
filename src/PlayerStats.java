import java.util.*;

public class PlayerStats {
    private String firstToFlag = null;
    private final Map<String, Integer> coinsCollected = new HashMap<>();
    private final Map<String, Integer> deaths = new HashMap<>();
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

    public Map<String, Integer> getCoinsCollected() {
        return coinsCollected;
    }

    public Map<String, Integer> getDeaths() {
        return deaths;
    }

    public Map<String, Integer> getMedals() {
        return medals;
    }

    public String getFirstToFlag() {
        return firstToFlag;
    }
}