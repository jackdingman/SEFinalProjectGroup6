import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.*;
import java.util.ArrayList;

/*
This class will deal with player mechanics on each of the various levels
This will involve aspects like gravity and movement
 */
public class Player implements KeyListener {
    private int x, y; //Position of the player
    private int xVelo = 0;
    private int yVelo = 0;
    private int width = 30;
    private int height = 30;
    private int coinCount = 0;

    //Below concepts will not need to be changed, hence being final
    private final int speed = 10;
    private final int gravity = 1;
    private final int jumpMechanic = -20;

    private boolean jumping = false;
    private boolean onGround = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean jumpPressed = false;

    private String user;

    public Player(String user, int startX, int startY) {
        this.user = user;
        this.x = startX;
        this.y = startY;
    }

    public void positionChange(ArrayList<Platform> platforms, ArrayList<Coin> coins) {
        if (!onGround) {
            yVelo = yVelo + gravity; // gravity's effect on player
        }

        xVelo = 0;
        if (leftPressed) {
            xVelo = -speed;
        }
        if (rightPressed) {
            xVelo = speed;
        }
        x += xVelo;

        if (jumpPressed && onGround) {
            yVelo = jumpMechanic;
            onGround = false;
            jumping = true;
        }
        y = y + yVelo;

        // Collision with platforms
        onGround = false;
        for (Platform p : platforms) {
            if (collidesWith(p)) {
                if (y + height - yVelo <= p.getY()) { //player must be coming from above
                    y = p.getY() - height; // Snap on top
                    yVelo = 0;
                    onGround = true;
                    jumping = false;
                } else {
                    // SIDE collision bounceback
                    if (x + width - xVelo <= p.getX()) {
                        x = p.getX() - width;
                        xVelo = (int)(-xVelo * 0.5);
                    } else if (x - xVelo >= p.getX() + p.getWidth()) {
                        x = p.getX() + p.getWidth();
                        xVelo = (int)(-xVelo * 0.5);
                    } else if (y - yVelo >= p.getY() + p.getHeight()) {
                        y = p.getY() + p.getHeight();
                        yVelo = (int)(-yVelo * 0.5);
                    }
                }
                break;
            }
        }

        // Prevent moving off-screen
        if (x < 0) x = 0;
        if (x > 970) x = 970;
    }

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

    public void draw(Graphics g) {
        g.setColor(Color.green);
        g.fillRect(x, y, width, height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void addCoin() {
        coinCount++;
        System.out.println("Total coins: " + coinCount);
    }

    public int getCoinCount() {
        return coinCount;
    }

    // New Method: Needed for level transitions
    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
        this.xVelo = 0;
        this.yVelo = 0;
        this.jumping = false;
        this.onGround = false;
    }
}
