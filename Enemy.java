import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Enemy {
    Vector position;
    Vector velocity;
    int size = Game.tileSize;
    int drawnsize = (size*3)/2;
    int speed = 3;
    Pathfinder pathfinder;
    Game game;
    char type;
    Color color;
    static final int inc = 4;

    BufferedImage images;
    BufferedImage image;
    boolean dead = false;

    int i = 0;
    int j = 0;
    float counter = 0.0f;
    int h = Game.eh;
    int w = Game.ew;
    float deathCount = 0.0f;
    int deathSeconds = 5;
    int deathSec = 5;
    int[] id;

    Enemy(int i, int j, char type, BufferedImage images, Game game) {
        position = new Vector(j*Game.tileSize + Game.tileSize/2, i*Game.tileSize + Game.tileSize/2);
        velocity = new Vector();
        pathfinder = new Pathfinder();
        this.game = game;
        this.type = type;
        this.images = images;
        id = new int[]{i, j};    
    }


    public int[] getID() {
        return new int[]{position.y/Game.tileSize, position.x/Game.tileSize};
    }



    private void chooseImage() {
        if (velocity.x > 0){ 
            i = 0;
        } else if (velocity.x < 0){
            i = 1;
        } else if (velocity.y < 0){
            i = 2;
        } else if (velocity.y > 0){
            i = 3;
        }
        if (counter > 0.5) {
            counter = 0;
            j++;
            if (j > 1)
                j = 0;
        }
        int x = i == 0 ? 0 : i == 1 ? w + (w - 5) : i == 2 ? w + (w - 5)*3 : i == 3 ? w + (w - 5)*5 : 0;
        int width = i == 0 && j == 0 ? w : w - 5;
        x += width*j;
        if (j == 1 && i == 0)
            x = w ;

        image = images.getSubimage(x, 0, width, h);
    }


    public void draw(Graphics g) {
        //g.setColor(color);
        if (dead) return;
        chooseImage();
        g.drawImage(image, position.x - drawnsize/2, position.y - drawnsize/2, drawnsize, drawnsize, null);
    }

    public void update() {
        if (dead) {
            deathCount += 1/60f;
            if (deathCount > 1) {
                deathCount = 0;
                deathSeconds--;
                if (deathSeconds == 0) {
                    deathSeconds = deathSec;
                    dead = false;
                    position = new Vector(id[1]*Game.tileSize + Game.tileSize/2, id[0]*Game.tileSize + Game.tileSize/2);
                }
            }
        }

        position.x += velocity.x;
        position.y += velocity.y;

        counter += 1/60f;

        int[] start = {position.y/Game.tileSize, position.x/Game.tileSize};
        int[] end = {game.player.position.y/Game.tileSize, game.player.position.x/Game.tileSize};
        if (distToPlayer() > inc + 1) {
            switch(type) {
                case 'u':
                    if (isValid(new int[]{end[0] - inc - 2, end[1]}))
                        end[0] -= (inc + 2);
                    break;
                case 'd':
                    if (isValid(new int[]{end[0] + 2 + inc, end[1]}))
                        end[0] += inc + 2;
                    break;
                case 'r':
                    if (isValid(new int[]{end[0], end[1] + inc + 2}))
                        end[1] += inc + 2;
                    break;
                case 'l':
                    if (isValid(new int[]{end[0], end[1] - inc - 2}))
                        end[1] -= (inc + 2);
                    break;
            }
        }
        if (game.paced)
            end = runaway(start, end);

        updateVelocity(pathfinder.findPath(start, end));
    }


    public int distToPlayer() {
        int[] me = {position.y/Game.tileSize, position.x/Game.tileSize};
        int[] pl = {game.player.position.y/Game.tileSize, game.player.position.x/Game.tileSize};
        
        int x = me[1] - pl[1] > 0 ? me[1] - pl[1] : pl[1] - me[1];
        int y = me[0] - pl[0] > 0 ? me[0] - pl[0] : pl[0] - me[0];
        
        return x + y;
    }



    public float[] dxdy() {
        float[] me = {position.y, position.x};
        float[] pl = {game.player.position.y, game.player.position.x};
        
        float x = me[1] - pl[1];
        float y = me[0] - pl[0];
        //float l = (float) Math.sqrt(x*x + y*y);
        
        return new float[]{  -1000 / x , -1 / y };
    }



    private void updateVelocity(char c) {
        switch(c) {
            case 'L':
                velocity.x = -speed;
                velocity.y = 0;
                break;
            case 'R':
                velocity.x = speed;
                velocity.y = 0;
                break;
            case 'U':
                velocity.y = -speed;
                velocity.x = 0;
                break;
            case 'D':
                velocity.y = speed;
                velocity.x = 0;
                break;
        }
    }


    private int[] runaway(int[] start, int[] end) {
        int[] n = {Game.numy - 1 - end[0], Game.numx - 1 - end[1]};
        int x = n[1];
        int y = n[0];

        int count = 1;
        boolean found = false;
        while(!isValid(n)) {
            for (int i = -count; i < count + 1; i++) {
                for (int j = -count; j < count + 1; j++) {
                    if (!isValid(new int[]{y + i, x + j})) continue;

                    n[0] = y + i;
                    n[1] = x + j;
                    found = true;
                    break;
                }
                if (found) break;
            }
            if (found) break;
            count++;
        }
        

        return n;
    }


    private boolean isValid(int[] id) {
        if (id[0] < 0 | id[0] > Game.numy - 1 | id[1] < 0 | id[1] > Game.numx - 1)
            return false;
        else if (Level.map[id[0]][id[1]] != 0)
            return false;
        return true;
    }


    public boolean isColliding(Vector position, int size) {
        if (position.x + size/2 > this.position.x - this.size/2 && position.x - size/2 < this.position.x + this.size/2
        && position.y + size/2 > this.position.y - this.size/2 && position.y - size/2 < this.position.y + this.size/2){
            if (game.paced){
                dead = true;
                game.score += 100;
            }
            return true;
        }
        return false;
    }
}
