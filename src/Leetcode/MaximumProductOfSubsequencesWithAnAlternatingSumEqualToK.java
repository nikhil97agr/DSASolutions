package Leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//Problem Link: https://leetcode.com/problems/maximum-product-of-subsequences-with-an-alternating-sum-equal-to-k/

/**
 * Solution for finding maximum product of subsequences with alternating sum equal to k.
 *
 * Given an array nums, find a subsequence where:
 * 1. The alternating sum equals k (alternating: +nums[i] -nums[j] +nums[k] ...)
 * 2. The product of elements is maximized
 * 3. The product does not exceed a given limit
 *
 * An alternating sum alternates between addition and subtraction:
 * Example: [a, b, c, d] -> a - b + c - d (starting with +)
 *
 * Uses dynamic programming with memoization:
 * - State: (index, current_sum, last_operation, current_product)
 * - Operations: 0 = no element selected yet, 1 = last was subtracted, 2 = last was added
 *
 * Time Complexity: O(n * sum_range * limit * 3) with memoization
 * Space Complexity: O(n * sum_range * limit * 3) for DP cache
 */
public class MaximumProductOfSubsequencesWithAnAlternatingSumEqualToK {

    Map<Long, Integer> dp;  // Memoization cache: state -> maximum product

    /**
     * Finds the maximum product of a subsequence with alternating sum equal to k.
     *
     * @param nums  Input array
     * @param k     Target alternating sum
     * @param limit Maximum allowed product
     * @return Maximum product achieving alternating sum k, or -1 if impossible
     */
    public int maxProduct(int[] nums, int k, int limit) {

        int sum = Arrays.stream(nums).sum();

        // Early termination: check if k is achievable
        // Maximum possible alternating sum: all positive elements added
        // Minimum possible alternating sum: all elements subtracted (negative sum)
        if (sum < k || k < -sum) {
            return -1;
        }

        dp = new HashMap<>();

        // Start DFS: index 0, sum 0, no operation yet (0), product 0
        return solve(0, 0, 0, 0, k, limit, nums);
    }

    /**
     * Recursive DP function to find maximum product.
     *
     * @param i      Current index in nums array
     * @param sum    Current alternating sum achieved so far
     * @param prevOp Previous operation: 0 = none, 1 = subtracted, 2 = added
     * @param prod   Current product of selected elements
     * @param k      Target alternating sum
     * @param limit  Maximum allowed product
     * @param nums   Input array
     * @return Maximum product achievable from index i onwards, or -1 if impossible
     */
    private int solve(int i, int sum, int prevOp, int prod, int k, int limit, int nums[]) {

        // Base case: reached end of array
        if (i == nums.length) {
            // Check validity:
            // 1. Alternating sum must equal k
            // 2. Must have selected at least one element (prevOp != 0)
            // 3. Product must not exceed limit
            if (sum != k || prevOp == 0 || prod > limit) {
                return -1;
            }
            return prod;
        }

        // Memoization: check if this state was already computed
        long key = getKey(i, sum, prod, prevOp);
        if (dp.containsKey(key)) {
            return dp.get(key);
        }

        // Option 1: Skip current element (don't include in subsequence)
        int ans = solve(i + 1, sum, prevOp, prod, k, limit, nums);

        // Option 2: Include current element (operation depends on prevOp)
        if (prevOp == 0) {
            // First element: must be added (alternating sum starts with +)
            // New operation: 2 (added), new sum: sum + nums[i], new product: nums[i]
            ans = Math.max(ans, solve(i + 1, sum + nums[i], 2, nums[i], k, limit, nums));
        } else if (prevOp == 1) {
            // Previous was subtracted, so current must be added
            // New operation: 2 (added), new sum: sum + nums[i]
            // Product: multiply current product by nums[i], but cap at limit+1 to avoid overflow
            ans = Math.max(ans, solve(i + 1, sum + nums[i], 2, Math.min(nums[i] * prod, limit + 1), k, limit, nums));
        } else {
            // prevOp == 2: Previous was added, so current must be subtracted
            // New operation: 1 (subtracted), new sum: sum - nums[i]
            // Product: multiply current product by nums[i], but cap at limit+1
            ans = Math.max(ans, solve(i + 1, sum - nums[i], 1, Math.min(nums[i] * prod, limit + 1), k, limit, nums));
        }

        // Cache and return result
        dp.put(key, ans);
        return ans;
    }

    /**
     * Generates a unique key for memoization from the DP state.
     *
     * Combines four parameters into a single long value:
     * - a: index (0 to n-1)
     * - b: sum (range approximately -2000 to 2000)
     * - c: product (0 to limit, typically up to 5000)
     * - d: prevOp (0, 1, or 2)
     *
     * @param a Index
     * @param b Sum
     * @param c Product
     * @param d Previous operation
     * @return Unique key as a long value
     */
    private long getKey(int a, int b, int c, int d) {

        // Shift b by 2000 to make it non-negative (sum can be negative)
        b += 2000;

        // Combine using prime multipliers to ensure uniqueness
        // Formula: (((a * 1001) + b) * 5003 + c) * 3 + d
        return ((((long) a * 1001) + b) * 5003 + c) * 3 + d;
    }

}