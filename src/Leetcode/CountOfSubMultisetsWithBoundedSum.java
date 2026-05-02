package Leetcode;

import java.util.HashMap;
import java.util.List;

//Problem Link: https://leetcode.com/problems/count-of-sub-multisets-with-bounded-sum/

/**
 * Solution for counting sub-multisets with bounded sum.
 *
 * Given a list of numbers (with possible duplicates), count how many sub-multisets
 * have a sum in the range [l, r].
 *
 * Uses dynamic programming with a sliding window technique to efficiently handle
 * multiple occurrences of the same number.
 *
 * Key insight: For a number that appears 'cnt' times, we can take 0, 1, 2, ..., cnt
 * instances of it. This creates a bounded knapsack problem for each unique number.
 */
public class CountOfSubMultisetsWithBoundedSum {

    int mod = 1_000_000_007;  // Modulo for result

    /**
     * Counts the number of sub-multisets with sum in range [l, r].
     *
     * @param nums List of integers (may contain duplicates)
     * @param l    Lower bound of sum (inclusive)
     * @param r    Upper bound of sum (inclusive)
     * @return Number of sub-multisets with sum in [l, r], modulo 10^9 + 7
     */
    public int countSubMultisets(List<Integer> nums, int l, int r) {

        // Count frequency of each unique number
        var map = new HashMap<Integer, Integer>();
        for (var x : nums) {
            map.merge(x, 1, Integer::sum);
        }

        // dp[sum] = number of ways to achieve sum using numbers processed so far
        var dp = new int[r + 1];

        // Process each unique number with its frequency
        for (var entry : map.entrySet()) {
            int num = entry.getKey();   // The unique number
            int cnt = entry.getValue(); // How many times it appears

            // Build prefix sum array for sliding window optimization
            // preSum helps us efficiently compute sum ranges
            var preSum = dp.clone();
            for (int sum = 0; sum <= r; sum++) {
                if (sum - num >= 0) {
                    // preSum[sum] = total ways to achieve any sum using unlimited instances of 'num'
                    preSum[sum] = add(preSum[sum], preSum[sum - num]);
                }
            }

            // Update dp array considering we can use at most 'cnt' instances of 'num'
            for (int sum = r; sum >= 0; sum--) {
                if (num > 0) {
                    // Calculate the position beyond which we'd use more than 'cnt' instances
                    // j represents the sum below which we can use all instances freely
                    int j = sum - prod(num, cnt + 1);

                    if (j >= 0) {
                        // We need to subtract ways that use more than 'cnt' instances
                        // dp[sum] = ways from sum <= j + (ways from j+1 to sum using <= cnt instances)
                        // preSum[sum] - preSum[j] gives ways using instances from j+1 to sum
                        dp[sum] = add(dp[j], add(preSum[sum], -preSum[j]));
                    } else {
                        // All ways to achieve 'sum' use at most 'cnt' instances
                        dp[sum] = add(dp[j], preSum[sum]);
                    }
                } else {
                    // Special case: num = 0
                    // We can either include or exclude each zero (cnt + 1 choices total)
                    // If we include k zeros (0 <= k <= cnt), sum doesn't change
                    // So we multiply the number of ways by (cnt + 1)
                    dp[sum] = prod(dp[sum], cnt + 1);
                }
            }
        }

        // Sum up all ways to achieve sums in the range [l, r]
        var ans = 0;
        for (var i = l; i <= r; i++) {
            ans = add(ans, dp[i]);
        }

        return ans;
    }

    /**
     * Adds two numbers with modulo arithmetic, handling negative values.
     *
     * @param a First number
     * @param b Second number
     * @return (a + b) mod (10^9 + 7), guaranteed to be non-negative
     */
    private int add(int a, int b) {

        // Add mod to handle negative intermediate results
        long sum = 1l * a + b + mod;
        return (int) (sum % mod);
    }

    /**
     * Multiplies two numbers with modulo arithmetic.
     *
     * @param a First number
     * @param b Second number
     * @return (a * b) mod (10^9 + 7)
     */
    private int prod(int a, int b) {

        long p = 1l * a * b;

        return (int) (p % mod);
    }
}