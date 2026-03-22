package Leetcode;

import java.util.Arrays;

// Problem Link: https://leetcode.com/problems/find-the-maximum-sequence-value-of-array/description/

/**
 * Solution for Find the Maximum Sequence Value of Array
 *
 * Problem: Given an array nums and an integer k, find the maximum "sequence value". - Select exactly k elements from
 * the left part of the array - Select exactly k elements from the right part of the array - The two parts must not
 * overlap (left indices < right indices) - Sequence value = (OR of left k elements) XOR (OR of right k elements) -
 * Maximize the sequence value
 *
 * Example: nums = [2, 6, 7], k = 1 - Left: select 2 (OR = 2), Right: select 7 (OR = 7) → value = 2 XOR 7 = 5 - Left:
 * select 2 (OR = 2), Right: select 6 (OR = 6) → value = 2 XOR 6 = 4 - Left: select 6 (OR = 6), Right: select 7 (OR = 7)
 * → value = 6 XOR 7 = 1 Maximum: 5
 *
 * Approach: DFS + Memoization + Two-Pointer 1. Use DFS to find all possible OR values achievable by selecting k
 * elements from left - Track the rightmost index where each OR value can be achieved 2. Use DFS to find all possible OR
 * values achievable by selecting k elements from right - Track the leftmost index where each OR value can be achieved
 * 3. For each pair of (left_OR, right_OR), if they don't overlap, compute XOR
 *
 * Key Insight: - OR values are bounded by 0-127 (since nums[i] <= 127 based on typical constraints) - We only need to
 * track the boundary indices for each OR value - indLeft[or] = rightmost index where we can achieve OR value 'or' using
 * k elements from left - indRight[or] = leftmost index where we can achieve OR value 'or' using k elements from right
 *
 * Time Complexity: O(n × 128 × k) for DFS + O(128²) for final computation = O(n × k) Space Complexity: O(n × 128 × k)
 * for memoization
 */
public class MaximumSequenceValueOfArray {

    /**
     * Finds the maximum sequence value
     *
     * @param nums The input array
     * @param k Number of elements to select from each side
     * @return Maximum sequence value (left_OR XOR right_OR)
     */
    public int maxValue(int[] nums, int k) {

        int n = nums.length;

        // indLeft[or] = rightmost index where we can achieve OR value 'or' using k elements from left
        // Initialize to MAX_VALUE (impossible)
        int indLeft[] = new int[128];
        Arrays.fill(indLeft, Integer.MAX_VALUE);

        // indRight[or] = leftmost index where we can achieve OR value 'or' using k elements from right
        // Initialize to -1 (impossible)
        int indRight[] = new int[128];
        Arrays.fill(indRight, -1);

        // DFS from left: find all achievable OR values and their rightmost indices
        dfs1(nums, indLeft, 0, 0, k, n, new Boolean[n][128][k + 1]);

        // DFS from right: find all achievable OR values and their leftmost indices
        dfs2(nums, indRight, n - 1, 0, k, n, new Boolean[n][128][k + 1]);

        int ans = 0;

        // Try all pairs of (left_OR, right_OR)
        for (int i = 0; i < 128; i++) {
            for (int j = 0; j < 128; j++) {
                // Check if the left and right selections don't overlap
                // indLeft[i] is the rightmost index of left selection
                // indRight[j] is the leftmost index of right selection
                // They don't overlap if indLeft[i] < indRight[j]
                if (indLeft[i] < indRight[j]) {
                    ans = Math.max(ans, i ^ j);
                }
            }
        }

        return ans;
    }

    /**
     * DFS from left to right: find all achievable OR values and their rightmost indices
     *
     * This function explores all ways to select exactly k elements from the left part of the array and computes the OR
     * of those elements.
     *
     * For each achievable OR value, we track the MINIMUM rightmost index where it can be achieved (to maximize the
     * space for the right selection).
     *
     * @param nums The input array
     * @param indices Array where indices[or] = rightmost index for achieving OR value 'or'
     * @param i Current index in the array
     * @param or Current OR value of selected elements
     * @param cnt Number of elements still needed to select
     * @param n Length of the array
     * @param dp Memoization table [index][or_value][count]
     */
    private void dfs1(int nums[], int indices[], int i, int or, int cnt, int n, Boolean dp[][][]) {

        // Base case: selected exactly k elements
        if (cnt == 0) {
            // Update the rightmost index for this OR value
            // i-1 because we've moved past the last selected element
            // We want the MINIMUM rightmost index (to leave more space for right selection)
            indices[or] = Math.min(indices[or], i - 1);
            return;
        }

        // Base case: reached end of array without selecting k elements
        if (i == n) {
            return;
        }

        // Memoization: if this state has been visited, skip
        if (dp[i][or][cnt] != null) {
            return;
        }

        // Mark this state as visited
        dp[i][or][cnt] = true;

        // Choice 1: Don't select nums[i], move to next index
        dfs1(nums, indices, i + 1, or, cnt, n, dp);

        // Choice 2: Select nums[i], update OR and decrement count
        dfs1(nums, indices, i + 1, or | nums[i], cnt - 1, n, dp);
    }

    /**
     * DFS from right to left: find all achievable OR values and their leftmost indices
     *
     * This function explores all ways to select exactly k elements from the right part of the array and computes the OR
     * of those elements.
     *
     * For each achievable OR value, we track the MAXIMUM leftmost index where it can be achieved (to maximize the space
     * for the left selection).
     *
     * @param nums The input array
     * @param indices Array where indices[or] = leftmost index for achieving OR value 'or'
     * @param i Current index in the array (starting from n-1, going to 0)
     * @param or Current OR value of selected elements
     * @param cnt Number of elements still needed to select
     * @param n Length of the array
     * @param dp Memoization table [index][or_value][count]
     */
    private void dfs2(int nums[], int indices[], int i, int or, int cnt, int n, Boolean dp[][][]) {

        // Base case: selected exactly k elements
        if (cnt == 0) {
            // Update the leftmost index for this OR value
            // i+1 because we've moved past the last selected element (going backwards)
            // We want the MAXIMUM leftmost index (to leave more space for left selection)
            indices[or] = Math.max(indices[or], i + 1);
            return;
        }

        // Base case: reached beginning of array without selecting k elements
        if (i == -1) {
            return;
        }

        // Memoization: if this state has been visited, skip
        if (dp[i][or][cnt] != null) {
            return;
        }

        // Mark this state as visited
        dp[i][or][cnt] = true;

        // Choice 1: Don't select nums[i], move to previous index
        dfs2(nums, indices, i - 1, or, cnt, n, dp);

        // Choice 2: Select nums[i], update OR and decrement count
        dfs2(nums, indices, i - 1, or | nums[i], cnt - 1, n, dp);
    }
}