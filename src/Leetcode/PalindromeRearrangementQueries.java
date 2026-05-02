package Leetcode;

import java.util.Arrays;

//Problem Link: https://leetcode.com/problems/palindrome-rearrangement-queries

/**
 * Solution for determining if a string can become a palindrome after rearranging query ranges.
 * <p>
 * Problem: Given a string s (even length) and queries [l1, r1, l2, r2]: - You can rearrange characters within substring
 * s[l1..r1] - You can rearrange characters within substring s[l2..r2] - After these rearrangements, can the entire
 * string become a palindrome?
 * <p>
 * Key insight: Split string into two halves and compare - For palindrome: s[i] must equal s[n-1-i] for all i - Split
 * into c1 (left half) and c2 (right half reversed) - After rearrangements, we need c1[i] == c2[i] for all i
 * <p>
 * Algorithm approach: 1. Check if total character counts in both halves are equal (prerequisite) 2. Build prefix
 * frequency arrays for efficient range queries 3. Track positions where c1[i] already equals c2[i] 4. For each query,
 * check if rearrangements can make all positions match
 * <p>
 * Time Complexity: O(n + q × 26) = O(n + q) Space Complexity: O(n) for preprocessing arrays
 */
public class PalindromeRearrangementQueries {

    /**
     * Determines if string can become palindrome for each query.
     *
     * @param s Input string (even length)
     * @param queries Array of [l1, r1, l2, r2] range pairs
     * @return Boolean array indicating if each query allows palindrome formation
     */
    public boolean[] canMakePalindromeQueries(String s, int[][] queries) {

        var n = s.length();
        var q = queries.length;
        var ch = s.toCharArray();

        // ================================================================
        // STEP 1: Validate that character counts match between halves
        // ================================================================
        // For a palindrome to be possible, left half and right half must
        // contain the same characters (with same frequencies)
        var cnt = new int[26];
        for (var i = 0; i < n / 2; i++) {
            cnt[ch[i] - 'a']++;              // Count characters in left half
            cnt[ch[i + n / 2] - 'a']--;      // Subtract characters in right half
        }

        // If any character has different count in two halves, impossible
        for (int i = 0; i < 26; i++) {
            if (cnt[i] != 0) {
                // All queries return false
                return new boolean[q];
            }
        }
        // ================================================================
        // STEP 2: Split string into two halves (left and right reversed)
        // ================================================================
        // For palindrome property: s[i] should equal s[n-1-i]
        // c1[i] = left half character at position i
        // c2[i] = right half character at position i (from right, reversed)
        var size = n / 2;
        char[] c1 = new char[n / 2];
        char[] c2 = new char[n / 2];
        for (int start = 0, end = n - 1; start < end; start++, end--) {
            c1[start] = ch[start];   // Left half: s[0], s[1], ..., s[n/2-1]
            c2[start] = ch[end];     // Right half reversed: s[n-1], s[n-2], ..., s[n/2]
        }

        // ================================================================
        // STEP 3: Build prefix frequency arrays
        // ================================================================
        // c1Cnt[i][c] = count of character c in c1[0..i]
        // c2Cnt[i][c] = count of character c in c2[0..i]
        // Used for efficient range frequency queries
        var c1Cnt = new int[size][26];
        var c2Cnt = new int[size][26];

        for (int i = 0; i < size; i++) {
            if (i > 0) {
                c1Cnt[i] = c1Cnt[i - 1].clone();  // Copy previous frequencies
                c2Cnt[i] = c2Cnt[i - 1].clone();
            }
            c1Cnt[i][c1[i] - 'a']++;  // Add current character
            c2Cnt[i][c2[i] - 'a']++;
        }

        // ================================================================
        // STEP 4: Build prefix array for already-matching positions
        // ================================================================
        // pre[i] = count of positions in [0..i] where c1[j] == c2[j]
        // Used to quickly check if a range already has all matching characters
        var pre = new int[size];
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                pre[i] = pre[i - 1];
            }

            if (c1[i] == c2[i]) {
                pre[i]++;  // This position already matches
            }
        }
        var empty = new int[26];  // Empty frequency array for boundary cases
        var res = new boolean[q];
        // ================================================================
        // STEP 5: Process each query
        // ================================================================
        for (int i = 0; i < q; i++) {
            // ============================================================
            // Map query ranges from original string to half-string indices
            // ============================================================
            // Original query: [l1, r1] in left half, [l2', r2'] in right half
            // Need to map right half range to our reversed c2 array
            var l1 = queries[i][0];       // Left range start (already in left half)
            var r1 = queries[i][1];       // Left range end
            var r2 = n - 1 - queries[i][2];  // Right range mapped to c2 index (reversed)
            var l2 = n - 1 - queries[i][3];  // Right range mapped to c2 index (reversed)

            // ============================================================
            // Calculate boundary positions
            // ============================================================
            // ll = rightmost position before both query ranges
            // rr = leftmost position after both query ranges
            var ll = Math.min(l1, l2) - 1;
            var rr = Math.max(r1, r2);

            // ============================================================
            // Check 1: Positions before query ranges must already match
            // ============================================================
            // If ll >= 0, check that all positions in [0..ll] have c1[j] == c2[j]
            if ((ll >= 0 && pre[ll] != ll + 1)) {
                continue;  // Not all positions match, impossible
            }

            // ============================================================
            // Check 2: Positions after query ranges must already match
            // ============================================================
            // If rr < size, check that all positions in [rr+1..size-1] match
            if (rr < size && pre[size - 1] - pre[rr] != size - rr - 1) {
                continue;  // Not all positions match, impossible
            }

            // ============================================================
            // CASE 1: Ranges don't overlap (disjoint ranges)
            // ============================================================
            // [l1...r1] and [l2...r2] are completely separate
            int[] c3 = l2 == 0 ? empty : c2Cnt[l2 - 1];
            int[] c4 = l1 == 0 ? empty : c2Cnt[l1 - 1];
            int[] c5 = l1 == 0 ? empty : c1Cnt[l1 - 1];
            int[] c6 = l2 == 0 ? empty : c1Cnt[l2 - 1];
            if (r1 < l2 || l1 > r2) {

                // Check the gap between the two ranges must already match
                if (r1 < l2) {
                    // Gap is [r1+1, l2-1], check if all positions match
                    if (pre[l2 - 1] - pre[r1] != (l2 - 1) - r1) {
                        continue;
                    }
                } else {
                    // Gap is [r2+1, l1-1], check if all positions match
                    if (pre[l1 - 1] - pre[r2] != (l1 - 1) - r2) {
                        continue;
                    }
                }

                // Check if range 1 [l1, r1] has matching character frequencies
                // d1 = character frequencies in c1[l1..r1]
                // d2 = character frequencies in c2[l1..r1]
                var d1 = diff(c1Cnt[r1], c5);
                var d2 = diff(c2Cnt[r1], c4);
                if (!Arrays.equals(d1, d2)) {
                    continue;  // Range 1 cannot be made to match
                }

                // Check if range 2 [l2, r2] has matching character frequencies
                d1 = diff(c1Cnt[r2], c6);
                d2 = diff(c2Cnt[r2], c3);
                if (Arrays.equals(d1, d2)) {
                    res[i] = true;  // Both ranges can match, palindrome possible
                }
                continue;
            }

            // ============================================================
            // CASE 2a: One range completely contains the other
            // ============================================================

            // Subcase: range1 contains range2 ([l1, r1] ⊇ [l2, r2])
            if (l1 <= l2 && r2 <= r1) {
                // Can rearrange the entire range [l1, r1] in both halves
                // Just check if character frequencies match in this range
                res[i] = check(diff(c1Cnt[r1], c5),
                        diff(c2Cnt[r1], c4));
                continue;
            }

            // Subcase: range2 contains range1 ([l2, r2] ⊇ [l1, r1])
            if (l2 <= l1 && r1 <= r2) {
                // Can rearrange the entire range [l2, r2] in both halves
                res[i] = check(diff(c1Cnt[r2], c6),
                        diff(c2Cnt[r2], c3));
                continue;
            }

            // ============================================================
            // CASE 2b: Ranges partially overlap
            // ============================================================
            // The two ranges overlap but neither contains the other
            // Overlap region: [max(l1, l2), min(r1, r2)]

            // Get character frequencies for each query range
            // d1 = frequencies in c1[l1..r1]
            // d2 = frequencies in c2[l2..r2]
            var d1 = diff(c1Cnt[r1], c5);
            var d2 = diff(c2Cnt[r2], c3);
            if (l1 <= l2) {
                // range1 starts first: [l1...r1] overlaps with [l2...r2]
                // Non-overlapping part of c2: [l1, l2-1]
                // This part of c2 must be covered by d1 (c1's range)
                var d3 = diff(c3, c4);
                if (!reduce(d1, d3)) {
                    continue;  // c1 range doesn't have enough to cover c2's non-overlap
                }

                // Non-overlapping part of c1: [r2+1, r1]
                // This part of c1 must be covered by d2 (c2's range)
                var d4 = diff(c1Cnt[r2], c1Cnt[r1]);
                if (!reduce(d2, d4)) {
                    continue;  // c2 range doesn't have enough to cover c1's non-overlap
                }

                // After reducing, check if remaining frequencies match
                res[i] = Arrays.equals(d1, d2);
            } else {
                // range2 starts first: [l2...r2] overlaps with [l1...r1]
                // Non-overlapping part of c1: [l2, l1-1]
                var d3 = diff(c5, c6);
                if (!reduce(d2, d3)) {
                    continue;  // c2 range doesn't have enough to cover c1's non-overlap
                }

                // Non-overlapping part of c2: [r1+1, r2]
                var d4 = diff(c2Cnt[r1], c2Cnt[r2]);
                if (!reduce(d1, d4)) {
                    continue;  // c1 range doesn't have enough to cover c2's non-overlap
                }

                res[i] = Arrays.equals(d1, d2);
            }

        }

        return res;


    }

    /**
     * Attempts to reduce c1 by c2 (subtract c2 from c1).
     * <p>
     * Modifies c1 in place by subtracting c2[i] from c1[i] for each character. Returns false if any c1[i] < c2[i]
     * (reduction not possible).
     * <p>
     * Used to check if one frequency set can "cover" another and compute remainder.
     *
     * @param c1 Frequency array to reduce (modified in place)
     * @param c2 Frequency array to subtract
     * @return true if reduction successful (c1 >= c2 for all characters)
     */
    private boolean reduce(int[] c1, int[] c2) {

        for (int i = 0; i < 26; i++) {
            if (c2[i] > c1[i]) {
                return false;  // Cannot reduce: c2 has more of character i
            }

            c1[i] -= c2[i];  // Reduce count
        }

        return true;
    }

    /**
     * Computes element-wise difference between two frequency arrays.
     * <p>
     * Returns a new array where res[i] = c2[i] - c1[i]. Used to extract frequency counts for a range using prefix
     * sums.
     *
     * @param c2 Minuend array (prefix frequency up to right boundary)
     * @param c1 Subtrahend array (prefix frequency up to left boundary - 1)
     * @return Array where res[i] = c2[i] - c1[i]
     */
    private int[] diff(int[] c2, int[] c1) {

        int res[] = new int[26];
        for (int i = 0; i < 26; i++) {
            res[i] = c2[i] - c1[i];
        }

        return res;
    }

    /**
     * Checks if two frequency arrays are equal.
     * <p>
     * Returns true if both arrays have the same character frequencies. Used to verify if two ranges can be rearranged
     * to match each other.
     *
     * @param c1 First frequency array
     * @param c2 Second frequency array
     * @return true if arrays are equal
     */
    private boolean check(int[] c1, int[] c2) {

        return Arrays.equals(c1, c2);
    }
}