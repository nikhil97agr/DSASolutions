package Leetcode;

import java.util.Arrays;
import java.util.List;

//Problem Link: https://leetcode.com/contest/biweekly-contest-187/problems/minimum-cost-to-convert-string-iii/

/**
 * Solution for "Minimum Cost to Transform String with Rules"
 *
 * Problem: Transform a source string into a target string using pattern-replacement rules.
 *
 * Each rule has:
 * - A pattern (can contain wildcards '*')
 * - A replacement string (same length as pattern)
 * - A base cost
 *
 * Total cost of applying a rule = base cost + number of '*' in pattern
 *
 * Constraints:
 * - Each position in the string can only be used once
 * - Pattern matching: '*' matches any character, other characters must match exactly
 * - Replacement must transform the matched portion to match target at those positions
 *
 * Strategy: Dynamic Programming (Bottom-Up)
 * - Process string from right to left
 * - At each position, decide: skip (if already matches) or apply a rule
 * - Find minimum cost to transform suffix starting at each position
 */
public class MinimumCostToConvertStringIII{

    /**
     * Calculate minimum cost to transform source into target using given rules.
     *
     * Algorithm: Dynamic Programming with Suffix Processing
     *
     * dp[i] = minimum cost to transform source[i..n-1] into target[i..n-1]
     *
     * Base case: dp[n] = 0 (empty suffix requires no cost)
     *
     * Transition at position i:
     * 1. If source[i] == target[i]: dp[i] = dp[i+1] (skip, no rule needed)
     * 2. Try each rule j:
     *    - Check if rule can be applied at position i
     *    - If valid: dp[i] = min(dp[i], modifiedCost[j] + dp[i + pattern_length])
     *
     * Time Complexity: O(n * m * max_pattern_length)
     * Space Complexity: O(n)
     *
     * @param source The starting string
     * @param target The desired target string
     * @param rules List of [pattern, replacement] pairs
     * @param costs Base cost for each rule
     * @return Minimum total cost, or -1 if impossible
     */
    public int minCost(String source, String target, List<List<String>> rules, int[] costs) {

        var n = source.length();

        // Strings must have same length (rules preserve length)
        if (source.length() != target.length()) {
            return -1;
        }

        var m = rules.size();

        // Preprocess rules: calculate actual costs and pattern lengths
        var length = new int[m];
        int modifiedCost[] = new int[m];

        for (int i = 0; i < m; i++) {
            int wild = 0;  // Count wildcards in pattern
            var pattern = rules.get(i).get(0);

            // Count '*' characters in pattern
            for (char c : pattern.toCharArray()) {
                if (c == '*') {
                    wild++;
                }
            }

            // Actual cost = base cost + number of wildcards
            modifiedCost[i] = costs[i] + wild;
            length[i] = pattern.length();
        }

        // DP array: dp[i] = min cost to transform source[i..n-1] to target[i..n-1]
        long dp[] = new long[n + 1];

        // Initialize all positions as impossible (MAX_VALUE)
        Arrays.fill(dp, Long.MAX_VALUE);

        // Base case: empty suffix costs 0
        dp[n] = 0;

        // Process from right to left (bottom-up DP)
        for (int i = n - 1; i >= 0; i--) {
            // Option 1: If characters already match, no rule needed
            if (source.charAt(i) == target.charAt(i) && dp[i + 1] != Long.MAX_VALUE) {
                dp[i] = dp[i + 1];
            }

            // Option 2: Try applying each rule at position i
            for (int j = 0; j < m; j++) {
                int l = length[j];

                // Check if rule fits within bounds
                if (i + l > n) {
                    continue;
                }

                // Check if rest of string is solvable
                if (dp[i + l] == Long.MAX_VALUE) {
                    continue;
                }

                // Check if rule can be applied at this position
                if (check(source, target, i, rules.get(j))) {
                    // Update minimum cost
                    dp[i] = Math.min(dp[i], modifiedCost[j] + dp[i + l]);
                }
            }
        }

        // Return result: -1 if impossible, otherwise the minimum cost
        return dp[0] == Long.MAX_VALUE ? -1 : (int) dp[0];
    }


    /**
     * Check if a rule can be applied at a specific position in the source string.
     *
     * A rule can be applied at position 'start' if:
     * 1. Pattern matches source at this position (considering wildcards)
     *    - '*' in pattern matches any character in source
     *    - Non-wildcard characters must match exactly
     *
     * 2. Replacement produces the target at this position
     *    - After applying replacement, result must match target exactly
     *
     * Example:
     * source = "abc", target = "def", pattern = "a*c", replacement = "d*f", start = 0
     * - Pattern check: 'a' == 'a' ✓, '*' matches 'b' ✓, 'c' == 'c' ✓
     * - Replacement check: 'd' == 'd' ✓, '*' → 'b' but need 'e' ✗
     *
     * @param src The source string
     * @param target The target string
     * @param start Starting position to check
     * @param rule The rule [pattern, replacement]
     * @return true if rule can be applied at this position, false otherwise
     */
    private boolean check(String src, String target, int start, List<String> rule) {

        String pattern = rule.get(0);
        String replace = rule.get(1);

        // Check each character in the pattern
        for (int j = 0; j < pattern.length(); j++) {
            char p = pattern.charAt(j);        // Pattern character
            char s = src.charAt(start + j);    // Source character at this position
            char t = target.charAt(start + j); // Target character at this position
            char r = replace.charAt(j);        // Replacement character

            // Pattern must match source (unless it's a wildcard)
            if (p != '*' && p != s) {
                return false;
            }

            // Replacement must produce target
            if (t != r) {
                return false;
            }
        }

        return true;
    }
}