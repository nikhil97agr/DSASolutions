package Leetcode;

import java.util.Arrays;

// Problem Link: https://leetcode.com/problems/maximum-strong-pair-xor-ii/

/**
 * Solution for Maximum Strong Pair XOR II
 *
 * Problem: Given an array nums, find the maximum XOR of a "strong pair".
 * A pair (x, y) is "strong" if |x - y| <= min(x, y).
 *
 * Constraint Analysis:
 * - If x <= y, then |x - y| = y - x
 * - Strong pair condition: y - x <= x
 * - Simplifies to: y <= 2x
 *
 * Example:
 * nums = [1, 2, 3, 4, 5]
 * Strong pairs:
 * - (1, 2): |1-2| = 1 <= min(1,2) = 1 ✓, XOR = 3
 * - (2, 3): |2-3| = 1 <= min(2,3) = 2 ✓, XOR = 1
 * - (2, 4): |2-4| = 2 <= min(2,4) = 2 ✓, XOR = 6
 * - (3, 5): |3-5| = 2 <= min(3,5) = 3 ✓, XOR = 6
 * Maximum: 6
 *
 * Approach: Binary Trie + Sliding Window
 * 1. Sort the array
 * 2. For each element x, maintain a trie of all elements y where x <= y <= 2x
 * 3. Use sliding window: add elements <= 2x, remove elements < x
 * 4. Query trie for maximum XOR with x
 *
 * Key Insight: After sorting, for element x at position i:
 * - All valid pairs have y in range [x, 2x]
 * - Use two pointers to maintain this window
 * - Binary trie enables O(log max_value) XOR maximization
 *
 * Time Complexity: O(n log n) for sorting + O(n × 20) for trie operations = O(n log n)
 * Space Complexity: O(n × 20) for trie
 */
public class MaximumStrongPairXor {

    /**
     * Finds the maximum XOR of a strong pair
     *
     * @param nums The input array
     * @return Maximum XOR value among all strong pairs
     */
    public int maximumStrongPairXor(int[] nums) {

        var trie = new Trie();

        // Sort array to enable sliding window approach
        Arrays.sort(nums);

        int n = nums.length;
        var ans = 0;
        var j = 0;  // Right pointer of sliding window

        // For each element x at position i (left pointer)
        for (int i = 0; i < n; i++) {
            int x = nums[i];      // Current element (smaller element in pair)
            int y = 2 * x;        // Maximum valid partner (y <= 2x for strong pair)

            // Expand window: add all elements in range [x, 2x]
            // Since array is sorted, add elements while nums[j] <= 2x
            while (j < n && nums[j] <= y) {
                trie.add(nums[j], 20);  // Add to trie (20 bits for values up to ~1M)
                j++;
            }

            // Query trie for maximum XOR with x
            ans = Math.max(ans, trie.query(x, 20));

            // Shrink window: remove x from trie (it will be too small for future iterations)
            trie.remove(nums[i], 20);
        }

        return ans;
    }

    /**
     * Binary Trie with deletion support for maximum XOR queries
     *
     * Stores numbers in binary representation from most significant bit to least significant bit.
     * Each node has at most 2 children: trie[0] for bit 0, trie[1] for bit 1.
     *
     * Key Feature: Supports deletion by tracking count of numbers passing through each node.
     * When count reaches 0, the node can be removed to save space and avoid stale data.
     *
     * Operations:
     * - add(num, bit): Insert a number into the trie
     * - remove(num, bit): Remove a number from the trie
     * - query(num, bit): Find maximum XOR of num with any number in the trie
     *
     * Time Complexity: O(maxBit) for all operations
     */
    class Trie {

        Trie trie[];  // trie[0] = child for bit 0, trie[1] = child for bit 1
        int cnt;      // Count of numbers passing through this node

        /**
         * Constructor: Initialize a trie node with 2 children and count 0
         */
        public Trie() {

            trie = new Trie[2];
            cnt = 0;  // Initialize count (note: local variable shadowing bug in original)

        }

        /**
         * Finds the maximum XOR of num with any number stored in the trie
         *
         * Strategy: At each bit position, try to go to the opposite bit to maximize XOR
         * - If current bit is 0, prefer path with bit 1 (XOR gives 1)
         * - If current bit is 1, prefer path with bit 0 (XOR gives 1)
         * - If preferred path doesn't exist, take the other path (XOR gives 0)
         *
         * @param num The number to XOR with
         * @param bit Current bit position (from maxBit down to 0)
         * @return Maximum XOR value achievable
         */
        public int query(int num, int bit) {

            // Base case: processed all bits
            if (bit == -1) {
                return 0;
            }

            // Extract the bit at position 'bit' (0 or 1)
            int b = (num >> bit) & 1;

            // Calculate the opposite bit (to maximize XOR)
            int req = b ^ 1;

            // Try to go to the opposite bit (preferred for maximum XOR)
            if (trie[req] != null) {
                // Opposite bit exists: XOR gives 1 at this position
                // Set bit at position 'bit' to 1 and recurse
                return (1 << bit) | trie[req].query(num, bit - 1);
            }
            // Opposite bit doesn't exist, try same bit
            else if (trie[b] != null) {
                // Same bit exists: XOR gives 0 at this position
                // Don't set bit at position 'bit', just recurse
                return trie[b].query(num, bit - 1);
            }

            // No path exists (trie is empty at this level)
            return 0;
        }

        /**
         * Removes a number from the trie
         *
         * Decrements the count at each node along the path.
         * If a node's count reaches 0, it's removed to save space and prevent stale queries.
         *
         * @param num The number to remove
         * @param bit Current bit position (from maxBit down to 0)
         */
        public void remove(int num, int bit) {

            // Decrement count at this node
            cnt--;

            // Base case: processed all bits
            if (bit == -1) {
                return;
            }

            // Extract the bit at position 'bit' (0 or 1)
            var b = (num >> bit) & 1;

            // Recursively remove from the appropriate child
            trie[b].remove(num, bit - 1);

            // If child's count reaches 0, remove it (garbage collection)
            if (trie[b].cnt == 0) {
                trie[b] = null;
            }

        }

        /**
         * Adds a number to the trie in binary representation
         *
         * Increments the count at each node along the path.
         * Creates new nodes as needed.
         *
         * @param num The number to add
         * @param bit Current bit position (from maxBit down to 0)
         */
        public void add(int num, int bit) {

            // Increment count at this node
            cnt++;

            // Base case: processed all bits
            if (bit == -1) {
                return;
            }

            // Extract the bit at position 'bit' (0 or 1)
            int b = (num >> bit) & 1;

            // Create child node if it doesn't exist
            if (trie[b] == null) {
                trie[b] = new Trie();
            }

            // Recursively add remaining bits
            trie[b].add(num, bit - 1);
        }
    }
}

