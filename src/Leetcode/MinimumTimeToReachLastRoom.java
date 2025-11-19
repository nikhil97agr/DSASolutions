package Leetcode;

import java.util.Comparator;
import java.util.PriorityQueue;

//Problem Link - https://leetcode.com/problems/find-minimum-time-to-reach-last-room-ii/
public class MinimumTimeToReachLastRoom {
    public int minTimeToReach(int[][] moveTime) {
        int n = moveTime.length;
        int m = moveTime[0].length;

        PriorityQueue<Move> que = new PriorityQueue<>(Comparator.comparingInt(m2 -> (m2.time + m2.nextTime)));
        que.offer(new Move(0, 0, 0, 1));
        boolean vis[][] = new boolean[n][m];
        vis[0][0] = true;
        while(!que.isEmpty()){
            Move move = que.poll();
            int time = move.time;
            int i = move.i;
            int j = move.j;
            int nextTime = move.nextTime;

            if(i==n-1 && j==m-1) return time;

            int dir[] = new int[]{0,1,0,-1,0};

            for(int d=0;d<4;d++){
                int ni = i + dir[d];
                int nj = j + dir[d+1];

                if(isValid(ni, nj, n, m, vis, nextTime)){
                    que.offer(new Move(ni, nj, Math.max(time, moveTime[ni][nj]) + nextTime, nextTime==1? 2 : 1));
                    vis[ni][nj]= true;
                }
            }
        }
        return 0;
    }

    private boolean isValid(int i, int j, int n, int m, boolean vis[][], int next){
        return i >=0 && j >=0 && i < n && j<m && !vis[i][j];
    }

    class Move{
        int time;
        int i;
        int j;
        int nextTime;

        public Move(int i, int j, int time, int nextTime){
            this.i = i;
            this.j = j;
            this.time= time;
            this.nextTime = nextTime;
        }
    }
}