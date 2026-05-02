package Leetcode;//Problem Link: https://leetcode.com/problems/smallest-substring-with-identical-characters-ii

/**
 * Solution for minimizing the longest substring of identical characters.
 *
 * Problem: Given a binary string s (containing only '0' and '1') and integer ops, you can perform at most ops
 * operations where each operation flips a character ('0' → '1' or '1' → '0'). Find the minimum possible length of the
 * longest substring consisting of identical characters.
 *
 * Key insight: Binary search on the answer - The answer is in range [1, n] - For a given max length L, we can check if
 * it's achievable with ops operations - Use binary search to find the minimum achievable length
 *
 * Special case: Length 1 - To achieve length 1, the string must alternate: "010101..." or "101010..." - We need to
 * check if we can transform s into either pattern with ops flips
 *
 * General case: Length L (L >= 2) - For each consecutive run of identical characters longer than L, we need to break it
 * up by flipping characters - Strategy: Insert opposite characters at intervals of (L+1) - Operations needed for a run
 * of length C: C / (L+1)
 *
 * Example: s = "0000111", ops = 2 - Check length 1: Would need alternating pattern, requires too many ops - Check
 * length 2: Run "0000" (length 4) needs 4/(2+1) = 1 flip Run "111" (length 3) needs 3/(2+1) = 1 flip Total = 2 flips ✓
 * Achievable! - Answer: 2
 *
 * Time Complexity: O(n log n) - binary search × O(n) check Space Complexity: O(n) for character array
 */
public class SmallestSubstringWithIdenticalCharactersII {

    /**
     * Finds the minimum possible length of the longest identical substring.
     *
     * @param s Binary string (contains only '0' and '1')
     * @param ops Maximum number of flip operations allowed
     * @return Minimum achievable length of longest identical substring
     */
    public int minLength(String s, int ops) {

        int n = s.length();
        char ch[] = s.toCharArray();

        // ================================================================
        // SPECIAL CASE: Check if we can achieve length 1 (alternating pattern)
        // ================================================================
        // Try both patterns: "010101..." and "101010..."
        // If either is achievable with ops flips, answer is 1
        boolean ans = len1(ch, n, ops, '1') || len1(ch, n, ops, '0');
        if (ans) {
            return 1;  // Best possible answer
        }

        // ================================================================
        // GENERAL CASE: Binary search for minimum length >= 2
        // ================================================================
        int start = 2;    // Minimum length to search (we know 1 is not achievable)
        int end = n;      // Maximum possible length

        // Binary search for the minimum achievable length
        while (start < end) {
            int mid = (start + end) / 2;

            // Check if we can achieve max substring length of 'mid' with ops flips
            if (check(ch, n, mid, ops)) {
                end = mid;        // mid is achievable, try smaller
            } else {
                start = mid + 1;  // mid is not achievable, need larger length
            }
        }

        return end;
    }

    /**
     * Checks if we can achieve a maximum identical substring length of 'len' with ops flips.
     *
     * Strategy: - Scan through the string and identify consecutive runs of identical characters - For each run longer
     * than 'len', calculate how many flips are needed to break it - Formula: flips needed for run of length C = C /
     * (len + 1)
     *
     * Why C / (len + 1)? - To break a run of C identical chars into chunks of max length 'len', we insert opposite
     * characters at intervals - Example: "00000" (C=5) with len=2 → "00100" (1 flip at position 2) We need 5/(2+1) = 1
     * flip - Example: "0000000" (C=7) with len=2 → "0010010" (2 flips) We need 7/(2+1) = 2 flips
     *
     * @param ch Character array
     * @param n Length of array
     * @param len Target maximum length of identical substrings
     * @param ops Available operations
     * @return true if achievable with ops flips, false otherwise
     */
    private boolean check(char ch[], int n, int len, int ops) {

        int cnt = 0;  // Count of current consecutive identical characters

        // Scan through string, tracking runs of identical characters
        for (int i = 0, prev = -1; i < n; i++) {
            if (ch[i] == prev) {
                // Continue the current run
                cnt++;
            } else {
                // End of current run, start of new run

                // Calculate flips needed for the completed run
                // If cnt > len, we need to break it up
                ops -= cnt / (len + 1);

                // Start new run
                cnt = 1;
                prev = ch[i];
            }
        }

        // Handle the last run (if it exceeds len)
        if (cnt > len) {
            ops -= cnt / (len + 1);
        }

        // Check if we have enough operations remaining
        return ops >= 0;
    }


    /**
     * Checks if we can achieve length 1 (alternating pattern) starting with a given character.
     *
     * To achieve maximum identical substring length of 1, the string must alternate: - Pattern starting with '0':
     * "0101010..." - Pattern starting with '1': "1010101..."
     *
     * Strategy: - Generate the target alternating pattern starting with 'curr' - Count how many characters differ from
     * the target pattern - These differences need to be flipped
     *
     * Example: s = "00110", ops = 2 - Pattern starting with '0': "01010" Differences at positions: 1, 2 → 2 flips
     * needed ✓ (ops >= 2) - Pattern starting with '1': "10101" Differences at positions: 0, 2, 3 → 3 flips needed ✗
     * (ops < 3)
     *
     * @param ch Character array
     * @param n Length of array
     * @param ops Available operations
     * @param curr Starting character for the alternating pattern ('0' or '1')
     * @return true if alternating pattern is achievable with ops flips, false otherwise
     */
    private boolean len1(char ch[], int n, int ops, char curr) {

        // Compare each position with the expected alternating pattern
        for (int i = 0; i < n; i++) {
            // If current character doesn't match expected pattern, we need a flip
            if (ch[i] == curr) {
                ops--;  // One flip needed (ch[i] should be opposite of curr)
            }

            // Alternate the expected character for next position
            curr = (curr == '1' ? '0' : '1');
        }

        // Check if we have enough operations
        return ops >= 0;
    }

}