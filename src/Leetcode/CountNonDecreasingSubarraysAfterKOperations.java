package Leetcode;

import java.util.ArrayDeque;

//Problem Link: https://leetcode.com/problems/count-non-decreasing-subarrays-after-k-operations

/**
 * Solution for counting non-decreasing subarrays after at most k increment operations.
 *
 * Problem: Given an array nums and integer k, count how many subarrays can be made non-decreasing (sorted in
 * non-decreasing order) using at most k operations, where each operation increments an element by 1.
 *
 * Key insight: Two-pointer sliding window from RIGHT to LEFT
 *
 * Why right to left? - To make a subarray non-decreasing, we can only increase elements (not decrease) - It's easier to
 * calculate: for position i, we raise elements to the right to match or exceed nums[i] - Processing right-to-left lets
 * us build up the cost incrementally
 *
 * Algorithm approach: 1. Use a monotonic deque (stack-like) to track elements in decreasing order 2. For each left
 * endpoint i (processing right to left): - The stack represents the "target heights" for elements to the right - Merge
 * smaller elements into larger ones (they need to be raised) - Track total operations needed to make subarray [i, j]
 * non-decreasing 3. Maintain a sliding window [i, j] where operations <= k - If operations > k, shrink from the right
 * by moving j leftward 4. For each i, count all valid subarrays starting at i: (j - i + 1)
 *
 * Example: nums = [3, 1, 5, 2], k = 3 - i=3 (elem=2): subarray [2], ops=0, count=1 - i=2 (elem=5): subarray [5,2], need
 * to raise 2→5 (3 ops), count=1 (just [5]) - i=1 (elem=1): subarray [1,5,2], need 1→5 (4 ops) + 2→5 (3 ops) = 7 ops > k
 * After shrinking: valid subarrays starting at 1
 *
 * Stack structure: - Each entry: [value, count] - value: The target height for elements - count: How many consecutive
 * elements should be at this height - Stack maintains decreasing order (from front to back)
 *
 * Time Complexity: O(n) - each element is pushed and popped at most once Space Complexity: O(n) for the stack
 */
public class CountNonDecreasingSubarraysAfterKOperations {

    /**
     * Counts subarrays that can be made non-decreasing with at most k operations.
     *
     * @param nums Array of integers
     * @param k Maximum number of increment operations allowed
     * @return Count of valid subarrays
     */
    public long countNonDecreasingSubarrays(int[] nums, int k) {

        var ans = 0l;  // Total count of valid subarrays
        var n = nums.length;
        var j = n - 1;  // Right pointer of the sliding window

        // Stack (deque) storing [value, count] pairs
        // Represents the target structure for non-decreasing subarray
        var stack = new ArrayDeque<int[]>();

        var op = 0l;  // Current total operations needed for subarray [i, j]

        // Process each left endpoint from right to left
        for (var i = n - 1; i >= 0; i--) {
            // ================================================================
            // STEP 1: Add current element to the stack
            // ================================================================
            // Merge all smaller elements on the stack into current element
            // (they need to be raised to nums[i] to maintain non-decreasing order)

            int cnt = 1;  // Count of elements that will be at height nums[i]

            while (!stack.isEmpty() && stack.peekLast()[0] <= nums[i]) {
                // Pop element with value <= nums[i]
                var peek = stack.removeLast();

                // Calculate operations needed to raise peek[0] to nums[i]
                var diff = nums[i] - peek[0];

                // Add operations: diff per element × number of such elements
                op += 1l * diff * peek[1];

                // These elements are now merged into current height
                cnt += peek[1];

            }

            // Push the merged group onto stack
            // All elements represented by this entry need to be at height nums[i]
            stack.offerLast(new int[]{nums[i], cnt});

            // ================================================================
            // STEP 2: Shrink window if operations exceed k
            // ================================================================
            // Remove elements from the right until operations <= k

            while (op > k && j >= i && !stack.isEmpty()) {

                // Get the rightmost element's target value
                var diff = stack.peekFirst()[0] - nums[j];

                // Removing element at position j saves 'diff' operations
                // (we no longer need to raise nums[j] to stack.peekFirst()[0])
                op -= diff;

                // Decrement count for this target height
                stack.peekFirst()[1]--;

                // If no elements remain at this height, remove from stack
                if (stack.peekFirst()[1] == 0) {
                    stack.removeFirst();
                }

                // Move right pointer leftward (shrink window)
                j--;

            }

            // ================================================================
            // STEP 3: Count valid subarrays starting at i
            // ================================================================
            // All subarrays [i, r] where i <= r <= j are valid
            // (they can be made non-decreasing with <= k operations)
            ans += (j - i + 1);
        }

        return ans;
    }
}