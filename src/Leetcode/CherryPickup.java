package Leetcode;

public class CherryPickup {
    public int cherryPickup(int[][] grid) {
        int n = grid.length;

        Integer dp[][][] = new Integer[n][n][n];

        return Math.max(solve(0, 0, 0, dp, n, grid), 0);
    }

    private int solve(int r1, int c1, int c2, Integer dp[][][], int n, int grid[][]){
        int r2 = r1 + c1 - c2;

        if(r1 >=n || r2 >= n || c1 >=n || c2 >= n || grid[r1][c1] == -1 || grid[r2][c2] == -1){
            return Integer.MIN_VALUE;
        }

        if(r1 == n-1 && c1 == n-1){
            return grid[r1][c1];
        }
        if(dp[r1][c1][c2]!=null) return dp[r1][c1][c2];
        int cherry = grid[r1][c1];

        if(c1!=c2 || r1!=r2){
            cherry += grid[r2][c2];
        }
        int max = Math.max(
                solve(r1+1, c1, c2, dp, n, grid),
                Math.max(solve(r1, c1+1, c2, dp, n, grid),
                        Math.max(solve(r1+1, c1, c2+1, dp, n, grid),
                                solve(r1, c1+1, c2+1, dp, n, grid)))
        );

        if(max == Integer.MIN_VALUE) return dp[r1][c1][c2] = Integer.MIN_VALUE;

        return dp[r1][c1][c2] = max + cherry;
    }
}