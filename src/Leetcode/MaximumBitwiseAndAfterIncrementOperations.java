package Leetcode;//Problem Link: https://leetcode.com/problems/maximum-bitwise-and-after-increment-operations/

import java.util.Arrays;

/**
 * Solution for maximizing bitwise AND after increment operations.
 *
 * Problem: Given an array nums, you can perform at most k increment operations (add 1 to any element). After
 * operations, select m elements and compute their bitwise AND. Maximize this AND value.
 *
 * Key insights:
 *
 * Bitwise AND property: - AND of multiple numbers has a bit set to 1 only if ALL numbers have that bit as 1 - To
 * maximize AND, we want to maximize the most significant bits first - A larger bit position contributes more to the
 * final value
 *
 * Greedy strategy: - Build the answer bit by bit from most significant (bit 30) to least significant (bit 0) - For each
 * bit position, try to set it to 1 in the answer - Check if we can make m numbers have all required bits with ≤ k
 * operations
 *
 * Why greedy works: - Setting bit i adds 2^i to the answer - Bit 30 contributes 2^30, bit 29 contributes 2^29, etc. -
 * 2^30 > 2^29 + 2^28 + ... + 2^0 (geometric series property) - So prioritizing higher bits always gives better results
 *
 * Example: nums = [5, 3, 7], k = 5, m = 2 - Binary: [101, 011, 111] - Try bit 2 (value 4): Need [1xx, 1xx] → increment
 * 3 to 4 or higher (cost 1) → possible - Try bit 1 (value 6): Need [11x, 11x] → increment 5 to 6 (cost 1), 3 to 6 (cost
 * 3) → total 4 ≤ 5 ✓ - Try bit 0 (value 7): Need [111, 111] → increment 5 to 7 (cost 2), 3 to 7 (cost 4) → total 6 > 5
 * ✗ - Answer: 110₂ = 6
 *
 * Operations calculation: - For each number, calculate minimum increments needed to have all required bits - Select the
 * m numbers with smallest costs (greedy choice)
 *
 * Time Complexity: O(31 * n log n) = O(n log n) - for each bit, sort operations array Space Complexity: O(n) for
 * operations array
 */
public class MaximumBitwiseAndAfterIncrementOperations {

    /**
     * Finds maximum bitwise AND after at most k increments, selecting m numbers.
     *
     * @param nums Array of integers
     * @param k Maximum number of increment operations
     * @param m Number of elements to select for AND operation
     * @return Maximum achievable bitwise AND value
     */
    public int maximumAND(int[] nums, int k, int m) {

        var ans = 0;  // Current answer being built bit by bit
        var n = nums.length;

        // ================================================================
        // Greedy bit-by-bit construction from MSB to LSB
        // ================================================================
        // Process each bit from most significant (30) to least significant (0)
        for (int i = 30; i >= 0; i--) {
            // Try to set bit i to 1 in the answer
            var expected = ans | (1 << i);

            // ============================================================
            // Calculate cost to make each number satisfy 'expected' bits
            // ============================================================
            int ops[] = new int[n];

            for (int j = 0; j < n; j++) {
                // How many increments needed to make nums[j] have all bits in 'expected'?
                ops[j] = getOps(nums[j], expected);
            }

            // ============================================================
            // Greedy selection: choose m numbers with smallest costs
            // ============================================================
            Arrays.sort(ops);

            long totalOps = 0;
            // Select the m cheapest numbers to increment
            for (int j = 0; j < m && totalOps <= k; j++) {
                totalOps += ops[j];
            }

            // ============================================================
            // If total cost is within budget, set this bit in answer
            // ============================================================
            if (totalOps <= k) {
                ans = expected;  // Can afford to set bit i to 1
            }
            // Otherwise, bit i remains 0 in answer (don't update ans)
        }

        return ans;
    }

    /**
     * Calculates minimum increments needed to make 'val' satisfy all bits in 'expected'.
     *
     * Strategy: Find the smallest number ≥ val that has all bits from 'expected'
     *
     * Cases: 1. val already has all expected bits: 0 operations needed 2. val is missing some expected bits: increment
     * to the smallest number with those bits
     *
     * Algorithm: - Scan bits from MSB to LSB - Find the first position where expected has 1 but val has 0 - At this
     * position, set the bit to 1 and copy all lower bits from expected - This gives the smallest number > val with all
     * expected bits
     *
     * Example 1: val = 5 (101), expected = 7 (111) - val & expected = 101 & 111 = 101 ≠ 111, so val doesn't have all
     * bits - Scan from bit 2 to 0: * Bit 2: expected=1, val=1 → match, copy to num * Bit 1: expected=1, val=0 →
     * mismatch! - Set bit 1: num = 010 - Copy lower bits from expected: num |= (111 & 001) = 010 | 001 = 011 - num =
     * 011 (binary) = 3... wait, this is wrong!
     *
     * Actually, the algorithm builds the target number: - Copy matching bits from val - When first mismatch found
     * (expected=1, val=0): * Set that bit to 1 * Copy all lower bits from expected - Result is smallest number ≥ val
     * with all expected bits
     *
     * Example 1 corrected: val = 5 (101), expected = 7 (111) - Bit 2: both 1 → num |= 100 → num = 100 - Bit 1:
     * expected=1, val=0 → MISMATCH * num |= (1 << 1) → num = 110 * num |= (111 & 001) → num = 111 - Result: num = 7,
     * operations = 7 - 5 = 2 ✓
     *
     * Example 2: val = 3 (011), expected = 6 (110) - Bit 2: expected=1, val=0 → MISMATCH (first one) * num |= (1 << 2)
     * → num = 100 * num |= (110 & 011) → num = 100 | 010 = 110 - Result: num = 6, operations = 6 - 3 = 3 ✓
     *
     * Example 3: val = 7 (111), expected = 5 (101) - val & expected = 101 = expected → already satisfies! Return 0 ✓
     *
     * @param val Original value
     * @param expected Bitmask of required bits
     * @return Minimum increment operations needed
     */
    private int getOps(int val, int expected) {

        // Case 1: val already has all bits from expected
        if ((val & expected) == expected) {
            return 0;  // No operations needed
        }

        // Case 2: Need to increment val to have all expected bits
        var num = 0;  // Build the target number

        // Scan bits from MSB to LSB
        for (int j = 30; j >= 0; j--) {
            var b1 = (expected >> j) & 1;  // Bit j in expected
            var b2 = (val >> j) & 1;       // Bit j in val

            // Found first position where expected=1 but val=0
            if (b1 == 1 && b2 == 0) {
                // Set this bit to 1
                num |= (1 << j);

                // Copy all lower bits from expected
                // (1 << j) - 1 creates a mask of all 1s below bit j
                // Example: j=2 → (1<<2)-1 = 011
                num |= (expected & ((1 << j) - 1));

                break;  // Found the target number, done
            }

            // If val has bit j set, copy it to num
            if (b2 == 1) {
                num |= 1 << j;
            }
        }

        // Return the number of increments needed
        return num - val;
    }
}