package Leetcode;

import java.util.Arrays;
import java.util.Stack;

//Problem Link: https://leetcode.com/problems/sum-of-total-strength-of-wizards

/**
 * Solution for calculating the total strength of all wizards. For each subarray, strength = (sum of subarray) *
 * (minimum element in subarray). Total strength = sum of strengths of all possible subarrays. Uses monotonic stacks and
 * prefix sums to efficiently calculate the contribution of each element when it's the minimum in various subarrays.
 */
public class SumOfTotalStrengthOfWizards {

    int mod = 1_000_000_007;  // Modulo for the result

    /**
     * Calculates the total strength of all wizards across all possible subarrays.
     *
     * @param strength Array of wizard strengths
     * @return Total strength modulo 10^9 + 7
     */
    public int totalStrength(int[] strength) {

        int n = strength.length;

        // Prefix sum: pre[i] = sum of strength[0..i-1]
        int pre[] = new int[n + 1];
        // Prefix sum of prefix sums: preTotal[i] = sum of pre[0..i-1]
        // This allows us to calculate sum of sums efficiently
        int preTotal[] = new int[n + 2];

        // Build prefix sum array
        for (int i = 0; i < n; i++) {
            pre[i + 1] = add(pre[i], strength[i]);

        }
        // Build prefix sum of prefix sums
        for (int i = 0; i <= n; i++) {
            preTotal[i + 1] = add(preTotal[i], pre[i]);
        }

        int ans = 0;
        // nge[i] = index of next element strictly smaller than strength[i] (or n if none)
        int nge[] = nge(strength, n);
        // pge[i] = index of previous element smaller or equal to strength[i] (or -1 if none)
        int pge[] = pge(strength, n);

        // For each element, calculate its contribution when it's the minimum
        for (int i = 0; i < n; i++) {
            int leftInd = pge[i];      // Left boundary (exclusive)
            int rightInd = nge[i];     // Right boundary (exclusive)
            int leftLen = i - leftInd;  // Number of positions to the left (inclusive of i)
            int rightLen = rightInd - i; // Number of positions to the right (inclusive of i)

            // Calculate sum contribution using prefix sums
            // leftSum: sum of prefix sums from leftInd+1 to i
            int leftSum = add(preTotal[i + 1], -preTotal[leftInd + 1]);
            // rightSum: sum of prefix sums from i+1 to rightInd
            int rightSum = add(preTotal[rightInd + 1], -preTotal[i + 1]);

            // Calculate total contribution of strength[i] as minimum
            // Formula: (rightSum * leftLen - leftSum * rightLen) * strength[i]
            int total = prod(rightSum, leftLen);
            total = add(total, -prod(leftSum, rightLen));
            ans = add(ans, prod(total, strength[i]));
        }

        return ans;
    }

    /**
     * Finds the Previous Greater or Equal element (PGE) for each index. Uses a monotonic stack to find the index of the
     * previous element that is smaller than or equal to nums[i].
     *
     * @param nums Input array
     * @param n Length of array
     * @return Array where pge[i] = index of previous smaller/equal element, or -1 if none
     */
    int[] pge(int nums[], int n) {

        int pge[] = new int[n];
        Arrays.fill(pge, -1);  // Default: no previous smaller/equal element
        var stack = new Stack<Integer>();

        // Traverse right to left
        for (int i = n - 1; i >= 0; i--) {
            // Pop elements that are greater than or equal to current element
            // and set their PGE to current index
            while (!stack.isEmpty() && nums[stack.peek()] >= nums[i]) {
                pge[stack.pop()] = i;
            }

            stack.push(i);
        }

        return pge;
    }

    /**
     * Finds the Next Greater Element (NGE) for each index. Uses a monotonic stack to find the index of the next element
     * that is strictly smaller than nums[i].
     *
     * @param nums Input array
     * @param n Length of array
     * @return Array where nge[i] = index of next strictly smaller element, or n if none
     */
    int[] nge(int nums[], int n) {

        int nge[] = new int[n];
        Arrays.fill(nge, n);  // Default: no next smaller element (use n as boundary)
        var stack = new Stack<Integer>();

        // Traverse left to right
        for (int i = 0; i < n; i++) {
            // Pop elements that are strictly greater than current element
            // and set their NGE to current index
            while (!stack.isEmpty() && nums[stack.peek()] > nums[i]) {
                nge[stack.pop()] = i;
            }

            stack.push(i);
        }

        return nge;
    }

    /**
     * Performs modular addition to prevent overflow and handle negative numbers. Adds mod before taking modulo to
     * handle negative results correctly.
     *
     * @param a First number
     * @param b Second number (can be negative)
     * @return (a + b) mod 10^9 + 7, always non-negative
     */
    private int add(long a, long b) {

        return (int) ((a + b + mod) % mod);
    }

    /**
     * Performs modular multiplication to prevent overflow.
     *
     * @param a First number
     * @param b Second number
     * @return (a * b) mod 10^9 + 7
     */
    private int prod(long a, long b) {

        return (int) ((a * b) % mod);
    }
}