package Leetcode;//Problem Link: https://leetcode.com/problems/count-the-number-of-houses-at-a-certain-distance-ii

/**
 * Solution for counting pairs of houses at each distance in a street graph with a shortcut.
 *
 * Problem setup: - Houses are arranged in a line: 1 - 2 - 3 - ... - n - There's an additional bidirectional road
 * (shortcut) between houses x and y - For each distance k (1 to n), count how many pairs of houses are exactly k apart
 *
 * Graph structure: - Normal path: 1 -- 2 -- 3 -- ... -- x -- ... -- y -- ... -- n - Shortcut: x ========== y (direct
 * edge)
 *
 * Key insight: Instead of counting distances for all O(n²) pairs directly, use a difference array technique: 1. For
 * each house i, compute how the shortcut affects distances to other houses 2. Use contribution technique: add/subtract
 * counts at specific distance boundaries 3. Apply prefix sum to convert from "difference array" to actual counts
 *
 * The shortcut between x and y creates three regions: - Left region: houses [1, x-1] - Middle region: houses [x, y] -
 * Right region: houses [y+1, n]
 *
 * For each house i, we need to consider: - Distances using only the linear path - Distances using the shortcut x-y -
 * Which path is shorter for each destination house
 *
 * Time Complexity: O(n) Space Complexity: O(n)
 */
public class CountTheNumberOfHousesAtACertainDistanceII {

    /**
     * Counts the number of house pairs at each distance.
     *
     * @param n Number of houses (numbered 1 to n)
     * @param x One endpoint of the shortcut edge
     * @param y Other endpoint of the shortcut edge
     * @return Array where ans[k-1] = number of pairs at distance k
     */
    public long[] countOfPairs(int n, int x, int y) {

        // Ensure x <= y for consistency (the problem is symmetric)
        if (x > y) {
            return countOfPairs(n, y, x);
        }

        // ans[k] will store the count of pairs at distance k+1
        // We use a difference array technique, then apply prefix sum at the end
        long ans[] = new long[n];

        // For each house i, calculate its contribution to the distance counts
        for (int i = 1; i <= n; i++) {
            // ================================================================
            // CONTRIBUTION 1: All pairs (i, j) where j != i
            // ================================================================
            // Initially, every house i can pair with any other house (n-1 pairs total)
            // Each such pair has some distance, so we add 2 to ans[0]
            // (2 because we count both (i,j) and (j,i), though they're the same pair)
            ans[0] += 2;

            // ================================================================
            // CONTRIBUTION 2: Subtract pairs going LEFT without using shortcut
            // ================================================================
            // For houses to the left of i (houses 1 to i-1):
            // - Without shortcut: distance = i - j (ranges from 1 to i-1)
            // - With shortcut via y: distance = |i - y| + 1 + |j - x|
            //   (go from i to y via shortcut, then from x to j)
            //
            // We subtract at the boundary where these distances would exceed the valid range
            // Math.min ensures we don't go beyond valid distances
            ans[Math.min(i - 1, Math.abs(i - y) + x)]--;

            // ================================================================
            // CONTRIBUTION 3: Subtract pairs going RIGHT without using shortcut
            // ================================================================
            // For houses to the right of i (houses i+1 to n):
            // - Without shortcut: distance = j - i (ranges from 1 to n-i)
            // - With shortcut via x: distance = |i - x| + 1 + (n - y)
            //   (go from i to x via shortcut, then from y onwards)
            ans[Math.min(n - i, Math.abs(i - x) + 1 + n - y)]--;

            // ================================================================
            // CONTRIBUTION 4: Add back pairs using path i -> x -> y -> j
            // ================================================================
            // For destinations j on the RIGHT side of the shortcut:
            // Distance via shortcut: |i - x| + 1 + |y - j|
            // But we need the minimum of this and |y - i| + 1 (direct to y then to j)
            ans[Math.min(Math.abs(i - x), Math.abs(y - i) + 1)]++;

            // ================================================================
            // CONTRIBUTION 5: Add back pairs using path i -> y -> x -> j
            // ================================================================
            // For destinations j on the LEFT side of the shortcut:
            // Distance via shortcut: |i - y| + 1 + |x - j|
            // But we need the minimum of this and |x - i| + 1 (direct to x then to j)
            ans[Math.min(Math.abs(i - x) + 1, Math.abs(y - i))]++;

            // ================================================================
            // CONTRIBUTION 6 & 7: Adjust for middle region [x, y]
            // ================================================================
            // For houses in the middle region (between x and y), the shortcut affects distances
            // r represents the "detour" needed if i is outside [x, y]
            int r = Math.max(x - i, 0) + Math.max(i - y, 0);

            // Subtract contributions at the midpoint of the shortcut path
            // This handles the case where using the shortcut crosses the middle region
            ans[r + (y - x) / 2]--;           // Floor division
            ans[r + (y - x + 1) / 2]--;       // Ceiling division

        }

        // ================================================================
        // FINAL STEP: Convert difference array to actual counts using prefix sum
        // ================================================================
        // After all contributions, ans[] is a difference array
        // Apply prefix sum to get the actual count at each distance
        for (int i = 1; i < n; i++) {
            ans[i] += ans[i - 1];
        }

        return ans;
    }
}