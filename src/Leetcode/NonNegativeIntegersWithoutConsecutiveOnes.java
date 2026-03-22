package Leetcode;// Problem Link: https://leetcode.com/problems/non-negative-integers-without-consecutive-ones/

/**
 * Solution for Non-negative Integers without Consecutive Ones
 *
 * Problem: Count how many integers in the range [0, n] have no consecutive 1s in their binary representation.
 *
 * Examples: - n = 5 (binary: 101) Valid numbers: 0(000), 1(001), 2(010), 4(100), 5(101) = 5 numbers Invalid: 3(011) has
 * consecutive 1s
 *
 * - n = 10 (binary: 1010) Valid: 0, 1, 2, 4, 5, 8, 9, 10 = 8 numbers Invalid: 3(011), 6(110), 7(111) have consecutive
 * 1s
 *
 * Approach: Digit DP on Binary Representation - Build numbers bit by bit from left to right - Track whether we're still
 * bounded by n (tight flag) - Track the previous bit to avoid consecutive 1s - Use memoization to avoid recomputation
 *
 * State: dp[position][tight_flag][previous_bit] - position: current bit position (0 to length-1) - tight_flag: 1 if we
 * must stay <= n, 0 if we can place any bit - previous_bit: the bit we placed at position-1 (0 or 1)
 */
public class NonNegativeIntegersWithoutConsecutiveOnes {

    // DP memoization table: dp[position][tight_flag][previous_bit]
    Integer[][][] dp;

    /**
     * Counts integers from 0 to n without consecutive 1s in binary
     *
     * @param n The upper bound (inclusive)
     * @return Count of valid integers
     */
    public int findIntegers(int n) {

        // Convert n to binary string for digit DP
        char[] ch = Integer.toBinaryString(n).toCharArray();

        // Initialize DP table
        // Dimensions: [bit_position][tight_flag: 0 or 1][previous_bit: 0 or 1]
        dp = new Integer[ch.length][2][2];

        // Start with tight flag = 1 (bounded by n), position = 0, previous bit = 0
        return solve(ch, 1, 0, ch.length, 0);
    }

    /**
     * Recursive function to count valid numbers using digit DP
     *
     * @param ch Binary representation of n as character array
     * @param flag Tight bound flag: 1 = must stay <= n (tight bound) 0 = can place any bit (no bound constraint)
     * @param i Current bit position (0-indexed from left)
     * @param n Total number of bits
     * @param prev Previous bit placed (0 or 1)
     * @return Count of valid numbers from this state
     */
    private int solve(char ch[], int flag, int i, int n, int prev) {

        // Base case: reached the end, found a valid number
        if (i == n) {
            return 1;
        }

        // Return memoized result if already computed
        if (dp[i][flag][prev] != null) {
            return dp[i][flag][prev];
        }

        int ans;

        // Case 1: Tight bound (flag == 1) - must respect the bound n
        if (flag == 1) {
            // If current bit in n is '0', we can only place '0'
            if (ch[i] == '0') {
                ans = solve(ch, 1, i + 1, n, 0);
            }
            // If current bit in n is '1', we have two options
            else {
                // Option 1: Place '0' (now we're strictly less than n, so flag becomes 0)
                ans = solve(ch, 0, i + 1, n, 0);

                // Option 2: Place '1' (only if previous bit was not 1, to avoid consecutive 1s)
                if (prev != 1) {
                    ans = ans + solve(ch, 1, i + 1, n, 1);
                }
                // If prev == 1, we can't place another 1, so we skip this option
            }
        }
        // Case 2: No tight bound (flag == 0) - can place any valid bit
        else {
            // If previous bit was 1, we can only place 0 (to avoid consecutive 1s)
            if (prev == 1) {
                ans = solve(ch, flag, i + 1, n, 0);
            }
            // If previous bit was 0, we can place either 0 or 1
            else {
                ans = solve(ch, flag, i + 1, n, 1) + solve(ch, flag, i + 1, n, 0);
            }
        }

        // Memoize and return the result
        return dp[i][flag][prev] = ans;
    }
}