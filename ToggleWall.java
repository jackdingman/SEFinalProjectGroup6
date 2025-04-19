import java.awt.*;

public class ToggleWall extends Platform {
    private boolean visible = false;

    public ToggleWall(int x, int y, int width, int height) {
        super(x, y, width, height);  // Inherits position and size from Platform
    }

    public void toggle() {
        visible = !visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public void draw(Graphics g) {
        if (visible) {
            super.draw(g);
        }
    }

    @Override
    public int getX() {
        return visible ? super.getX() : -9999; // Avoid collision if hidden
    }

    @Override
    public int getY() {
        return visible ? super.getY() : -9999;
    }

    @Override
    public int getWidth() {
        return visible ? super.getWidth() : 0;
    }

    @Override
    public int getHeight() {
        return visible ? super.getHeight() : 0;
    }
}
