package Leetcode;

// Problem Link: https://leetcode.com/problems/find-the-occurrence-of-first-almost-equal-substring/description/

/**
 * Solution for Find the Occurrence of First Almost Equal Substring
 *
 * Problem: Find the first index where pattern occurs in s with at most 1 character difference
 * "Almost equal" means strings are equal OR differ in exactly one position
 *
 * Approach: Z-Algorithm with Prefix and Suffix Matching
 * - Use Z-algorithm to find longest matching prefixes
 * - Use Z-algorithm on reversed strings to find longest matching suffixes
 * - For each position, check if prefix_match + 1 + suffix_match >= pattern_length
 * - The "+1" accounts for the one allowed mismatch
 */
public class FindTheOccurrenceOfFirstAlmostEqualSubstring {

    /**
     * Finds the minimum starting index where pattern almost matches
     *
     * @param s The main string to search in
     * @param pattern The pattern to find
     * @return Minimum starting index, or -1 if not found
     */
    public int minStartingIndex(String s, String pattern) {

        int n = s.length();
        int m = pattern.length();

        // Z-array for prefix matching: pattern + s
        // zPre[i+m] tells how many characters match from position i in s
        int zPre[] = z((pattern + s).toCharArray(), m + n);

        // Z-array for suffix matching: reverse(s + pattern)
        // zSuff[n-i] tells how many characters match from the end
        int zSuff[] = z(new StringBuilder(s + pattern).reverse().toString().toCharArray(), m + n);

        // Check each possible starting position
        for (int i = 0; i <= n - m; i++) {
            // If prefix_match + 1 (mismatch) + suffix_match >= pattern_length
            // then we have at most 1 mismatch
            if (zPre[i + m] + 1 + zSuff[n - i] >= m) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Z-Algorithm: Computes Z-array for pattern matching
     *
     * Z[i] = length of longest substring starting from i that matches prefix
     * Example: For "aabcaab", Z = [0, 1, 0, 0, 3, 1, 0]
     *
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     *
     * @param ch Character array
     * @param n Length of array
     * @return Z-array
     */
    private int[] z(char ch[], int n) {

        int z[] = new int[n];
        int left = 0;   // Left boundary of Z-box
        int right = 0;  // Right boundary of Z-box

        for (int i = 1; i < n; i++) {
            // Case 1: i is outside the Z-box, compute from scratch
            if (i > right) {
                left = right = i;
                while (right < n && ch[right] == ch[right - left]) {
                    right++;
                }

                z[i] = right - left;
                right--;
            }
            // Case 2: i is inside the Z-box, use previously computed values
            else {
                int k = i - left;  // Corresponding position in prefix

                // If z[k] is within the Z-box, we can reuse it
                if (z[k] < right - i + 1) {
                    z[i] = z[k];
                }
                // Otherwise, we need to extend beyond the Z-box
                else {
                    left = i;
                    while (right < n && ch[right] == ch[right - left]) {
                        right++;
                    }
                    z[i] = right - left;
                    right--;
                }
            }
        }
        return z;

    }
}