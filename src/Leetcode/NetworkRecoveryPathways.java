package Leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

//Problem Link: https://leetcode.com/problems/network-recovery-pathways/description

public class NetworkRecoveryPathways {

    List<Edge> adjList[];
    int n;
    boolean[] online;
    long k;

    public int findMaxPathScore(int[][] edges, boolean[] online, long k) {

        if (edges.length == 0) {
            return -1;
        }
        int ans = -1;
        int min = 0;
        int max = 0;
        n = online.length;
        this.online = online;
        this.k = k;
        adjList = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            adjList[i] = new ArrayList<>();
        }

        for (var e : edges) {
            var u = e[0];
            var v = e[1];
            var cost = e[2];
            if (!online[u] || !online[v]) {
                continue;
            }
            max = Math.max(max, cost);
            adjList[u].add(new Edge(cost, v));
        }

        while (min <= max) {
            var mid = min + (max - min) / 2;

            if (check(mid)) {
                ans = mid;
                min = mid + 1;
            } else {
                max = mid - 1;
            }
        }

        return ans;
    }

    private boolean check(int mid) {

        var que = new PriorityQueue<Edge>(Comparator.comparingLong(a -> a.cost));
        que.offer(new Edge(0, 0));
        var dp = new long[n];
        var max = k * 2;
        Arrays.fill(dp, max);
        dp[0] = 0;
        while (!que.isEmpty()) {
            var next = que.poll();

            if (next.dest == n - 1) {
                return true;
            }
            if (dp[next.dest] < next.cost) {
                continue;
            }

            var curr = next.dest;
            var cost = next.cost;
            for (var child : adjList[curr]) {
                var dest = child.dest;
                if (!online[dest] || child.cost < mid) {
                    continue;
                }
                var newCost = cost + child.cost;

                if (newCost <= k && newCost < dp[dest]) {
                    dp[dest] = newCost;
                    que.offer(new Edge(newCost, dest));
                }
            }

        }
        return false;
    }


    record Edge(long cost, int dest) {

    }
}