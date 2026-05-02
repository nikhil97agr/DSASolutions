package Leetcode;

import java.util.HashMap;
import java.util.Map;

//Problem Link: https://leetcode.com/problems/maximize-subarray-sum-after-removing-all-occurrences-of-one-element/

/**
 * Solution for maximizing subarray sum after removing all occurrences of one element value.
 *
 * Problem: Given an array, you can optionally remove ALL occurrences of one specific value, then find the maximum
 * subarray sum.
 *
 * Key insight: Modified Kadane's algorithm with prefix sum tracking - For each position, consider: 1. Normal subarray
 * (no removal) 2. Subarray after removing all occurrences of some negative value
 *
 * Strategy: - Use prefix sums to calculate subarray sums efficiently - For removing a value v: track the minimum prefix
 * sum among positions NOT containing v - Only track negative values (removing positive values never helps)
 *
 * Example: nums = [1, -2, 3, -2, 5] - Without removal: max subarray = [3, -2, 5] = 6 - Remove all -2s: effective array
 * = [1, 3, 5], max = 9 ✓
 *
 * How it works: - map[v] = minimum prefix sum achievable if we remove all occurrences of v - For position i with value
 * v: * If we remove v, we can't use any prefix that contained v * So we track: min prefix sum before any occurrence of
 * v, then add v's sum contribution
 *
 * Example trace: nums = [2, -3, 1, -3, 4] - i=0, val=2: pre=2, ans=2 - i=1, val=-3: pre=-1, map[-3]=0+(-3)=-3, ans=2 -
 * i=2, val=1: pre=0, ans=3 (using pre - map[-3] = 0 - (-3) = 3) - i=3, val=-3: pre=-3, map[-3]=-3+(-3)=-6 - i=4, val=4:
 * pre=1, ans=7 (using pre - map[-3] = 1 - (-6) = 7)
 *
 * Time Complexity: O(n) Space Complexity: O(k) where k = number of distinct negative values
 */
public class MaximizeSubarraySumAfterRemovingAllOccurrencesOfOneElement {

    /**
     * Finds the maximum subarray sum after optionally removing all occurrences of one value.
     *
     * @param nums Input array
     * @return Maximum possible subarray sum
     */
    public long maxSubarraySum(int[] nums) {

        long ans = nums[0];  // Initialize with first element (handle all-negative case)

        // map[value] = minimum prefix sum if we remove all occurrences of 'value'
        // This tracks the best starting point for a subarray that excludes all 'value's
        Map<Integer, Long> map = new HashMap<>();

        long pre = 0;              // Prefix sum up to current position
        long minFromStart = 0;     // Minimum prefix sum seen so far (for standard Kadane)
        long overallMin = 0;       // Global minimum considering all removal strategies

        // ================================================================
        // Process each element from left to right
        // ================================================================
        for (int val : nums) {
            // Update prefix sum
            pre += val;

            // ============================================================
            // Strategy 1: Maximum subarray using best removal strategy
            // ============================================================
            // Subarray sum from some past position to current = pre - overallMin
            // overallMin gives us the best starting point considering:
            // - No removal (minFromStart)
            // - Removing all occurrences of some negative value (map[v])
            ans = Math.max(ans, pre - overallMin);

            // ============================================================
            // Strategy 2: Update removal tracking for negative values
            // ============================================================
            // Only track negative values (removing positives never helps)
            if (val < 0) {
                // prevMinForVal = minimum prefix sum before including any occurrence of val
                // If val hasn't been seen: use minFromStart (current min before first occurrence)
                // If val was seen before: use the tracked minimum for this value
                long prevMinForVal = map.getOrDefault(val, minFromStart);

                // Update map[val] to include current occurrence of val
                // We can start from:
                // 1. prevMinForVal (best prefix before any val)
                // 2. minFromStart (current minimum prefix)
                // Choose the smaller, then add val (since we're "removing" it conceptually,
                // we add its contribution to the accumulated sum of vals to remove)
                map.put(val, Math.min(prevMinForVal, minFromStart) + val);

                // Update overall minimum with this new removal strategy
                overallMin = Math.min(overallMin, map.get(val));
            }

            // ============================================================
            // Update minimum prefix sum from start (standard Kadane)
            // ============================================================
            minFromStart = Math.min(minFromStart, pre);

            // Update overall minimum (no removal vs. with removal)
            overallMin = Math.min(overallMin, minFromStart);
        }

        return ans;
    }
}