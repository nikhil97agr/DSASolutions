package Leetcode;

//Problem Link : https://leetcode.com/problems/combination-sum-iv/

public class CombinationSum {
    public int combinationSum4(int[] nums, int target) {
        int dp[] = new int[target + 1];
        dp[0] = 1;
        for (int i = 0; i <= target; i++) {
            for (int x : nums) {
                if (x <= i) {
                    dp[i] += dp[i - x];
                }
            }
        }
        return dp[target];
    }
}
