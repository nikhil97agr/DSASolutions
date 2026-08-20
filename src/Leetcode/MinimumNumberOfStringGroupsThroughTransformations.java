package Leetcode;

import java.util.HashSet;

//Problem Link: https://leetcode.com/contest/weekly-contest-511/problems/minimum-number-of-string-groups-through-transformations/description/

/**
 * Solution for "Minimum Groups of String Equivalence Through Even-Odd Cyclic Shifts"
 *
 * Problem: Partition words into minimum groups where strings are equivalent if one can be transformed into the other
 * through a specific transformation.
 *
 * Transformation definition: 1. Extract characters at even indices (E) and odd indices (O) separately 2. Independently
 * cyclically shift E and O to the right by any amount 3. Reconstruct string with shifted E at even positions, shifted O
 * at odd positions
 *
 * Example: "abcdef" - Even indices (0,2,4): "ace" - Odd indices (1,3,5): "bdf" - Can shift "ace" → "eac" and "bdf" →
 * "fbd" to get "efabcd" - So "abcdef" is equivalent to "efabcd"
 *
 * Key Insight: Canonical Form Representation - Two strings are equivalent if they have the same even/odd subsequences
 * (up to rotation) - Use minimum lexicographical rotation as canonical form - Group strings by their canonical forms
 *
 * Strategy: 1. For each word, extract even and odd subsequences 2. Find minimum lexicographical rotation for each
 * (canonical form) 3. Use "canonical_even:canonical_odd" as unique identifier 4. Count distinct identifiers using
 * HashSet
 */
public class MinimumNumberOfStringGroupsThroughTransformations {

    /**
     * Calculate minimum number of groups needed to partition equivalent strings.
     *
     * Algorithm: 1. For each word: a. Separate characters at even/odd indices b. Find canonical form (minimum
     * lexicographical rotation) for each c. Create unique signature: "canonical_even:canonical_odd" 2. Count distinct
     * signatures (each represents one equivalence class)
     *
     * Why this works: - Strings are equivalent ⟺ same even subsequence (up to rotation) AND same odd subsequence (up to
     * rotation) - Canonical form ensures all rotations map to same representation - Different canonical forms ⟹
     * different equivalence classes
     *
     * Time Complexity: O(N * L) where N = number of words, L = average word length Space Complexity: O(N * L) for
     * storing canonical forms in set
     *
     * @param words Array of input strings
     * @return Minimum number of groups (equivalence classes)
     */
    public int minimumGroups(String[] words) {

        var set = new HashSet<String>();

        for (String w : words) {
            // Special case: strings of length ≤ 2 have at most 1 char at even/odd positions
            // No meaningful rotation possible, so they are their own canonical form
            if (w.length() <= 2) {
                set.add(w);
                continue;
            }

            // Extract even-indexed and odd-indexed characters
            StringBuilder even = new StringBuilder();
            StringBuilder odd = new StringBuilder();

            for (int i = 0; i < w.length(); i++) {
                if (i % 2 == 0) {
                    even.append(w.charAt(i));  // Characters at positions 0, 2, 4, ...
                } else {
                    odd.append(w.charAt(i));   // Characters at positions 1, 3, 5, ...
                }
            }

            // Find minimum lexicographical rotation for even and odd subsequences
            var minE = get(even.toString());
            var minO = get(odd.toString());

            // Create canonical signature: even_canonical:odd_canonical
            // All equivalent strings will have the same signature
            set.add(minE + ":" + minO);
        }

        // Number of distinct signatures = number of equivalence classes = minimum groups
        return set.size();
    }

    /**
     * Find the minimum lexicographical rotation of a string (Booth's Algorithm).
     *
     * This is a classic algorithm to find the lexicographically smallest rotation of a circular string in O(n) time.
     *
     * Example: - Input: "bca" - Rotations: "bca", "cab", "abc" - Minimum: "abc" - Output: "abc"
     *
     * Algorithm (Booth's Algorithm): - Maintain two candidate starting positions i and j - Compare rotations starting
     * at i and j character by character - Eliminate the lexicographically larger one - Skip over matching prefixes
     * efficiently
     *
     * Invariant: All positions in [0, min(i,j)) have been ruled out as optimal starts
     *
     * @param s The input string
     * @return The lexicographically minimum rotation of s
     */
    private String get(String s) {

        var n = s.length();
        int i = 0;  // First candidate starting position
        int j = 1;  // Second candidate starting position
        int k = 0;  // Current comparison offset

        // Continue until we've checked all positions
        while (i < n && j < n && k < n) {
            // Compare characters at offset k from positions i and j (circular)
            char c1 = s.charAt((i + k) % n);
            char c2 = s.charAt((j + k) % n);

            if (c1 == c2) {
                // Characters match, advance comparison offset
                k++;
            } else {
                // Characters differ, eliminate the larger rotation
                if (c1 > c2) {
                    // Rotation starting at i is lexicographically larger
                    // Skip all positions i, i+1, ..., i+k (they all start with worse prefix)
                    i += (k + 1);
                } else {
                    // Rotation starting at j is lexicographically larger
                    j += (k + 1);
                }

                // Ensure i and j are different
                if (i == j) {
                    j++;
                }

                // Reset comparison offset
                k = 0;
            }
        }

        // The minimum rotation starts at position min(i, j)
        int start = Math.min(i, j);
        return s.substring(start) + s.substring(0, start);
    }
}