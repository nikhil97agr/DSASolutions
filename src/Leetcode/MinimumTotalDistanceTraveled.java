package Leetcode;

import java.util.ArrayList;

// Problem link : https://leetcode.com/problems/minimum-total-distance-traveled/

import java.util.Collections;
import java.util.List;

class Solution {

    public long minimumTotalDistance(List<Integer> robot, int[][] factory) {
        int n = robot.size();
        Collections.sort(robot);
        List<Integer> factories =new ArrayList<>();
        
        for(int f[] : factory){         //add all the factories with the count how many times they can be used
            for(int i=0;i<f[1];i++){
                factories.add(f[0]);
            }
        }
        Collections.sort(factories);
        int m = factories.size();
        Long dp[][] = new Long[n+1][m+1];

        return solve(robot, factories, n, m, dp);

    }

    private long solve(List<Integer> robot,List<Integer> factory, int n, int m, Long dp[][]){

        if(n==0) return 0l;     // if all the robots are repaired 

        if(m==0) return Long.MAX_VALUE;     //if all the factories are traversed but still robots are left

        if(dp[n][m]!=null) return dp[n][m];     // if already calculated the result for this robot & factory combination
        
        long ans1 = solve(robot, factory, n-1, m-1, dp);    //get robot prepared by the current factory
        long ans2 = solve(robot, factory, n, m-1, dp);      //skip this factory and move to next one

        if(ans1 != Long.MAX_VALUE) ans2 = Math.min(ans2, ans1 + Math.abs(factory.get(m-1)-robot.get(n-1)));     //check out of both the answers which is giving minimum distance

        return dp[n][m] = ans2;
    
    }

}