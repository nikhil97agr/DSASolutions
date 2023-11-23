package Leetcode;

// Problem Link : https://leetcode.com/problems/interleaving-string

public class InterleavingString {
    public boolean isInterleave(String s1, String s2, String s3) {
        int l1 = s1.length();
        int l2 = s2.length();
        int l3 = s3.length();
        if (l1 + l2 != l3)
            return false;
        Boolean dp[][] = new Boolean[l1 + 1][l2 + 1];
        return solve(s1, s2, s3, l1, l2, l3, 0, 0, dp);
    }

    private boolean solve(String s1, String s2, String s3, int l1, int l2, int l3, int i, int j,
            Boolean[][] dp) {

        if (i == l1 && j == l2) {
            return true;
        }
        if (dp[i][j] != null) {
            return dp[i][j];
        }

        boolean ans = (i < l1 && s1.charAt(i) == s3.charAt(i + j) ? solve(s1, s2, s3, l1, l2, l3, i + 1, j, dp) : false)
                ||
                (j < l2 && s2.charAt(j) == s3.charAt(i + j) ? solve(s1, s2, s3, l1, l2, l3, i, j + 1, dp) : false);

        return dp[i][j] = ans;
    }

}
