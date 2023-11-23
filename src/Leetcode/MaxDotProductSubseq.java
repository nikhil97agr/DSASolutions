package Leetcode;

//Problem Link : https://leetcode.com/problems/max-dot-product-of-two-subsequences

import java.util.Arrays;

public class MaxDotProductSubseq {
    public int maxDotProduct(int[] nums1, int[] nums2) {
        int n = nums1.length;
        int m = nums2.length;
        Integer dp[][] = new Integer[n][m];
        return solve(0, 0, nums1, nums2, nums1.length, nums2.length, dp);
    }

    private int solve(int i, int j, int n1[], int n2[], int n, int m, Integer dp[][]) {
        if (i == n || j == m)
            return Integer.MIN_VALUE;

        if (dp[i][j] != null)
            return dp[i][j];

        int ans = max(n1[i] * n2[j] + max(solve(i + 1, j + 1, n1, n2, n, m, dp), 0), solve(i + 1, j, n1, n2, n, m, dp),
                solve(i, j + 1, n1, n2, n, m, dp));
        return dp[i][j] = ans;

    }

    private int max(Integer... nums) {
        return Arrays.stream(nums).max((a, b) -> Integer.compare(a, b)).get();
    }

}
