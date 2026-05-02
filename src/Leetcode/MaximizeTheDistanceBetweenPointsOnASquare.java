package Leetcode;

import java.util.Arrays;

//Problem Link: https://leetcode.com/problems/maximize-the-distance-between-points-on-a-square/

/**
 * Solution for maximizing minimum distance between k points on a square's perimeter.
 *
 * Problem: Given a square with side length 'side' and n points on its perimeter, select k points to maximize the
 * minimum pairwise distance along the perimeter.
 *
 * Key insight: Convert 2D square perimeter to 1D circular array - Map each point on square perimeter to a 1D position
 * [0, perimeter) - Use binary search on the answer (minimum distance) - Check if we can select k points with at least
 * 'mid' distance between consecutive ones
 *
 * Coordinate mapping to 1D: - Bottom edge (y=0): position = x - Right edge (x=side): position = side + y - Top edge
 * (y=side): position = 3*side - x - Left edge (x=0): position = 4*side - y (wraps to perimeter - y)
 *
 * Visual representation of a square with side=3: (0,3) ←―――――――――――――→ (3,3) ↑   pos: 9→6         ↓ |
 * | (0,0) ―→―――――――――――→ (3,0) pos: 0→3      pos: 3→6
 *
 * Perimeter positions: - (0,0) → 0 - (1,0) → 1 - (2,0) → 2 - (3,0) → 3 - (3,1) → 4 - (3,2) → 5 - (3,3) → 6 - (2,3) → 7
 * - (1,3) → 8 - (0,3) → 9 - (0,2) → 10 - (0,1) → 11 - Back to (0,0) → 12 (wraps)
 *
 * Circular array trick: - Duplicate the positions array to handle wrap-around - extended = [positions..., positions +
 * perimeter...] - This allows checking circular patterns without modulo arithmetic
 *
 * Binary search strategy: - Search for maximum achievable minimum distance - For each candidate distance 'mid', check
 * if k points can be placed - Use greedy: place points as far apart as possible
 *
 * Time Complexity: O(n² log n log(perimeter)) Space Complexity: O(n)
 */
public class MaximizeTheDistanceBetweenPointsOnASquare {

    /**
     * Finds maximum minimum distance when selecting k points from square perimeter.
     *
     * @param side Side length of the square
     * @param points Array of [x, y] coordinates on square perimeter
     * @param k Number of points to select
     * @return Maximum achievable minimum distance between selected points
     */
    public int maxDistance(int side, int[][] points, int k) {

        var n = points.length;
        var perimeter = 4l * side;  // Total perimeter length

        // ================================================================
        // STEP 1: Convert 2D coordinates to 1D positions on perimeter
        // ================================================================
        var positions = new long[n];
        for (var i = 0; i < n; i++) {
            var p = points[i];
            var x = p[0];
            var y = p[1];

            // Map each edge to a range on the perimeter
            if (x == 0) {
                // Left edge: position decreases as y increases
                // Positions: [perimeter - side, perimeter)
                positions[i] = perimeter - y;
            } else if (y == 0) {
                // Bottom edge: position increases with x
                // Positions: [0, side]
                positions[i] = x;
            } else if (y == side) {
                // Top edge: position decreases as x increases
                // Positions: [2*side, 3*side]
                positions[i] = 3l * side - x;
            } else {
                // Right edge: position increases with y
                // Positions: [side, 2*side]
                positions[i] = side + y;
            }
        }

        // ================================================================
        // STEP 2: Create extended array to handle circular wraparound
        // ================================================================
        var extended = new long[2 * n];
        var ind = 0;

        // Sort positions first
        Arrays.sort(positions);

        // First copy: original positions
        for (int i = 0; i < n; i++) {
            extended[ind++] = positions[i];
        }

        // Second copy: positions shifted by perimeter (for circular handling)
        for (int i = 0; i < n; i++) {
            extended[ind++] = positions[i] + perimeter;
        }

        // ================================================================
        // STEP 3: Binary search on the minimum distance
        // ================================================================
        var ans = 0l;
        var start = 0l;
        var end = 2l * side;  // Maximum possible distance is half perimeter

        while (start <= end) {
            var mid = start + (end - start) / 2;

            // Check if we can select k points with minimum distance >= mid
            if (check(mid, extended, perimeter, n, k)) {
                ans = mid;          // mid is achievable, try larger
                start = mid + 1;
            } else {
                end = mid - 1;      // mid is too large, try smaller
            }
        }

        return (int) ans;
    }

    /**
     * Checks if we can select k points with minimum distance >= mid.
     *
     * Strategy: Greedy placement with circular handling - Try each point as a starting position - Greedily select the
     * next point that is at least 'mid' distance away - Use the extended array to handle circular wraparound naturally
     * - Verify that the distance from the last selected point back to the first >= mid
     *
     * Why try each starting point? - On a circle, the optimal selection might start from any point - We need to check
     * all possibilities to ensure we don't miss the answer
     *
     * Extended array benefits: - Positions are duplicated: [p₀, p₁, ..., pₙ₋₁, p₀+P, p₁+P, ..., pₙ₋₁+P] - When starting
     * at position i, we can check up to position i+n - This naturally handles wrap-around without modulo arithmetic
     *
     * Example: positions = [0, 3, 5, 8], perimeter = 12, k = 3, mid = 4 - Start at 0: pick 0 → next >= 4 is 5 → next >=
     * 9 is (0+12)=12 - Distance from 12 back to 0 = 0 + 12 - 12 = 0 < 4 ✗ - Start at 3: pick 3 → next >= 7 is 8 → next
     * >= 12 is (3+12)=15 - Distance from 15 back to 3 = 3 + 12 - 15 = 0 < 4 ✗
     *
     * @param mid Minimum distance to check
     * @param extended Extended positions array (size 2n)
     * @param perimeter Total perimeter length
     * @param n Number of original points
     * @param k Number of points to select
     * @return true if k points can be selected with minimum distance >= mid
     */
    private boolean check(long mid, long extended[], long perimeter, int n, int k) {

        // ================================================================
        // Try each point as a potential starting point
        // ================================================================
        for (int i = 0; i < n; i++) {
            int cnt = 1;               // Count of selected points (start with first)
            long prev = extended[i];   // Position of last selected point
            int prevStart = i;         // Index of last selected point

            // ============================================================
            // Greedily select k-1 more points
            // ============================================================
            // We can only look within one perimeter's worth of positions
            // from the starting point (i to i+n in the extended array)
            while (cnt < k && prevStart < i + n) {
                // Binary search for the next point at least 'mid' away
                int start = prevStart + 1;
                int end = i + n - 1;
                int ind = -1;

                while (start <= end) {
                    int m = (start + end) / 2;

                    // Check if this point is far enough from previous
                    if (extended[m] - prev >= mid) {
                        ind = m;       // Found a candidate
                        end = m - 1;   // Try to find an earlier one (greedy: pick leftmost valid)
                    } else {
                        start = m + 1; // Too close, search right
                    }
                }

                // No valid point found
                if (ind == -1) {
                    break;
                }

                // Select this point and continue
                prev = extended[ind];
                prevStart = ind;
                cnt++;
            }

            // ============================================================
            // Verify circular constraint
            // ============================================================
            // Check if we selected k points AND the distance from the last point
            // back to the first point is also >= mid
            // Distance from last to first (going forward on circle):
            //   = (first + perimeter) - last
            if (cnt >= k && extended[i] + perimeter - prev >= mid) {
                return true;  // Found a valid selection!
            }
        }

        return false;  // No valid selection found for this minimum distance
    }
}