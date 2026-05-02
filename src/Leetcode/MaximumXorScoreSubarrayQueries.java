package Leetcode;
//Problem Link: https://leetcode.com/problems/maximum-xor-score-subarray-queries


public class MaximumXorScoreSubarrayQueries {

    public int[] maximumSubarrayXor(int[] nums, int[][] queries) {

        int n = nums.length;
        int xor[][] = new int[n][n];
        int score[][] = new int[n][n];
        for (int i = 0; i < n; i++) {
            score[i][i] = xor[i][i] = nums[i];
        }
        for (int len = 2; len <= n; len++) {
            for (int i = 0, j = len - 1; i < n - (len - 1); j++, i++) {
                xor[i][j] = xor[i + 1][j] ^ xor[i][j - 1];
                score[i][j] = max(score[i][j - 1], score[i + 1][j], xor[i][j]);
            }
        }

        var q = queries.length;
        var ans = new int[q];
        for (int i = 0; i < q; i++) {
            int l = queries[i][0];
            int r = queries[i][1];

            ans[i] = score[l][r];
        }

        return ans;
    }

    private int max(int... arr) {

        int ans = 0;
        for (int x : arr) {
            ans = Math.max(ans, x);
        }

        return ans;
    }
}