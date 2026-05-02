package Leetcode;

import java.util.Arrays;

//Problem Link: https://leetcode.com/problems/sum-of-good-subsequences

/**
 * Solution for computing the sum of all "good" subsequences.
 *
 * Problem: Given an array nums, a subsequence is "good" if for every pair of consecutive elements in the subsequence,
 * their absolute difference is exactly 1.
 *
 * Definition of "good" subsequence: - For consecutive elements a[i] and a[j] (where i < j in the subsequence), |a[i] -
 * a[j]| = 1 - This means each element differs from the next by exactly +1 or -1
 *
 * Examples: - [1, 2, 3] is good: |1-2|=1, |2-3|=1 ✓ - [1, 2, 1] is good: |1-2|=1, |2-1|=1 ✓ - [1, 3] is NOT good:
 * |1-3|=2 ✗
 *
 * Task: Find the sum of all elements in all good subsequences.
 *
 * Key insight: Dynamic Programming by value - For each value v, track: 1. cntSeq[v]: Count of good subsequences ending
 * with value v 2. seqSum[v]: Sum of all elements in all good subsequences ending with value v
 *
 * Transition: - When we see value x, we can extend subsequences ending with (x-1) or (x+1) - Or start a new subsequence
 * with just x
 *
 * Why this works: - A good subsequence ending with x can be extended from: * Any good subsequence ending with (x-1) + x
 * * Any good subsequence ending with (x+1) + x * A new subsequence [x]
 *
 * Example: nums = [1, 2, 1] - Process 1: subsequences = {[1]}, sum = 1 - Process 2: can extend [1] → [1,2], plus [2],
 * subsequences = {[1], [1,2], [2]}, sum = 1+3+2 = 6 - Process 1: can extend [1,2] → [1,2,1], [2] → [2,1], plus [1]
 * subsequences = {[1], [1,2], [2], [1,2,1], [2,1], [1]}, sum = 1+3+2+4+3+1 = 14
 *
 * Time Complexity: O(n + max_value) where n = array length Space Complexity: O(max_value)
 */
public class SumOfGoodSubsequences {

    int mod = 1_000_000_007;  // Modulo for preventing overflow

    /**
     * Computes the sum of all good subsequences.
     *
     * @param nums Input array
     * @return Sum of all elements in all good subsequences, modulo 10^9+7
     */
    public int sumOfGoodSubsequences(int[] nums) {

        // Find maximum value to determine array sizes (add buffer for x+1 indexing)
        int max = Arrays.stream(nums).max().getAsInt();

        // cntSeq[v]: Count of good subsequences ending with value v
        int cntSeq[] = new int[max + 3];

        // seqSum[v]: Sum of all elements in all good subsequences ending with value v
        int seqSum[] = new int[max + 3];

        int ans = 0;

        // Process each element in the array
        for (int x : nums) {
            int val = x;  // Original value (for sum calculation)
            x = x + 1;    // Shift by 1 to handle 0-indexing safely (avoid negative indices)

            // ================================================================
            // STEP 1: Update count of subsequences ending with value 'val'
            // ================================================================

            // We can extend subsequences ending with (val-1) or (val+1)
            // Plus we can start a new subsequence with just 'val' (hence the +1)
            int prevCnt = add(cntSeq[x - 1], cntSeq[x + 1] + 1);

            // Add to existing count of subsequences ending with 'val'
            cntSeq[x] = add(prevCnt, cntSeq[x]);

            // ================================================================
            // STEP 2: Update sum of subsequences ending with value 'val'
            // ================================================================

            // Inherit sums from subsequences ending with (val-1) and (val+1)
            // These subsequences are being extended by adding 'val'
            seqSum[x] = add(seqSum[x], seqSum[x - 1], seqSum[x + 1]);

            // Add contribution of 'val' to all new subsequences
            // prevCnt subsequences are being created/extended, each adds 'val' to its sum
            seqSum[x] = add(seqSum[x], prod(prevCnt, val));
        }

        // ================================================================
        // STEP 3: Sum all subsequence sums across all ending values
        // ================================================================
        for (int x : seqSum) {
            ans = add(ans, x);
        }

        return ans;
    }

    /**
     * Adds multiple numbers with modulo arithmetic.
     *
     * Prevents overflow by applying modulo after each addition. Supports variable number of arguments.
     *
     * @param arr Numbers to add
     * @return Sum of all numbers modulo 10^9+7
     */
    private int add(long... arr) {

        long total = 0;
        for (long x : arr) {
            total += x;
            total %= mod;  // Apply modulo to prevent overflow
        }

        return (int) total;
    }

    /**
     * Multiplies multiple numbers with modulo arithmetic.
     *
     * Prevents overflow by applying modulo after each multiplication. Supports variable number of arguments.
     *
     * @param arr Numbers to multiply
     * @return Product of all numbers modulo 10^9+7
     */
    private int prod(long... arr) {

        long total = 1;
        for (long x : arr) {
            total *= x;
            total %= mod;  // Apply modulo to prevent overflow
        }

        return (int) total;
    }
}