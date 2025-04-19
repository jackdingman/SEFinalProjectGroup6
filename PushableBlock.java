import java.awt.*;
import java.util.ArrayList;

public class PushableBlock {
    private int x, y;
    private final int width = 30, height = 30;
    private int xVelo = 0;
    private int yVelo = 0;
    private final int gravity = 1;
    private boolean onGround = false;
    private String id;
    private int originalX, originalY;

    public PushableBlock(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.originalX = x;
        this.originalY = y;
    }
    public String getId() {
        return id;
    }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }


    public void update(ArrayList<Platform> platforms, Player player) {
        // Gravity
        if (!onGround) {
            yVelo += gravity;
        } else {
            yVelo = 0;
        }

        // Check horizontal push from player
        Rectangle playerBounds = new Rectangle(player.getX(), player.getY(), 30, 30);
        Rectangle blockBounds = getBounds();

        if (playerBounds.intersects(blockBounds)) {
            int playerRight = player.getX() + 30;
            int blockLeft = x;
            int playerLeft = player.getX();
            int blockRight = x + width;

            if (playerRight > blockLeft && playerRight < blockLeft + 15 && player.isRightPressed()) {
                x += 5;
            } else if (playerLeft < blockRight && playerLeft > blockRight - 15 && player.isLeftPressed()) {
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

        // Stay within screen
        if (x < 0) x = 0;
        if (x > 970) x = 970;
        if (y > 1200) {
            x = originalX;
            y = originalY;
        }

    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void draw(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, width, height);
    }
}
