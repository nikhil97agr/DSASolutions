package Leetcode;

// Problem Link : https://leetcode.com/problems/soup-servings

public class SoupServing {

    public double soupServing(int n) {
        if (n >= 5000) // Why this? because as N increases the probability of a always filled first
                       // will increase near to 1
            return 1d;

        Double dp[][] = new Double[n][n];

        return solve(n, n, dp);
    }

    private double solve(int n, int m, Double dp[][]) {
        if (n <= 0 && m <= 0)
            return 0.5d;

        if (n <= 0)
            return 1d;

        if (m <= 0)
            return 0;

        if (dp[n][m] != null)
            return dp[n][m];

        return dp[n][m] = (solve(n - 100, m, dp) + solve(n - 75, m - 25, dp) + solve(n - 50, m - 50, dp)
                + solve(n - 25, m - 75, dp)) / 4d;
    }
}
