package Leetcode;

//Problem Link: https://leetcode.com/problems/count-palindromic-subsequences/

public class CountPalindromicSubsequences {

    int mod = 1_000_000_007;
    int[][][] preSub;
    int[][][] suffSub;

    public int countPalindromes(String s) {

        int ans = 0;
        int n = s.length();
        int ch[] = new int[n];
        for (int i = 0; i < n; i++) {
            ch[i] = s.charAt(i) - '0';
        }

        preSub = new int[n + 1][10][10];
        suffSub = new int[n + 1][10][10];

        for (int d1 = 0; d1 < 10; d1++) {

            for (int d2 = 0; d2 < 10; d2++) {
                int sub = 0;
                int c1 = 0;
                for (int i = 0; i < n; i++) {
                    if (ch[i] != d1 && ch[i] != d2) {
                        preSub[i + 1][d1][d2] = sub;
                        continue;
                    }

                    if (d2 == ch[i]) {
                        sub = add(sub, c1);
                    }
                    if (d1 == ch[i]) {
                        c1++;
                    }

                    preSub[i + 1][d1][d2] = sub;
                }
            }
        }

        for (int d1 = 0; d1 < 10; d1++) {

            for (int d2 = 0; d2 < 10; d2++) {
                int sub = 0;
                int c1 = 0;
                for (int i = n - 1; i >= 0; i--) {
                    if (ch[i] != d1 && ch[i] != d2) {
                        suffSub[i][d1][d2] = sub;
                        continue;
                    }

                    if (d2 == ch[i]) {
                        sub = add(sub, c1);
                    }
                    if (d1 == ch[i]) {
                        c1++;
                    }
                    suffSub[i][d1][d2] = sub;
                }
            }
        }
        for (int i = 0; i < n; i++) {
            for (int d1 = 0; d1 < 10; d1++) {
                for (int d2 = 0; d2 < 10; d2++) {

                    int left = preSub[i][d1][d2];
                    int right = suffSub[i + 1][d1][d2];
                    ans = add(ans, prod(left, right));
                }
            }
        }

        return ans;
    }

    private int add(long a, long b) {

        return (int) ((a + b + mod) % mod);
    }

    private int prod(long a, long b) {

        return (int) ((a * b) % mod);
    }
}