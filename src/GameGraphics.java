import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

import javax.swing.Timer;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/*
 * DOCUMENTATION:
 * 
 * 1. The default gravity for this game is 0.2 or 2px per 1ms.
 *    To adjust, reinstantiate ACCELARATION_DUE_TO_GRAVITY
 *    variable accordingly. 
 * 2. The default jump strength of the bird is -40 (upward strength). 
 *    To adjust, reinstantiate JUMP_STRENGTH variable accordingly.
 * 3. The initial position of the bird is approximately 300px above
 *    ground. To adjust, reinstantiate the birdPosY variable inside
 *    constructor accordingly.
 * 4. When updating the fps of the game, make sure to adjust the time
 *    to add the columns as to prevent weird things from happening.
 * 5. Fun bug hint: Staying on top always counts.😉  
*/

public class GameGraphics extends JPanel implements ActionListener, KeyListener {

    final int PANEL_WIDTH = 500;
    final int PANEL_HEIGHT = 500;

    final int PIPE_WIDTH = 80;
    final int MIN_PIPE_HEIGHT = 50;
    final int COLUMN_OPENING = 100;

    final double ACCELERATION_DUE_TO_GRAVITY = 0.2; 
    final double JUMP_STRENGTH = -40; 

    ArrayList<Column> columns;

    Color pipeColorDay = new Color(175, 252, 65);
    Color bgColorDay = new Color(72, 202, 228);

    Color pipeColorNight = Color.orange;
    Color bgColorNight = new Color(135, 150, 200);

    int birdPosX; 
    int birdPosY;

    double initialVelocity;
    double finalVelocity;

    int currScore;
    int currHighScore;

    boolean gameHasStarted;

    Timer screenUpdateTimer;
    Timer addColumnTimer;
    Random random;
    Image bird;
    Image backgroundImage;
    Image gameOverImage;
    Graphics2D g2D;
    FontMetrics fm;

    GameGraphics() {

        // Instantiate important variables
        birdPosX = 100;
        birdPosY = 200;
        currHighScore = 0;
        gameHasStarted = false;
        random = new Random();
        columns = new ArrayList<Column>();

        // Make the panel
        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setBackground(bgColorDay);

        // Set bird image
        bird = new ImageIcon("media/flappy_bird.png").getImage();
        
        // Set game over image
        gameOverImage = new ImageIcon("media/game_over.png").getImage();

        // Add key listener 
        this.setFocusable(true);
        this.addKeyListener(this);
    }

    // This function starts the game
    public void startGame() {

        birdPosY = 200;

        initialVelocity = 0;
        finalVelocity = 0;

        currScore = 0;

        columns = new ArrayList<Column>();

        // Start necessary timers to start the game
        screenUpdateTimer = new Timer(10, this); // Update screen every 10ms (100fps)
        addColumnTimer = new Timer(3000, this); // Add a column every 3000ms or 300 frames

        screenUpdateTimer.start();
        addColumnTimer.start();

        // Indicate that game has started
        gameHasStarted = true; 
    }


    // This function adds a column containing two pipes
    public void addColumn() {

        int upperPipeHeight;
        int lowerPipeHeight;

        int x = PANEL_WIDTH;
        int upperPipePosY = 0;
        int lowerPipePosY;

        // Set pipes height
        upperPipeHeight = random.nextInt(PANEL_HEIGHT - (COLUMN_OPENING + MIN_PIPE_HEIGHT * 2)) + MIN_PIPE_HEIGHT; // Randomize upper pipe height
        lowerPipeHeight = PANEL_HEIGHT - (COLUMN_OPENING + upperPipeHeight);

        // Set y position of the lower pipe
        lowerPipePosY = upperPipeHeight + COLUMN_OPENING;

        columns.add(new Column(x, upperPipePosY, lowerPipePosY, upperPipeHeight, lowerPipeHeight));
    }

    // This function checks if game is already over
    public boolean isGameOver() {
        
        boolean over = false;

        // Detect for collision of bird and pipes per column
        for (Column column : columns) {
            Rectangle birdBounds = new Rectangle(birdPosX, birdPosY, bird.getWidth(null), bird.getHeight(null));
            Rectangle upperPipeBounds = new Rectangle(column.columnPosX, column.upperPipePosY, PIPE_WIDTH, column.upperPipeHeight);
            Rectangle lowerPipeBounds = new Rectangle(column.columnPosX, column.lowerPipePosY, PIPE_WIDTH, column.lowerPipeHeight);

            // If bird collides with the upper or lower pipe
            if (birdBounds.intersects(upperPipeBounds) || birdBounds.intersects(lowerPipeBounds)) {
                over = true;
            }
        }

        // If bird is on the ground
        if (birdPosY >= PANEL_HEIGHT - bird.getHeight(null)) {
            over = true;
        }  

        return over;
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        g2D = (Graphics2D) g; // Cast Graphics to Graphics2D
        fm = g2D.getFontMetrics(); // Initialize font metrics 

        g2D.setFont(new Font("Monospaced", Font.PLAIN, 24));

        // Set pipe color
        g2D.setPaint(pipeColorDay);
        
        // Set flappy bird icon
        g2D.drawImage(bird, birdPosX, birdPosY, null);

        // Rendering of new column positions
        for (Column column : columns) {

            // Create upper pipe 
            g2D.fillRect(column.columnPosX, column.upperPipePosY, PIPE_WIDTH, column.upperPipeHeight);

            // Create lower pipe
            g2D.fillRect(column.columnPosX, column.lowerPipePosY, PIPE_WIDTH, column.lowerPipeHeight);
        }

        // Set a new color
        g2D.setPaint(Color.WHITE);
        
        // Display label for the current score
        g2D.drawString(Integer.toString(currScore), (PANEL_WIDTH - fm.stringWidth(Integer.toString(currScore)) * 2) / 2, 50);

        // Display label for the current high score
        g2D.drawString("High Score: " + Integer.toString(currHighScore), (PANEL_WIDTH - fm.stringWidth("High Score: " + Integer.toString(currHighScore)) * 2) / 2, PANEL_HEIGHT - 50);

        // If game is over, stop the game and display game over img
        if (isGameOver()) {

            // Stop the game
            screenUpdateTimer.stop();
            addColumnTimer.stop();

            // Indicate that game has stopped
            gameHasStarted = false; 

            // Display the game over img
            g2D.drawImage(gameOverImage, 0, 0, null);
        }

        // If game hasn't started, display press key msg
        if (!gameHasStarted) {
            g2D.drawString("Press SPACE key to start!", (PANEL_WIDTH - fm.stringWidth("Press SPACE key to start!") * 2) / 2, PANEL_HEIGHT - 100);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Update screen (frame) every 10ms
        if (e.getSource() == screenUpdateTimer) {
            
            // Use iterator to prevent errors when removing a column object
            Iterator<Column> iterator = columns.iterator();

            // Update column position and current score
            while (iterator.hasNext()) {

                Column column = iterator.next();

                // Remove column if it's outside the panel
                if (column.columnPosX == PIPE_WIDTH * -1) {
                    iterator.remove();
                    System.out.println("Column removed"); // Test if column is removed
                }
                // Else, update its position
                else {
                    column.columnPosX -= 1;
                }

                // Update the current score and high score
                if (column.columnPosX == birdPosX - PIPE_WIDTH) {
                    currScore++;

                    if (currHighScore == 0 || currHighScore < currScore) {
                        currHighScore = currScore;
                    } 
                
                    System.out.println("Current score: " + currScore);
                }
            }

            // Simulate gravity for the bird
            finalVelocity = initialVelocity + ACCELERATION_DUE_TO_GRAVITY * 10;
            birdPosY += (int) finalVelocity / 10;
            initialVelocity = finalVelocity;
          
            repaint(); // Update screen graphic
        }

        // Add a column every 3000ms
        if (e.getSource() == addColumnTimer) {
            addColumn();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {      
            
            // If game hasn't started, start the game
            if (!gameHasStarted) {
                startGame();
            }

            // If game has started, make the bird jump
            if (gameHasStarted) {
                initialVelocity = JUMP_STRENGTH; // Make the bird flap by adjusting its velocity
            } 
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

