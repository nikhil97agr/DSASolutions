package Leetcode;//Problem Link: https://leetcode.com/problems/find-the-lexicographically-largest-string-from-the-box-ii/

/**
 * Solution for finding the lexicographically largest string after splitting into parts.
 *
 * Problem: Given a string 'word' and numFriends, split the string into numFriends non-empty parts. Each friend gets one
 * part. You can choose to keep one part for yourself. Find the lexicographically largest part you can obtain.
 *
 * Constraint: All parts must be non-empty (each has at least 1 character).
 *
 * Key observations:
 *
 * 1. Maximum length constraint: - Since we need numFriends parts (all non-empty), minimum total chars = numFriends - If
 * we take k characters, others get (numFriends - 1) parts with at least 1 char each - Maximum we can take: n -
 * (numFriends - 1)
 *
 * 2. Optimal strategy: - We want the lexicographically largest substring of length ≤ maxLen - Don't need to consider
 * all substrings, just find the one starting with the largest char - Among substrings starting with the same char, pick
 * the lexicographically largest
 *
 * 3. Algorithm: Modified substring comparison (similar to Lyndon factorization) - Maintain two candidate positions:
 * 'start' and 'end' - Compare substrings starting at these positions character by character - Keep the
 * lexicographically larger one
 *
 * Example: word = "dbca", numFriends = 2 - maxLen = 4 - (2 - 1) = 3 - Possible substrings: "d", "db", "dbc", "b", "bc",
 * "bca", "c", "ca", "a" - Answer: "dbc" (starts with 'd', largest first character)
 *
 * Example 2: word = "gggg", numFriends = 2 - maxLen = 3 - All substrings start with 'g', so pick longest: "ggg"
 *
 * Comparison algorithm: - start: current best candidate position - end: next candidate position to compare - length:
 * how many characters match so far - Compare word[start+length] with word[end+length]: * If equal: extend comparison
 * (length++) * If word[start+length] > word[end+length]: start is better, skip end * If word[start+length] <
 * word[end+length]: end is better, update start
 *
 * Time Complexity: O(n) - each character is visited at most twice Space Complexity: O(1) - only a few variables used
 */
public class FindTheLexicographicallyLargestStringFromTheBoxII {

    /**
     * Finds the lexicographically largest substring that can be obtained.
     *
     * @param word Input string to split
     * @param numFriends Number of parts to split into
     * @return Lexicographically largest part
     */
    public String answerString(String word, int numFriends) {

        // ================================================================
        // Special case: Only one friend (no splitting needed)
        // ================================================================
        if (numFriends == 1) {
            return word;  // Take the entire string
        }

        var n = word.length();

        // ================================================================
        // Calculate maximum length we can take
        // ================================================================
        // We need to leave at least (numFriends - 1) characters for others
        // Each of the other (numFriends - 1) parts must have at least 1 character
        int maxLen = n - (numFriends - 1);

        // ================================================================
        // Find the lexicographically largest substring using comparison
        // ================================================================
        int start = 0;    // Current best candidate starting position
        int end = 1;      // Next candidate position to compare
        int length = 0;   // Number of matching characters in current comparison

        // Compare all possible starting positions
        while (end + length < n) {
            // Compare characters at position 'length' in both substrings
            int diff = word.charAt(start + length) - word.charAt(end + length);

            if (diff == 0) {
                // ========================================================
                // Characters match: extend the comparison
                // ========================================================
                length++;
            } else if (diff > 0) {
                // ========================================================
                // word[start+length] > word[end+length]
                // ========================================================
                // Substring at 'start' is lexicographically larger
                // Skip 'end' and all positions up to end+length
                // Why? Any substring starting in [end, end+length] would also lose
                // because they share the same prefix that already lost
                end += length + 1;
                length = 0;  // Reset comparison length
            } else {
                // ========================================================
                // word[start+length] < word[end+length]
                // ========================================================
                // Substring at 'end' is lexicographically larger
                // Update 'start' to the better position
                // But start must be at least 'end' (can't go backwards)
                start = Math.max(start + length + 1, end);
                end = start + 1;  // Next position to compare
                length = 0;       // Reset comparison length
            }
        }

        // ================================================================
        // Extract the best substring with length constraint
        // ================================================================
        // Take substring from 'start' with maximum allowed length
        return word.substring(start, Math.min(n, start + maxLen));

    }
}