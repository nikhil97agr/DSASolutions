package Leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

//Problem Link: https://leetcode.com/problems/shortest-path-with-at-most-k-consecutive-identical-characters/description/

public class ShortestPathWithAtMostKConsecutiveIenticalChars {

    public int shortestPath(int n, int[][] edges, String labels, int k) {

        List<int[]> adjList[] = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            adjList[i] = new ArrayList<>();
        }

        for (var e : edges) {
            var u = e[0];
            var v = e[1];

            var w = e[2];
            adjList[u].add(new int[]{v, w});
        }
        char ch[] = labels.toCharArray();

        PriorityQueue<State> que = new PriorityQueue<>((a, b) -> {
            return a.sum - b.sum;
        });

        que.offer(new State(0, 0, 1));
        int dp[][] = new int[n][k + 1];
        for (var a : dp) {
            Arrays.fill(a, Integer.MAX_VALUE);
        }

        dp[0][1] = 0;
        var ans = Integer.MAX_VALUE;
        while (!que.isEmpty()) {
            var next = que.poll();

            var u = next.u;
            var sum = next.sum;
            var cnt = next.cnt;

            if (cnt > k || (dp[u][cnt] < sum)) {
                continue;
            }

            if (u == n - 1) {
                return sum;
            }

            for (var child : adjList[u]) {
                var v = child[0];
                var w = child[1];

                var newSum = sum + w;
                int newCnt = (ch[v] == ch[u]) ? cnt + 1 : 1;
                if (newCnt > k || dp[v][newCnt] <= newSum) {
                    continue;
                }

                dp[v][newCnt] = newSum;

                que.offer(new State(v, newSum, newCnt));
            }
        }

        return -1;
    }


    class State {

        int u;
        int sum;
        int cnt;

        public State(int u, int sum, int cnt) {

            this.u = u;
            this.sum = sum;
            this.cnt = cnt;

        }
    }


}