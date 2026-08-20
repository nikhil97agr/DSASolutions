package Leetcode;// Problem Link: https://leetcode.com/problems/maximum-subarray-sum-after-multiplier/

/**
 * This class solves the problem of finding the maximum subarray sum after applying operations.
 *
 * Problem: Given an array of integers and a multiplier k, you can perform operations on elements: 1. Keep the element
 * as is 2. Multiply the element by k 3. Divide the element by k (with floor/ceil based on sign)
 *
 * The goal is to find the maximum sum of any contiguous subarray after applying these operations.
 */
public class MaximumSubarraySumAfterMultiplier {

    /**
     * Finds the maximum subarray sum after applying optional operations (multiply, divide, or keep as is).
     *
     * Strategy: Uses dynamic programming to track the maximum subarray sum ending at each position, considering all
     * possible operations on each element and whether to extend previous subarrays or start new ones.
     *
     * @param nums the input array of integers
     * @param k the multiplier/divisor value
     * @return the maximum subarray sum achievable
     */
    public long maxSubarraySum(int[] nums, int k) {
        // Initialize with a very small value to handle negative sums
        long negativeInfinity = Long.MIN_VALUE / 2;

        // Track maximum subarray sum ending here with different last operations:
        long maxSumWithOriginalValue = negativeInfinity;      // Last element kept as original value
        long maxSumWithMultipliedValue = negativeInfinity;    // Last element multiplied by k
        long maxSumWithDividedValue = negativeInfinity;       // Last element divided by k
        long maxSumAfterAnyOperation = negativeInfinity;      // Best sum after any of the above operations

        // Global maximum subarray sum found so far
        long globalMaxSum = negativeInfinity;

        // Process each element in the array
        for (int currentNum : nums) {
            // Calculate the three possible transformations of the current element
            long multipliedValue = (long) currentNum * k;
            long dividedValue = currentNum >= 0 ? floor(currentNum, k) : ceil(currentNum, k);

            // Calculate new max sum ending here after keeping original value of current element
            // Options: start new subarray with this element, or extend previous subarrays
            long newMaxWithOriginal = Math.max(currentNum, maxSumAfterAnyOperation + currentNum);
            newMaxWithOriginal = Math.max(newMaxWithOriginal, maxSumWithMultipliedValue + currentNum);
            newMaxWithOriginal = Math.max(newMaxWithOriginal, maxSumWithDividedValue + currentNum);

            // Calculate new max sum ending here after multiplying current element by k
            // Options: start new with multiplied value, or extend compatible previous subarrays
            long newMaxWithMultiplied = Math.max(multipliedValue, maxSumWithMultipliedValue + multipliedValue);
            newMaxWithMultiplied = Math.max(newMaxWithMultiplied, maxSumWithOriginalValue + multipliedValue);

            // Calculate new max sum ending here after dividing current element by k
            // Options: start new with divided value, or extend compatible previous subarrays
            long newMaxWithDivided = Math.max(dividedValue, maxSumWithDividedValue + dividedValue);
            newMaxWithDivided = Math.max(newMaxWithDivided, maxSumWithOriginalValue + dividedValue);

            // Calculate new max sum ending here after any operation on current element
            // This allows starting fresh with the current element in its original form
            long newMaxWithAnyOp = Math.max(currentNum, maxSumWithOriginalValue + currentNum);

            // Update the state variables for the next iteration
            maxSumAfterAnyOperation = newMaxWithOriginal;
            maxSumWithMultipliedValue = newMaxWithMultiplied;
            maxSumWithDividedValue = newMaxWithDivided;
            maxSumWithOriginalValue = newMaxWithAnyOp;

            // Find the best sum ending at current position across all operation types
            long currentBestSum = Math.max(maxSumAfterAnyOperation, maxSumWithMultipliedValue);
            currentBestSum = Math.max(currentBestSum, maxSumWithDividedValue);
            currentBestSum = Math.max(currentBestSum, maxSumWithOriginalValue);

            // Update global maximum if current position has a better sum
            globalMaxSum = Math.max(globalMaxSum, currentBestSum);
        }

        return globalMaxSum;
    }

    /**
     * Performs ceiling division of x by k. Used for negative numbers to ensure proper rounding towards positive
     * infinity.
     *
     * @param value the numerator
     * @param divisor the denominator
     * @return the ceiling of value/divisor
     */
    private long ceil(int value, int divisor) {

        return (long) Math.ceil((1.0 * value) / divisor);
    }

    /**
     * Performs floor division of x by k. Used for positive numbers to ensure proper rounding towards negative
     * infinity.
     *
     * @param value the numerator
     * @param divisor the denominator
     * @return the floor of value/divisor
     */
    private long floor(int value, int divisor) {

        return (value / divisor);
    }
}