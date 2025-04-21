package game.entity;

import java.awt.*;
import java.util.ArrayList;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

/*
This class will deal with player mechanics on each of the various levels
This will involve aspects like gravity and movement
 */
public class Player implements KeyListener {
    // Current X and Y coordinate of the player
    private int x, y;

    // Current horizontal velocity
    private int xVelo = 0;

    // Current vertical velocity
    private int yVelo = 0;

    // Width of the players sprite
    private int width = 30;

    // Height of the players sprite
    private int height = 30;

    // Number of coins the player has collected
    private int coinCount = 0;

    // Movement speed
    private final int speed = 10;

    // Downward acceleration applied when airborne
    private final int gravity = 1;

    // Initial upward velocity when jumping
    private final int jumpMechanic = -20;

    // Whether the player is currently jumping
    private boolean jumping = false;

    // Whether the player is currently on the ground
    private boolean onGround = false;

    // Whether the left arrow is pressed
    private boolean leftPressed = false;

    // Whether the right arrow is pressed
    private boolean rightPressed = false;

    // Whether the jump key is pressed
    private boolean jumpPressed = false;

    // Identifier for the player
    private String user;

    // Constructor
    public Player(String user, int startX, int startY) {
        this.user = user;
        this.x = startX;
        this.y = startY;
    }

    // Updates the players position, applies gravity, handles jumping and resolves collisions with platforms and coins
    public void positionChange(List<Platform> platforms, List<Coin> coins) {
        // Apply gravity if not on the ground
        if (!onGround) {
            yVelo += gravity;
        } else {
            yVelo = 0;
        }

        // Resets horizontal velocity each frame
        xVelo = 0;
        if (leftPressed)  xVelo = -speed;
        if (rightPressed) xVelo =  speed;

        // Jump if pressed and on the ground
        if (jumpPressed && onGround) {
            yVelo    = jumpMechanic;
            onGround = false;
            jumping  = true;
        }

        // Moves horizontally
        x += xVelo;
        Rectangle playerBounds = new Rectangle(x, y, width, height);
        for (Platform p : platforms) {
            Rectangle plat = new Rectangle(p.getX(), p.getY(), p.getWidth(), p.getHeight());
            if (playerBounds.intersects(plat)) {
                if (xVelo > 0) {
                    // Ran into a wall on the right
                    x = p.getX() - width;
                } else if (xVelo < 0) {
                    // Ran into a wall on the left
                    x = p.getX() + p.getWidth();
                }
                xVelo = 0;
                playerBounds.setLocation(x, y);
            }
        }

        // Vertical pass
        y += yVelo;
        onGround = false;
        playerBounds.setLocation(x, y);
        for (Platform p : platforms) {
            Rectangle plat = new Rectangle(p.getX(), p.getY(), p.getWidth(), p.getHeight());
            if (playerBounds.intersects(plat)) {
                if (yVelo > 0) {
                    // Landing on top
                    y  = p.getY() - height;
                    onGround = true;
                    jumping  = false;
                } else if (yVelo < 0) {
                    // Hitting head
                    y = p.getY() + p.getHeight();
                }
                yVelo = 0;
                playerBounds.setLocation(x, y);
            }
        }

        // Clamp to screen and reset if fallen
        if (x < 0) x = 0;
        if (x > 970) x = 970;
        if (y > 1200) {
            x = 100;
            y = 500;
        }
}

        // Checks if a player bounding box intersects the given platform
    private boolean collidesWith(Platform p) {
        return (x + width > p.getX() && x < p.getX() + p.getWidth() &&
                y + height > p.getY() && y < p.getY() + p.getHeight());
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            jumpPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            jumpPressed = false;
        }
    }

    private void sendMovementChange() {
        System.out.println(user + " moved to (" + x + "," + y + ")");
    }

    // Draw the player as a filled rectangle
    public void draw(Graphics g) {
        g.setColor(Color.green);
        g.fillRect(x, y, width, height);
    }

    // Return X-coordinate
    public int getX() {
        return x;
    }

    // Return Y-coordinate
    public int getY() {
        return y;
    }

    // Increments the coin count when a coin is collected
    public void addCoin(
    ) {
        coinCount++;
        System.out.println("Total coins: " + coinCount);
    }

    // Return the number of coins collected
    public int getCoinCount() {
        return coinCount;
    }

    // Resets the players position and velocity
    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
        this.xVelo = 0;
        this.yVelo = 0;
        this.jumping = false;
        this.onGround = false;
    }

    // Return true if left key is currently pressed
    public boolean isLeftPressed() {
        return leftPressed;
    }

    // Return true if right key is currently pressed
    public boolean isRightPressed() {
        return rightPressed;
    }

}