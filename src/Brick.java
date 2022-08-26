import javax.swing.ImageIcon;

public class Brick extends Sprite {
    private boolean destroyed;     // state of brick
    public Brick(int x, int y) {
        initBrick(x, y);
    }
    private void initBrick(int x, int y) {
        this.x = x;
        this.y = y;
        destroyed = false;
        loadImage();
        getImageDimensions();
    }
    private void loadImage() {
        var imageIcon = new ImageIcon("resources/brick.png");
        image = imageIcon.getImage();
    }
    boolean isDestroyed() {
        return destroyed;
    }
    void setDestroyed() {
        destroyed = true;
    }
}