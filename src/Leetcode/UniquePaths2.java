package Leetcode;

//Problem Link : https://leetcode.com/problems/unique-paths/description

public class UniquePaths2 {
    public int uniquePaths(int m, int n) {
        int dp[][] = new int[m][n];
        dp[m - 1][n - 1] = 1;

        return solve(m, n, dp, 0, 0);
    }

    private int solve(int m, int n, int dp[][], int i, int j) {
        if (i >= m || j >= n) {
            return 0;
        }

        if (dp[i][j] != 0) {
            return dp[i][j];
        }

        return dp[i][j] = solve(m, n, dp, i + 1, j) + solve(m, n, dp, i, j + 1);
    }
}