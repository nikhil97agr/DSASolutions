package Leetcode;

//Problem Link : https://leetcode.com/problems/champagne-tower

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class ChampagneTower {
    public double champagneTower(int p, int qr, int qg) {
        List<Double> list[] = new ArrayList[qr + 1];
        if (p == 0) {
            return 0d;
        }
        if (p == 1) {
            if (qr == 0)
                return 1d;
            return 0d;
        }

        list[0] = new ArrayList<>();
        list[0].add((double) p);
        for (int i = 1; i <= qr; i++) {
            list[i] = new ArrayList<>();
            for (int j = 0; j <= list[i - 1].size(); j++) {
                if (j == 0) {
                    list[i].add(Math.max(0d, (list[i - 1].get(j) - 1) / 2));
                } else if (j == list[i - 1].size()) {
                    list[i].add(Math.max(0d, (list[i - 1].get(j - 1) - 1) / 2));

                } else {
                    double a = Math.max(0d, (list[i - 1].get(j - 1) - 1) / 2);
                    double b = Math.max(0d, (list[i - 1].get(j) - 1) / 2);

                    list[i].add(a + b);
                }
            }

        }
        return Math.min(1, list[qr].get(qg));
    }
}
