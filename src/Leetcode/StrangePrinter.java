package Leetcode;

// Problem Link : https://leetcode.com/problems/strange-printer/

import java.util.Arrays;

public class StrangePrinter {
    public int strangePrinter(String s) {
        int n = s.length();
        int dp[][] = new int[n][n];
        for (int x[] : dp) {
            Arrays.fill(x, Integer.MAX_VALUE);
        }

        return strangePrinter(0, n - 1, s, dp);
    }

    private int strangePrinter(int i, int j, String s, int dp[][]) {
        if (i == j)
            return 1;

        if (dp[i][j] != Integer.MAX_VALUE) {
            return dp[i][j];
        }
        int turns = Integer.MAX_VALUE;
        for (int k = i; k < j; k++) {
            turns = Math.min(turns, strangePrinter(i, k, s, dp) + strangePrinter(k + 1, j, s, dp));
        }

        return dp[i][j] = s.charAt(i) == s.charAt(j) ? turns - 1 : turns;
    }
}