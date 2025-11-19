package Leetcode;

public class LCWeekly437D {
    public int lenOfVDiagonal(int[][] grid) {
        int n = grid.length;
        int m = grid[0].length;

        Integer dp[][][][] = new Integer[n][m][2][4];
        int ans = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (grid[i][j] == 1) {
                    ans = Math.max(ans, solve(i, j, n, m, -1, 0, grid, dp, 0));
                }
            }
        }
        return ans;
    }

    private int solve(int i, int j, int n, int m, int prev, int change, int grid[][], Integer dp[][][][], int currDir) {
        if (i < 0 || j < 0 || i >= n || j >= m) return 0;
        if (grid[i][j] == prev) return 0;
        if (prev == 1 && grid[i][j] != 2) return 0;
        if (prev != -1 && grid[i][j] == 1) return 0;
        if (dp[i][j][change][currDir] != null) return dp[i][j][change][currDir];
        int dir[][] = new int[][]{
                {1, 1, 0}, {1, -1, 1}, {-1, 1, 2}, {-1, -1, 3}
        };
        if (prev == -1) {
            int ans = 1;
            for (int d[] : dir) {
                int ni = i + d[0];
                int nj = j + d[1];
                ans = Math.max(ans, 1 + solve(ni, nj, n, m, 1, change, grid, dp, d[2]));
            }

            return dp[i][j][change][currDir] = ans;
        }

        int ans = 1 + solve(i + dir[currDir][0], j + dir[currDir][1], n, m, grid[i][j], change, grid, dp, currDir);

        if (change == 0) {
            for (int d[] : dir) {
                if ((currDir == 0 || currDir == 3) && (d[2] == 0 || d[2] == 3)) continue;
                if ((currDir == 1 || currDir == 2) && (d[2] == 1 || d[2] == 2)) continue;
                int ni = i + d[0];
                int nj = j + d[1];
                ans = Math.max(ans, 1 + solve(ni, nj, n, m, grid[i][j], 1, grid, dp, d[2]));
            }
        }
        return dp[i][j][change][currDir] = ans;
    }



}