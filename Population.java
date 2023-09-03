import java.awt.Graphics;

public class Population {
    static final double MUTATION_RATE = 0.1;
    static final int population_size = 20;
    static final double CROSS_OVER_RATE = 0.8;
    Game[] games = new Game[population_size];
    int output_size = 4;
    int input_size = 6;
    Game bestGame;
    int alive = population_size;
    int total_score;
    int maxi = 0;

    Population() {
        for (int i = 0; i < population_size; i++) {
            games[i] = new Game();
            //games[i].network = new Network(input_size, output_size);
            games[i].population = this;
        }
        //calcTotalScore();
    }  


    private void calcTotalScore() {
        int t = 0;
        for (int i = 0; i < Game.numy; i++) {
            for (int j = 0; j < Game.numx; j++) {
                if (Level.map[i][j] == 0)
                    t += 1;
            }
        }
        t += 49*4;
        total_score = t; 
    }

    public void draw(Graphics g) {
        if (Math.random() < 0.01) maxi = (int) (Math.random()*population_size);

        if (maxi != -1)
            games[maxi].draw(g);
    }



    private void nextGen() {
        //int count = 0;
        //Game pop[] = new Game[population_size];

        for (int i = 0; i < population_size; i++) {
            /*if (Math.random() < CROSS_OVER_RATE) continue;
            int i1 = select();
            int i2 = select();

            cross(games[i1].network, games[i2].network);
            pop[count] = games[i1];
            pop[count+1] = games[i2];
            count++;*/
            games[i].network = new Network(bestGame.network);
        }
        /*for (int i = count; i < population_size; i++) {
            pop[i] = new Game();
            pop[i].population = this;
        }*/
        //games = pop;

        bestGame = null;
        for (Game game : games) {
            game.reset();
            //if (Math.random() < MUTATION_RATE)
            game.network.mutate();
        }
    }


    private void cross(Network n1, Network n2) {
        for (int i = 0; i < n1.weights.length; i++) {
            for (int k = 0; k < n1.weights[i].shape[0]; k++) {
                int start = (int) (n1.weights[i].shape[1]*Math.random());
                
                for (int l = start; l < n1.weights[i].shape[1]; l++) {
                    float r = n1.weights[i].mat[k][l];
                    n1.weights[i].mat[k][l] = n2.weights[i].mat[k][l];
                    n2.weights[i].mat[k][l] = r;
                }
            } 

            int start = (int) (n1.biases[i].l*Math.random());
            for (int k = start; k < n1.biases[i].l; k++) {
                float r = n1.biases[i].v[k];
                n1.biases[i].v[k] = n2.biases[i].v[k];
                n2.biases[i].v[k] = r;
            } 
        }
    }



    private int select() {
        double slice = Math.random()*total_score;
        double cftotal = 0;
        for (int i = 0; i < population_size; i++) {
            cftotal += games[i].score;
            if (cftotal > slice) {
                return i;
            }
        }
        return (int) (population_size*Math.random());
    }



    public void update() {
        int score = -1;
        //bestGame = null;
        maxi = -1;

        for (int i = 0; i < population_size; i++) {
            if (games[i].end) continue;

            games[i].update();
            /*if (games[i].score > score) {
                score = games[i].score;
                bestGame = games[i];
                maxi = i;
            }*/   
        }

        for (int i = 0; i < population_size; i++) {
            if (games[i].end) continue;

            //games[i].update();
            //System.out.println(games[i].score);
            if (games[i].score >= score) {
                score = games[i].score;
                bestGame = games[i];
                maxi = i;
                //System.out.println(i);
                //System.out.println(maxi);
                //System.out.println("\n");
            }   
        }

        if (Math.random() < 0.01) {
            //System.out.println(score);
        }
        if (alive == 0 | maxi == -1) {
            alive = population_size;
            nextGen();
            //System.out.println("nextgen");
            //System.out.println(games[maxi].score);
            System.out.println("nextgen\n");
        }
    }
}
