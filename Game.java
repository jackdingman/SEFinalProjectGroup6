import javax.swing.*;

public class Game extends JFrame {
    public Game(){
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(new GamePanel());
    }
    public static void main(String[] args) {

        Game game = new Game();
        game.setVisible(true);

    }
}
