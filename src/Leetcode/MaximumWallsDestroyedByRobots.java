package Leetcode;

import java.util.Arrays;

public class MaximumWallsDestroyedByRobots {
    public int maxWalls(int[] robots, int[] distance, int[] walls) {
        Arrays.sort(walls);
        int m = walls.length;

        int n = robots.length;

        Robot robot[] = new Robot[n];
        for(int i=0;i<n;i++){
            robot[i] = new Robot(robots[i], distance[i]);
        }

        Arrays.sort(robot, (r1, r2) -> r1.pos - r2.pos);
        
        for(int i=0;i<n;i++){
            Robot r = robot[i];
            int start = Math.max(walls[0], r.pos - r.dist);
            int end = r.pos;

            if(i > 0){
                start = Math.max(start, robot[i-1].pos + 1);
            }

            Interval interval = search(walls, start, end);

            r.left = interval;

             end = Math.min(walls[m-1], r.pos + r.dist);
             start = r.pos;

            if(i < n-1){
                end = Math.min(end, robot[i+1].pos - 1);
            }

             interval = search(walls, start, end);

            r.right = interval;
        }
        
        
        Integer dp[][] = new Integer[n][2];

        return solve(robot, 0, 0, n, dp);
        
    }

    private int solve(Robot[] robot, int i, int dir, int n, Integer[][] dp) {

        if(i==n) return 0;

        if(dp[i][dir]!=null) return dp[i][dir];
        Robot r = robot[i];
        if(i==0){
            int ans1 = (r.left != null ?  r.left.end - r.left.start + 1 : 0) + solve(robot, i+1, 0,n, dp);
            int ans2 = (r.right !=null ? r.right.end - r.right.start + 1 : 0 ) + solve(robot, i+1, 1, n, dp);

            return dp[i][dir] = Math.max(ans1, ans2);
        }else{
            int ans1 = 0;
            int ans2 = (r.right !=null ? r.right.end - r.right.start + 1 : 0 ) + solve(robot, i+1, 1, n, dp);


            if(dir ==0 ){
              ans1 =  (r.left != null ?  r.left.end - r.left.start + 1 : 0) + solve(robot, i+1, 0,n, dp);
            }else{
                if(r.left ==null){
                    ans1 = solve(robot, i+1, 0, n, dp);
                }else{
                    Robot left = robot[i-1];
                    if(left.right == null){
                        ans1 = r.left.end - r.left.start + 1 +  solve(robot, i+1,0 , n, dp);
                    }else{
                        int start= r.left.start;
                        int end = r.left.end;

                        start = Math.max(start, left.right.end + 1);
                        if(start > end){
                            ans1 = solve(robot, i+1, 0, n,dp);
                        }else{
                            ans1 = end - start + 1 + solve(robot, i+1, 0, n, dp);
                        }
                    }
                }
            }
            return dp[i][dir] = Math.max(ans1, ans2);
        }


    }

    private Interval search(int walls[], int start, int end){
        int a = -1;
        int b = -1;
        int n = walls.length;
        int i = 0;
        int j = n-1;
        while( i <= j){
            int mid = (i + j ) /2;

            if(walls[mid] >= start){
                a = mid;
                j = mid - 1;
            }else{
                i = mid + 1;
            }
        }

        i=0;
        j=n-1;
        while(i <= j){
            int mid = (i+j)/2;

            if(walls[mid] <= end){
                b = mid;
                i = mid + 1;
            }else{
                j = mid - 1;
            }
        }

        if(a == -1 || b == -1) return null;

        return new Interval(a,b);
        
        
    }

    class Interval{
        int start;
        int end;
        public Interval(int start, int end){
            this.start = start;
            this.end = end;
        }
    }

    class Robot{
        int dist;
        int pos;
        Interval left;
        Interval right;

        public Robot(int pos, int dist){
            this.pos= pos;
            this.dist = dist;
        }
    }
}