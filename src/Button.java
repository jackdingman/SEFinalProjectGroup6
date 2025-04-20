import java.awt.*;
import java.util.List;

public class Button {
    private int x, y;
    private int width = 40, height = 10;
    private boolean activated = false;
    private Runnable onActivate;
    private Runnable onDeactivate;  // <-- add this
    private String id;
    public Button(String id,int x, int y, Runnable onActivate, Runnable onDeactivate) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.onActivate = onActivate;
        this.onDeactivate = onDeactivate;
    }
    public String getId() {
        return id;
    }

    public void update(List<PlayerUpdate> allPlayers, List<PushableBlock> blocks) {
        Rectangle bounds = new Rectangle(x, y, width, height);

        boolean nowActivated = false;

        for (PlayerUpdate p : allPlayers) {
            Rectangle playerBounds = new Rectangle(p.x, p.y, 30, 30);
            if (bounds.intersects(playerBounds)) {
                nowActivated = true;
                break;
            }
        }

        if (!nowActivated) {
            for (PushableBlock block : blocks) {
                if (bounds.intersects(block.getBounds())) {
                    nowActivated = true;
                    break;
                }
            }
        }

        if (!activated && nowActivated) {
            onActivate.run();
        } else if (activated && !nowActivated) {
            onDeactivate.run();
        }

        activated = nowActivated;
    }


    public boolean isActivated() {
        return activated;
    }

    public Runnable getActivateRunnable() {
        return onActivate;
    }

    public Runnable getDeactivateRunnable() {
        return onDeactivate;
    }

    public void setActivated(boolean value) {
        this.activated = value;
    }


    public void draw(Graphics g) {
        g.setColor(activated ? Color.GREEN : Color.RED);
        g.fillRect(x, y, width, height);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}