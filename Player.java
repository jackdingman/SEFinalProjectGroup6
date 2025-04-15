import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.*;
import java.util.ArrayList;

/*
This class will deal with player mechanics on each of the various levels
This will involve aspects like gravity and movement

 */
public class Player implements KeyListener {
    private int x, y; //Position of hte player
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


    public Player(String user, int startX, int startY)
    {
        this.user = user;
        this.x = startX;
        this.y = startY;
    }

    public void positionChange(ArrayList<Platform> platforms, ArrayList<Coin> coins)
    {
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

        //Updates will need to be sent to the server

        onGround = false;
        for (Platform p : platforms) {
            if (collidesWith(p)) {
                if (y + height - yVelo <= p.getY()) { //player must be coming from above, not the side
                    y = p.getY() - height; // Snap on top
                    yVelo = 0;
                    onGround = true;
                    jumping = false;
                } else {
                    // SIDE collision bounceback
                    if (x + width - xVelo <= p.getX()) {
                        // Hit left side of platform
                        x = p.getX() - width;
                        xVelo = (int)(-xVelo * 0.5); // Bounce back with 50% velocity
                    } else if (x - xVelo >= p.getX() + p.getWidth()) {
                        // Hit right side of platform
                        x = p.getX() + p.getWidth();
                        xVelo = (int)(-xVelo * 0.5);
                    }

                    // Optionally slow down vertical velocity if bumping under platform
                    else if (y - yVelo >= p.getY() + p.getHeight()) {
                        y = p.getY() + p.getHeight();
                        yVelo = (int)(-yVelo * 0.5); // Bump head, bounce downward
                    }
                }
                break;
            }

        }
        /*for (Coin c : coins) {
            if (c.isCoinCollected(x, y, width, height)){
                c.draw(null);
            }
        }*/

        // Prevent moving off screen
        if (x < 0) x = 0;
        if (x > 770) x = 770; // 800 - player width
        if (y > 1200 ) {
            x = 100;
            y = 500;
        }
    }

    private boolean collidesWith(Platform p) {
        return (x + width > p.getX() && x < p.getX() + p.getWidth() &&
                y + height > p.getY() && y < p.getY() + p.getHeight());
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

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

    private void sendMovementChange(){
        System.out.println(user + "moved to ("+x+","+y+")");

    }

    public void draw(Graphics g){
        g.setColor(Color.green);
        g.fillRect(x,y,width,height);
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public void addCoin() {
        coinCount++; //increments the coin count by one
        System.out.println("Total coins: " + coinCount);
    }
    public int getCoinCount(){
        return coinCount;
    }

}
