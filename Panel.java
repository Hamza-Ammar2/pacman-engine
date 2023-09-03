import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyAdapter;
//import java.awt.event.KeyEvent;

public class Panel extends JPanel implements Runnable {
    Thread gameThread;

    Image image;
    Graphics graphics;
    //Game game = new Game();
    Population population = new Population();

    Panel() {
        this.addKeyListener(new KeyControl());
        this.setVisible(true);
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT));

        gameThread = new Thread(this);
        gameThread.start();
    }

    private void draw(Graphics g) {
        //game.draw(g);
        population.draw(g);
    }


    public void paint(Graphics g) {
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image, 0, 0, this);
    }


    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta < 1) continue;
            delta--;

            population.update();
            //game.update();
            repaint();
        }
    }


    class KeyControl extends KeyAdapter {
        /*@Override
        public void keyPressed(KeyEvent e) {
            int c = e.getKeyCode();
            switch(c) {
                case KeyEvent.VK_UP:
                    game.player.changeVel(true,false, true);
                    break;
                case KeyEvent.VK_DOWN:
                    game.player.changeVel(true,true, true);
                    break;
                case KeyEvent.VK_RIGHT:
                    game.player.changeVel(false,true, true);
                    break;
                case KeyEvent.VK_LEFT:
                    game.player.changeVel(false,false, true);
                    break;
            }
        }


        @Override
        public void keyReleased(KeyEvent e) {
            int c = e.getKeyCode();
            switch(c) {
                case KeyEvent.VK_UP:
                    game.player.changeVel(true,false, false);
                    break;
                case KeyEvent.VK_DOWN:
                    game.player.changeVel(true,true, false);
                    break;
                case KeyEvent.VK_RIGHT:
                    game.player.changeVel(false,true, false);
                    break;
                case KeyEvent.VK_LEFT:
                    game.player.changeVel(false,false, false);
                    break;
            }
        }*/
    }
}