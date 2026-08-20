package Leetcode;
//Problem Link: https://leetcode.com/contest/weekly-contest-511/problems/transform-binary-string-using-subsequence-sort/description/

/**
 * Solution for "Transform Binary String Through Subsequence Sorting"
 *
 * Problem: Given a binary string s and array of pattern strings (with wildcards '?'), determine if s can be transformed
 * into each pattern through subsequence sorting operations.
 *
 * Operation allowed: Select any subsequence of s, sort it (0s before 1s), replace it back.
 *
 * Key Insight: Sorting subsequences can only move 0s to the left. - We can move 0s leftward by selecting them with 1s
 * on their left and sorting - We CANNOT move 1s to the left (sorting keeps 1s after 0s) - We CANNOT move 0s to the
 * right
 *
 * Constraint Check: For s to be transformable into target string t: 1. Must have same count of 1s (1s cannot be
 * created/destroyed) 2. At every prefix position i: count_of_0s_in_t[0..i] <= count_of_0s_in_s[0..i] (We can only move
 * 0s left, so target can't require more 0s at any prefix than source has)
 *
 * Strategy: - Precompute prefix count of 0s in s - For each pattern, assign '?' greedily to satisfy constraints -
 * Verify the "0s invariant" at every position
 */
public class TransformBinaryStringUsingSubsequenceSort {

    /**
     * Check if s can be transformed into each pattern string through subsequence sorting.
     *
     * Algorithm: 1. Preprocess s: calculate prefix count of '0's 2. For each pattern in strs: a. Assign wildcards '?'
     * to '0' or '1' to match count of 1s b. Verify that at every prefix position, we have enough 0s
     *
     * Time Complexity: O(n + m*n) where n = length of s, m = number of patterns Space Complexity: O(n)
     *
     * @param s The source binary string
     * @param strs Array of pattern strings (with '0', '1', '?')
     * @return Boolean array indicating if each pattern is achievable
     */
    public boolean[] transformStr(String s, String[] strs) {

        int n = s.length();
        int m = strs.length;
        boolean ans[] = new boolean[m];

        // Precompute prefix count of '0's in source string s
        // pre[i] = number of '0's in s[0..i-1]
        int pre[] = new int[n + 1];
        for (int i = 0; i < n; i++) {
            pre[i + 1] = pre[i];
            if (s.charAt(i) == '0') {
                pre[i + 1]++;
            }
        }

        // Total count of '1's in source string
        int one = n - pre[n];

        // Check each pattern string
        for (int i = 0; i < m; i++) {
            if (check(pre, strs[i].toCharArray(), one)) {
                ans[i] = true;
            }
        }

        return ans;
    }


    /**
     * Check if a pattern (with wildcards) can be achieved from source string s.
     *
     * Two-phase checking:
     *
     * Phase 1: Count Validation - Count existing '1's in pattern - Calculate how many wildcards '?' need to become '1'
     * to match source's count of 1s - If impossible (need negative or more than available wildcards), return false
     *
     * Phase 2: Prefix Invariant Check (GREEDY WILDCARD ASSIGNMENT) - Assign wildcards '?' greedily from left to right -
     * First (w - diff) wildcards become '0', rest become '1' - At each position i, verify: count_of_0s_in_pattern[0..i]
     * <= count_of_0s_in_source[0..i]
     *
     * Why greedy works: - We want to maximize 0s on the left (since we can only move 0s left via sorting) - Assigning
     * early '?' to '0' is always safe if later we have enough 1s
     *
     * Example: s = "00110", pattern = "??1?0" - s has 3 zeros, 2 ones - pattern has 1 one, 3 wildcards - Need 1 more
     * one from wildcards: diff = 2 - 1 = 1 - Assign first (3-1=2) wildcards to '0': "001?0" → "00110" ✓
     *
     * @param pre Prefix count array of 0s in source string
     * @param c2 Character array of pattern string
     * @param ones Total count of 1s in source string
     * @return true if pattern is achievable, false otherwise
     */
    private boolean check(int pre[], char c2[], int ones) {

        // Count existing '1's and wildcards '?' in pattern
        int o = 0;    // Count of '1's in pattern
        int w = 0;    // Count of '?'s (wildcards) in pattern
        for (char c : c2) {
            if (c == '1') {
                o++;
            } else if (c == '?') {
                w++;
            }
        }

        // Calculate how many wildcards must become '1'
        int diff = ones - o;

        // Validation: Check if we can match the count of 1s
        if (diff < 0 || diff > w) {
            // Need negative 1s (impossible) or need more 1s than wildcards available
            return false;
        }

        // Phase 2: Greedy wildcard assignment and prefix validation
        // Assign first (w - diff) wildcards to '0', rest to '1'
        int wVis = 0;  // Count of wildcards visited so far
        int z = 0;     // Count of '0's in pattern up to current position

        for (int i = 0; i < c2.length; i++) {
            if (c2[i] == '0') {
                z++;
            } else if (c2[i] == '?') {
                wVis++;

                // Assign this wildcard to '0' if we haven't used up our '0' quota
                if (wVis <= w - diff) {
                    z++;
                }
                // Otherwise, this wildcard becomes '1' (don't increment z)
            }
            // If c2[i] == '1', don't increment z

            // Invariant check: At position i, pattern must not have more 0s than source
            // pre[i+1] = count of 0s in source[0..i]
            // z = count of 0s in pattern[0..i]
            if (z < pre[i + 1]) {
                // Source has more 0s at this prefix than pattern
                // Since we can only move 0s LEFT, we cannot achieve this pattern
                return false;
            }
        }

        return true;
    }
}