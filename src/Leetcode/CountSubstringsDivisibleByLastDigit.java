package Leetcode;

//Problem Link: https://leetcode.com/problems/count-substrings-divisible-by-last-digit/

/**
 * Solution for counting substrings where the numeric value is divisible by its last digit.
 *
 * Problem: Given a numeric string s, count how many substrings represent numbers that are divisible by their last
 * digit.
 *
 * Examples: - "124": Substrings "12" (12 % 2 = 0 ✓), "4" (4 % 4 = 0 ✓), "24" (24 % 4 = 0 ✓) - "35": Substring "35" (35
 * % 5 = 0 ✓) - Single digits are always divisible by themselves
 *
 * Key insight: Dynamic Programming with modulo tracking - For each position i, we need to know all possible substrings
 * ending at i - Track the remainder when divided by each possible last digit (1-9) - A substring is valid if:
 * (substring_value % last_digit) == 0
 *
 * State representation: - dp[d][r] = count of substrings ending at current position with: * Last digit = d * Remainder
 * when divided by d = r
 *
 * Transition: - When adding digit 'x' at position i: 1. Create new single-digit substring "x" → remainder = x % x = 0
 * (always valid) 2. Extend all previous substrings ending at i-1 by appending x - Old substring with value V → new
 * value = V * 10 + x - New remainder = (old_remainder * 10 + x) % last_digit
 *
 * Example walkthrough: s = "124" - Position 0 ('1'): - Substring "1": 1 % 1 = 0 ✓ → count = 1
 *
 * - Position 1 ('2'): - Substring "2": 2 % 2 = 0 ✓ → count = 2 - Substring "12" ending with '2': 12 % 2 = 0 ✓ → count =
 * 3
 *
 * - Position 2 ('4'): - Substring "4": 4 % 4 = 0 ✓ → count = 4 - Substring "24" ending with '4': 24 % 4 = 0 ✓ → count =
 * 5 - Substring "124" ending with '4': 124 % 4 = 0 ✓ → count = 6
 *
 * Time Complexity: O(n * 45) = O(n) where n = string length For each digit, we process at most 1+2+3+...+9 = 45 states
 * Space Complexity: O(1) - dp array has constant size 10×10
 */
public class CountSubstringsDivisibleByLastDigit {

    /**
     * Counts substrings divisible by their last digit.
     *
     * @param s Numeric string (digits 0-9)
     * @return Count of valid substrings
     */
    public long countSubstrings(String s) {

        // dp[last_digit][remainder] = count of substrings ending at current position
        // with given last digit and remainder when divided by that last digit
        // We only care about last_digit 1-9 (division by 0 is undefined)
        var dp = new long[10][10];

        var ans = 0l;  // Total count of valid substrings

        // ================================================================
        // Process each digit from left to right
        // ================================================================
        for (var d : s.toCharArray()) {
            var digit = d - '0';

            // ============================================================
            // Update DP for all possible last digits (1-9)
            // ============================================================
            // For each possible last digit that a substring might have
            for (var mod = 1; mod <= 9; mod++) {
                // cnt[r] = new count of substrings ending at current position
                // with last digit = mod and remainder = r
                var cnt = new long[mod];

                // Case 1: Start a new substring with just the current digit
                // If current digit is 'digit' and we're tracking mod 'mod':
                // This substring has remainder = digit % mod
                cnt[digit % mod]++;

                // Case 2: Extend all previous substrings with last digit = mod
                // Iterate through all possible remainders from previous position
                for (var reminder = 0; reminder < mod; reminder++) {
                    // Old substring had value V with V % mod = reminder
                    // New substring = V * 10 + digit
                    // New remainder = (V * 10 + digit) % mod
                    //               = (reminder * 10 + digit) % mod
                    var newMod = (reminder * 10 + digit) % mod;

                    // Add count of substrings with old remainder to new remainder
                    cnt[newMod] += dp[mod][reminder];

                }

                // Update dp for this last digit
                dp[mod] = cnt;

            }

            // ============================================================
            // Add valid substrings ending at current position
            // ============================================================
            // Current digit is 'digit', so we check dp[digit][0]
            // This gives count of substrings ending here with:
            // - Last digit = digit (current position)
            // - Remainder = 0 (divisible by digit)
            ans += dp[digit][0];
        }

        return ans;


    }
}