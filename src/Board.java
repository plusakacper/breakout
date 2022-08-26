import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Board extends JPanel {      //game logic
    private Timer timer;
    private String gameMessage = "Game over!";
    private Ball ball;
    private Paddle paddle;
    private Brick[] bricks;
    private boolean inGame = true;
    private boolean ifWon;
    private int destroyedBricksCounter = 0;
    private long start;
    private long end;
    private LocalDate myDate;
    private LocalTime myTime;
    public Board() {
        initBoard();
    }
    private void initBoard() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        setPreferredSize(new Dimension(Commons.WIDTH, Commons.HEIGHT));
        gameInit();
    }
    private void gameInit() {
        bricks = new Brick[Commons.NUMBER_OF_BRICKS];
        ball = new Ball();
        paddle = new Paddle();
        int k = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                bricks[k] = new Brick(j * 40 + 30, i * 10 + 50);
                k++;
            }
        }
        timer = new Timer(Commons.PERIOD, new GameCycle());
        timer.start();     //timer
        start = System.currentTimeMillis();     //counting gameplay time
        myDate = LocalDate.now();      //date
        myTime = LocalTime.now();
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        var g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        if (inGame) {
            drawObjects(g2D);
        } else {
            gameFinished(g2D);
        }
        Toolkit.getDefaultToolkit().sync();
    }
    private void drawObjects(Graphics2D g2D) {
        g2D.drawImage(ball.getImage(), ball.getX(), ball.getY(),
                ball.getImageWidth(), ball.getImageHeight(), this);
        g2D.drawImage(paddle.getImage(), paddle.getX(), paddle.getY(),
                paddle.getImageWidth(), paddle.getImageHeight(), this);
        for (int i = 0; i < Commons.NUMBER_OF_BRICKS; i++) {
            if (!bricks[i].isDestroyed()) {
                g2D.drawImage(bricks[i].getImage(), bricks[i].getX(),
                        bricks[i].getY(), bricks[i].getImageWidth(),
                        bricks[i].getImageHeight(), this);
            }
        }
    }
    private void saveResults() {       //writing to file
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        String formattedDate = myDate.format(dateFormat);
        String formattedTime = myTime.format(timeFormat);
        double total = (double)(end - start) / 1000;
        int remainingBricks = Commons.NUMBER_OF_BRICKS - destroyedBricksCounter;
        try {
            FileWriter fileWriter = new FileWriter("results.txt");
            fileWriter.write("\"BREAKOUT\" Game\nDate: " + formattedDate + ", " + formattedTime +
                            "\n\nSummary:\nGame ended in " + total + "s\nYou ");
            if (ifWon) {
                fileWriter.write("won!");
            }
            if (!ifWon) {
                fileWriter.write("lost!");
            }
            fileWriter.write("\nScored points: " + destroyedBricksCounter +
                    "\nRemaining bricks: " + remainingBricks);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("There has been a problem with your file!");
            e.printStackTrace();
        }
    }
    private void gameFinished(Graphics2D g2D) {
        var messageFont = new Font("Verdana", Font.BOLD, 18);
        var scoreFont = new Font("Verdana", Font.BOLD, 13);
        FontMetrics fontMetrics = this.getFontMetrics(messageFont);
        g2D.setColor(Color.BLACK);
        g2D.setFont(messageFont);
        g2D.drawString(gameMessage,
                (Commons.WIDTH - fontMetrics.stringWidth(gameMessage)) / 2,
                Commons.WIDTH / 2);
        g2D.setFont(scoreFont);
        String scoreMessage = "Your score has been saved to a file.";
        g2D.drawString(scoreMessage,
                (Commons.WIDTH - fontMetrics.stringWidth(gameMessage)) / 2 - 67,
                Commons.WIDTH / 2 + 30);
        saveResults();
    }
    private class TAdapter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            paddle.keyReleased(e);
        }
        @Override
        public void keyPressed(KeyEvent e) {
            paddle.keyPressed(e);
        }
    }
    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }
    private void doGameCycle() {
        ball.move();
        paddle.move();
        checkCollision();
        repaint();
    }
    private void stopGame() {
        inGame = false;
        timer.stop();
        end = System.currentTimeMillis();
    }
    private void checkCollision() {
        if (ball.getRect().getMaxY() > Commons.BOTTOM_EDGE) {     //ball hits bottom - game over
            stopGame();
        }
        for (int i = 0, j = 0; i < Commons.NUMBER_OF_BRICKS; i++) {
            if (bricks[i].isDestroyed()) {      //counting destroyed bricks
                j++;
                destroyedBricksCounter = j;
            }
            if (j == Commons.NUMBER_OF_BRICKS) {     //every brick destroyed - game won
                gameMessage = "You won!";
                ifWon = true;
                stopGame();
            }
        }
        if ((ball.getRect()).intersects(paddle.getRect())) {
            int paddleLPos = (int) paddle.getRect().getMinX();
            int ballLPos = (int) ball.getRect().getMinX();
            int first = paddleLPos + 8;
            int second = paddleLPos + 16;
            int third = paddleLPos + 24;
            int fourth = paddleLPos + 32;
            if (ballLPos < first) {      //setting direction of ball depending on part of paddle it hit
                ball.setXDir(-1);
                ball.setYDir(-1);
            }
            if (ballLPos >= first && ballLPos < second) {
                ball.setXDir(-1);
                ball.setYDir(-1 * ball.getYDir());
            }
            if (ballLPos >= second && ballLPos < third) {
                ball.setXDir(0);
                ball.setYDir(-1);
            }
            if (ballLPos >= third && ballLPos < fourth) {
                ball.setXDir(1);
                ball.setYDir(-1 * ball.getYDir());
            }
            if (ballLPos > fourth) {
                ball.setXDir(1);
                ball.setYDir(-1);
            }
        }
        for (int i = 0; i < Commons.NUMBER_OF_BRICKS; i++) {
            if ((ball.getRect()).intersects(bricks[i].getRect())) {
                int ballLeft = (int) ball.getRect().getMinX();
                int ballHeight = (int) ball.getRect().getHeight();
                int ballWidth = (int) ball.getRect().getWidth();
                int ballTop = (int) ball.getRect().getMinY();
                var pointRight = new Point(ballLeft + ballWidth + 1, ballTop);
                var pointLeft = new Point(ballLeft - 1, ballTop);
                var pointTop = new Point(ballLeft, ballTop - 1);
                var pointBottom = new Point(ballLeft, ballTop + ballHeight + 1);
                if (!bricks[i].isDestroyed()) {
                    if (bricks[i].getRect().contains(pointRight)) {
                        ball.setXDir(-1);
                    } else if (bricks[i].getRect().contains(pointLeft)) {
                        ball.setXDir(1);
                    }
                    if (bricks[i].getRect().contains(pointTop)) {
                        ball.setYDir(1);
                    } else if (bricks[i].getRect().contains(pointBottom)) {
                        ball.setYDir(-1);
                    }
                    bricks[i].setDestroyed();
                }
            }
        }
    }
}