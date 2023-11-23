package Leetcode;

// Problem Link : https://leetcode.com/problems/maximal-network-rank

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class MaximalNetworkRank {
    class Road {
        int a;
        int b;

        public Road(int a, int b) {
            this.a = Math.min(a, b);
            this.b = Math.max(a, b);
        }

        @Override
        public boolean equals(Object obj) {
            Road road = (Road) obj;
            return road.a == this.a && road.b == this.b;
        }

        @Override
        public int hashCode() {
            return ("" + a + ":" + b).hashCode();
        }

    }

    public int maximalNetworkRank(int n, int[][] roads) {
        Set<Road> set = new HashSet<>();
        TreeSet<Integer> maxRank = new TreeSet<>(Collections.reverseOrder());
        int degree[] = new int[n];

        for (int road[] : roads) {
            set.add(new Road(road[0], road[1]));
            degree[road[0]]++;
            degree[road[1]]++;
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (set.contains(new Road(i, j))) {
                    maxRank.add(degree[i] + degree[j] - 1);
                } else {
                    maxRank.add(degree[i] + degree[j]);
                }
            }
        }
        return maxRank.first();
    }
}
