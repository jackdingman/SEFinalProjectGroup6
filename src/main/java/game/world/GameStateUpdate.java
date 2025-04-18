package game.world;

import game.entity.PlayerUpdate;

import java.io.Serializable;
import java.util.HashMap;

public class GameStateUpdate implements Serializable {
    public HashMap<String, PlayerUpdate> players;
    public GameWorldState worldState;

    public GameStateUpdate(HashMap<String, PlayerUpdate> players, GameWorldState worldState) {
        this.players = players;
        this.worldState = worldState;
    }
}
