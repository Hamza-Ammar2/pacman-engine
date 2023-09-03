public class Network {
    Matrix weights[];
    Vec biases[];
    int sizes[];

    Network(int sizes[]) {
        this.sizes = sizes;
        weights = new Matrix[sizes.length-1];
        biases = new Vec[sizes.length-1];

        for (int i = 0; i < sizes.length - 1; i++) {
            Matrix matrix = new Matrix(sizes[i+1], sizes[i]);
            matrix.random();

            Vec vec = new Vec(sizes[i+1]);
            vec.random();
            weights[i] = matrix;
            biases[i] = vec;
        }
    }

    Network(Network network) {
        this.sizes = network.sizes;
        weights = new Matrix[sizes.length-1];
        biases = new Vec[sizes.length-1];

        for (int i = 0; i < sizes.length - 1; i++) {
            Matrix matrix = new Matrix(network.weights[i]);
            //matrix.random();

            Vec vec = new Vec(network.biases[i]);
            //vec.random();
            weights[i] = matrix;
            biases[i] = vec;
        }
    }


    int out(float[] inputs) {
        Vec inps = new Vec(inputs);
        float max = -99999999;
        int index = -1;

        for (int i = 0; i < sizes.length - 1; i++) {
            inps = weights[i].dot(inps).add(biases[i]);
            //inps.func();
        }

        for (int i = 0; i < inps.l; i++) {
            if (inps.v[i] > max) {
                max = inps.v[i];
                index = i;
            }
        }
        //System.out.println(inps.l);
        //if (max > 0.8) return -1;
        return index;
    }

    void mutate() {
        for (int i = 0; i < weights.length; i++) {
            for (int k = 0; k < weights[i].shape[0]; k++) {
                for (int l = 0; l < weights[i].shape[1]; l++) {
                    if (Math.random() < Population.MUTATION_RATE)
                        weights[i].mat[k][l] = (float) Math.random();
                }

                if (Math.random() < Population.MUTATION_RATE)
                    biases[i].v[k] = (float) Math.random();
            } 
        }
    }
}
