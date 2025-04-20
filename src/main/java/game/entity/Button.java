package game.entity;

import java.awt.*;
import java.util.List;

import game.util.PushableBlock;

public class Button {
    // X position of the button
    private int x, y;

    // Dimensions of the button
    private int width = 40, height = 10;

    // Current activation state
    private boolean activated = false;

    // Action to run when button becomes activated
    private Runnable onActivate;

    // Action to run when button becomes deactivated
    private Runnable onDeactivate;

    // Unique identifier for this button
    private String id;

    // Constructor: sets id, position, and activation/deactivation handlers
    public Button(String id, int x, int y, Runnable onActivate, Runnable onDeactivate) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.onActivate = onActivate;
        this.onDeactivate = onDeactivate;
    }

    // Returns this button's ID
    public String getId() {
        return id;
    }

    // Updates activation state based on player or block overlap
    public void update(List<PlayerUpdate> allPlayers, List<PushableBlock> blocks) {
        // Button bounds for collision tests
        Rectangle bounds = new Rectangle(x, y, width, height);

        boolean nowActivated = false;

        // Check overlap with any player
        for (PlayerUpdate p : allPlayers) {
            Rectangle playerBounds = new Rectangle(p.x, p.y, 30, 30);
            if (bounds.intersects(playerBounds)) {
                nowActivated = true;
                break;
            }
        }

        // If not activated by player, check overlap with blocks
        if (!nowActivated) {
            for (PushableBlock block : blocks) {
                if (bounds.intersects(block.getBounds())) {
                    nowActivated = true;
                    break;
                }
            }
        }

        // Trigger activate action when crossing from off to on
        if (!activated && nowActivated) {
            onActivate.run();
        }
        // Trigger deactivate action when crossing from on to off
        else if (activated && !nowActivated) {
            onDeactivate.run();
        }

        // Store new state
        activated = nowActivated;
    }

    // Returns whether the button is currently activated
    public boolean isActivated() {
        return activated;
    }

    // Returns the activation Runnable
    public Runnable getActivateRunnable() {
        return onActivate;
    }

    // Returns the deactivation Runnable
    public Runnable getDeactivateRunnable() {
        return onDeactivate;
    }

    // Manually set activation state
    public void setActivated(boolean value) {
        this.activated = value;
    }

    // Draws the button: green if activated, red otherwise
    public void draw(Graphics g) {
        g.setColor(activated ? Color.GREEN : Color.RED);
        g.fillRect(x, y, width, height);
    }

    // Accessors for position and size
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
