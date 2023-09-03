class Matrix {
    int shape[];
    float mat[][];
    Matrix(int r, int c) {
        shape = new int[]{r, c};
        mat = new float[r][c];

        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                mat[i][j] = 0;
            }
        }
    }

    Matrix(Matrix matrix) {
        this.shape = new int[]{matrix.shape[0], matrix.shape[1]};
        mat = new float[shape[0]][shape[1]];

        for (int i = 0; i < shape[0]; i++) {
            for (int j = 0; j < shape[1]; j++) {
                mat[i][j] = matrix.mat[i][j];
            }
        }
    }

    Matrix add(Matrix matrix) {
        if (!(shape[0] == matrix.shape[0] && shape[1] == matrix.shape[1])) return null;

        Matrix newmat = new Matrix(shape[0], shape[1]);
        for (int i = 0; i < shape[0]; i++) {
            for (int j = 0; j < shape[1]; j++) {
                newmat.mat[i][j] = mat[i][j] + matrix.mat[i][j];
            }
        } 

        return newmat;
    }

    void random() {
        for (int i = 0; i < shape[0]; i++) {
            for (int j = 0; j < shape[1]; j++) {
                mat[i][j] = (float) Math.random();
            }
        } 
    }


    Matrix sub(Matrix matrix) {
        if (!(shape[0] == matrix.shape[0] && shape[1] == matrix.shape[1])) return null;

        Matrix newmat = new Matrix(shape[0], shape[1]);
        for (int i = 0; i < shape[0]; i++) {
            for (int j = 0; j < shape[1]; j++) {
                newmat.mat[i][j] = mat[i][j] - matrix.mat[i][j];
            }
        } 

        return newmat;
    }

    Matrix scale(float s) {
        Matrix newmat = new Matrix(shape[0], shape[1]);
        for (int i = 0; i < shape[0]; i++) {
            for (int j = 0; j < shape[1]; j++) {
                newmat.mat[i][j] = mat[i][j]*s;
            }
        } 

        return newmat;
    }


    Vec dot(Vec vec) {
        if (vec.l != shape[1]) return null;
        Vec newvec = new Vec(shape[0]);
        for (int i = 0; i < shape[0]; i++) {
            float f = 0;

            for (int j = 0; j < shape[1]; j++) {
                f += mat[i][j]*vec.v[j];
            }
            newvec.v[i] = f;
        }

        return newvec;
    }
}