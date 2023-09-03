public class Vec {
    float v[];
    int l;

    Vec(int l) {
        v = new float[l];
        this.l = l;

        for (int i = 0; i < l; i++) {
            v[i] = 0;
        }
    }

    Vec(float[] v) {
        this.v = v;
        this.l = v.length;
    }

    Vec(Vec vec) {
        v = new float[vec.l];
        this.l = vec.l;

        for (int i = 0; i < l; i++) {
            v[i] = vec.v[i];
        }
    }

    void random() {
        for (int i = 0; i < l; i++) {
            v[i] = (float) Math.random();
        }
    }

    void func() {
        for (int i = 0; i < l; i++) {
            //v[i] = (float) (1.0 / (1.0 + Math.exp(-v[i])));
            v[i] = v[i] > 0 ? v[i] : 0;
        }
    }


    Vec add(Vec vec) {
        if (vec.l != l) return null;
        Vec newvec = new Vec(l);
        for (int i = 0; i < l; i++) {
            newvec.v[i] = v[i] + vec.v[i];
        }

        return newvec;
    }

    Vec sub(Vec vec) {
        if (vec.l != l) return null;
        Vec newvec = new Vec(l);
        for (int i = 0; i < l; i++) {
            newvec.v[i] = v[i] - vec.v[i];
        }

        return newvec;
    }

    float dot(Vec vec) {
        if (vec.l != l) {
            System.out.println("dot failed vector");
            return -2;
        }
        float res = 0;
        for (int i = 0; i < l; i++) {
            res += v[i]*vec.v[i];
        }

        return res;
    }
}
