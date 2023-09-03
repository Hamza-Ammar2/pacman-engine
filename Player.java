import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Player {
    Vector position;
    Vector velocity;
    int size = (Game.tileSize);
    int drawnsize = (size*3)/2;
    int speed = 4;

    BufferedImage images;
    BufferedImage image;
    int i = 0;
    int j = 0;
    float counter = 0.0f;
    Game game;
    int h = Game.ph;
    int w = Game.pw;

    Player(BufferedImage images, Game game) {
        position = new Vector(8*Game.tileSize + Game.tileSize/2, Game.tileSize*20 + Game.tileSize/2);
        velocity = new Vector();
        this.images = images;
        this.game = game;
    }

    public void draw(Graphics g) {
        g.drawImage(image, position.x - drawnsize/2, position.y - drawnsize/2, drawnsize, drawnsize, null);
    }

    private void chooseImage(int dx, int dy) {
        if (dy == 0 && dx < 0)
            i = 0;
        else if (dy == 0 && dx > 0)
            i = 1;
        else if (dy > 0 && dx == 0)
            i = 2;
        else if (dy < 0 && dx == 0)
            i = 3;
        
        if (counter > 0.1) {
            counter = 0;
            j++;
            if (j > 1)
                j = 0;
        }
        image = images.getSubimage(j*w, i*h, w, h);
    }

    public void update() {
        int dx = position.x;
        int dy = position.y;

        position.x += velocity.x;
        position.y += velocity.y;

        game.collide(position, velocity, size);
        dx -= position.x;
        dy -= position.y;

        chooseImage(dx, dy);
        counter += 1/60f;
    }

    public void changeVel(boolean isVertical, boolean isPositive, boolean isPressed) {
        if (isPressed) {
            int factor = isPositive ? speed : -speed;
            if (isVertical){
                velocity.y = factor;
                //velocity.x = 0;
            }else {
                velocity.x = factor;
                //velocity.y = 0;
            }
            return;
        }

        if (isVertical){
            if ((velocity.y > 0) == isPositive)
                velocity.y = 0;
        } else {
            if ((velocity.x > 0) == isPositive)
                velocity.x = 0;
        }
    }
}
