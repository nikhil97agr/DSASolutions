package Leetcode;

import algorithms.ManacherAlgorithm;

//Problem Link: https://leetcode.com/problems/maximum-product-of-the-length-of-two-palindromic-substrings/

/**
 * Solution for finding maximum product of lengths of two non-overlapping palindromic substrings.
 *
 * Problem: Given a string s, find two non-overlapping palindromic substrings such that the product of their lengths is
 * maximum.
 *
 * Example: s = "ababbb" - Can choose "aba" (length 3) and "bbb" (length 3) - Product = 3 * 3 = 9
 *
 * Example 2: s = "zaaaxbbby" - Can choose "aaa" (length 3) and "bbb" (length 3) - Product = 3 * 3 = 9
 *
 * Key insight: Manacher's Algorithm + Dynamic Programming
 *
 * Observation 1: Non-overlapping constraint - Split string at some position i: left part [0, i] and right part [i+1,
 * n-1] - Find longest palindrome in left part and longest in right part - Try all split positions and take maximum
 * product
 *
 * Observation 2: Efficient palindrome finding - Use Manacher's algorithm to find all palindromes in O(n) - For each
 * center position, get the radius of palindrome centered there
 *
 * Observation 3: Precompute optimal palindromes - left[i] = length of longest palindrome that ends at or before
 * position i - right[i] = length of longest palindrome that starts at or after position i - Answer = max(left[i] *
 * right[i+1]) for all i
 *
 * Manacher's Algorithm: - Finds all palindromic substrings in O(n) time - Returns array p where p[i] = radius of
 * palindrome centered at position i - Works on transformed string with separators (e.g., "aba" → "#a#b#a#")
 *
 * Algorithm: 1. Use Manacher's to find all palindrome radii 2. Extract radii for original character positions 3. Build
 * left[] array: longest palindrome ending at or before each position 4. Build right[] array: longest palindrome
 * starting at or after each position 5. Optimize left[] and right[] with constraint propagation 6. Find maximum product
 * by trying all split positions
 *
 * Time Complexity: O(n) - Manacher's + linear passes Space Complexity: O(n) for arrays
 */
public class MaximumProductOfTheLengthOfTwoPalindromicSubstrings {

    /**
     * Finds maximum product of two non-overlapping palindrome lengths.
     *
     * @param s Input string
     * @return Maximum product of two non-overlapping palindrome lengths
     */
    public long maxProduct(String s) {

        // ================================================================
        // STEP 1: Run Manacher's algorithm to find all palindromes
        // ================================================================
        ManacherAlgorithm algo = new ManacherAlgorithm(s);
        int p[] = algo.p;  // p[i] = radius of palindrome at position i in transformed string
        int n = s.length();

        // Arrays to store optimal palindrome lengths
        int left[] = new int[n];   // left[i] = longest palindrome ending at or before i
        int right[] = new int[n];  // right[i] = longest palindrome starting at or after i
        int radius[] = new int[n]; // radius[i] = palindrome radius at original position i

        // ================================================================
        // STEP 2: Extract palindrome radii for original positions
        // ================================================================
        // Manacher's works on transformed string: "abc" → "#a#b#c#"
        // Original characters are at odd positions: 1, 3, 5, ...
        // Extract radii for these positions
        for (int i = 1, j = 0; j < n; i += 2, j++) {
            radius[j] = p[i];
        }

        // ================================================================
        // STEP 3: Build left[] array - longest palindrome ending at or before i
        // ================================================================
        // For each palindrome centered at i with radius r:
        // - Actual length in original string: radius[i] - 1
        // - This palindrome ends at position: i + half
        // - Update left[ending_position] with this length
        for (int i = 0; i < n; i++) {
            int len = radius[i] - 1;  // Actual palindrome length
            int half = len / 2;        // Half length (distance from center to edge)

            // Palindrome centered at i extends to i+half on the right
            left[i + half] = Math.max(left[i + half], len);

            // Propagate: if position i-1 has a palindrome, it also works for i
            if (i > 0) {
                left[i] = Math.max(left[i], left[i - 1]);
            }
        }

        // ================================================================
        // STEP 4: Build right[] array - longest palindrome starting at or after i
        // ================================================================
        // For each palindrome centered at i with radius r:
        // - This palindrome starts at position: i - half
        // - Update right[starting_position] with this length
        for (int i = n - 1; i >= 0; i--) {
            int len = radius[i] - 1;  // Actual palindrome length
            int half = len / 2;        // Half length

            // Palindrome centered at i extends to i-half on the left
            right[i - half] = Math.max(right[i - half], len);

            // Propagate: if position i+1 has a palindrome, it also works for i
            if (i + 1 < n) {
                right[i] = Math.max(right[i], right[i + 1]);
            }
        }

        // ================================================================
        // STEP 5: Optimize left[] with constraint propagation
        // ================================================================
        // Key insight: If we have a palindrome of length L ending at position i+1,
        // we can have a palindrome of length at least L-2 ending at position i
        // (by removing first and last characters).
        //
        // Why L-2? If palindrome ends at i+1, we can "shift" it left by 1,
        // losing 2 characters (one from each end).
        //
        // Process right to left to propagate constraints
        for (int i = n - 2; i >= 0; i--) {
            left[i] = Math.max(left[i], left[i + 1] - 2);
        }

        // ================================================================
        // STEP 6: Optimize right[] with constraint propagation
        // ================================================================
        // Similar logic: If we have a palindrome of length L starting at i-1,
        // we can have a palindrome of length at least L-2 starting at i.
        //
        // Process left to right to propagate constraints
        for (int i = 1; i < n; i++) {
            right[i] = Math.max(right[i - 1] - 2, right[i]);
        }

        // ================================================================
        // STEP 7: Find maximum product by trying all split positions
        // ================================================================
        // For each split position i:
        // - Left palindrome: best palindrome in [0, i]
        // - Right palindrome: best palindrome in [i+1, n-1]
        // - Product: left[i] * right[i+1]
        long ans = 0;
        for (int i = 0; i < n - 1; i++) {
            ans = Math.max(ans, 1l * left[i] * right[i + 1]);
        }
        return ans;
    }
}

