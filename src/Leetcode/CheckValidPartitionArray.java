package Leetcode;

// Problem Link: https://leetcode.com/problems/check-if-there-is-a-valid-partition-for-the-array/

public class CheckValidPartitionArray {
    public boolean validPartition(int[] nums) {
        int n = nums.length;
        Boolean dp[] = new Boolean[n];
        return solve(nums, n, dp, 0);
    }

    private boolean solve(int[] nums, int n, Boolean[] dp, int i) {
        if (i == n)
            return true;

        if (i == n - 1)
            return false;

        if (dp[i] != null)
            return dp[i];

        boolean result = false;
        if (nums[i] == nums[i + 1]) {
            result = solve(nums, n, dp, i + 2);

        }

        if (!result && i + 2 < n && nums[i] == nums[i + 1] && nums[i] == nums[i + 2]) {
            result = solve(nums, n, dp, i + 3);
        }

        if (!result && i + 2 < n && nums[i] + 1 == nums[i + 1] && nums[i + 1] + 1 == nums[i + 2]) {
            result = solve(nums, n, dp, i + 3);
        }

        dp[i] = result;
        return result;

    }
}
