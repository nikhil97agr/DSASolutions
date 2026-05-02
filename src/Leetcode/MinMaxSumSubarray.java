package Leetcode;

import java.util.Arrays;
import java.util.Stack;

//Problem Link: https://leetcode.com/problems/maximum-and-minimum-sums-of-at-most-size-k-subarrays/

/**
 * Solution for computing the sum of (max + min) across all subarrays of size at most k.
 *
 * For each subarray of length <= k, we compute (maximum_element + minimum_element), then sum all these values across
 * all valid subarrays.
 *
 * Algorithm: 1. For each element, determine the range where it serves as the maximum 2. For each element, determine the
 * range where it serves as the minimum 3. Count how many subarrays (of size <= k) have this element as max/min 4. Sum
 * up contributions: element_value * count_of_subarrays
 *
 * Uses monotonic stack to efficiently find: - NGE (Next Greater Element): next index with greater/smaller element - PGE
 * (Previous Greater Element): previous index with greater/smaller element
 *
 * Time Complexity: O(n) Space Complexity: O(n)
 */
public class MinMaxSumSubarray {

    /**
     * Computes the sum of (max + min) for all subarrays of length at most k.
     *
     * @param nums Input array
     * @param k Maximum subarray length
     * @return Sum of (max_element + min_element) across all valid subarrays
     */
    public long minMaxSubarraySum(int[] nums, int k) {

        int n = nums.length;

        // Calculate sum of maximums + sum of minimums separately
        return max(nums, k, n) + min(nums, k, n);
    }

    /**
     * Calculates the sum of maximum elements across all subarrays of length <= k.
     *
     * For each element, determines its contribution as a maximum element.
     *
     * @param nums Input array
     * @param k Maximum subarray length
     * @param n Array length
     * @return Sum of all maximum elements
     */
    private long max(int nums[], int k, int n) {

        // Find ranges where each element is the maximum
        int nge[] = nge(nums, n, true);  // Next index with greater or equal element
        int pge[] = pge(nums, n, true);  // Previous index with greater element
        return calc(nge, pge, nums, k, n);
    }

    /**
     * Calculates the sum of minimum elements across all subarrays of length <= k.
     *
     * For each element, determines its contribution as a minimum element.
     *
     * @param nums Input array
     * @param k Maximum subarray length
     * @param n Array length
     * @return Sum of all minimum elements
     */
    private long min(int nums[], int k, int n) {

        // Find ranges where each element is the minimum
        int nge[] = nge(nums, n, false);  // Next index with smaller or equal element
        int pge[] = pge(nums, n, false);  // Previous index with smaller element

        return calc(nge, pge, nums, k, n);
    }

    /**
     * Calculates the total contribution of each element within its dominance range.
     *
     * For each element nums[i], determines the range [leftIndex+1, rightIndex-1] where it serves as the max/min
     * element. Then counts how many subarrays of length <= k contain i and have nums[i] as their max/min.
     *
     * @param nge Next greater/smaller element indices
     * @param pge Previous greater/smaller element indices
     * @param nums Input array
     * @param k Maximum subarray length
     * @param n Array length
     * @return Total contribution sum
     */
    private long calc(int nge[], int pge[], int nums[], int k, int n) {

        long ans = 0;

        // For each element, calculate its contribution
        for (int i = 0; i < n; i++) {
            // Range where nums[i] is the max/min element
            int leftIndex = pge[i];   // Previous greater/smaller element index
            int rightIndex = nge[i];  // Next greater/smaller element index

            // Maximum possible range where nums[i] is max/min
            int leftLen = i - leftIndex;      // Positions to the left (including i)
            int rightLen = rightIndex - i;    // Positions to the right (including i)

            // Case 1: All subarrays in the dominance range have length <= k
            // In this case, we can use all combinations of left and right boundaries
            if (leftLen + rightLen - 1 <= k) {
                // Total subarrays containing i in range [leftIndex+1, rightIndex-1]
                // = (ways to choose left boundary) * (ways to choose right boundary)
                long sub = 1l * leftLen * rightLen;
                ans += sub * nums[i];
                continue;
            }

            // Case 2: Some subarrays would exceed length k
            // Need to carefully count only subarrays of length <= k

            // Constrain the range to ensure subarrays don't exceed length k
            int minLeft = Math.max(i - k + 1, leftIndex + 1);   // Leftmost valid start
            int minRight = Math.min(i + k - 1, rightIndex - 1); // Rightmost valid end

            leftLen = i - minLeft;      // Constrained left range
            rightLen = minRight - i;    // Constrained right range

            long sub = 0;

            // Optimization: iterate over the smaller dimension to reduce time complexity
            if (leftLen < rightLen) {
                // Iterate over left boundaries
                for (int j = minLeft; j <= i; j++) {
                    // For each left boundary j, find valid right boundaries
                    // Right boundary can be at most j + k - 1 (length constraint)
                    // and at most minRight (dominance constraint)
                    int right = Math.min(minRight, j + k - 1);

                    // Count subarrays [j, ...i..., end] where end in [i, right]
                    sub += (right - i + 1);
                }
            } else {
                // Iterate over right boundaries
                for (int j = minRight; j >= i; j--) {
                    // For each right boundary j, find valid left boundaries
                    // Left boundary can be at least j - k + 1 (length constraint)
                    // and at least minLeft (dominance constraint)
                    int left = Math.max(j - k + 1, minLeft);

                    // Count subarrays [start, ...i..., j] where start in [left, i]
                    sub += (i - left + 1);
                }
            }

            // Add contribution: element value * count of subarrays
            ans += sub * nums[i];
        }

        return ans;
    }

    /**
     * Finds the Previous Greater/Smaller Element for each position.
     *
     * Uses a monotonic stack to efficiently find, for each element, the index of the nearest previous element that is
     * greater (or smaller) than it.
     *
     * @param nums Input array
     * @param n Array length
     * @param isMax If true, finds previous greater element; if false, finds previous smaller
     * @return Array where pge[i] is the index of the previous greater/smaller element, or -1 if no such element exists
     */
    private int[] pge(int nums[], int n, boolean isMax) {

        int pge[] = new int[n];
        var stack = new Stack<Integer>();  // Stack stores indices
        Arrays.fill(pge, -1);  // Default: no previous greater/smaller element

        // Traverse from right to left
        for (int i = n - 1; i >= 0; i--) {
            // Pop elements from stack that are smaller/greater than current
            // When we pop index j, it means i is its previous greater/smaller element
            while (!stack.isEmpty() &&
                    (isMax ? nums[stack.peek()] < nums[i] : nums[stack.peek()] > nums[i])) {
                pge[stack.pop()] = i;
            }

            stack.push(i);  // Add current index to stack
        }

        return pge;
    }

    /**
     * Finds the Next Greater/Smaller Element for each position.
     *
     * Uses a monotonic stack to efficiently find, for each element, the index of the nearest next element that is
     * greater or equal (or smaller or equal) to it.
     *
     * @param nums Input array
     * @param n Array length
     * @param isMax If true, finds next greater/equal element; if false, finds next smaller/equal
     * @return Array where nge[i] is the index of the next greater/smaller element, or n if no such element exists
     */
    private int[] nge(int nums[], int n, boolean isMax) {

        int nge[] = new int[n];
        var stack = new Stack<Integer>();  // Stack stores indices
        Arrays.fill(nge, n);  // Default: no next greater/smaller element (use n as sentinel)

        // Traverse from left to right
        for (int i = 0; i < n; i++) {
            // Pop elements from stack that are smaller/greater or equal to current
            // When we pop index j, it means i is its next greater/smaller element
            while (!stack.isEmpty() &&
                    (isMax ? nums[stack.peek()] <= nums[i] : nums[stack.peek()] >= nums[i])) {
                nge[stack.pop()] = i;
            }

            stack.push(i);  // Add current index to stack
        }

        return nge;
    }

}