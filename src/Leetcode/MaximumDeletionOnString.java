package Leetcode;
// Problem Link: https://leetcode.com/problems/maximum-deletions-on-a-string/

public class MaximumDeletionOnString {

    Integer dp[];

    public int deleteString(String s) {

        int n = s.length();
        dp = new Integer[n];
        char ch = s.charAt(0);
        if (s.equals(("" + ch).repeat(n))) {
            return n;
        }
        return solve(s, 0, n);


    }

    private int solve(String s, int i, int n) {

        if (i >= n) {
            return 0;
        }

        if (dp[i] != null) {
            return dp[i];
        }

        int ans = 1;

        int z[] = zArray(s.substring(i));
        int ind = 1;
        int j = i + 1;
        for (; ind < z.length; j++, ind++) {
            int len = ind + 1;
            int half = len / 2;
            if (len % 2 == 1) {
                continue;
            }
            if (z[half] >= half) {
                ans = Math.max(ans, 1 + solve(s, j - half + 1, n));
            }
        }
        return dp[i] = ans;
    }

    private int[] zArray(String s) {

        int n = s.length();
        int z[] = new int[n];
        int l = 0;
        int r = 0;
        for (int i = 1; i < n; i++) {
            if (i > r) {
                l = r = i;
                while (r < n && s.charAt(r) == s.charAt(r - l)) {
                    r++;
                }

                z[i] = r - l;
                r--;
            } else {
                int k = i - l;
                if (z[k] < r - i + 1) {
                    z[i] = z[k];
                } else {
                    l = i;
                    while (r < n && s.charAt(r) == s.charAt(r - l)) {
                        r++;
                    }

                    z[i] = r - l;
                    r--;
                }
            }
        }

        return z;
    }
}