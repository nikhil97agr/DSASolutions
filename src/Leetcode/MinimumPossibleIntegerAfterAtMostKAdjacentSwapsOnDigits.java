package Leetcode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

//Problem Link: https://leetcode.com/problems/minimum-possible-integer-after-at-most-k-adjacent-swaps-on-digits/

/**
 * Solution for finding the minimum possible integer after at most k adjacent swaps.
 *
 * Problem: Given a string representing a number and k swaps allowed, find the lexicographically
 * smallest number that can be obtained by performing at most k adjacent swaps on the digits.
 *
 * Strategy: Greedy approach - at each position, try to place the smallest digit that can be
 * moved to that position within the remaining swap budget.
 */
public class MinimumPossibleIntegerAfterAtMostKAdjacentSwapsOnDigits {

    /**
     * Finds the minimum integer after at most k adjacent swaps.
     *
     * Algorithm:
     * 1. Store positions of each digit (0-9) in queues
     * 2. For each position in result, greedily select the smallest digit that can be moved
     *    to this position within the remaining swap budget
     * 3. Use a segment tree to efficiently track which digits have been moved (removed)
     *    and calculate the actual number of swaps needed
     *
     * Time Complexity: O(n * 10 * log n) where n is the length of the number
     * Space Complexity: O(n)
     *
     * @param num The input number as a string
     * @param k   Maximum number of adjacent swaps allowed
     * @return    Lexicographically smallest number achievable
     */
    public String minInteger(String num, int k) {

        int n = num.length();
        StringBuilder ans = new StringBuilder();

        // Create queues to store positions of each digit (0-9)
        var que = new ArrayList<Queue<Integer>>();
        for (int i = 0; i <= 9; i++) {
            que.add(new LinkedList<>());
        }

        // Populate queues with original positions of each digit
        for (int i = 0; i < n; i++) {
            int dig = num.charAt(i) - '0';
            que.get(dig).add(i);
        }

        // Segment tree to track which positions have been used (removed from original string)
        SegmentTree tree = new SegmentTree(n);

        // Build result string position by position
        for (int i = 0; i < n; i++) {
            // Try digits from 0 to 9 (greedy: pick smallest possible)
            for (int dig = 0; dig <= 9; dig++) {
                if (que.get(dig).isEmpty()) {
                    continue;  // No more occurrences of this digit
                }

                // Get the next available position of this digit in original string
                int index = que.get(dig).peek();

                // Count how many digits before 'index' have already been removed
                int cnt = tree.query(0, index, 0, n - 1, 1);

                // Calculate actual swaps needed to move this digit to current position
                // (original position - already removed digits = current position in remaining string)
                int left = index - cnt;

                // If we can afford the swaps, use this digit
                if (left <= k) {
                    k -= left;  // Deduct swaps used
                    tree.add(index, 0, n - 1, 1);  // Mark this position as used
                    ans.append(dig);  // Add digit to result
                    que.get(dig).poll();  // Remove this position from queue
                    break;  // Move to next position in result
                }
            }
        }

        return ans.toString();
    }

    /**
     * Segment Tree data structure for efficient range sum queries and point updates.
     *
     * Used to track which positions in the original string have been used (removed).
     * Each position is either 0 (not used) or 1 (used).
     * Supports:
     * - Point update: mark a position as used
     * - Range query: count how many positions in a range have been used
     */
    private class SegmentTree {

        int tree[];  // Segment tree array storing sum of ranges

        /**
         * Constructor to initialize segment tree.
         *
         * @param n Size of the array (number of digits)
         */
        public SegmentTree(int n) {

            tree = new int[n * 4];  // Segment tree needs 4*n space

        }

        /**
         * Marks a position as used (sets value to 1).
         *
         * @param val       The position to mark as used
         * @param start     Start of current segment
         * @param end       End of current segment
         * @param treeIndex Current node index in segment tree
         */
        public void add(int val, int start, int end, int treeIndex) {

            // If val is outside current segment, return
            if (val < start || val > end) {
                return;
            }

            // Leaf node: set value to 1
            if (start == end) {
                tree[treeIndex] = 1;
                return;
            }

            // Recursively update left and right children
            int mid = (start + end) >> 1;
            add(val, start, mid, 2 * treeIndex);
            add(val, mid + 1, end, 2 * treeIndex + 1);

            // Update current node as sum of children
            tree[treeIndex] = tree[2 * treeIndex] + tree[2 * treeIndex + 1];
        }

        /**
         * Queries the sum (count of used positions) in a range.
         *
         * @param ql      Query range left boundary
         * @param qr      Query range right boundary
         * @param l       Current segment left boundary
         * @param r       Current segment right boundary
         * @param treeInd Current node index in segment tree
         * @return        Count of used positions in range [ql, qr]
         */
        public int query(int ql, int qr, int l, int r, int treeInd) {

            // No overlap: query range is outside current segment
            if (qr < l || r < ql) {
                return 0;
            }

            // Complete overlap: current segment is fully within query range
            if (ql <= l && r <= qr) {
                return tree[treeInd];
            }

            // Partial overlap: query both children and combine results
            int mid = (l + r) >> 1;

            return query(ql, qr, l, mid, 2 * treeInd) + query(ql, qr, mid + 1, r, 2 * treeInd + 1);
        }
    }
}