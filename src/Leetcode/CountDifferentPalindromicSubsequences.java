package Leetcode;
//Problem Link: https://leetcode.com/problems/count-different-palindromic-subsequences/

public class CountDifferentPalindromicSubsequences {

    public int countPalindromicSubsequences(String s) {

        int n = s.length();
        char ch[] = s.toCharArray();
        int dp[][] = new int[n][n];
        for (int i = n - 1; i >= 0; i--) {
            /**
             * String of length 1 is always palindrome
             */
            dp[i][i] = 1;
            for (int j = i + 1; j < n; j++) {

                /**
                 * if character at the end ar not equals, then we can form palindrome by either
                 * taking the left part or right part but not both because it will lead to
                 * double counting
                 */
                if (ch[i] != ch[j]) {
                    dp[i][j] = add(dp[i + 1][j], add(dp[i][j - 1], -dp[i + 1][j - 1]));

                } else {
                    /**
                     * if characters at the end are equal then we find the start and end point in the string between i & j which are equals to ch[i]
                     * and then 3 cases are possible
                     */
                    dp[i][j] = prod(dp[i + 1][j - 1], 2);
                    int left = i + 1;
                    int right = j - 1;
                    while (left <= right && ch[left] != ch[i]) {
                        left++;
                    }

                    while (left <= right && ch[right] != ch[i]) {
                        right--;
                    }

                    /**
                     * if no such character found then we add 2 to the answer because we can form palindrome of length 1 and 2
                     */
                    if (left > right) {
                        dp[i][j] = add(dp[i][j], 2);

                    }
                    /**
                     * if we found only one character then we add 1 to the answer because we can form palindrome of length 2 as of length 1 is already there
                     */
                    else if (left == right) {
                        dp[i][j] = add(dp[i][j], 1);
                    }
                    /**
                     * if we found two characters then we subtract the dp[left+1][right-1] because it will be counted twices
                     */
                    else {
                        dp[i][j] = add(dp[i][j], -dp[left + 1][right - 1]);
                    }
                }
            }
        }

        return dp[0][n - 1];
    }

    private int add(long a, long b) {

        long mod = 1_000_000_007;
        return (int) ((a + b + mod) % mod);
    }

    private int prod(long a, long b) {

        long mod = 1_000_000_007;
        return (int) ((a * b) % mod);
    }
}