package Leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoinPath{
    public List<Integer> cheapestJump(int[] coins, int maxJump) {
        int n = coins.length;
        int par[] = new int[n];
        int dp[] = new int[n];
        Arrays.fill(par, -1);

        for(int i=n-2;i>=0;i--){
            int max = Integer.MAX_VALUE;

            for(int j=i+1;j<n && j <= i+ maxJump;j++){
                int c = dp[j] + coins[i];
                if(coins[i] >= 0 && coins[j] >= 0 && c < max){
                    max = c;
                    par[i] = j;
                }
            }
            dp[i] = max;
        }

        List<Integer> res = new ArrayList<>();

        int i=0;
        while( i < n && par[i] > 0) {
            res.add(i+1);
            i= par[i];
        }
        if(i == n-1 && coins[i] >= 0){
            res.add(n);
        }else{
            res.clear();
        }

        return res;
    }
}