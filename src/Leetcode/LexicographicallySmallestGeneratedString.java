package Leetcode;

import java.util.Arrays;

//Problem Link: https://leetcode.com/problems/lexicographically-smallest-generated-string

/**
 * Solution for finding the lexicographically smallest string that satisfies constraints.
 *
 * Given two strings str1 and str2: - str1 contains 'T' (true) and 'F' (false) characters - str2 is a pattern string -
 * Need to generate a result string where: - If str1[i] == 'T', then str2 must appear as a substring starting at
 * position i in result - If str1[i] == 'F', then str2 must NOT appear as a substring starting at position i in result -
 * Return the lexicographically smallest such string, or empty string if impossible
 */
public class LexicographicallySmallestGeneratedString {

    /**
     * Generates the lexicographically smallest string satisfying the constraints.
     *
     * Algorithm: 1. First pass: Place str2 at all positions where str1[i] == 'T' (required matches) 2. Fill remaining
     * positions with 'a' (smallest character) 3. Second pass: For positions where str1[i] == 'F', ensure str2 doesn't
     * match by changing the rightmost unfilled character to 'b'
     *
     * @param str1 String of 'T' and 'F' indicating where str2 should/shouldn't appear
     * @param str2 The pattern string to match or avoid
     * @return Lexicographically smallest valid string, or empty string if impossible
     */
    public String generateString(String str1, String str2) {

        int n = str1.length();
        int m = str2.length();
        char c1[] = str1.toCharArray();
        char c2[] = str2.toCharArray();

        // Result string has length n + m - 1 (minimum to accommodate all positions)
        char res[] = new char[n + m - 1];
        Arrays.fill(res, '?');  // '?' represents unfilled positions
        int len = n + m - 1;

        // Track which positions were explicitly filled (not just defaulted to 'a')
        boolean filled[] = new boolean[n + m - 1];

        // First pass: Handle all 'T' positions (str2 MUST appear here)
        for (int i = 0; i < n; i++) {
            if (c1[i] == 'F') {
                continue;  // Skip 'F' positions for now
            }

            // Place str2 starting at position i
            for (int j = 0; j < m; j++) {
                int pos = i + j;

                // Check if position is compatible with str2[j]
                if (res[pos] == '?' || res[pos] == c2[j]) {
                    res[pos] = c2[j];
                    filled[pos] = true;
                } else {
                    // Conflict: different character already required at this position
                    return "";
                }
            }
        }

        // Fill all remaining unfilled positions with 'a' (lexicographically smallest)
        for (int i = 0; i < len; i++) {
            if (res[i] == '?') {
                res[i] = 'a';
            }
        }

        // Second pass: Handle all 'F' positions (str2 must NOT appear here)
        for (int i = 0; i < n; i++) {
            if (c1[i] == 'T') {
                continue;  // Already handled in first pass
            }

            // Check if str2 currently matches at position i
            if (!eq(res, i, c2, m)) {
                continue;  // Already doesn't match, no action needed
            }

            // str2 matches but shouldn't - need to break the match
            // Change the rightmost unfilled character to 'b' (next smallest after 'a')
            int j;
            for (j = m - 1; j >= 0; j--) {
                int pos = i + j;
                if (!filled[pos]) {
                    filled[pos] = true;
                    res[pos] = 'b';  // Change from 'a' to 'b' to break the match
                    break;
                }
            }

            // If no unfilled position found, impossible to break the match
            if (j == -1) {
                return "";
            }
        }

        return new String(res);
    }

    /**
     * Helper method to check if str2 matches the result string at a given position.
     *
     * @param res The result string being constructed
     * @param start Starting position in res to check
     * @param c2 The pattern string (str2) to match against
     * @param len Length of the pattern string
     * @return true if c2 matches res starting at position 'start', false otherwise
     */
    private boolean eq(char res[], int start, char c2[], int len) {

        // Compare each character of c2 with corresponding position in res
        for (int j = 0; j < len; j++) {
            if (res[start + j] != c2[j]) {
                return false;
            }
        }

        return true;
    }
}