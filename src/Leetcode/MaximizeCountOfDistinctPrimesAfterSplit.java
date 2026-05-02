package Leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

//Problem Link: https://leetcode.com/problems/maximize-count-of-distinct-primes-after-split/

/**
 * Solution for maximizing count of distinct primes after splitting array.
 *
 * Problem: Given an array and queries [index, value]:
 * - Update nums[index] = value
 * - After update, split array into two non-empty parts at any position
 * - Count distinct primes in left part + distinct primes in right part
 * - Return maximum possible count for each query
 *
 * Key insight: Dynamic contribution tracking with segment tree
 *
 * Observation:
 * - For a prime p appearing at positions [i1, i2, ..., ik]:
 *   * If we split before i1 or after ik: p contributes 1 (appears in one part)
 *   * If we split between i1 and ik: p contributes 2 (appears in both parts)
 * - To maximize: choose split position that maximizes total contributions
 *
 * Contribution calculation:
 * - For each split position i (split between i and i+1):
 *   * Count = Σ (contribution of each prime at split position i)
 * - Prime p with leftmost occurrence L and rightmost occurrence R:
 *   * Positions [0, L-1]: contributes 1 (only in right part)
 *   * Positions [L, R-1]: contributes 2 (in both parts) ← KEY RANGE
 *   * Positions [R, n-1]: contributes 1 (only in left part)
 *
 * Example: nums = [2, 3, 2, 5], prime 2 at positions [0, 2]
 * - Split at 0: left=[], right=[2,3,2,5] → 2 contributes 1
 * - Split at 1: left=[2], right=[3,2,5] → 2 contributes 2 (both parts)
 * - Split at 2: left=[2,3], right=[2,5] → 2 contributes 2 (both parts)
 * - Split at 3: left=[2,3,2], right=[5] → 2 contributes 1
 *
 * Segment tree approach:
 * - tree[i] = maximum distinct prime count if we split at position i
 * - For each prime, add contributions to relevant ranges
 * - Query: find maximum value across all split positions
 *
 * Update strategy when prime changes:
 * 1. Remove old prime's contributions
 * 2. Add new prime's contributions (if it's prime)
 *
 * Time Complexity: O((n + q) log n + max log max) for sieve
 * Space Complexity: O(n + max) for segment tree and prime sieve
 */
public class MaximizeCountOfDistinctPrimesAfterSplit {

    /**
     * Processes queries and returns maximum distinct prime count for each.
     *
     * @param nums    Initial array
     * @param queries Array of [index, value] updates
     * @return Maximum distinct prime count after each query
     */
    public int[] maximumCount(int[] nums, int[][] queries) {

        var n = nums.length;
        var q = queries.length;
        var ans = new int[q];

        // ================================================================
        // STEP 1: Precompute all primes using Sieve of Eratosthenes
        // ================================================================
        // Find maximum value to determine sieve range
        var max = Math.max(Arrays.stream(nums).max().getAsInt(),
                Arrays.stream(queries).mapToInt(x -> x[1]).max().getAsInt());

        // Sieve of Eratosthenes: primes[i] = true if i is prime
        var primes = new boolean[max + 1];
        Arrays.fill(primes, true);
        primes[0] = primes[1] = false;  // 0 and 1 are not prime

        for (int i = 2; i <= max; i++) {
            if (primes[i]) {
                // Mark all multiples of i as non-prime
                for (int j = i << 1; j <= max; j += i) {
                    primes[j] = false;
                }
            }
        }
        // ================================================================
        // STEP 2: Initialize segment tree and prime position tracking
        // ================================================================
        // Segment tree tracks contribution count at each split position
        var tree = new SegmentTree(n);

        // Map: prime value → TreeSet of positions where it appears
        // TreeSet allows efficient first/last lookup
        var map = new HashMap<Integer, TreeSet<Integer>>();

        // Build initial map of prime positions
        for (int i = 0; i < n; i++) {
            if (!primes[nums[i]]) {
                continue;  // Skip non-primes
            }
            map.computeIfAbsent(nums[i], x -> new TreeSet<>()).add(i);
        }

        // ================================================================
        // STEP 3: Add initial contributions for all primes
        // ================================================================
        for (var entry : map.entrySet()) {
            var set = entry.getValue();  // Positions of this prime

            if (set.size() == 1) {
                // Prime appears at only one position
                // Contributes 1 to all split positions
                tree.update(1, 0, n - 1, 0, n - 1, 1);
                continue;
            }

            // Prime appears at multiple positions
            var l = set.first();  // Leftmost occurrence
            var r = set.last();   // Rightmost occurrence

            // Add contributions based on split position
            update(1, tree, l, r, n);
        }

        // ================================================================
        // STEP 4: Process each query
        // ================================================================
        for (int i = 0; i < q; i++) {
            var ind = queries[i][0];  // Index to update
            var val = queries[i][1];  // New value

            // ============================================================
            // Remove old value's contribution if it was prime
            // ============================================================
            if (primes[nums[ind]]) {
                remove(nums[ind], map, tree, ind, n);
            }

            // ============================================================
            // If new value is not prime, just query and continue
            // ============================================================
            if (!primes[val]) {
                ans[i] = tree.query(1, 0, n - 1, 0, n - 1);
                nums[ind] = val;
                continue;
            }

            // ============================================================
            // Add new prime's contribution
            // ============================================================
            // If this prime already exists elsewhere, remove its old contribution
            if (map.containsKey(val)) {
                var set = map.get(val);
                update(-1, tree, set.first(), set.last(), n);
            }

            // Add position to this prime's occurrence set
            map.computeIfAbsent(val, x -> new TreeSet<>()).add(ind);
            var set = map.get(val);

            // Add new contribution with updated position set
            update(1, tree, set.first(), set.last(), n);

            // Query maximum across all split positions
            ans[i] = tree.query(1, 0, n - 1, 0, n - 1);
            nums[ind] = val;

        }

        return ans;

    }

    /**
     * Updates contribution for a prime with occurrences in range [l, r].
     *
     * Contribution logic for prime appearing at positions [l, r]:
     * - Split positions [0, l-1]: prime only in right part → contributes val
     * - Split positions [l, r-1]: prime in both parts → contributes 2*val
     * - Split positions [r, n-1]: prime only in left part → contributes val
     *
     * Special case: If l == r (single occurrence):
     * - Prime appears in only one position
     * - All splits have prime in exactly one part → contributes val everywhere
     *
     * Example: Prime at positions [2, 5], n=8
     * - Splits [0,1]: contributes val (prime in right: positions 2,5)
     * - Splits [2,4]: contributes 2*val (prime in both: left has 2, right has 5)
     * - Splits [5,7]: contributes val (prime in left: positions 2,5)
     *
     * @param val  Contribution value (+1 to add, -1 to remove)
     * @param tree Segment tree tracking contributions
     * @param l    Leftmost position of this prime
     * @param r    Rightmost position of this prime
     * @param n    Array length
     */
    private void update(int val, SegmentTree tree, int l, int r, int n) {

        if (l == r) {
            // Single occurrence: contributes val to all split positions
            tree.update(1, 0, n - 1, 0, n - 1, val);
            return;
        }

        // Multiple occurrences: different contributions for different ranges
        tree.update(1, 0, n - 1, 0, l - 1, val);      // Before first occurrence
        tree.update(1, 0, n - 1, l, r - 1, 2 * val);  // Between first and last
        tree.update(1, 0, n - 1, r, n - 1, val);      // After last occurrence
    }

    /**
     * Removes contribution of a prime at a specific position.
     *
     * Strategy:
     * 1. If this is the last occurrence: remove contribution everywhere
     * 2. Otherwise:
     *    a. Remove old contribution (with current first/last positions)
     *    b. Remove this position from the occurrence set
     *    c. Add back contribution (with updated first/last positions)
     *
     * Example: Prime 3 at positions [1, 4, 7], removing position 4
     * - Old contribution: first=1, last=7
     * - Remove old: ranges [0,0]=1, [1,6]=2, [7,n-1]=1
     * - Update set: [1, 7]
     * - Add new: ranges [0,0]=1, [1,6]=2, [7,n-1]=1
     *
     * @param val  Prime value to remove
     * @param map  Map of prime → position set
     * @param tree Segment tree
     * @param ind  Position to remove
     * @param n    Array length
     */
    private void remove(int val, Map<Integer, TreeSet<Integer>> map, SegmentTree tree, int ind, int n) {

        var set = map.get(val);
        if (set.size() == 1) {
            // Last occurrence: remove contribution everywhere
            tree.update(1, 0, n - 1, 0, n - 1, -1);
            remove(val, map, ind);
            return;
        }

        // Remove old contribution
        update(-1, tree, set.first(), set.last(), n);

        // Update position set
        set.remove(ind);

        // Add back contribution with new first/last positions
        update(1, tree, set.first(), set.last(), n);
    }

    /**
     * Removes a position from prime's occurrence set and cleans up if empty.
     *
     * @param val Prime value
     * @param map Map of prime → position set
     * @param ind Position to remove
     */
    private void remove(int val, Map<Integer, TreeSet<Integer>> map, int ind) {

        map.get(val).remove(ind);
        if (map.get(val).isEmpty()) {
            map.remove(val);  // Clean up empty sets
        }
    }

    /**
     * Segment Tree with lazy propagation for range updates and max queries.
     *
     * Each node tracks:
     * - tree[i]: Maximum contribution count in this range
     * - lazy[i]: Pending increment value to be propagated to children
     *
     * Supports:
     * - Range update: Add value to all positions in [ql, qr]
     * - Range query: Get maximum value in range [ql, qr]
     */
    static class SegmentTree {

        int lazy[];  // Lazy propagation array
        int tree[];  // Maximum values for each segment

        /**
         * Constructor to initialize segment tree.
         *
         * @param n Size of the array (number of split positions)
         */
        public SegmentTree(int n) {

            lazy = new int[4 * n];  // Standard segment tree size
            tree = new int[4 * n];
        }

        /**
         * Applies pending lazy update to current node and propagates to children.
         *
         * When we add 'lazy' to all values in range:
         * - Update current node's maximum by adding lazy
         * - If not a leaf, propagate lazy to children
         * - Clear lazy value after applying
         *
         * @param ind Current node index in segment tree
         * @param l   Left boundary of current segment
         * @param r   Right boundary of current segment
         */
        public void push(int ind, int l, int r) {

            if (lazy[ind] != 0) {
                // Apply lazy update to current node
                tree[ind] += lazy[ind];

                // Propagate to children if not a leaf
                if (l != r) {
                    lazy[2 * ind] += lazy[ind];
                    lazy[2 * ind + 1] += lazy[ind];
                }

                // Clear lazy value
                lazy[ind] = 0;
            }
        }

        /**
         * Updates a range [ql, qr] by adding val to all positions.
         *
         * Uses lazy propagation for efficiency:
         * - If current segment is fully within query range, mark lazy and return
         * - Otherwise, recursively update children
         * - Update current node's max by taking max of children
         *
         * @param ind Current node index in segment tree
         * @param l   Left boundary of current segment
         * @param r   Right boundary of current segment
         * @param ql  Query left boundary
         * @param qr  Query right boundary
         * @param val Value to add to the range
         */
        public void update(int ind, int l, int r, int ql, int qr, int val) {

            // Apply any pending lazy updates first
            push(ind, l, r);

            // Current segment is outside query range or invalid range
            if (ql > qr || qr < l || r < ql) {
                return;
            }

            // Current segment is fully within query range
            if (ql <= l && r <= qr) {
                lazy[ind] += val;  // Mark lazy value
                push(ind, l, r);   // Apply immediately
                return;
            }

            // Partial overlap: recursively update children
            var mid = (l + r) >> 1;

            update(2 * ind, l, mid, ql, qr, val);
            update(2 * ind + 1, mid + 1, r, ql, qr, val);

            // Recompute current node's max from children
            tree[ind] = Math.max(tree[2 * ind], tree[2 * ind + 1]);

        }

        /**
         * Queries the range [ql, qr] to get the maximum value.
         *
         * Returns the maximum contribution count across all split positions
         * in the query range.
         *
         * @param ind Current node index in segment tree
         * @param l   Left boundary of current segment
         * @param r   Right boundary of current segment
         * @param ql  Query left boundary
         * @param qr  Query right boundary
         * @return Maximum value in query range
         */
        public int query(int ind, int l, int r, int ql, int qr) {

            // Apply any pending lazy updates first
            push(ind, l, r);

            // Current segment is outside query range or invalid range
            if (ql > qr || qr < l || r < ql) {
                return 0;  // Identity for max operation
            }

            // Current segment is fully within query range
            if (ql <= l && r <= qr) {
                return tree[ind];
            }

            // Partial overlap: query both children and return max
            int mid = (l + r) >> 1;

            return Math.max(query(2 * ind, l, mid, ql, qr), query(2 * ind + 1, mid + 1, r, ql, qr));
        }
    }
}