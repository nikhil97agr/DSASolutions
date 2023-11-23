package Leetcode;

public class MinimumMovesToSpreadStones {
    public int minimumMoves(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        return solve(grid,0);
    }
    
    private int solve(int grid[][], int ind){
        if(ind>=9){
            return 0;
        }
        int ans = Integer.MAX_VALUE;
        int i = ind/3;
        int j = ind%3;
        if(grid[i][j] > 1){
            for(int k=0;k<9;k++){
                int ni = k/3;
                int nj = k%3;
                
                if(grid[ni][nj] ==0){
                    grid[ni][nj]++;
                    grid[i][j]--;
                    int ans1 = solve(grid, ind) + (Math.abs(ni-i)+Math.abs(nj-j));
                    ans = Math.min(ans, ans1);
                    grid[ni][nj]--;
                    grid[i][j]++;
                }
            }
        }else{
            ans = solve(grid, ind+1);
        }
        return ans;
    }
}
