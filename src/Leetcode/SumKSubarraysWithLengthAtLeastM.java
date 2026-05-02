package Leetcode;

import java.util.Arrays;

//Problem Link: https://leetcode.com/problems/sum-of-k-subarrays-with-length-at-least-m/

public class SumKSubarraysWithLengthAtLeastM {

    int prefix[];
    int n;
    int m;
    int nums[];
    Integer dp[][][];

    public int maxSum(int[] nums, int k, int m) {

        this.nums = nums;
        int n = nums.length;
        this.n = n;
        this.m = m;
        dp = new Integer[n][k + 1][2];
        if (n % m == 0 && k == n / m) {
            return Arrays.stream(nums).sum();
        }
        prefix = new int[n];
        prefix[0] = nums[0];
        for (int i = 1; i < n; i++) {
            prefix[i] = prefix[i - 1] + nums[i];
        }

        return solve(0, k, 0);
    }

    private int solve(int i, int k, int isPreviousContinue) {

        if (n - i < k * m) {
            return Integer.MIN_VALUE;
        }

        if (i == n) {
            if (k == 0) {
                return 0;
            }
            return Integer.MIN_VALUE;
        }

        if (dp[i][k][isPreviousContinue] != null) {
            return dp[i][k][isPreviousContinue];
        }

        int exclude = solve(i + 1, k, 0);
        int include = Integer.MIN_VALUE;

        if (isPreviousContinue == 1) {
            int ans = solve(i + 1, k, 1);
            if (ans != Integer.MIN_VALUE) {
                include = Math.max(include, ans + nums[i]);
            }
        }

        if (k > 0 && i + m <= n) {
            int ans = solve(i + m, k - 1, 1);

            if (ans != Integer.MIN_VALUE) {
                include = Math.max(include, ans + prefix[i + m - 1] - (i > 0 ? prefix[i - 1] : 0));
            }
        }

        return dp[i][k][isPreviousContinue] = Math.max(include, exclude);
    }
}