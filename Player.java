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

    //Below concepts will not need to be changed, hence being final
    private final int speed = 10;
    private final int gravity = 1;
    private final int jumpMechanic = -12;

    private boolean jumping = false;
    private boolean onGround = false;

    private String user;


    public Player(String user, int startX, int startY)
    {
        this.user = user;
        this.x = startX;
        this.y = startY;
    }

    public void positionChange(ArrayList<Platform> platforms)
    {
        if (!onGround) {
            yVelo = yVelo + gravity; // gravity's effect on player
        }

        x = x + xVelo; //tracks position changes
        y = y + yVelo;

        //Updates will need to be sent to the server

        onGround = false;
        for (Platform p : platforms) {
            if (collidesWith(p)) {
                y = p.getY() - height; // Place player on top of the platform
                yVelo = 0;
                onGround = true;
                jumping = false;
                break;
            }
        }

        // Prevent moving off screen
        if (x < 0) x = 0;
        if (x > 770) x = 770; // 800 - player width
        if (y > 600 ) {
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

        if(e.getKeyCode() == KeyEvent.VK_LEFT){
            xVelo = -speed;
        }
        if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            xVelo = speed;
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            if(onGround){
                jumping = true;
                onGround = false;
                yVelo = jumpMechanic;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT)
            xVelo = 0;
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

}
