package game.util;

import game.entity.Platform;

import java.awt.*;

// A wall that can be toggled between visible and invisible states
public class ToggleWall extends Platform {

    // Flag indicating whether the wall is currently visible
    private boolean visible = false;

    // Constructor
    public ToggleWall(int x, int y, int width, int height) {
        super(x, y, width, height);  // Inherits position and size from Platform
    }

    // Toggles the visibility explicitly
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    // Checks whether the wall is currently visible
    public boolean isVisible() {
        return visible;
    }

    // Draws the wall only if its is visible
    @Override
    public void draw(Graphics g) {
        if (visible) {
            super.draw(g);
        }
    }

}