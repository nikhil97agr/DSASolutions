package Leetcode;

//Problem Link : https://leetcode.com/problems/knight-probability-in-chessboard

public class KnightProbabilityInChessboard {

    public double knightProbability(int n, int k, int row, int column) {
        Double dp[][][] = new Double[n][n][k+1];    // DP to hold the value of r,c,k combination
        return solve(n, k, row, column, dp);
    }

    private double solve(int n, int k, int r, int c, Double dp[][][]){
        if(!valid(r, c, n)) return 0d;  //if (r,c) are out of bounds

        if(k==0) return 1d; //if all the moves are over & (r,c) is inside the grid
        if(dp[r][c][k]!=null) return dp[r][c][k];   //if (r,c,k) combination is already calculated before
        int dr[] = new int[]{2, 2, 1, -1, -2, -2, 1 ,-1};   //from each cell there will 8 moves possible that knight can move
        int dc[] = new int[]{-1, 1, 2, 2, 1, -1, -2, -2};
        double ans = 0;
        for(int i=0;i<8;i++){
            ans = ans + solve(n, k-1, r+dr[i], c+dc[i], dp)/8d; //call the function recursively for each next cell it can go to & divide by 8 because next 8 moves are possible and from each cell
        }

        return dp[r][c][k] = ans;
    }

    private boolean valid(int r, int c, int n){
        return r>=0 && r<n && c>=0 && c<n;
    }

}
