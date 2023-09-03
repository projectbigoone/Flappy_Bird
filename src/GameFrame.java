import javax.swing.JFrame;

public class GameFrame extends JFrame {
    
    GameGraphics graphics;

    GameFrame() {

        graphics = new GameGraphics();

        this.add(graphics);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }
}
