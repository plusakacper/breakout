import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Paddle extends Sprite  {
    private int dX;
    public Paddle() {
        initPaddle();
    }
    private void initPaddle() {
        loadImage();
        getImageDimensions();
        resetState();
    }
    private void loadImage() {
        var imageIcon = new ImageIcon("resources/paddle.png");
        image = imageIcon.getImage();
    }
    void move() {      //only x updated because paddle moves only in horizontal direction
        x += dX;
        if (x <= 0) {
            x = 0;
        }
        if (x >= Commons.WIDTH - imageWidth) {     //ensure paddle doesn't pass window edges.
            x = Commons.WIDTH - imageWidth;
        }
    }
    void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            dX = -1;
        }
        if (key == KeyEvent.VK_RIGHT) {
            dX = 1;
        }
    }
    void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            dX = 0;
        }
        if (key == KeyEvent.VK_RIGHT) {
            dX = 0;
        }
    }
    private void resetState() {
        x = Commons.INIT_PADDLE_X;
        y = Commons.INIT_PADDLE_Y;
    }
}