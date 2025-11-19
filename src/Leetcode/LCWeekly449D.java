package Leetcode;

import java.util.HashMap;
import java.util.Map;

public class LCWeekly449D {
    public boolean canPartitionGrid(int[][] grid) {
        int m = grid.length;
        int n =grid[0].length;

        if(n==1 && m==1){
            return false;
        }

        if(n==1){
            long top = 0;
            for(int g[] : grid){
                top = top + g[0];
            }
            long bottom = 0;

            for(int i=0;i<m-1;i++){
                long x = grid[i][0];

                top -= x;
                bottom += x;

                if(top == bottom ||
                top  - grid[i+1][0] == bottom ||
                top - grid[m-1][0] == bottom ||
                top == bottom - grid[0][0] ||
                top == bottom - grid[i][0]){
                    return true;
                }
            }

            return false;
        }

        if(m==1){
            long top = 0;
            for(int x : grid[0]){
                top = top + x;
            }

            long bottom = 0;

            for(int i=0;i<n-1;i++){
                long x = grid[0][i];

                top -= x;
                bottom+=x;

                if(top == bottom ||

                   top -  grid[0][i+1] == bottom ||
                        top - grid[0][n-1] == bottom ||
                        top == bottom - grid[0][0] ||
                        top == bottom - grid[0][i]
                ) {
                    return true;
                }
            }
            return false;
        }

        return horizontal(grid, m, n) || vertical(grid, m, n);
    }

    private boolean horizontal(int grid[][], int m, int n){
        long top =0;
        long bottom = 0;

        Map<Long, Integer> m1 = new HashMap<>();
        Map<Long, Integer> m2 = new HashMap<>();
        int topSize=m;
        for(int g[] : grid){
            for(int x : g){
                top = top + x;
                add(x, m1);
            }
        }
        int bSize = 0;
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                bottom = bottom + grid[i][j];
                top  = top - grid[i][j];
                remove(grid[i][j], m1);
                add(grid[i][j], m2);
            }
            topSize--;
            bSize++;
            if(top == bottom) return true;

            if(top > bottom){
                long diff = top - bottom;
                if(m1.containsKey(diff)){

                    if(topSize > 1 || 1l*grid[m-1][0] == diff || 1l*grid[m-1][n-1] == diff){
                        return true;
                    }
                }
            }else{
                long diff= bottom - top;

                if(m2.containsKey(diff) && (bSize > 1 || 1l*grid[0][0] == diff || 1l*grid[0][n-1] == diff)){
                    return true;
                }
            }
        }
        return false;

    }

    private void add(long val, Map<Long, Integer> map){
        int cnt = map.getOrDefault(val, 0);
        map.put(val, cnt+1);
    }

    private void remove(long val, Map<Long, Integer> map){
        int cnt = map.get(val);
        if(cnt ==1) map.remove(val);
        else map.put(val, cnt-1);
    }

    private boolean vertical(int grid[][], int m, int n){
        long top =0;
        long bottom = 0;

        Map<Long, Integer> m1 = new HashMap<>();
        Map<Long, Integer> m2 = new HashMap<>();
        int topSize=n;
        for(int g[] : grid){
            for(int x : g){
                top = top + x;
                add(x, m1);
            }
        }
        int bSize = 0;
        for(int i=0;i<n;i++){
            for(int j=0;j<m;j++){
                bottom = bottom + grid[j][i];
                top  = top - grid[j][i];
                remove(grid[j][i], m1);
                add(grid[j][i], m2);
            }
            topSize--;
            bSize++;
            if(top == bottom) return true;

            if(top > bottom){
                long diff = top - bottom;
                if(m1.containsKey(diff)){
                    if(topSize > 1) {
                        return true;
                    }

                    if(1l*grid[0][n-1] == diff || 1l*grid[m-1][n-1] == diff) return true;
                }
            }else{
                long diff= bottom - top;

                if(m2.containsKey(diff)){
                    if(bSize > 1) {
                        return true;
                    }

                    if(1l*grid[0][0] == diff || 1l*grid[m-1][0] == diff) return true;
                }
            }
        }
        return false;
    }
}