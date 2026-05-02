package Leetcode;

import java.util.TreeMap;

//Problem Link: https://leetcode.com/problems/maximum-balanced-subsequence-sum/

/**
 * Solution for finding the maximum sum of a balanced subsequence.
 *
 * A subsequence is "balanced" if for any two consecutive elements in the subsequence at indices i and j (where i < j in
 * the original array): nums[j] - nums[i] >= j - i
 *
 * Key insight: Rearranging the inequality gives us: nums[j] - j >= nums[i] - i
 *
 * This means we can transform each element to (nums[i] - i) and maintain a DP structure where we track the maximum sum
 * achievable for each transformed value.
 *
 * Uses a TreeMap to efficiently maintain and query the best subsequence sums with monotonic properties (both keys and
 * values should be increasing).
 */
public class MaximumBalancedSubsequenceSum {

    /**
     * Finds the maximum sum of a balanced subsequence.
     *
     * Algorithm: 1. Transform each element: key = nums[i] - i 2. For each element, find the best subsequence ending at
     * a smaller key 3. Maintain a TreeMap where: - Keys are transformed values (nums[i] - i) - Values are maximum
     * subsequence sums ending at that transformed value 4. Keep the map optimal by removing dominated entries
     *
     * @param nums Input array of integers
     * @return Maximum sum of any balanced subsequence
     */
    public long maxBalancedSubsequenceSum(int[] nums) {

        // TreeMap to store (transformed_value -> max_sum_ending_here)
        // Maintains sorted order by keys for efficient floor/ceiling queries
        TreeMap<Integer, Long> map = new TreeMap<>();
        long ans = Long.MIN_VALUE;

        int n = nums.length;

        // Process elements left to right
        for (int i = 0; i < n; i++) {
            // Transform the element: key represents nums[i] - i
            // For a balanced subsequence, we need nums[j] - j >= nums[i] - i
            int key = nums[i] - i;

            // Optimization: negative/zero values can only decrease the sum
            // So we only consider them as single-element subsequences
            if (nums[i] <= 0) {
                ans = Math.max(ans, nums[i]);
                continue;
            }

            // Find the best subsequence we can extend (with key <= current key)
            // floor gives us the largest key that is <= our current key
            var floor = map.floorEntry(key);
            long curr = nums[i];  // Start with just the current element

            if (floor != null) {
                // Extend the best subsequence ending at floor.key
                curr += floor.getValue();
            }

            // Remove all entries with key >= current key that have worse sums
            // These entries are dominated by our current (key, curr) pair
            // If key' >= key and sum' <= curr, then (key', sum') is never optimal
            var ceiling = map.ceilingEntry(key);
            while (ceiling != null && ceiling.getValue() <= curr) {
                map.remove(ceiling.getKey());
                ceiling = map.ceilingEntry(key);
            }

            // Check again if we should add current entry to the map
            floor = map.floorEntry(key);

            // Only add if this entry is better than what we have for this key range
            // If floor exists and has a better sum, we don't add current entry
            if (floor == null || floor.getValue() < curr) {
                map.put(key, curr);
            }

            // Update global maximum
            ans = Math.max(ans, curr);
        }

        return ans;
    }


}

