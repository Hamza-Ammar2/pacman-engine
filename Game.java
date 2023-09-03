import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Game {
    static final int numx = 28;
    static final int numy = 31;
    static final int tileSize = 20;
    static final int SCREEN_WIDTH = numx*tileSize;
    static final int SCREEN_HEIGHT = numy*tileSize;
    static final char[] moves = {'U', 'D', 'L', 'R', 'S'};
    char m;

    int[][] map = Level.map;
    BufferedImage images;
    Tile[][] tiles = new Tile[numy][numx];
    int h = 8;
    int w = 8;
    int shift = 680/3;
    Player player;
    Enemy[] enemies = new Enemy[0];

    int score = 0;
    static final int ph = 16;
    static final int pw = 18;

    static final int ew = 21;
    static final int eh = 16;
    boolean paced = false;
    float paceCount = 0.0f;
    int paceSeconds = 8;
    int paceAmount = 8;
    float time = 0;
    int life = 5;

    boolean end = false;
    Network network = new Network(new int[]{8, 17, 5});
    Population population;
    int X, Y;

    Game() {
        try {
            images = ImageIO.read(new File("assets/sprites.png"));
        } catch (IOException e) {
        }
        createLevel();
        player = new Player(removeBlack(images.getSubimage(shift*2, 0, pw*2, ph*4)), this);
        player.velocity.x = player.speed;
        player.velocity.y = 0;
        /*enemies[0] = new Enemy(11, 10, 'g', 
            removeBlack(images.getSubimage(shift*2, ph*4, ew*8, eh))
        , this);
        enemies[1] = new Enemy(11, 12, 'd', 
            removeBlack(images.getSubimage(shift*2, ph*4 + eh, ew*8, eh))
            , this);
        enemies[2] = new Enemy(11, 15, 'r', 
            removeBlack(images.getSubimage(shift*2, ph*4 + eh*2, ew*8, eh))
        , this);
        enemies[3] = new Enemy(11, 17, 'l', 
            removeBlack(images.getSubimage(shift*2, ph*4 + eh*3, ew*8, eh))
        , this);*/
    }


    public void reset() {
        end = false;
        score = 0;
        createLevel();
        player = new Player(removeBlack(images.getSubimage(shift*2, 0, pw*2, ph*4)), this);
        player.velocity.x = player.speed;
        player.velocity.y = 0;
        /*enemies[0] = new Enemy(11, 10, 'g', 
            removeBlack(images.getSubimage(shift*2, ph*4, ew*8, eh))
        , this);
        enemies[1] = new Enemy(11, 12, 'd', 
            removeBlack(images.getSubimage(shift*2, ph*4 + eh, ew*8, eh))
            , this);
        enemies[2] = new Enemy(11, 15, 'r', 
            removeBlack(images.getSubimage(shift*2, ph*4 + eh*2, ew*8, eh))
        , this);
        enemies[3] = new Enemy(11, 17, 'l', 
            removeBlack(images.getSubimage(shift*2, ph*4 + eh*3, ew*8, eh))
        , this);*/
    }


    private void isHurt() {
        for (Enemy enemy : enemies)
            if (enemy.isColliding(player.position, player.size)){
                if (!paced){
                    end = true;
                    population.alive--;
                }
            }
    }


    private BufferedImage removeBlack(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x <  width; x++) {
                Color color = new Color(image.getRGB(x, y), true);
                if (color.getBlue() < 10 && color.getRed() < 10 && color.getGreen() < 10) 
                    color = new Color(0, 0, 0, 0);

                newImage.setRGB(x, y, color.getRGB());
            }
        }

        return newImage;
    }


    private void collect() {
        int i = player.position.y/tileSize;
        int j = player.position.x/tileSize;
        if (j > numx - 1) j = numx - 1;
        if (i > numy - 1) i = numy - 1;

        tiles[i][j].collect();
    }


    private void createLevel() {
        for (int i = 0; i < numy; i++) {
            for (int j = 0; j < numx; j++) {
                Tile tile = new Tile(i, j);
                if (map[i][j] != 1)
                    tile.type = true;

                tiles[i][j] = tile;
            }
        }

        tiles[3][1].pacer = true;
        tiles[3][numx - 2].pacer = true;
        tiles[23][1].pacer = true;
        tiles[23][numx - 2].pacer = true;
    }


    public void collide(Vector position, Vector velocity, int size) {
        int oldx = (position.x)/tileSize;
        int oldy = (position.y)/tileSize;

        int forx = (position.x + size/2)/tileSize;
        int backx = (position.x - size/2)/tileSize;
        int up = (position.y - size/2)/tileSize;
        int down = (position.y + size/2)/tileSize;

        if (map[up][oldx] == 1 | map[down][oldx] == 1)
            position.y = oldy*tileSize + tileSize/2;
        if (map[oldy][forx] == 1 | map[oldy][backx] == 1)
            position.x = oldx*tileSize + tileSize/2;
        if (map[oldy][oldx] == 1) {
            int fx = velocity.x > 0 ? -1 : velocity.x < 0 ? 1 : 0;
            int fy = velocity.y > 0 ? -1 : velocity.y < 0 ? 1 : 0;

            position.x = (oldx + fx)*Game.tileSize + Game.tileSize/2;
            position.y = (oldy + fy)*Game.tileSize + Game.tileSize/2;
        }
    }


    private void aiMove() {
        int x = player.position.x/tileSize;
        int y = player.position.y/tileSize;
        if (x > numx - 1) x = numx - 1;
        if (y > numy - 1) y = numy - 1; 

        int fo = x + 1 < numx - 1 ? x + 1 : x;
        int ba = x - 1 > 0 ? x - 1 : x;
        int up = y + 1 < numy - 1 ? y + 1 : y;
        int dow = y - 1 > 0 ? y - 1 : y;

        float dists[] = distToWall();

        /*if (map[y][x] != 2) return;
        if (x == X && y == Y) {
            //char h = moves[(int) (Math.random()*4)];
            //face(h);
            return;
        }
        X = x;
        Y = y;*/
        int things[] = new int[4];
        switch(player.i) {
            case 0:
                things = new int[]{
                    tiles[y][fo].getScore(),
                    tiles[y][ba].getScore(),
                    tiles[up][x].getScore(),
                    tiles[dow][x].getScore(),
                };
                break;
            case 1:
                things = new int[]{
                    tiles[y][fo].getScore(),
                    tiles[y][ba].getScore(),
                    tiles[up][x].getScore(),
                    tiles[dow][x].getScore(),
                };
                break;
            case 2:
                things = new int[]{
                    tiles[up][x].getScore(),
                    tiles[dow][x].getScore(),
                    tiles[y][fo].getScore(),
                    tiles[y][ba].getScore(),
                };
                break;
            case 3:
                things = new int[]{
                    tiles[up][x].getScore(),
                    tiles[dow][x].getScore(),
                    tiles[y][fo].getScore(),
                    tiles[y][ba].getScore(),
                };
                break;
        }


        float[] inputs = {
            //(enemies[0].distToPlayer())/((enemies[0].dxdy()[1]) + 1),
            //((enemies[0].dxdy()[1])),
            //((enemies[0].dxdy()[0]))
            
            /*tiles[y][fo].getScore(),
            tiles[y][ba].getScore(),
            tiles[up][x].getScore(),
            tiles[dow][x].getScore(),*/
            things[0], things[1], things[2], things[3],
            player.velocity.x,
            player.velocity.y,
            x, y, //dists[0], dists[1], dists[2], dists[3]
            //tiles[y][x].dirs[0], 
            //tiles[y][x].dirs[1], 
            //tiles[y][x].dirs[2], 
            //tiles[y][x].dirs[3]
            /*((enemies[1].dxdy()[1])),
            ((enemies[1].dxdy()[0])),
            ((enemies[2].dxdy()[1])),
            ((enemies[2].dxdy()[0])),*/
        };

        int index = network.out(inputs);
        if (index == -1) {
            //System.out.println("Huh");
            return;}

        char move = moves[index];
        //movePlayer(move);
        m = move;
        face(move);
        /*if (x == X && y == Y) return;
        X = x;
        Y = y;
        player.position.x = x*tileSize + tileSize/2;
        player.position.y = y*tileSize + tileSize/2;*/
    }



    float[] distToWall() {
        float dist[] = new float[4];
        boolean blocks[] = {false, false, false, false};
        int x = player.position.x / tileSize;
        int y = player.position.y / tileSize;

        for (int i = 1; i < Math.max(numx, numy) - 1; i++) {
            int[] forw = {y, x + i};
            int[] backw = {y, x - i};
            int[] up = {y - i, x};
            int[] down = {y + i, x};

            int[][] poses = {forw, backw, up, down};
            for (int j = 0; j < 4; j++) {
                if (blocks[j]) continue;
                if (map[poses[j][0]][poses[j][1]] == 0) continue;

                blocks[j] = true;
                dist[j] = (float) i*tileSize;
                dist[j] = 1 / dist[j];
                if (dist[j] < 0) dist[j] *= -1;
                //System.out.println(dist[j]);
            }
        }

        return dist;
    }




    void face(char c) {
        //int v;
        //movePlayer(c);

        switch(c) {
            case 'S':
                switch(player.i) {
                    case 0:
                        player.velocity.x = player.speed;
                        player.velocity.y = 0;
                        break;
                    case 1:
                        player.velocity.x = -player.speed;
                        player.velocity.y = 0;
                        break;
                    case 2:
                        player.velocity.x = 0;
                        player.velocity.y = -player.speed;
                        break;
                    case 3:
                        player.velocity.x = 0;
                        player.velocity.y = player.speed;
                        break;
                }
                break;

            case 'R':
                player.velocity.x = player.i == 0 | player.i == 1 ? player.speed : player.velocity.x; 
                player.velocity.y = player.i == 2 ? player.speed : player.i == 3 ? -player.speed : player.velocity.y;
                break;
            case 'L':
                player.velocity.x = player.i == 0 | player.i == 1 ? -player.speed : player.velocity.x; 
                player.velocity.y = player.i == 2 ? -player.speed : player.i == 3 ? player.speed : player.velocity.y;
                break;
            case 'U':
                player.velocity.y = player.i == 0 | player.i == 1 ? -player.speed : player.velocity.y; 
                player.velocity.x = player.i == 2 ? -player.speed : player.i == 3 ? player.speed : player.velocity.x;
                break;
            case 'D':
                player.velocity.y = player.i == 0 | player.i == 1 ? player.speed : player.velocity.y; 
                player.velocity.x = player.i == 2 ? player.speed : player.i == 3 ? -player.speed : player.velocity.x;
                break;
        }
    }


    private void movePlayer(char c) {
        switch(c) {
            case 'L':
                player.velocity.x = -player.speed;
                //player.velocity.y = 0;
                break;
            case 'R':
                player.velocity.x = player.speed;
                //player.velocity.y = 0;
                break;
            case 'U':
                player.velocity.y = -player.speed;
                //player.velocity.x = 0;
                break;
            case 'D':
                player.velocity.y = player.speed;
                //player.velocity.x = 0;
                break;
        }
    }



    public void update() {
        time += 1 / 60.0f;
        if (time >= life) {
            end = true;
            population.alive--;
            time = 0;
        }

        if (paced) {
            paceCount += 1/60f;
            if (paceCount > 1) {
                paceCount = 0;
                paceSeconds--;
                if (paceSeconds == 0) {
                    paced = false;
                    paceSeconds = paceAmount;
                }
            }
        }

        collect();
        player.update();
        aiMove();
        for (Enemy enemy : enemies) {
            enemy.update();
            collide(enemy.position, enemy.velocity, enemy.size);
        }
        isHurt();
    }

    public void draw(Graphics g) {
        for (Tile[] row : tiles)
            for (Tile tile : row)
                if (tile != null) tile.draw(g);
        for (Enemy enemy : enemies)
            enemy.draw(g);
        player.draw(g);
        System.out.println(m);
    }


    class Tile {
        BufferedImage image;
        int x;
        int y;
        boolean type = false;
        boolean collected = false;
        int[] id;
        boolean pacer = false;
        //int score;
        int[] dirs;
        boolean huh = false;

        Tile(int i, int j) {
            x = j*tileSize;
            y = i*tileSize;
            id = new int[]{i, j};

            image = images.getSubimage(j*w, i*h, w, h);
            if (map[i][j] == 2) {
                huh = true;
                dirs = new int[4];

                int[] forw = {i, j + 1};
                int[] backw = {i, j - 1};
                int[] up = {i - 1, j};
                int[] down = {i + 1, j};

                int[][] poses = {up, down, backw, forw};
                for (int k = 0; k < 4; k++) {
                    if (map[poses[k][0]][poses[k][1]] == 0) {
                        dirs[k] = 1;
                        continue;
                    }
                    dirs[k] = 0;
                }
            }
        }

        public void draw(Graphics g) {
            if (huh) {
                g.setColor(Color.RED);
                g.fillRect(x, y, tileSize, tileSize);
                return;
            }

            g.drawImage(image, x, y, tileSize, tileSize, null);
        }

        public void collect() {
            if (!type | collected) return;
            if (pacer) {
                //paced = true;
                score += 50;
            }
            score += 5;
            collected = true;
            image = images.getSubimage(shift + id[1]*w, id[0]*h, w, h);
            time = 0;
            //System.out.println(score);
        }

        public int getScore() {
            for (Enemy enemy : enemies) {
                int[] ID = enemy.getID();
                if (ID[0] == id[0] && ID[1] == id[1])
                    return -1;
            }

            if (type && !collected) {
                return 30;
            } else if (!type) {
                return -1;
            } else {
                return (int) (Math.random()*10);
            }
        }
    }
}
