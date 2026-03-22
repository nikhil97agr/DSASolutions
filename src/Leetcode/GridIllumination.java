package Leetcode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

//Problem Link: https://leetcode.com/problems/grid-illumination

public class GridIllumination {

    int dir[][] = new int[][]{
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1}, {0, 0}, {0, 1},
            {1, -1}, {1, 0}, {1, 1}
    };

    public int[] gridIllumination(long n, int[][] lamps, int[][] queries) {

        var row = new HashMap<Integer, Integer>();
        var col = new HashMap<Integer, Integer>();
        var diagUp = new HashMap<Integer, Integer>();
        var diagDown = new HashMap<Integer, Integer>();
        var activeLamps = new HashSet<Long>();

        for (int l[] : lamps) {
            long key = n * l[0] + l[1];
            int r = l[0];
            int c = l[1];
            if (!activeLamps.contains(key)) {
                activeLamps.add(key);
                add(r, row);
                add(c, col);
                add(r - c, diagUp);
                add(r + c, diagDown);
            }
        }
        int ans[] = new int[queries.length];
        for (int i = 0; i < queries.length; i++) {
            int r = queries[i][0];
            int c = queries[i][1];
            if (row.containsKey(r) || col.containsKey(c) || diagUp.containsKey(r - c) || diagDown.containsKey(r + c)) {
                ans[i] = 1;
            }

            for (int d[] : dir) {
                int nr = r + d[0];
                int nc = c + d[1];
                long key = n * nr + nc;
                if (activeLamps.contains(key)) {
                    activeLamps.remove(key);
                    remove(nr, row);
                    remove(nc, col);
                    remove(nr - nc, diagUp);
                    remove(nr + nc, diagDown);
                }
            }
        }
        return ans;
    }

    private void add(int val, Map<Integer, Integer> map) {

        map.put(val, map.getOrDefault(val, 0) + 1);
    }

    private void remove(int val, Map<Integer, Integer> map) {

        int count = map.get(val);
        if (count == 1) {
            map.remove(val);
        } else {
            map.put(val, count - 1);
        }
    }
}