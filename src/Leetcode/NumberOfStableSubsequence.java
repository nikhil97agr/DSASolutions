package Leetcode;

import java.util.Arrays;

//Problem Link: https://leetcode.com/problems/number-of-stable-subsequences/description/
public class NumberOfStableSubsequence {

    int dp[][][];

    public int countStableSubsequences(int[] nums) {

        int n = nums.length;
        dp = new int[n][3][3];
        for (int d[][] : dp) {
            for (int dd[] : d) {
                Arrays.fill(dd, -1);
            }
        }
        return add(solve(0, n, nums, 2, 2), -1);
    }

    private int solve(int i, int n, int nums[], int parityA, int parityB) {

        if (i == n) {
            return 1;
        }

        if (dp[i][parityA][parityB] != -1) {
            return dp[i][parityA][parityB];
        }

        int ans = solve(i + 1, n, nums, parityA, parityB);

        if (parityA == parityB) {
            if (nums[i] % 2 != parityA) {
                ans = add(ans, solve(i + 1, n, nums, parityB, nums[i] % 2));
            }
        } else {
            ans = add(ans, solve(i + 1, n, nums, parityB, nums[i] % 2));
        }

        return dp[i][parityA][parityB] = ans;
    }

    private int add(long a, long b) {

        long mod = (long) 1e9 + 7;

        long sum = a + b + mod;

        return (int) (sum % mod);
    }


}