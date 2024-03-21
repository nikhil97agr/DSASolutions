package Leetcode;

import java.util.HashMap;
import java.util.Map;

public class LC387C{
    public int minimumOperationsToWriteY(int[][] grid) {
        int n = grid.length;

        Map<Integer, Integer> y = new HashMap<>();
        Map<Integer, Integer> notY = new HashMap<>();

        int i=0;
        int j=0;
        while(i<= n/2){
            int dig = grid[i][j];
            grid[i][j] = -1;
            i++;
            j++;
            y.put(dig, y.getOrDefault(dig,0)+1);

        }
        i=0;
        j=n-1;
        while(i < n/2){
            int dig = grid[i][j];
            grid[i][j] = -1;
            i++;
            j--;
            y.put(dig, y.getOrDefault(dig, 0)+1);
        }
        i = (n+1)/2;
        j = (n)/2;
        while(i< n){
            int dig = grid[i][j];
            grid[i][j] = -1;
            i++;
            y.put(dig, y.getOrDefault(dig, 0)+1);

        }
        for(i=0;i<n;i++){
            for(j=0;j<n;j++){
                if(grid[i][j]!=-1){
                    notY.put(grid[i][j], notY.getOrDefault(grid[i][j],0)+1);
                }
            }
        }

        int ans = Integer.MAX_VALUE;

        ans = Math.min(calc(0, 1, 2, y, notY), Math.min(calc(1, 0,2, y, notY), calc(2, 1, 0, y, notY)));
        return ans;
    }

    private int calc(int i, int i1, int i2, Map<Integer, Integer> y, Map<Integer, Integer> notY) {

        int sum1 = y.getOrDefault(i1, 0) + y.getOrDefault(i2, 0);
        int sum2 = notY.getOrDefault(i,0) + Math.min(notY.getOrDefault(i1, 0), notY.getOrDefault(i2, 0));
        return sum1 + sum2;
    }

}