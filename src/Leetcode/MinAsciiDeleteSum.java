package Leetcode;

// Problem Link : https://leetcode.com/problems/minimum-ascii-delete-sum-for-two-strings

public class MinAsciiDeleteSum {
    public int minimumDeleteSum(String s1, String s2) {
        int n = s1.length();
        int m = s2.length();
        Integer dp[][] = new Integer[n][m];
        return solve(s1, s2, 0, 0, n, m, dp);
    }

    private int solve(String s1, String s2, int i, int j, int n, int m, Integer dp[][]) {
        if (i == n) {
            int ans = calcRemainingAscii(j, s2, m);
            return ans;
        }

        if (j == m) {
            int ans = calcRemainingAscii(i, s1, n);
            return ans;
        }

        if (dp[i][j] != null)
            return dp[i][j];

        int sum = Math.min(solve(s1, s2, i + 1, j, n, m, dp) + s1.charAt(i),
                solve(s1, s2, i, j + 1, n, m, dp) + s2.charAt(j));

        if (s1.charAt(i) == s2.charAt(j)) {
            sum = Math.min(sum, solve(s1, s2, i + 1, j + 1, n, m, dp));
        }

        return dp[i][j] = sum;
    }

    private int calcRemainingAscii(int i, String s, int n) {
        int sum = 0;
        while (i < n) {
            sum += s.charAt(i);
            i++;
        }
        return sum;
    }
}
