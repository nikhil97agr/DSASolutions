package Leetcode;

import java.util.ArrayList;
import java.util.List;

//Problem Link: https://leetcode.com/contest/biweekly-contest-179/problems/count-non-decreasing-arrays-with-given-digit-sums/

/**
 * Solution for counting arrays with strictly increasing elements where each element has a specific digit sum.
 *
 * Given an array digitSum[], count how many strictly increasing arrays can be formed where the i-th element has a digit
 * sum equal to digitSum[i].
 *
 * Algorithm: 1. Precompute all numbers [0, 5000] grouped by their digit sums 2. Use DP where dp[x] = count of valid
 * arrays ending with value x 3. For each position, use prefix sums to efficiently count arrays where the next element
 * is strictly greater than the previous
 *
 * Time Complexity: O(max * n) where max = 5000 and n = length of digitSum Space Complexity: O(max)
 */
public class CountNonDecreasingArrays {

    int max = 5000;          // Maximum value to consider for array elements
    List<Integer> list[];    // list[i] contains all numbers with digit sum = i

    /**
     * Counts the number of valid strictly increasing arrays.
     *
     * @param digitSum Array where digitSum[i] is the required digit sum for the i-th element
     * @return Count of valid arrays modulo 10^9 + 7
     */
    public int countArrays(int[] digitSum) {

        // Initialize array of lists to group numbers by their digit sum
        list = new ArrayList[51];  // Digit sum can be at most 50 (for numbers up to 5000)
        for (int i = 0; i <= 50; i++) {
            list[i] = new ArrayList<>();
        }

        // Precompute: group all numbers [0, max] by their digit sum
        for (int i = 0; i <= max; i++) {
            int sum = 0;
            int val = i;

            // Calculate digit sum of the number
            while (val > 0) {
                sum += val % 10;  // Add last digit
                val /= 10;        // Remove last digit
            }

            // Skip numbers with digit sum > 50 (shouldn't happen for max=5000)
            if (sum > 50) {
                continue;
            }

            // Add number to the list corresponding to its digit sum
            list[sum].add(i);
        }

        // dp[x] = number of ways to form a valid array ending with value x
        int dp[] = new int[max + 1];

        // Base case: initialize first position
        // All numbers with digit sum = digitSum[0] can be the first element
        List<Integer> first = list[digitSum[0]];
        for (int x : first) {
            dp[x] = 1;  // One way to have an array of length 1 ending at x
        }

        // Process each subsequent position in the array
        int n = digitSum.length;
        for (int i = 1; i < n; i++) {
            // Build prefix sum array for efficient range sum queries
            // pre[j] = sum of dp[0] + dp[1] + ... + dp[j]
            int pre[] = new int[max + 1];
            pre[0] = dp[0];
            for (int j = 1; j <= max; j++) {
                pre[j] = add(pre[j - 1], dp[j]);
            }

            // Create new DP array for current position
            int newDp[] = new int[max + 1];

            // For each valid number at position i (with correct digit sum)
            for (int x : list[digitSum[i]]) {
                // Count all arrays ending at values < x (strictly increasing constraint)
                // pre[x-1] would give sum of dp[0..x-1], but we use pre[x] which includes dp[x]
                // This works because we're building newDp, so dp[x] refers to previous position
                newDp[x] = pre[x];
            }

            // Update dp to newDp for next iteration
            dp = newDp;
        }

        // Sum all ways across all possible ending values
        int ans = 0;
        for (int x : dp) {
            ans = add(ans, x);
        }

        return ans;


    }

    /**
     * Adds two numbers with modulo arithmetic.
     *
     * @param a First number
     * @param b Second number
     * @return (a + b) mod (10^9 + 7)
     */
    private int add(int a, int b) {

        int mod = 1_000_000_007;

        return (int) ((1l * a + b) % mod);
    }
}