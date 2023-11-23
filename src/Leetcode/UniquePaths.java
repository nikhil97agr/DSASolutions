package Leetcode;

//Problem Link : https://leetcode.com/problems/unique-paths-ii/

import java.util.Arrays;

public class UniquePaths {
    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int m = obstacleGrid.length;
        int n = obstacleGrid[0].length;

        int dp[][] = new int[m][n];
        for (int d[] : dp) {
            Arrays.fill(d, -1);
        }
        dp[m - 1][n - 1] = obstacleGrid[m - 1][n - 1] == 0 ? 1 : 0;
        return solve(obstacleGrid, m, n, dp, 0, 0);
    }

    private int solve(int mat[][], int m, int n, int dp[][], int i, int j) {
        if (i >= m || j >= n) {
            return 0;
        }

        if (mat[i][j] == 1) {
            return 0;
        }

        if (dp[i][j] != -1) {
            return dp[i][j];
        }

        return dp[i][j] = solve(mat, m, n, dp, i + 1, j) + solve(mat, m, n, dp, i, j + 1);
    }
}
