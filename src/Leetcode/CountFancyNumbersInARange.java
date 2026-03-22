package Leetcode;

import java.util.HashSet;
import java.util.Set;

// Problem Link: https://leetcode.com/contest/biweekly-contest-178/problems/count-fancy-numbers-in-a-range

/**
 * Solution for Count Fancy Numbers in a Range
 *
 * Problem: Count "fancy" numbers in the range [l, r].
 * A number is "fancy" if it satisfies at least one of these conditions:
 * 1. The digits are strictly increasing OR strictly decreasing
 * 2. The sum of digits is a "good" number (strictly increasing or decreasing digits)
 *
 * Examples:
 * - 123: Fancy (digits strictly increasing)
 * - 321: Fancy (digits strictly decreasing)
 * - 111: NOT fancy (digits neither increasing nor decreasing, sum=3 is not good)
 * - 145: Fancy (digits strictly increasing)
 * - 246: Fancy (sum = 12, which has digits "12" that are strictly increasing)
 * - 999: Fancy (sum = 27, which has digits "27" that are strictly increasing)
 *
 * Approach: Digit DP
 * - State: (index, tight_flag, prev_digit, is_inc, is_dec, sum, has_started)
 *   - index: current position in the number
 *   - tight_flag: whether we're still bounded by the upper limit
 *   - prev_digit: previous digit placed (to check increasing/decreasing)
 *   - is_inc: whether digits so far are non-decreasing (1 if yes, 0 if no)
 *   - is_dec: whether digits so far are non-increasing (1 if yes, 0 if no)
 *   - sum: sum of digits so far
 *   - has_started: whether we've placed a non-zero digit (to handle leading zeros)
 *
 * Key Insight: A number is fancy if:
 * - (is_inc == 0 OR is_dec == 0) → strictly increasing or strictly decreasing
 * - OR sum is a "good" number
 *
 * Time Complexity: O(n × 2 × 11 × 2 × 2 × 150 × 2) = O(n × 7920) where n is number of digits
 * Space Complexity: O(n × 7920) for memoization
 */
public class CountFancyNumbersInARange {

    Set<Integer> set;  // Set of "good" sums (sums with strictly increasing/decreasing digits)

    /**
     * Counts fancy numbers in the range [l, r]
     *
     * Uses the standard digit DP technique: count(r) - count(l-1)
     *
     * @param l Left boundary of the range
     * @param r Right boundary of the range
     * @return Number of fancy numbers in [l, r]
     */
    public long countFancy(long l, long r) {

        // Precompute all "good" sums (0 to 150, since max sum for 16 digits is 9*16=144)
        set = new HashSet<>();
        for (int i = 0; i <= 150; i++) {
            if (good(i)) {
                set.add(i);
            }
        }

        // Convert l-1 and r to character arrays for digit DP
        char c1[] = Long.toString(l - 1).toCharArray();
        char c2[] = Long.toString(r).toCharArray();

        // Count fancy numbers in [0, l-1]
        long a = solve(c1, 0, c1.length, 1, 0, 0, 0, 0, 0, new Long[c1.length][2][11][2][2][150][2]);

        // Count fancy numbers in [0, r]
        long b = solve(c2, 0, c2.length, 1, 0, 0, 0, 0, 0, new Long[c2.length][2][11][2][2][150][2]);

        // Return count in [l, r] = count[0, r] - count[0, l-1]
        return b - a;
    }

    /**
     * Digit DP recursive function to count fancy numbers
     *
     * @param ch Character array representing the upper bound number
     * @param i Current index (position in the number)
     * @param n Total number of digits
     * @param flag Tight bound flag (1 if still bounded by ch, 0 if not)
     * @param started Whether we've placed a non-zero digit (0 = no, 1 = yes)
     * @param prev Previous digit placed (0-9, or 0 if not started)
     * @param inc Whether digits are non-decreasing so far (0 = strictly increasing, 1 = not)
     * @param dec Whether digits are non-increasing so far (0 = strictly decreasing, 1 = not)
     * @param sum Sum of digits placed so far
     * @param dp Memoization table [index][flag][prev][inc][dec][sum][started]
     * @return Count of fancy numbers
     */
    private long solve(char ch[], int i, int n, int flag, int started, int prev,
            int inc, int dec, int sum,
            Long dp[][][][][][][]) {

        // Base case: processed all digits
        if (i == n) {

            // If we never started (number is 0), it's not fancy
            if (started == 0) {
                return 0;
            }

            // Check if the number is fancy:
            // 1. Digits are strictly increasing (inc == 0) OR strictly decreasing (dec == 0)
            // 2. OR the sum of digits is a "good" number
            boolean goodNumber = (inc == 0 || dec == 0);
            boolean goodSum = set.contains(sum);

            return (goodNumber || goodSum) ? 1 : 0;
        }

        // Memoization: return cached result if available
        if (dp[i][flag][prev][inc][dec][sum][started] != null) {
            return dp[i][flag][prev][inc][dec][sum][started];
        }

        // Determine the maximum digit we can place at this position
        // If tight flag is 1, we're bounded by ch[i]; otherwise, we can use any digit 0-9
        int limit = (flag == 1) ? ch[i] - '0' : 9;

        long ans = 0;

        // Try placing each digit from 0 to limit
        for (int d = 0; d <= limit; d++) {

            // Update tight flag: stays 1 only if we place the limit digit
            int nextFlag = (flag == 1 && d == limit) ? 1 : 0;

            // Case 1: Leading zero (haven't started and placing 0)
            if (started == 0 && d == 0) {

                // Don't update prev, inc, dec, or sum (still leading zeros)
                ans += solve(ch, i + 1, n, nextFlag, 0, 0, inc, dec, sum, dp);

            }
            // Case 2: Placing a significant digit
            else {

                // Sub-case 2a: First significant digit (starting the number)
                if (started == 0) {

                    // Start the number: set started=1, prev=d, add d to sum
                    // inc and dec remain 0 (no violations yet with single digit)
                    ans += solve(ch, i + 1, n, nextFlag, 1, d, inc, dec, sum + d, dp);

                }
                // Sub-case 2b: Continuing the number (already started)
                else {

                    int ninc = inc;  // New inc flag
                    int ndec = dec;  // New dec flag

                    // Check if placing d violates strictly increasing property
                    // If d >= prev, it's not strictly increasing (set ninc = 1)
                    if (d >= prev) {
                        ninc = 1;
                    }

                    // Check if placing d violates strictly decreasing property
                    // If d <= prev, it's not strictly decreasing (set ndec = 1)
                    if (d <= prev) {
                        ndec = 1;
                    }

                    // Recurse with updated state
                    ans += solve(ch, i + 1, n, nextFlag, 1, d, ninc, ndec, sum + d, dp);
                }
            }
        }

        // Memoize and return the result
        return dp[i][flag][prev][inc][dec][sum][started] = ans;
    }

    /**
     * Checks if a number is "good" (has strictly increasing or strictly decreasing digits)
     *
     * A number is "good" if its digits are either:
     * - Strictly increasing: each digit > previous digit (e.g., 123, 1489)
     * - Strictly decreasing: each digit < previous digit (e.g., 321, 9630)
     *
     * Examples:
     * - good(123) = true (strictly increasing)
     * - good(321) = true (strictly decreasing)
     * - good(111) = false (neither increasing nor decreasing)
     * - good(132) = false (neither increasing nor decreasing)
     * - good(12) = true (strictly increasing)
     * - good(5) = true (single digit is both increasing and decreasing)
     *
     * @param sum The number to check
     * @return true if the number is "good", false otherwise
     */
    private boolean good(int sum) {

        // Convert number to character array to check digits
        char ch[] = Integer.toString(sum).toCharArray();

        boolean inc = true;  // Assume strictly increasing until proven otherwise
        boolean dec = true;  // Assume strictly decreasing until proven otherwise

        // Check each consecutive pair of digits
        for (int i = 0; i < ch.length - 1; i++) {

            // If current digit >= next digit, it's not strictly increasing
            if (ch[i] >= ch[i + 1]) {
                inc = false;
            }

            // If current digit <= next digit, it's not strictly decreasing
            if (ch[i] <= ch[i + 1]) {
                dec = false;
            }
        }

        // Number is "good" if it's strictly increasing OR strictly decreasing
        return inc || dec;
    }
}