package Leetcode;//Problem Link: https://leetcode.com/problems/minimum-operations-to-make-a-rotated-palindrome-ii/description/


/**
 * Problem: Minimum Operations to Make String Palindrome
 *
 * Given a string of lowercase English letters, find the minimum number of operations needed to make it a palindrome
 * using: 1. Increment operation: Replace s[i] with next letter ('a' follows 'z') 2. Left rotate operation: Move first
 * character to end
 *
 * Strategy: - Try all possible rotation positions (0 to n-1 rotations) - For each rotation, calculate the cost to make
 * it a palindrome using increments - The total cost = number of rotations + increment cost for that configuration -
 * Return the minimum total cost across all rotations
 */
public class MinimumOperationsToMakeARotatedPalindromeII {

    public int minOperations(String s) {

        // Store the length of the string
        int n = s.length();
        // Convert string to char array for easier indexing
        char[] ch = s.toCharArray();

        /**
         * Precompute the minimum distance between any two characters.
         * dist[i][j] = minimum operations to transform character i to character j
         *
         * Since we can increment cyclically ('z' -> 'a'), we need to consider:
         * 1. Direct distance: |i - j| (forward increments)
         * 2. Wraparound distance: 26 - |i - j| (backward through 'z' to 'a')
         *
         * Example: 'a' to 'z':
         * - Direct: 25 increments (a->b->c->...->z)
         * - Wraparound: 1 increment (a wraps to z)
         * - We choose min(25, 1) = 1
         */
        int[][] dist = new int[26][26];
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 26; j++) {
                int d = Math.abs(i - j);
                // Choose the shorter path: direct or wraparound
                dist[i][j] = Math.min(d, 26 - d);
            }
        }

        // Initialize answer to a large value
        int ans = Integer.MAX_VALUE;

        /**
         * Case 0: No rotation (k = 0)
         * Calculate the cost to make the original string a palindrome using only increments.
         *
         * For a palindrome, characters at position i and (n-1-i) must match.
         * We only need to check the first half of the string.
         *
         * Example: "abc" -> compare 'a' with 'c', 'b' is the middle
         * Cost = dist['a']['c'] = min(2, 24) = 2
         */
        int currentIncrementCost = 0;
        for (int j = 0; j < n / 2; j++) {
            // Add the cost to make mirrored positions match
            currentIncrementCost += dist[ch[j] - 'a'][ch[n - 1 - j] - 'a'];
        }

        // Update the answer with the cost for no rotation
        ans = Math.min(ans, currentIncrementCost);

        /**
         * Try all possible rotation amounts (k = 1 to n-1)
         *
         * For each rotation:
         * - Left rotate the string k times (costs k operations)
         * - Calculate increment cost to make the rotated string a palindrome
         * - Total cost = k (rotations) + increment cost
         *
         * Example: "abc" with k=1 rotation becomes "bca"
         * - Original indices: [0,1,2] = ['a','b','c']
         * - After 1 rotation: ['b','c','a']
         * - New index mapping: position 0 has ch[1], position 1 has ch[2], position 2 has ch[0]
         */
        for (int k = 1; k < n; k++) {
            /**
             * Optimization: Early exit pruning
             * If k rotations alone >= current best answer, no point checking further
             * since we still need to add increment costs
             */
            if (k >= ans) {
                break;
            }

            // Total cost starts with k (the number of rotations performed)
            int shiftCost = k;

            /**
             * After k left rotations, the new string starts at index k
             * left pointer: tracks the start of rotated string (moves forward)
             * right pointer: tracks the end of rotated string (moves backward)
             *
             * Example: "abcde" with k=2 becomes "cdeab"
             * - left starts at index 2 (character 'c')
             * - right starts at index (2+5-1)%5 = 1 (character 'b')
             */
            int left = k;
            int right = (k + n - 1) % n;

            /**
             * Check each pair of mirrored positions in the rotated string
             * For a palindrome of length n, we need to check n/2 pairs
             */
            for (int j = 0; j < n / 2; j++) {
                // Add the cost to make the current pair of characters match
                shiftCost += dist[ch[left] - 'a'][ch[right] - 'a'];

                /**
                 * Optimization: Early termination for current rotation
                 * If current rotation's cost already exceeds best answer,
                 * no need to check remaining pairs
                 */
                if (shiftCost >= ans) {
                    break;
                }

                /**
                 * Move pointers for next pair:
                 * - left moves forward (with wraparound at end)
                 * - right moves backward (with wraparound at start)
                 *
                 * Example: n=5, left=2, right=1
                 * - Next: left=3, right=0
                 * - Next: left=4, right=4
                 */
                left = (left + 1 == n) ? 0 : left + 1;
                right = (right == 0) ? n - 1 : right - 1;
            }

            // Update answer if this rotation gives a better result
            ans = Math.min(ans, shiftCost);
        }

        /**
         * Return the minimum cost across all rotation possibilities
         *
         * Time Complexity: O(n²) - for each of n rotations, we check n/2 pairs
         * Space Complexity: O(1) - only using constant extra space (dist array is fixed 26x26)
         */
        return ans;
    }
}