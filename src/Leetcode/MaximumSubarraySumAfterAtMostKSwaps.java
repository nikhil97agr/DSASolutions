package Leetcode;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * Problem: Maximum Subarray Sum After K Swaps
 *
 * Given an integer array nums and an integer k, you are allowed to perform at most k swap operations.
 * In one swap operation, you may choose any two indices i and j and swap nums[i] and nums[j].
 * Return the maximum possible subarray sum after performing the swaps.
 *
 * Problem Link: https://leetcode.com/problems/maximum-subarray-sum-after-at-most-k-swaps
 *
 * Approach: Greedy strategy with sliding window
 * - For each possible starting position, simulate building an optimal subarray
 * - Use k swaps to replace smaller elements with the largest available elements
 * - Maintain two sets: 'excluded' (smallest n-k elements that can be swapped out)
 *   and 'included' (largest k elements that can be swapped in)
 * - TreeMaps are used to efficiently access the largest available element to swap in
 */
public class MaximumSubarraySumAfterAtMostKSwaps{

    /**
     * Calculates the maximum possible subarray sum after performing at most k swaps.
     *
     * Time Complexity: O(n^2 * log n) where n is the length of nums
     * Space Complexity: O(n) for the sorted clone and TreeMaps
     *
     * @param nums The input array of integers
     * @param k The maximum number of swaps allowed
     * @return The maximum possible subarray sum
     */
    public long maxSum(int[] nums, int k) {

        // Initialize answer to a very small value to handle arrays with all negative numbers
        long ans = -1_000_000_000_000_0L;

        // Create a sorted copy to identify which elements are candidates for swapping
        // The largest k elements in clone will be our "swap-in pool"
        int clone[] = nums.clone();
        Arrays.sort(clone);
        var n = nums.length;

        // Try every possible starting position for the subarray
        for (int i = 0; i < n; i++) {
            long sum = 0;

            // excluded: TreeMap containing the smallest (n-k) elements (can be swapped out)
            // included: TreeMap containing the largest k elements (can be swapped in)
            // Using TreeMap to maintain sorted order and efficiently get max/min elements
            var excluded = new TreeMap<Integer, Integer>();
            var included = new TreeMap<Integer, Integer>();

            // Populate 'excluded' with the smallest (n-k) elements from sorted array
            // These are elements we DON'T want in our subarray initially
            for (int j = 0; j < n - k; j++) {
                add(excluded, clone[j]);
            }

            // Populate 'included' with the largest k elements from sorted array
            // These are the best elements we CAN swap into our subarray
            for (int j = n - k; j < n; j++) {
                add(included, clone[j]);
            }

            // Build subarray starting from position i
            // For each position j in the subarray, greedily choose the best element to add
            for (int j = i; j < n; j++) {
                // If there are still elements in the excluded set, we can perform swaps
                if (!excluded.isEmpty()) {
                    // Check if the current element nums[j] is in the excluded set
                    if (excluded.containsKey(nums[j])) {
                        // nums[j] is a small element we don't want
                        // Move it from excluded to included (effectively swapping it out)
                        add(included, nums[j]);
                        remove(excluded, nums[j]);
                    } else {
                        // nums[j] is either already good or not in excluded
                        // Take the largest element from excluded set and add it to included
                        // This simulates swapping: swap out a large excluded element for nums[j]
                        int max = excluded.lastKey();
                        add(included, max);
                        remove(excluded, max);
                    }
                }

                // Add the largest available element from included set to our subarray sum
                // This is the optimal choice at this position
                sum += included.lastKey();
                remove(included, included.lastKey());

                // Update the maximum sum found so far
                ans = Math.max(ans, sum);
            }
        }

        // Return the maximum subarray sum found across all starting positions
        return ans;
    }

    /**
     * Helper method to add a value to the TreeMap (multiset implementation).
     * Increments the frequency count of the value in the map.
     *
     * @param map The TreeMap representing a multiset
     * @param val The value to add
     */
    private void add(Map<Integer, Integer> map, int val) {
        // merge() adds 1 to the existing count, or sets it to 1 if not present
        map.merge(val, 1, Integer::sum);
    }

    /**
     * Helper method to remove a value from the TreeMap (multiset implementation).
     * Decrements the frequency count of the value in the map.
     * If the count reaches 0, removes the entry from the map.
     *
     * @param map The TreeMap representing a multiset
     * @param val The value to remove
     */
    private void remove(Map<Integer, Integer> map, int val) {
        // merge() subtracts 1 from the existing count
        map.merge(val, -1, Integer::sum);
        // Remove the key completely if its count becomes 0
        if (map.get(val) == 0) {
            map.remove(val);
        }
    }
}