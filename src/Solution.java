import java.util.LinkedList;
import java.util.Queue;

class Solution {
    String alice = "A";
    String bob = "B";
    public int maxMoves(int kx, int ky, int[][] positions) {
        int n = positions.length;
        Integer dp[][] = new Integer[2][1<<n];
        return solve(kx, ky, n, positions, 0, (1<<n)-1,alice, dp);
    }

    private int  solve(int kx, int ky, int n, int[][] pos, int mask, int maxMask, String turn, Integer dp[][]){
        if(mask == maxMask) return 0;
        if(dp[turn.equals(alice) ? 0 : 1][mask]!=null) return dp[turn.equals(alice) ? 0 : 1][mask];
        if(turn.equals(alice)){
            int ans = 0;
            for(int i=0;i<n;i++){
                if((mask&(1<<i)) == 0){
                    int score = calcScore(kx, ky, pos[i][0], pos[i][1]);
                    ans = Math.max(ans, score + solve(pos[i][0], pos[i][1], n, pos, mask | (1<<i), maxMask, bob, dp));
                }
            }
            return dp[turn.equals(alice) ? 0 :1][mask]= ans;

        }else{
            int ans = Integer.MAX_VALUE;
            for(int i=0;i<n;i++){
                if((mask&(1<<i)) == 0){
                    int score = calcScore(kx, ky, pos[i][0], pos[i][1]);

                    ans = Math.min(ans, score + solve(pos[i][0], pos[i][1], n, pos, mask | (1<<i), maxMask, alice, dp));
                }
            }

            return dp[turn.equals(alice) ? 0 :1][mask]= ans;
        }
    }

    private int calcScore(int kx, int ky, int x, int y){
        boolean visited[][] = new boolean[50][50];

        Queue<int[]> que = new LinkedList<>();
        int dir[][] = {
                {2 ,1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {-1, 2}, {1, -2}, {-1, -2}
        };
        que.offer(new int[]{kx, ky, 0});
        visited[kx][ky] = true;
        int ans = Integer.MAX_VALUE;
        while(!que.isEmpty()){
            int p[] = que.poll();
            kx = p[0];
            ky = p[1];
            int score = p[2];

            if(kx == x && ky ==y){
                ans = Math.min(ans, score);
                continue;
            }

            for(int d[] : dir){
                int nx = kx + d[0];
                int ny = ky + d[1];

                if(nx >=0 && nx < 50 && ny >=0 && ny < 50 && !visited[nx][ny]){
                    visited[nx][ny] = true;
                    que.offer(new int[]{nx, ny, score+1});
                }
            }
        }

        return ans;
    }

    public static void main(String[] args) {
        System.out.println(new Solution().maxMoves(0, 0, new int[][]{{6,9}, {2,8}, {0,10}}));
    }
}