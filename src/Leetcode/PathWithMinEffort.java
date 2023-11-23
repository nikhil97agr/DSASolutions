package Leetcode;

//Problem Link : https://leetcode.com/problems/path-with-minimum-effort

import java.util.Arrays;
import java.util.PriorityQueue;

public class PathWithMinEffort {
    public int minimumEffortPath(int[][] heights) {
        int m = heights.length;
        int n = heights[0].length;

        int dist[][] = new int[m][n];
        for(int x[]: dist){
            Arrays.fill(x, Integer.MAX_VALUE);
        }

        PriorityQueue<int[]> que = new PriorityQueue<>((a,b) -> a[2]-b[2]);

        que.add(new int[]{0,0,0});
        dist[0][0] = 0;
        while(!que.isEmpty()){
            int cell[] = que.poll();
            int x = cell[0];
            int y = cell[1];
            int eff = cell[2];

            if(eff > dist[x][y]) continue;

            if(x==m-1 && y==n-1) return eff;

            int diff[] = new int[]{1,0,-1,0,1};
            for(int i=0;i<4;i++){
                int nx = x + diff[i];
                int ny = y + diff[i+1];

                if(nx<0 || nx>=m || ny<0 || ny>=n) continue;

                int nEff = Math.max(eff, Math.abs(heights[x][y] - heights[nx][ny]));

                if(nEff < dist[nx][ny]){
                    dist[nx][ny] = nEff;
                    que.add(new int[]{nx, ny, nEff});
                }
            }
        }

        return -1;
    }
}
