package Leetcode;

import java.util.ArrayList;
import java.util.List;

//Problem Link: https://leetcode.com/contest/weekly-contest-498/problems/count-good-integers-on-a-grid-path/

/**
 * Solution for counting "good integers" on a grid path.
 *
 * Problem: Given a range [l, r] and a path through a 4×4 grid, count integers where:
 * - The integer is in range [l, r]
 * - When written as a 16-digit number (4×4 grid positions 0-15), the digits along
 *   the given path form a non-decreasing sequence
 *
 * Grid layout (row-major order):
 *   0  1  2  3
 *   4  5  6  7
 *   8  9 10 11
 *  12 13 14 15
 *
 * Example: directions = "DRR" (Down, Right, Right)
 * - Path: position 0 → 4 → 5 → 6
 * - Good integer: 0123456789012345 if digits at positions [0,4,5,6] are non-decreasing
 *
 * Key insight: Digit DP with path constraint
 *
 * Observation 1: Grid position mapping
 * - Grid is 4×4, so 16 positions total
 * - Position at (row, col) = 4*row + col
 * - Given directions, we compute path positions
 *
 * Observation 2: Non-decreasing constraint
 * - Only positions on the path need to satisfy non-decreasing property
 * - Other positions can have any digit (0-9)
 * - Track previous digit on path
 *
 * Observation 3: Range counting
 * - Count in range [l, r] = count(r) - count(l-1)
 * - Use digit DP with tight bound
 *
 * Observation 4: Digit DP state
 * - i: current position (0-15)
 * - flag: tight constraint (1 = must stay ≤ limit, 0 = free)
 * - prev: previous digit on path (10 = no previous digit yet)
 * - ind: current index in path (which path position we're at)
 *
 * Algorithm:
 * 1. Convert path directions to position indices
 * 2. Use digit DP to count valid numbers ≤ n
 * 3. At each position:
 *    - If on path: ensure digit ≥ prev
 *    - Otherwise: any digit is valid
 * 4. Respect tight bound constraint
 *
 * Time Complexity: O(16 × 2 × 10 × 8 × 10) = O(25,600) per query
 * Space Complexity: O(16 × 2 × 11 × 8) = O(2,816) for DP table
 */
public class CountGoodIntegersOnPath {

    /**
     * Counts good integers in range [l, r].
     *
     * Uses inclusion-exclusion: count(r) - count(l-1)
     *
     * @param l          Lower bound of range
     * @param r          Upper bound of range
     * @param directions Path through grid ("D" = down, "R" = right)
     * @return Count of good integers in [l, r]
     */
    public long countGoodIntegersOnPath(long l, long r, String directions) {

        // Inclusion-exclusion principle for range query
        return solve(r, directions) - solve(l - 1, directions);
    }

    /**
     * Counts good integers from 0 to n.
     *
     * @param n          Upper bound
     * @param directions Path directions
     * @return Count of good integers in [0, n]
     */
    private long solve(long n, String directions) {

        // ================================================================
        // STEP 1: Convert n to 16-digit string representation
        // ================================================================
        // Pad with leading zeros to get exactly 16 digits (4×4 grid)
        String s = String.format("%016d", n);

        // ================================================================
        // STEP 2: Build path positions from directions
        // ================================================================
        // Start at position (0,0) = index 0
        List<Integer> path = new ArrayList<>();
        int r = 0;  // Current row
        int c = 0;  // Current column
        path.add(0);  // Starting position

        // Follow directions to build path
        for (char ch : directions.toCharArray()) {
            if (ch == 'D') {
                r++;  // Move down (increment row)
            } else {
                c++;  // Move right (increment column)
            }
            // Convert (row, col) to linear index: 4*row + col
            path.add(4 * r + c);
        }

        // ================================================================
        // STEP 3: Initialize DP table and solve
        // ================================================================
        // dp[position][tight_flag][previous_digit][path_index]
        // - position: 0-16 (current digit position in 16-digit number)
        // - tight_flag: 0-1 (whether we're still bounded by n)
        // - previous_digit: 0-10 (last digit on path, 10 = none yet)
        // - path_index: 0-7 (current position in path, max 8 positions)
        Long dp[][][][] = new Long[17][2][11][8];

        // Start digit DP: position 0, tight=1, prev=10 (no prev), path index 0
        return solve(0, 1, 10, 0, s, path, dp);

    }

    /**
     * Recursive digit DP solver.
     *
     * Builds valid numbers digit by digit, ensuring:
     * 1. Number doesn't exceed upper bound (tight constraint)
     * 2. Digits along path are non-decreasing
     *
     * @param i    Current position in 16-digit number (0-15)
     * @param flag Tight constraint flag (1 = bounded by num, 0 = free)
     * @param prev Previous digit on path (10 = no previous digit yet)
     * @param ind  Current index in path list (which path position we're at)
     * @param num  Upper bound as 16-digit string
     * @param path List of positions on the path
     * @param dp   Memoization table
     * @return Count of valid numbers from this state
     */
    private long solve(int i, int flag, int prev, int ind, String num, List<Integer> path, Long dp[][][][]) {

        // ================================================================
        // Base case: All 16 positions filled
        // ================================================================
        if (i == 16) {
            return 1;  // Successfully built a valid number
        }

        // ================================================================
        // Memoization: Return cached result if available
        // ================================================================
        if (dp[i][flag][prev][ind] != null) {
            return dp[i][flag][prev][ind];
        }

        // ================================================================
        // Determine digit limit at current position
        // ================================================================
        // If tight flag is set, we can't exceed the corresponding digit in num
        int limit = (flag == 1) ? (num.charAt(i) - '0') : 9;

        long ans = 0;

        // ================================================================
        // Try all valid digits at current position
        // ================================================================
        for (int dig = 0; dig <= limit; dig++) {
            // Update tight flag: remains 1 only if we choose the limit digit
            int newFlag = (flag == 1 && dig == limit) ? 1 : 0;

            // ============================================================
            // Check if current position is on the path
            // ============================================================
            if (ind < 7 && i == path.get(ind)) {
                // Current position is on the path
                // Constraint: digit must be ≥ previous digit on path
                if (prev == 10 || dig >= prev) {
                    // Valid: either first path digit (prev=10) or non-decreasing
                    ans += solve(i + 1, newFlag, dig, ind + 1, num, path, dp);
                }
                // If dig < prev, this choice is invalid (skip it)
            } else {
                // ========================================================
                // Current position is NOT on the path
                // ========================================================
                // No constraint: any digit is valid
                ans += solve(i + 1, newFlag, prev, ind, num, path, dp);
            }
        }

        // Memoize and return result
        return dp[i][flag][prev][ind] = ans;
    }
}