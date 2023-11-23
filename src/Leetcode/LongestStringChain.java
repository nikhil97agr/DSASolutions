package Leetcode;

// Problem Link : https://leetcode.com/problems/longest-string-chain

import java.util.Arrays;

public class LongestStringChain {
    public int longestStrChain(String[] words) {
        int n = words.length;
        int dp[] = new int[n];
        Arrays.fill(dp, 1);
        int ans = 1;
        Arrays.sort(words, (a, b) -> Integer.compare(a.length(), b.length()));

        int length[] = new int[n];

        for (int i = 0; i < n; i++) {
            length[i] = words[i].length();
        }

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (length[i] == length[j] + 1 && isSubSeq(words[i], words[j]) && dp[i] < dp[j] + 1) {
                    dp[i] = dp[j] + 1;
                }
            }
        }
        for (int x : dp) {
            ans = Math.max(ans, x);
        }
        return ans;

    }

    private boolean isSubSeq(String s, String t) {
        int n = s.length();
        int m = t.length();

        int i = 0;
        int j = 0;

        while (i < n && j < m) {
            if (s.charAt(i) == t.charAt(j)) {
                j++;
            }
            i++;
        }

        return j == m;
    }

}
