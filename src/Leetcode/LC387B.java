package Leetcode;

public class LC387B {
    public int countSubmatrices(int[][] grid, int k) {
        int cnt = 0;
        int n = grid.length;
        int m = grid[0].length;

        int colSum[] = new int[m];
        for(int i=0;i<n;i++){
            int s = 0;
            for(int j=0;j<m;j++){
                int sum  = s+ grid[i][j] + colSum[j];
                s += grid[i][j];
                if(sum <= k){
                    cnt++;
                }
                colSum[j] = sum;

            }
        }
        return cnt;
    }

}