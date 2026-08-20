package Leetcode;//Problem Link: https://leetcode.com/contest/biweekly-contest-187/problems/minimum-adjacent-swaps-to-partition-array/

/**
 * Solution for "Minimum Adjacent Swaps to Make Array Good"
 *
 * Problem: Given an array nums and two integers a and b (a < b), partition the array into three contiguous parts such
 * that: - Part 1: All elements < a - Part 2: All elements in range [a, b] - Part 3: All elements > b
 *
 * Strategy: Use a greedy approach with counting inversions.
 *
 * Key Insight: Process array from left to right. For the final configuration to be valid: - All elements < a should be
 * on the LEFT - All elements in [a, b] should be in the MIDDLE - All elements > b should be on the RIGHT
 *
 * When we encounter an element that belongs to an earlier partition (left side), we need to move it past all elements
 * that belong to later partitions (middle and right).
 */
public class MinimumAdjacentSwapsToPartitionArray {

    // Modulo constant as answer can be very large
    int mod = 1_000_000_007;

    /**
     * Calculate minimum adjacent swaps to partition array into three parts.
     *
     * Algorithm: We traverse the array from LEFT to RIGHT and count how many swaps each element needs.
     *
     * Think of the final state: [<a elements] [a-b elements] [>b elements]
     *
     * As we scan left to right: - c1: count of elements in range [a, b] that we've seen so far - c2: count of elements
     * > b that we've seen so far
     *
     * When we encounter an element: 1. If x < a: This element belongs to the LEFT partition - It needs to move past all
     * [a,b] elements (c1) and all >b elements (c2) seen so far - Swaps needed: c1 + c2
     *
     * 2. If a <= x <= b: This element belongs to the MIDDLE partition - It needs to move past all >b elements (c2) seen
     * so far, but NOT past <a elements - Swaps needed: c2 - Increment c1 (we've now seen one more [a,b] element)
     *
     * 3. If x > b: This element belongs to the RIGHT partition - It's already in the correct relative position
     * (rightmost) - Swaps needed: 0 - Increment c2 (we've now seen one more >b element)
     *
     * @param nums The input array
     * @param a Lower bound for middle partition
     * @param b Upper bound for middle partition
     * @return Minimum number of adjacent swaps modulo 10^9 + 7
     */
    public int minAdjacentSwaps(int[] nums, int a, int b) {

        int ans = 0;  // Total number of swaps needed
        int c1 = 0;   // Count of elements in range [a, b] seen so far
        int c2 = 0;   // Count of elements > b seen so far

        // Process each element from left to right
        for (var x : nums) {
            if (x < a) {
                // Element belongs to LEFT partition
                // Must swap past all middle (c1) and right (c2) elements encountered so far
                ans = add(ans, add(c1, c2));
            } else if (x <= b) {
                // Element belongs to MIDDLE partition (a <= x <= b)
                // Must swap past all right (c2) elements encountered so far
                ans = add(ans, c2);
                // Increment count of middle partition elements
                c1 = add(c1, 1);
            } else {
                // Element belongs to RIGHT partition (x > b)
                // Already in correct relative position, no swaps needed
                // Just increment the count
                c2 = add(c2, 1);
            }
        }

        return ans;
    }

    /**
     * Helper method to add two numbers with modulo arithmetic. Adds mod before taking modulo to handle potential
     * negative numbers safely.
     *
     * @param a First number
     * @param b Second number
     * @return (a + b) % mod
     */
    private int add(long a, long b) {

        long sum = a + b + mod;

        return (int) (sum % mod);
    }
}