package Leetcode;

import java.util.Arrays;

//Problem Link: https://leetcode.com/problems/lexicographically-smallest-string-after-adjacent-removals/

/**
 * Solution for finding lexicographically smallest string after removing adjacent pairs.
 *
 * Problem: Given a string, repeatedly remove adjacent pairs of characters where the characters are "adjacent" in the
 * alphabet (differ by 1, considering circular: a-z-a). Find the lexicographically smallest string that can be
 * achieved.
 *
 * Adjacent character definition: - Characters differ by exactly 1: (a,b), (b,c), ..., (y,z) - Circular: (z,a) are also
 * adjacent - Can be in any order: "ab" or "ba" both qualify
 *
 * Key observations: 1. Removals must be balanced (remove even-length substrings) 2. A substring [i,j] can be completely
 * removed if: - It has even length - Can be reduced to nothing through valid pair removals 3. Need to determine which
 * substrings are "removable"
 *
 * Example: s = "acdb" - Can remove "cd" → "ab" - Can remove "ab" → "" - Or remove "ac" first → "db", then remove "db" →
 * "" - Result: "" is lexicographically smallest
 *
 * Algorithm (Two-phase DP):
 *
 * Phase 1: Determine which substrings [i,j] can be completely removed - remove[i][j] = true if substring from i to j
 * can be fully removed - Base case: Adjacent pairs that satisfy the adjacency condition - Recurrence: [i,j] removable
 * if we can pair ch[i] with some ch[k] where (i+1, k-1) and (k+1, j) are both removable
 *
 * Phase 2: Find lexicographically smallest result - dp[i] = lexicographically smallest string starting from index i -
 * Option 1: Keep ch[i] and take dp[i+1] - Option 2: Remove substring [i,j] if removable, take dp[j+1] - Choose the
 * lexicographically smallest option
 *
 * Time Complexity: O(n³) for interval DP + O(n²) for result construction Space Complexity: O(n²) for remove array +
 * O(n) for dp array
 */
public class LexicographicallySmallestStringAfterAdjacentRemovals {

    /**
     * Finds the lexicographically smallest string after optimal removals.
     *
     * @param s Input string
     * @return Lexicographically smallest achievable string
     */
    public String lexicographicallySmallestString(String s) {

        var n = s.length();

        // remove[i][j] = true if substring from index i to j (inclusive) can be fully removed
        var remove = new boolean[n][n];
        var ch = s.toCharArray();

        // ================================================================
        // PHASE 1: Build removability table using interval DP
        // ================================================================

        // Base case: Check all adjacent pairs (length 2)
        for (int i = 0; i + 1 < n; i++) {
            if (check(ch[i], ch[i + 1])) {
                remove[i][i + 1] = true;  // This pair can be removed
            }
        }

        // Build up for even lengths (only even-length substrings can be fully removed)
        // Process lengths 4, 6, 8, ... up to n
        for (var len = 4; len <= n; len += 2) {
            for (int i = 0; i + len - 1 < n; i++) {
                int j = i + len - 1;  // End index of current substring

                // Try pairing ch[i] with ch[k] for all valid k
                // k must be at odd distance from i (to maintain even-length partitions)
                for (int k = i + 1; k <= j; k += 2) {
                    if (check(ch[i], ch[k])) {
                        // Check if we can remove ch[i] and ch[k] as a pair
                        // This requires:
                        // 1. Everything between i and k (exclusive) is removable
                        // 2. Everything between k and j (exclusive) is removable

                        var left = k == i + 1 || remove[i + 1][k - 1];   // Left part removable
                        var right = k == j || remove[k + 1][j];          // Right part removable

                        if (left && right) {
                            // We can remove [i,k] by pairing ch[i] with ch[k]
                            // and everything in between can also be removed
                            remove[i][j] = true;
                            break;  // Found a valid removal strategy, no need to try other k
                        }
                    }
                }
            }
        }

        // ================================================================
        // PHASE 2: Compute lexicographically smallest result using DP
        // ================================================================

        // dp[i] = lexicographically smallest string starting from index i
        String dp[] = new String[n];
        Arrays.fill(dp, "");

        // Build dp array from right to left
        for (int i = n - 1; i >= 0; i--) {
            // Option 1: Keep character at position i
            dp[i] = ch[i] + (i + 1 < n ? dp[i + 1] : "");

            // Option 2: Try removing substring [i,j] if possible
            for (int j = i + 1; j < n; j += 2) {  // j must be at odd distance (even-length substring)
                if (remove[i][j]) {
                    // We can remove substring [i,j] completely
                    if (j + 1 == n) {
                        // Removing to the end results in empty string
                        dp[i] = "";
                    } else if (dp[j + 1].compareTo(dp[i]) < 0) {
                        // Removing [i,j] gives lexicographically smaller result
                        dp[i] = dp[j + 1];
                    }
                }
            }
        }

        return dp[0];  // Best result starting from index 0
    }

    /**
     * Checks if two characters are adjacent in the alphabet (considering circular wrap).
     *
     * Adjacent means: - Differ by 1: a-b, b-c, ..., y-z - Circular: z-a (difference of 25)
     *
     * Examples: - check('a', 'b') = true  (diff = 1) - check('b', 'a') = true  (diff = 1) - check('z', 'a') = true
     * (diff = 25, circular) - check('a', 'z') = true  (diff = 25, circular) - check('a', 'c') = false (diff = 2)
     *
     * @param a First character
     * @param b Second character
     * @return true if characters are adjacent in alphabet, false otherwise
     */
    private boolean check(char a, char b) {

        int diff = Math.abs(a - b);

        // Adjacent if differ by 1, or wrap around (z to a: diff = 25)
        return diff == 1 || diff == 25;
    }
}