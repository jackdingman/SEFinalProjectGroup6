package game.util;

import game.entity.Platform;
import game.entity.Player;

import java.awt.*;
import java.util.ArrayList;

public class PushableBlock {
    // X and Y coordinate of blocks top-left and top-right corners
    private int x, y;

    // Height and width of the block in pixels
    private final int width = 30, height = 30;

    // Horizontal and Vertical velocity of the block
    private int xVelo = 0;
    private int yVelo = 0;

    // Downward acceleration applied when no on the ground
    private final int gravity = 1;

    // Flag indicating whether the block is resting on a platform
    private boolean onGround = false;

    // Unique ID for the block instance
    private String id;

    // Original spawn point - used for resets
    private int originalX, originalY;

    // Constructor
    public PushableBlock(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.originalX = x;
        this.originalY = y;
    }

    // Returns the blocks ID
    public String getId() {
        return id;
    }

    // Set the blocks position
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Updates the blocks physics and collision with platforms and players
    public void update(ArrayList<Platform> platforms, Player player) {
        // Gravity
        if (!onGround) yVelo += gravity;
        else             yVelo = 0;

        // Push by player when overlapping
        Rectangle playerBounds = new Rectangle(player.getX(), player.getY(), 30, 30);
        if (playerBounds.intersects(getBounds())) {
            if (player.isRightPressed()) {
                x += 10;
                player.setPosition(player.getX() - 10, player.getY()); // prevent clipping
            } else if (player.isLeftPressed()) {
                x -= 10;
                player.setPosition(player.getX() + 10, player.getY()); // prevent clipping
            }
        }


        // Apply velocity
        x += xVelo;
        y += yVelo;

        // Collision with each platform…
        onGround = false;
        for (Platform p : platforms) {
            Rectangle plat = new Rectangle(p.getX(), p.getY(), p.getWidth(), p.getHeight());
            if (getBounds().intersects(plat)) {
                if (y + height - yVelo <= p.getY()) {
                    y = p.getY() - height;
                    yVelo = 0;
                    onGround = true;
                } else if (x + width - xVelo <= p.getX()) {
                    x = p.getX() - width;
                } else if (x - xVelo >= p.getX() + p.getWidth()) {
                    x = p.getX() + p.getWidth();
                } else if (y - yVelo >= p.getY() + p.getHeight()) {
                    y = p.getY() + p.getHeight();
                    yVelo = 0;
                }
            }
        }

        // Screen bounds and reset if fallen
        if (x < 0) x = 0;
        if (x > 970) x = 970;
        if (y > 1200) {
            x = originalX;
            y = originalY;
        }
    }


    // Return the bounding rectangle for collision detection
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Renders the block
    public void draw(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, width, height);
    }
}