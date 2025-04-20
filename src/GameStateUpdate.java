import java.util.HashMap;
import java.io.Serializable;

public class GameStateUpdate implements Serializable {
    // Map of player usernames to their latest state updates
    public HashMap<String, PlayerUpdate> players;

    // Snapshot of the shared game world state
    public GameWorldState worldState;

    // Constructor
    public GameStateUpdate(HashMap<String, PlayerUpdate> players, GameWorldState worldState) {
        this.players = players;
        this.worldState = worldState;
    }
}
