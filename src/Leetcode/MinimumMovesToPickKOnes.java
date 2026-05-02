package Leetcode;

import java.util.ArrayList;
import java.util.List;

//Problem Link: https://leetcode.com/problems/minimum-moves-to-pick-k-ones/

/**
 * Solution for finding minimum moves to pick k ones from a binary array.
 *
 * Problem: Given a binary array and starting at some position, you can: 1. Pick a '1' at your current position (0
 * moves) 2. Move left or right by 1 position (1 move) 3. Change a '0' to '1' at any position and pick it (2 moves
 * total: change + pick) You can perform up to maxChanges operations of type 3. Find minimum moves to collect k ones.
 *
 * Key insights:
 *
 * Move cost breakdown: - Pick '1' at current position: 0 moves - Pick '1' at distance d: d moves (move there and pick)
 * - Change '0' to '1' at any position and pick: 2 moves (change costs 1, pick costs 1)
 *
 * Optimal strategy: 1. Stand at an optimal position (usually near a cluster of '1's) 2. Pick existing '1's nearby by
 * moving to them (cost = distance) 3. Use maxChanges to create '1's anywhere and pick (cost = 2 per change)
 *
 * Two cases:
 *
 * Case 1: maxChanges >= k - Can create all k ones using changes (cost = 2k) - But check if using existing adjacent '1's
 * is cheaper - If standing at a '1' with neighbors: pick current (0) + left neighbor (1) + right neighbor (1) + create
 * rest (2 each)
 *
 * Case 2: maxChanges < k - Must pick (k - maxChanges) existing '1's by moving - Use maxChanges to create remaining ones
 * (cost = 2 * maxChanges) - Find optimal window of consecutive '1's to minimize movement
 *
 * Window optimization: - Need to pick at least (k - maxChanges) existing '1's - Try windows of size (k - maxChanges) to
 * (k - maxChanges + 2) * Size (k - maxChanges): minimum required, rest from changes * Size (k - maxChanges + 1): pick 1
 * extra existing '1', save 1 change * Size (k - maxChanges + 2): pick 2 extra existing '1's, save 2 changes - For each
 * window, stand at median position to minimize total distance
 *
 * Median property: - When collecting items from positions, standing at the median minimizes total distance - Example:
 * positions [1, 3, 7], median = 3, distances = |1-3| + |3-3| + |7-3| = 2 + 0 + 4 = 6
 *
 * Time Complexity: O(n) where n = array length Space Complexity: O(m) where m = number of ones
 */
public class MinimumMovesToPickKOnes {

    /**
     * Finds minimum moves to collect k ones.
     *
     * @param nums Binary array (0s and 1s)
     * @param k Number of ones to collect
     * @param maxChanges Maximum number of changes allowed (0→1)
     * @return Minimum number of moves required
     */
    public long minimumMoves(int[] nums, int k, int maxChanges) {

        List<Long> preSum = new ArrayList<>();  // Prefix sum of one indices
        long prev = 0l;
        long ans = (long) 1e15;  // Initialize with large value
        var n = nums.length;

        List<Integer> oneIndex = new ArrayList<>();  // Positions of all ones

        // ================================================================
        // CASE 1: maxChanges >= k (can create all ones)
        // ================================================================
        if (maxChanges >= k) {
            ans = k * 2;  // Baseline: create all k ones (cost = 2k)

            // Check if using existing adjacent ones is cheaper
            for (int i = 0; i < n; i++) {
                if (nums[i] == 1) {
                    long steps = 0;      // Steps to pick adjacent ones
                    long remain = k - 1; // Remaining ones needed (already at position i)

                    // Check left neighbor
                    if (remain > 0 && i - 1 >= 0 && nums[i - 1] == 1) {
                        remain--;
                        steps++;  // Cost 1 to move left and pick
                    }

                    // Check right neighbor
                    if (remain > 0 && i + 1 < n && nums[i + 1] == 1) {
                        remain--;
                        steps++;  // Cost 1 to move right and pick
                    }

                    // Total cost: steps for adjacent + 2 per created one
                    ans = Math.min(ans, steps + 2 * remain);

                }
            }

            return ans;
        }

        // ================================================================
        // CASE 2: maxChanges < k (must pick existing ones)
        // ================================================================

        // Build list of one positions and prefix sums
        for (int i = 0; i < n; i++) {
            if (nums[i] == 1) {
                oneIndex.add(i);
                prev += i;
                preSum.add(prev);  // Cumulative sum of positions
            }
        }

        // step2: Minimum number of existing ones we must pick
        int step2 = k - maxChanges;

        // ================================================================
        // Try different window sizes
        // ================================================================
        // Why window sizes [step2, step2+2]?
        // - step2: minimum required existing ones, rest from changes
        // - step2+1: pick 1 extra existing one, save 1 change (trade 2 cost for distance cost)
        // - step2+2: pick 2 extra existing ones, save 2 changes
        // Beyond step2+2, we'd be picking more ones than necessary

        for (int window = step2; window <= step2 + 2; window++) {
            if (window > k) {
                break;  // Can't pick more ones than k
            }

            // Try all sliding windows of this size
            for (int i = 0; i < oneIndex.size(); i++) {
                int j = i + window - 1;  // End of window

                if (j >= oneIndex.size()) {
                    break;  // Window extends beyond available ones
                }

                // ============================================================
                // Calculate cost for this window
                // ============================================================

                // Stand at the median position to minimize total distance
                int midIndex = (i + j) / 2;
                long midValue = oneIndex.get(midIndex);  // Position of median one

                // Cost = moves to collect from window + changes for remaining ones
                long currMove = (k - window) * 2;  // Cost to create (k - window) ones

                // Calculate distance to collect ones to the left of median
                // For each position p in [i, midIndex-1]:
                //   Distance = midValue - p
                // Total = midValue * count - sum of positions
                long leftMove = midValue * (midIndex - i) - getSum(i, midIndex - 1, preSum);

                // Calculate distance to collect ones to the right of median
                // For each position p in [midIndex+1, j]:
                //   Distance = p - midValue
                // Total = sum of positions - midValue * count
                long rightMove = getSum(midIndex + 1, j, preSum) - (j - midIndex) * midValue;

                // Total cost for this configuration
                currMove += leftMove + rightMove;

                ans = Math.min(ans, currMove);

            }
        }

        return ans;
    }

    /**
     * Gets the sum of one positions in range [l, r] using prefix sum.
     *
     * This is used to calculate total distance when collecting ones.
     * Instead of summing individual positions, use prefix sum for O(1) query.
     *
     * Formula: sum[l, r] = prefix[r] - prefix[l-1]
     *
     * @param l      Left index (inclusive) in oneIndex array
     * @param r      Right index (inclusive) in oneIndex array
     * @param prefix Prefix sum array of one positions
     * @return Sum of positions from oneIndex[l] to oneIndex[r]
     */
    private long getSum(int l, int r, List<Long> prefix) {

        if (l > r) {
            return 0;  // Empty range
        }
        if (l == 0) {
            return prefix.get(r);  // Sum from beginning
        }
        return prefix.get(r) - prefix.get(l - 1);  // Standard prefix sum query
    }
}