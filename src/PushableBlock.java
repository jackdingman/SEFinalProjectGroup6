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
        // Apply gravity when not on the ground
        if (!onGround) {
            yVelo += gravity;
        } else {
            yVelo = 0;
        }

        // Determine if player is pushing from left or right
        Rectangle playerBounds = new Rectangle(player.getX(), player.getY(), 30, 30);
        Rectangle blockBounds = getBounds();

        if (playerBounds.intersects(blockBounds)) {
            int playerRight = player.getX() + 30;
            int blockLeft = x;
            int playerLeft = player.getX();
            int blockRight = x + width;

            // Push to the right
            if (playerRight > blockLeft && playerRight < blockLeft + 15 && player.isRightPressed()) {
                x += 5;
            }
            // Push to the left
            else if (playerLeft < blockRight && playerLeft > blockRight - 15 && player.isLeftPressed()) {
                x -= 5;
            }
        }
        // Apply horizontal movement
        x += xVelo;

        // Apply vertical movement
        y += yVelo;

        // Reset onGround, check collisions
        onGround = false;
        for (Platform p : platforms) {
            Rectangle platBounds = new Rectangle(p.getX(), p.getY(), p.getWidth(), p.getHeight());

            if (getBounds().intersects(platBounds)) {
                // Coming from above
                if (y + height - yVelo <= p.getY()) {
                    y = p.getY() - height;
                    yVelo = 0;
                    onGround = true;
                }
                // Hit platform from left
                else if (x + width - xVelo <= p.getX()) {
                    x = p.getX() - width;
                }
                // Hit platform from right
                else if (x - xVelo >= p.getX() + p.getWidth()) {
                    x = p.getX() + p.getWidth();
                }
                // Hit platform from below
                else if (y - yVelo >= p.getY() + p.getHeight()) {
                    y = p.getY() + p.getHeight();
                    yVelo = 0;
                }
            }
        }

        // Stay within screen
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