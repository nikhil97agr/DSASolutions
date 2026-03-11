package Leetcode;
// Problem Link: https://leetcode.com/problems/count-stepping-numbers-in-range

/**
 * Solution for Count Stepping Numbers in Range
 *
 * A stepping number is a number where each adjacent digit differs by exactly 1. Examples of stepping numbers: 0, 1, 2,
 * ..., 9, 10, 12, 21, 23, 32, 34, 43, 45, 54, 56, 65, 67, 76, 78, 87, 89, 98 Examples of non-stepping numbers: 11
 * (1-1=0), 13 (3-1=2), 100 (0-0=0)
 *
 * Problem: Count stepping numbers in the range [low, high]
 *
 * Approach: Digit DP (Dynamic Programming on Digits) - Use digit DP to count stepping numbers <= high - Use digit DP to
 * count stepping numbers < low - Answer = count(high) - count(low) + (1 if low is stepping number)
 *
 * Key Insight: Build numbers digit by digit, ensuring each new digit differs by 1 from previous
 */
public class CountSteppingNumbersInRange {

    // Modulo for large number handling
    long mod = (long) 1e9 + 7;

    /**
     * Counts stepping numbers in the range [low, high]
     *
     * @param low Lower bound of the range (inclusive)
     * @param high Upper bound of the range (inclusive)
     * @return Count of stepping numbers in [low, high] modulo 10^9+7
     */
    public int countSteppingNumbers(String low, String high) {

        // Count stepping numbers <= low
        int a = check(low.toCharArray());

        // Count stepping numbers <= high
        int b = check(high.toCharArray());

        // Answer = count(high) - count(low)
        int ans = add(b, -a);

        // Check if 'low' itself is a stepping number
        // If yes, we need to add 1 because we excluded it in the subtraction
        boolean flag = true;
        for (int i = 1; i < low.length(); i++) {
            if (Math.abs(low.charAt(i) - low.charAt(i - 1)) != 1) {
                flag = false;
                break;
            }
        }
        if (flag) {
            ans = add(ans, 1);
        }

        return ans;
    }

    /**
     * Counts stepping numbers less than or equal to the number represented by ch[]
     *
     * Strategy: 1. Count numbers with fewer digits (all are valid if they're stepping numbers) 2. Count numbers with
     * same number of digits but less than ch[]
     *
     * @param ch Character array representing the upper bound number
     * @return Count of stepping numbers <= ch[]
     */
    private int check(char ch[]) {

        int n = ch.length;
        int ans = 0;

        // Case 1: Numbers with same length, starting with digits less than ch[0]
        // For each starting digit < ch[0], count all valid stepping numbers
        for (int dig = 1; dig < ch[0] - '0'; dig++) {
            ans = add(ans, solve(1, dig, ch, 0, n));
        }

        // Case 2: Numbers with same length, starting with ch[0]
        // Need to be careful not to exceed ch[], so use flag=1 (tight bound)
        ans = add(ans, solve(1, ch[0] - '0', ch, 1, n));

        // Case 3: Numbers with fewer digits
        // For each length < n, count all stepping numbers starting with 1-9
        for (int i = 1; i < n; i++) {
            for (int dig = 1; dig <= 9; dig++) {
                ans = add(ans, solve(i + 1, dig, ch, 0, n));
            }
        }

        return ans;
    }

    /**
     * Recursive function to count stepping numbers using digit DP
     *
     * @param i Current position (index) in the number being built
     * @param prev Previous digit placed (to ensure stepping property)
     * @param ch The upper bound number as character array
     * @param flag Tight bound flag: 0 = we can place any valid digit (no upper bound constraint) 1 = we must stay <=
     * ch[i] (tight bound constraint)
     * @param n Total length of the number
     * @return Count of valid stepping numbers from this state
     */
    private int solve(int i, int prev, char ch[], int flag, int n) {

        // Base case: reached the end of number, found a valid stepping number
        if (i == n) {
            return 1;
        }

        // Case 1: No tight bound (flag == 0)
        // We can freely choose next digit as long as it differs by 1 from prev
        if (flag == 0) {
            int ans = 0;

            // Try placing prev+1 (if valid)
            if (prev < 9) {
                ans = add(ans, solve(i + 1, prev + 1, ch, 0, n));
            }

            // Try placing prev-1 (if valid)
            if (prev > 0) {
                ans = add(ans, solve(i + 1, prev - 1, ch, 0, n));
            }

            return ans;
        }
        // Case 2: Tight bound (flag == 1)
        // We must ensure the number doesn't exceed ch[]
        else {
            int ans = 0;

            // Try placing prev+1
            if (prev < 9) {
                // If prev+1 equals ch[i], maintain tight bound
                if (prev + 1 == ch[i] - '0') {
                    ans = add(ans, solve(i + 1, prev + 1, ch, 1, n));
                }

                // If prev+1 < ch[i], we're now strictly less, so no tight bound
                if (prev + 1 < ch[i] - '0') {
                    ans = add(ans, solve(i + 1, prev + 1, ch, 0, n));
                }
                // If prev+1 > ch[i], this path is invalid (would exceed bound)
            }

            // Try placing prev-1
            if (prev > 0) {
                // If prev-1 equals ch[i], maintain tight bound
                if (prev - 1 == ch[i] - '0') {
                    ans = add(ans, solve(i + 1, prev - 1, ch, 1, n));
                }

                // If prev-1 < ch[i], we're now strictly less, so no tight bound
                if (prev - 1 < ch[i] - '0') {
                    ans = add(ans, solve(i + 1, prev - 1, ch, 0, n));
                }
                // If prev-1 > ch[i], this path is invalid (would exceed bound)
            }

            return ans;
        }

    }

    /**
     * Adds two numbers with modulo arithmetic Handles negative numbers by adding mod before taking modulo
     *
     * @param a First number
     * @param b Second number (can be negative)
     * @return (a + b) % mod, always non-negative
     */
    private int add(long a, long b) {

        long sum = a + b + mod;
        return (int) (sum % mod);
    }

}