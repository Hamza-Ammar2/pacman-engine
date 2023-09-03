import java.util.*;

public class Pathfinder {
    int numx = Game.numx;
    int numy = Game.numy;
    int[][] map = Level.map;
    List<String> que = new ArrayList<>();

    Pathfinder() {}

    public char findPath(int[] start, int[] end) {
        que.clear();
        char[] opts = {'U', 'D', 'L', 'R'};
        String add = "";
        que.add(add);
        while (!compareIDs(stringToID(add, start), end)) {
            add = que.get(0);
            que.remove(0);
            for (char c : opts) {
                String string = add + c;
                if (add.length() > 0) {
                    char last = add.charAt(add.length() - 1);
                    if ((c == 'L' && last == 'R') | (c == 'R' && last == 'L')) continue;
                    if ((c == 'U' && last == 'D') | (c == 'D' && last == 'U')) continue;
                }

                if (isValid(stringToID(string, start)))
                    que.add(string);
            }
            if (que.size() > 10000)
                break;
        }
        add = que.get(que.size() - 1);
        char r = add.length() > 0 ? add.charAt(0) : 's';
        return r;
    }


    private boolean compareIDs(int[] id, int[] id2) {
        if (id[0] == id2[0] && id[1] == id2[1])
            return true;
        return false;
    }


    private int[] stringToID(String string, int[] start) {
        int x = start[1];
        int y = start[0];
        for (int i = 0; i < string.length(); i++) {
            switch(string.charAt(i)) {
                case 'L':
                    x -= 1;
                    continue;
                case 'R':
                    x += 1;
                    continue;
                case 'U':
                    y -= 1;
                    continue;
                case 'D':
                    y += 1;
                    continue;
            }
        }

        return new int[]{y, x};
    }



    private boolean isValid(int[] id) {
        if (id[0] < 0 | id[0] > numy - 1 | id[1] < 0 | id[1] > numx - 1)
            return false;
        else if (map[id[0]][id[1]] != 0)
            return false;
        return true;
    }
}
