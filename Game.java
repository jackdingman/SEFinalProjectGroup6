import javax.swing.*;

public class Game extends JFrame {
    public Game(){
        setSize(1600,1200);
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(new GamePanel());
    }
    public static void main(String[] args) {

        Game game = new Game();
        game.setVisible(true);

    }
}
