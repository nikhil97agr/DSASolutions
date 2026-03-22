package Leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

// Problem Link: https://leetcode.com/problems/maximum-profitable-triplets-with-increasing-prices-ii/

/**
 * Solution for Maximum Profitable Triplets with Increasing Prices II
 *
 * Problem: Given two arrays prices[] and profits[], find a triplet (i, j, k) such that: - i < j < k (indices are in
 * increasing order) - prices[i] < prices[j] < prices[k] (prices are strictly increasing) - Maximize: profits[i] +
 * profits[j] + profits[k]
 *
 * Example: prices  = [10, 2, 3, 4] profits = [100, 2, 7, 10]
 *
 * Valid triplets: - (1, 2, 3): prices [2, 3, 4], profits = 2 + 7 + 10 = 19
 *
 * Answer: 19
 *
 * Approach: Segment Tree + Sliding Window 1. For each middle element j, find: - Maximum profit on the left where price
 * < prices[j] - Maximum profit on the right where price > prices[j] 2. Use two segment trees: - pre: tracks maximum
 * profit for each price seen so far (left side) - suf: tracks maximum profit for each price to be seen (right side) 3.
 * Use HashMap + TreeMap to handle duplicate prices on the right side
 *
 * Key Insight: Segment tree allows O(log n) queries for "max profit with price in range [l, r]"
 *
 * Time Complexity: O(n log max_price) Space Complexity: O(max_price)
 */
public class MaximumProfitableTripletsWithIncreasingPricesII {

    /**
     * Finds the maximum profit from a triplet with strictly increasing prices
     *
     * @param prices Array of prices
     * @param profits Array of profits corresponding to each price
     * @return Maximum sum of profits for a valid triplet, or -1 if no triplet exists
     */
    public int maxProfit(int[] prices, int[] profits) {

        // Find maximum price to determine segment tree size
        int max = Arrays.stream(prices).max().getAsInt();

        // Segment tree for prefix (left side): stores max profit for each price seen so far
        SegmentTree pre = new SegmentTree(max);

        // Segment tree for suffix (right side): stores max profit for each price to come
        SegmentTree suf = new SegmentTree(max);

        // Map to track profit frequencies on the right side
        // Key: price, Value: TreeMap of (profit -> count)
        var map = new HashMap<Integer, TreeMap<Integer, Integer>>();

        // Initialize map with all possible prices
        for (int i = 0; i <= max; i++) {
            push(map, i, 0);
        }

        int n = prices.length;

        // Build suffix segment tree: process all elements except first (from right to left)
        for (int i = n - 1; i > 0; i--) {
            int p = prices[i];
            int pr = profits[i];
            // Insert maximum profit for this price into suffix tree
            suf.insert(1, 0, max, p, pr, true);
            // Track this profit in the map
            push(map, p, pr);
        }

        int ans = -1;

        // Add first element to prefix tree
        pre.insert(1, 0, max, prices[0], profits[0], true);

        // Process each element as the middle element of the triplet
        for (int i = 1; i < n - 1; i++) {
            int p = prices[i];      // Current price (middle element)
            int pr = profits[i];    // Current profit (middle element)

            // Remove current element from suffix (it's now the middle, not on the right)
            pop(map, p, pr);

            // Update suffix tree: set price p to the maximum remaining profit for that price
            // If no more elements with this price exist on the right, it becomes 0
            suf.insert(1, 0, max, p, map.get(p).lastKey(), false);

            // Query prefix tree: find max profit with price < p (left element)
            int l = pre.query(1, 0, max, 0, p - 1);

            // Query suffix tree: find max profit with price > p (right element)
            int r = suf.query(1, 0, max, p + 1, max);

            // If both left and right elements exist, update answer
            if (l != 0 && r != 0) {
                ans = Math.max(ans, l + r + pr);
            }

            // Add current element to prefix tree for future iterations
            pre.insert(1, 0, max, p, pr, true);
        }

        return ans;
    }

    /**
     * Adds a (price, profit) pair to the map
     *
     * The map tracks how many times each profit appears for each price. This is needed to handle duplicate prices on
     * the right side.
     *
     * @param map The map tracking price -> (profit -> count)
     * @param price The price to add
     * @param profit The profit to add
     */
    private void push(Map<Integer, TreeMap<Integer, Integer>> map, int price, int profit) {

        // Get or create TreeMap for this price, then increment count for this profit
        map.computeIfAbsent(price, x -> new TreeMap<>()).merge(profit, 1, Integer::sum);
    }

    /**
     * Removes a (price, profit) pair from the map
     *
     * Decrements the count for this profit at this price. If count reaches 0, removes the profit entry entirely.
     *
     * @param map The map tracking price -> (profit -> count)
     * @param price The price to remove
     * @param profit The profit to remove
     */
    private void pop(Map<Integer, TreeMap<Integer, Integer>> map, int price, int profit) {

        // Decrement count for this profit
        map.get(price).merge(profit, -1, Integer::sum);

        // If count reaches 0, remove this profit entry
        if (map.get(price).get(profit) == 0) {
            map.get(price).remove(profit);
        }
    }

    /**
     * Segment Tree for Range Maximum Query
     *
     * Stores the maximum profit for each price position. Supports: - insert(pos, val): Update value at position pos -
     * query(l, r): Get maximum value in range [l, r]
     *
     * Structure: Binary tree where each node stores the maximum of its range - Leaf nodes: individual prices - Internal
     * nodes: maximum of left and right children
     *
     * Time Complexity: O(log n) for both insert and query
     */
    class SegmentTree {

        int tree[];     // Array representation of segment tree
        int max = 0;    // Maximum price (determines tree size)

        /**
         * Constructor: Initialize segment tree for range [0, max]
         *
         * @param max Maximum price value
         */
        public SegmentTree(int max) {

            this.max = max;
            // Segment tree needs 4*n space for n elements
            tree = new int[max * 4];
        }

        /**
         * Insert or update a value at a specific position
         *
         * @param treeInd Current node index in the tree (1-indexed)
         * @param l Left boundary of current node's range
         * @param r Right boundary of current node's range
         * @param pos Position to update (the price)
         * @param val Value to insert (the profit)
         * @param isMax If true, take max with existing value; if false, replace
         */
        public void insert(int treeInd, int l, int r, int pos, int val, boolean isMax) {

            // Base case: reached the leaf node for position pos
            if (l == r) {
                if (isMax) {
                    // Take maximum of current value and new value
                    tree[treeInd] = Math.max(tree[treeInd], val);
                } else {
                    // Replace with new value
                    tree[treeInd] = val;
                }
                return;
            }

            // Recursive case: navigate to the correct child
            int mid = (l + r) / 2;

            if (pos <= mid) {
                // Position is in left half
                insert(2 * treeInd, l, mid, pos, val, isMax);
            } else {
                // Position is in right half
                insert(2 * treeInd + 1, mid + 1, r, pos, val, isMax);
            }

            // Update current node: maximum of left and right children
            tree[treeInd] = Math.max(tree[2 * treeInd], tree[2 * treeInd + 1]);
        }

        /**
         * Query for maximum value in a range
         *
         * @param treeInd Current node index in the tree (1-indexed)
         * @param l Left boundary of current node's range
         * @param r Right boundary of current node's range
         * @param ql Query range left boundary
         * @param qr Query range right boundary
         * @return Maximum value in range [ql, qr]
         */
        public int query(int treeInd, int l, int r, int ql, int qr) {

            // Invalid range
            if (l > r) {
                return 0;
            }

            // No overlap between query range and current node's range
            if (ql > r || qr < l) {
                return 0;
            }

            // Current node's range is completely within query range
            if (ql <= l && r <= qr) {
                return tree[treeInd];
            }

            // Partial overlap: query both children
            int mid = (l + r) / 2;

            return Math.max(
                    query(2 * treeInd, l, mid, ql, qr),        // Query left child
                    query(2 * treeInd + 1, mid + 1, r, ql, qr) // Query right child
            );
        }
    }
}