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

    // Returns the x-coordinate if visible
    @Override
    public int getX() {
        return visible ? super.getX() : -9999; // Avoid collision if hidden
    }

    // Returns the y-coordinate if visible
    @Override
    public int getY() {
        return visible ? super.getY() : -9999;
    }

    // Returns the width if visible
    @Override
    public int getWidth() {
        return visible ? super.getWidth() : 0;
    }

    // Returns the height if visible
    @Override
    public int getHeight() {
        return visible ? super.getHeight() : 0;
    }
}