package Leetcode;
//Problem Link: https://leetcode.com/problems/find-all-good-strings/

/**
 * Solution class for finding the count of "good" strings. A good string is a string of length n that: 1. Is
 * lexicographically between s1 and s2 (inclusive) 2. Does not contain the substring "evil"
 */
public class FindAllGoodStrings {

    /**
     * Finds the number of good strings between s1 and s2 that don't contain evil substring. Uses digit DP with KMP
     * pattern matching algorithm.
     *
     * @param n Length of the strings
     * @param s1 Lower bound string (inclusive)
     * @param s2 Upper bound string (inclusive)
     * @param evil The substring that must not appear in good strings
     * @return Count of good strings modulo 10^9 + 7
     */
    public int findGoodStrings(int n, String s1, String s2, String evil) {

        // Edge case: if s1 equals s2, check if it contains evil
        if (s1.equals(s2)) {
            if (s1.contains(evil)) {
                return 0;
            }
            return 1;
        }

        // Build LPS (Longest Prefix Suffix) array for KMP pattern matching
        int lps[] = lps(evil.toCharArray());

        // Count strings from 'a...a' to s2 (upper bound)
        int a = solve(s2.toCharArray(), 0, n, lps, evil.toCharArray(), 0, 1, new Integer[n][evil.length()][2]);

        // Count strings from 'a...a' to s1 (lower bound)
        int b = solve(s1.toCharArray(), 0, n, lps, evil.toCharArray(), 0, 1, new Integer[n][evil.length()][2]);

        // Calculate difference: strings in [s1, s2] = strings in [a...a, s2] - strings in [a...a, s1)
        int total = add(a, -b);

        // If s1 itself is a good string, we need to add 1 because we excluded it in the subtraction
        if (!s1.contains(evil)) {
            total = add(total, 1);
        }

        return total;

    }

    /**
     * Recursive DP function to count valid strings using digit DP approach.
     *
     * @param ch The boundary string (s1 or s2)
     * @param i Current position in the string being built
     * @param n Total length of the string
     * @param lps LPS array for the evil string (for KMP)
     * @param evil The evil substring to avoid
     * @param evilPos Current matching position in the evil string (KMP state)
     * @param flag 1 if we're still bounded by ch, 0 if we can use any character
     * @param dp Memoization table [position][evilPos][flag]
     * @return Count of valid strings from this state
     */
    private int solve(char ch[], int i, int n, int lps[], char evil[], int evilPos, int flag,
            Integer dp[][][]) {

        // Base case: if evil substring is fully matched, this path is invalid
        if (evilPos == evil.length) {
            return 0;
        }

        // Base case: if we've built a complete string of length n, it's valid
        if (i == n) {
            return 1;
        }

        // Return cached result if already computed
        if (dp[i][evilPos][flag] != null) {
            return dp[i][evilPos][flag];
        }

        int ans = 0;

        // Case 1: flag == 0 means we're no longer bounded, can use any character 'a' to 'z'
        if (flag == 0) {
            for (char c = 'a'; c <= 'z'; c++) {
                // Use KMP to find next state after adding character c
                int ind = evilPos;
                while (ind > 0 && evil[ind] != c) {
                    ind = lps[ind - 1];
                }

                // Update KMP state based on whether c matches
                if (evil[ind] == c) {
                    ans = add(ans, solve(ch, i + 1, n, lps, evil, ind + 1, flag, dp));
                } else {
                    ans = add(ans, solve(ch, i + 1, n, lps, evil, ind, flag, dp));
                }
            }
        } else {
            // Case 2: flag == 1 means we're still bounded by ch[i]
            char last = ch[i];

            // Try all characters less than ch[i] (these make us unbounded)
            for (char c = 'a'; c < last; c++) {
                // Use KMP to find next state
                int ind = evilPos;
                while (ind > 0 && evil[ind] != c) {
                    ind = lps[ind - 1];
                }

                // Recurse with flag=0 since we're now below the bound
                if (evil[ind] == c) {
                    ans = add(ans, solve(ch, i + 1, n, lps, evil, ind + 1, 0, dp));
                } else {
                    ans = add(ans, solve(ch, i + 1, n, lps, evil, ind, 0, dp));
                }
            }

            // Try using exactly ch[i] (keeps us bounded)
            var ind = evilPos;
            while (ind > 0 && evil[ind] != last) {
                ind = lps[ind - 1];
            }

            // Recurse with flag=1 since we're still at the bound
            if (evil[ind] == last) {
                ans = add(ans, solve(ch, i + 1, n, lps, evil, ind + 1, 1, dp));
            } else {
                ans = add(ans, solve(ch, i + 1, n, lps, evil, ind, 1, dp));
            }
        }

        // Cache and return result
        return dp[i][evilPos][flag] = ans;

    }

    /**
     * Helper function to add two numbers with modulo arithmetic. Handles negative numbers correctly by adding mod
     * before taking modulo.
     *
     * @param a First number
     * @param b Second number
     * @return (a + b) mod 10^9+7, always non-negative
     */
    private int add(long a, long b) {

        int mod = 1000000007;

        return (int) ((a + b + mod) % mod);
    }

    /**
     * Builds the LPS (Longest Prefix Suffix) array for KMP pattern matching. LPS[i] = length of the longest proper
     * prefix of pattern[0..i] which is also a suffix. This is used to efficiently skip characters during pattern
     * matching.
     *
     * @param ch The pattern string (evil substring)
     * @return LPS array where lps[i] is the length of longest proper prefix-suffix for ch[0..i]
     */
    private int[] lps(char ch[]) {

        int lps[] = new int[ch.length];
        var len = 0;  // Length of the previous longest prefix suffix
        var i = 1;    // Start from index 1 (lps[0] is always 0)
        var n = ch.length;

        while (i < n) {
            if (ch[i] == ch[len]) {
                // Characters match: extend the current prefix-suffix
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len == 0) {
                    // No prefix-suffix match possible
                    i++;
                } else {
                    // Try shorter prefix-suffix using previously computed LPS
                    len = lps[len - 1];
                }
            }
        }

        return lps;
    }
}