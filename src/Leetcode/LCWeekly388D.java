package Leetcode;

public class LCWeekly388D {
    public long maximumStrength(int[] nums, int k) {
        int n = nums.length;
        Long dp[][][] = new Long[n][k+1][2];
        return solve(0, k, dp, nums, n, 0 );
    }

    private long solve(int i, int k, Long[][][] dp, int[] nums, int n, int flag) {
        if(k==0) return 0;

        if(i==n){
            if(k==1 && flag == 1){
                return 0;
            }
            return (long)-1e15;

        }

        if(dp[i][k][flag]!= null) return dp[i][k][flag];
        long ans = 1L*(k%2==0 ? -1  : 1) * nums[i]*k + solve(i+1, k, dp, nums, n, 1);

        if(flag == 1){
            ans = Math.max( solve(i, k-1, dp, nums,n, 0), ans);
        }else{
            ans = Math.max(ans, solve(i+1,k , dp, nums, n, 0));
        }

        return dp[i][k][flag] = ans;
    }
}