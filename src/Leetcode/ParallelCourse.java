package Leetcode;

//Problem Link : https://leetcode.com/problems/parallel-courses-iii

import java.util.LinkedList;

public class ParallelCourse {
    public int minimumTime(int n, int[][] relations, int[] time) {
        LinkedList<Integer> adjList[] = new LinkedList[n];
        for (int i = 0; i < n; i++) {
            adjList[i] = new LinkedList<>();
        }
        for (int rel[] : relations) {
            adjList[rel[1] - 1].add(rel[0] - 1);
        }

        int ans = 0;
        Integer dp[] = new Integer[n];
        for (int i = 0; i < n; i++) {
            ans = Math.max(ans, solve(dp, adjList, time, i));
        }
        return ans;
    }

    private int solve(Integer[] dp, LinkedList<Integer>[] adjList, int[] time, int i) {

        if (dp[i] != null)
            return dp[i];

        int total = 0;
        for (int prev : adjList[i]) {
            total = Math.max(total, solve(dp, adjList, time, prev));
        }

        return dp[i] = total + time[i];
    }
}
