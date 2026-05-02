package Leetcode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//Problem Link: https://leetcode.com/problems/count-number-of-possible-root-nodes

public class CountNumberOfPossibleRootNodes {

    Set<Integer> adjList[];
    Map<Integer, Set<Integer>> guess;
    int k;

    public int rootCount(int[][] edges, int[][] guesses, int k) {

        int n = edges.length + 1;
        this.k = k;
        adjList = new HashSet[n];
        for (int i = 0; i < n; i++) {
            adjList[i] = new HashSet<>();
        }

        guess = new HashMap<>();
        for (int g[] : guesses) {
            int u = g[0];
            int v = g[1];
            guess.computeIfAbsent(u, x -> new HashSet<>()).add(v);
        }
        for (int e[] : edges) {
            int u = e[0];
            int v = e[1];
            adjList[u].add(v);
            adjList[v].add(u);
        }

        int cnt = dfs(0, -1);

        return dfs(0, -1, cnt);
    }

    private int dfs(int curr, int par, int cnt) {

        if (guess.getOrDefault(curr, new HashSet<>()).contains(par)) {
            cnt++;
        }
        int ans = 0;
        if (cnt >= k) {
            ans = 1;
        }

        for (int child : adjList[curr]) {
            if (child == par) {
                continue;
            }

            if (guess.getOrDefault(curr, new HashSet<>()).contains(child)) {
                ans += dfs(child, curr, cnt - 1);
            } else {
                ans += dfs(child, curr, cnt);
            }
        }
        return ans;
    }

    private int dfs(int curr, int par) {

        int cnt = 0;

        for (int child : adjList[curr]) {
            if (child == par) {
                continue;
            }

            if (guess.getOrDefault(curr, new HashSet<>()).contains(child)) {
                cnt++;
            }
            cnt += dfs(child, curr);
        }

        return cnt;
    }
}