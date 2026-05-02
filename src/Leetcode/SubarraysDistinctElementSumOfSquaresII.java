package Leetcode;

import java.util.HashMap;

//Problem Link: https://leetcode.com/problems/subarrays-distinct-element-sum-of-squares-ii/

/**
 * Solution for computing sum of squares of distinct element counts in all subarrays.
 *
 * Problem: For each subarray of nums, count the number of distinct elements, square this count, and sum all these
 * squared counts across all subarrays.
 *
 * Formula: Σ (count of distinct elements in subarray [i,j])² for all i,j where i ≤ j
 *
 * Example: nums = [1, 2, 1] - Subarray [1]: 1 distinct element → 1² = 1 - Subarray [2]: 1 distinct element → 1² = 1 -
 * Subarray [1]: 1 distinct element → 1² = 1 - Subarray [1,2]: 2 distinct elements → 2² = 4 - Subarray [2,1]: 2 distinct
 * elements → 2² = 4 - Subarray [1,2,1]: 2 distinct elements → 2² = 4 - Total: 1 + 1 + 1 + 4 + 4 + 4 = 15
 *
 * Key insight: Contribution tracking with segment tree
 *
 * Observation: - Process array from left to right, fixing the right endpoint at position i - For each position i,
 * consider all subarrays ending at i: [0,i], [1,i], ..., [i,i] - Track how many distinct elements each subarray has
 *
 * Contribution method: - When we add element nums[i]: * If nums[i] is new (not seen before), it adds 1 to distinct
 * count of all subarrays ending at i * If nums[i] was seen at position j, it only adds 1 to subarrays starting after j
 *
 * Challenge: Computing (count)² efficiently - If we increment count by 1: (count + 1)² = count² + 2*count + 1 - Need to
 * track both count² (squareSum) and count (sum) in each range
 *
 * Segment tree approach: - Position j in segment tree represents subarray [j, currentRight] - tree[j].sum = number of
 * distinct elements in [j, currentRight] - tree[j].squareSum = (number of distinct elements in [j, currentRight])²
 *
 * Update formula when incrementing counts by val in range [ql, qr]: - new_sum = old_sum + val - new_squareSum =
 * (old_sum + val)² = old_sum² + 2*old_sum*val + val² = old_squareSum + 2*old_sum*val + val²
 *
 * Algorithm: 1. Process elements left to right 2. For element at position i: - Find last occurrence of nums[i] at
 * position prevIndex - Increment distinct count for all subarrays [j, i] where j > prevIndex - This is a range update:
 * [prevIndex+1, i] 3. Query total squareSum and add to answer
 *
 * Time Complexity: O(n log n) - n elements, each with O(log n) segment tree operations Space Complexity: O(n) for
 * segment tree
 */
public class SubarraysDistinctElementSumOfSquaresII {

    private int mod = 1_000_000_007;  // Modulo for preventing overflow

    /**
     * Computes sum of squares of distinct element counts for all subarrays.
     *
     * @param nums Input array
     * @return Sum of (distinct count)² for all subarrays, modulo 10^9+7
     */
    public int sumCounts(int[] nums) {

        int ans = 0;  // Accumulator for total sum

        // Map to track last occurrence of each element
        var map = new HashMap<Integer, Integer>();
        var n = nums.length;

        // Segment tree where position j represents subarray [j, currentRight]
        var tree = new SegmentTree(n);

        // ================================================================
        // Process each position as the right endpoint of subarrays
        // ================================================================
        for (var i = 0; i < n; i++) {
            // ============================================================
            // Find last occurrence of current element
            // ============================================================
            var prevIndex = -1;
            if (map.containsKey(nums[i])) {
                prevIndex = map.get(nums[i]);
            }

            // ============================================================
            // Update distinct counts for affected subarrays
            // ============================================================
            // Subarrays starting from prevIndex+1 to i all gain this element
            // Increment their distinct count by 1
            tree.update(1, 0, n - 1, prevIndex + 1, i, 1);

            // ============================================================
            // Query total sum of squares across all subarrays ending at i
            // ============================================================
            Node res = tree.query(1, 0, n - 1, 0, n - 1);

            // Add to answer
            ans = add(ans, res.squareSum);

            // ============================================================
            // Update last occurrence of current element
            // ============================================================
            map.put(nums[i], i);
        }

        return ans;
    }

    /**
     * Adds multiple numbers with modulo arithmetic.
     *
     * Handles both positive and potentially negative numbers by adding mod before taking modulo.
     *
     * @param a Variable number of long values to add
     * @return (a[0] + a[1] + ... + a[n-1]) % mod
     */
    private int add(long... a) {

        long ans = 0;
        for (long x : a) {
            ans += x;
            ans += mod;  // Ensure positive before modulo
            ans %= mod;
        }

        return (int) ans;
    }

    /**
     * Multiplies multiple numbers with modulo arithmetic.
     *
     * Applies modulo after each multiplication to prevent overflow.
     *
     * @param a Variable number of long values to multiply
     * @return (a[0] * a[1] * ... * a[n-1]) % mod
     */
    private int prod(long... a) {

        long prod = 1;
        for (long p : a) {
            prod *= p;
            prod %= mod;
        }

        return (int) prod;
    }

    /**
     * Segment Tree with lazy propagation for range updates and queries.
     *
     * Each node tracks: - sum: Sum of distinct counts in this range - squareSum: Sum of (distinct count)² in this range
     * - lazy: Pending increment value to be propagated
     *
     * Supports: - Range update: Add value to all positions in [ql, qr] - Range query: Get sum and squareSum for range
     * [ql, qr]
     */
    class SegmentTree {

        Node tree[];  // Array storing segment tree nodes

        /**
         * Constructor to initialize segment tree.
         *
         * @param n Size of the array
         */
        public SegmentTree(int n) {

            tree = new Node[4 * n];  // Standard segment tree size
            build(1, 0, n - 1);
        }

        /**
         * Builds the segment tree recursively.
         *
         * Initializes all nodes with sum=0, squareSum=0, lazy=0.
         *
         * @param ind Current node index in segment tree
         * @param l Left boundary of current segment
         * @param r Right boundary of current segment
         */
        public void build(int ind, int l, int r) {

            if (r < l) {
                return;  // Invalid range
            }

            // Leaf node
            if (l == r) {
                tree[ind] = new Node(0, 0, 0);
                return;
            }

            int mid = (l + r) >> 1;

            // Build left and right children
            build(ind << 1, l, mid);
            build((ind << 1) | 1, mid + 1, r);

            // Merge children to get current node's values
            tree[ind] = merge(tree[ind << 1], tree[(ind << 1) | 1]);
        }

        /**
         * Updates a range [ql, qr] by adding val to all positions.
         *
         * Uses lazy propagation for efficiency: - If current segment is fully within query range, mark lazy and return
         * - Otherwise, recursively update children
         *
         * When we add val to counts in a range: - sum increases by val * range_length - squareSum changes according to:
         * (x + val)² = x² + 2*x*val + val²
         *
         * @param ind Current node index in segment tree
         * @param l Left boundary of current segment
         * @param r Right boundary of current segment
         * @param ql Query left boundary
         * @param qr Query right boundary
         * @param val Value to add to the range
         */
        public void update(int ind, int l, int r, int ql, int qr, int val) {

            // Apply any pending lazy updates first
            pushToChild(ind, l, r);

            // Current segment is outside query range
            if (qr < l || r < ql) {
                return;
            }

            // Current segment is fully within query range
            if (ql <= l && r <= qr) {
                // Mark lazy value (will be pushed down later)
                tree[ind].lazy = add(tree[ind].lazy, val);
                // Apply update to current node
                pushToChild(ind, l, r);
                return;
            }

            // Partial overlap: recursively update children
            int mid = (l + r) >> 1;
            update(ind << 1, l, mid, ql, qr, val);
            update((ind << 1) | 1, mid + 1, r, ql, qr, val);

            // Recompute current node from updated children
            tree[ind] = merge(tree[ind << 1], tree[(ind << 1) | 1]);
        }

        /**
         * Queries the range [ql, qr] to get sum and squareSum.
         *
         * Returns a Node containing: - sum: Sum of all distinct counts in the range - squareSum: Sum of all (distinct
         * count)² in the range
         *
         * @param ind Current node index in segment tree
         * @param l Left boundary of current segment
         * @param r Right boundary of current segment
         * @param ql Query left boundary
         * @param qr Query right boundary
         * @return Node with aggregated sum and squareSum for query range
         */
        private Node query(int ind, int l, int r, int ql, int qr) {

            // Apply any pending lazy updates first
            pushToChild(ind, l, r);

            // Current segment is outside query range
            if (qr < l || r < ql) {
                return null;  // No contribution
            }

            // Current segment is fully within query range
            if (ql <= l && r <= qr) {
                return tree[ind];
            }

            // Partial overlap: query both children
            int mid = (l + r) >> 1;
            Node left = query(ind << 1, l, mid, ql, qr);
            Node right = query((ind << 1) | 1, mid + 1, r, ql, qr);

            // Handle null results (segments outside query range)
            if (left == null && right == null) {
                return null;
            }
            if (left == null) {
                return right;
            }
            if (right == null) {
                return left;
            }

            // Merge results from both children
            return merge(left, right);
        }

        /**
         * Applies pending lazy update to current node and propagates to children.
         *
         * When we add 'lazy' to all counts in range [l, r]: - Each position increases count by 'lazy' - new_sum =
         * old_sum + lazy * (r - l + 1) - new_squareSum = Σ(old_count + lazy)² = Σ(old_count² + 2*old_count*lazy +
         * lazy²) = old_squareSum + 2*lazy*old_sum + lazy²*(r-l+1)
         *
         * The formula comes from: (x + val)² = x² + 2*x*val + val² Summing over all positions in range gives the
         * formula above.
         *
         * @param ind Current node index in segment tree
         * @param l Left boundary of current segment
         * @param r Right boundary of current segment
         */
        private void pushToChild(int ind, int l, int r) {

            Node curr = tree[ind];
            if (curr != null && curr.lazy != 0) {
                // ============================================================
                // Apply lazy update to current node
                // ============================================================
                // Update squareSum: (x + lazy)² = x² + 2*x*lazy + lazy²
                // Summed over range: old_squareSum + 2*sum*lazy + lazy²*(r-l+1)
                curr.squareSum = add(curr.squareSum,
                        prod(2, curr.sum, curr.lazy),           // 2 * sum * lazy
                        prod(curr.lazy, curr.lazy, r - l + 1)); // lazy² * range_length

                // Update sum: each position increases by lazy
                curr.sum = add(curr.sum, prod(curr.lazy, r - l + 1));

                // ============================================================
                // Propagate lazy value to children (if not a leaf node)
                // ============================================================
                if (l != r) {
                    tree[ind << 1].lazy = add(tree[ind << 1].lazy, curr.lazy);
                    tree[(ind << 1) | 1].lazy = add(tree[(ind << 1) | 1].lazy, curr.lazy);
                }

                // Clear lazy value after applying
                curr.lazy = 0;
            }
        }

        /**
         * Merges two nodes by combining their sum and squareSum values.
         *
         * Used to combine results from left and right children.
         *
         * @param a Left node
         * @param b Right node
         * @return Merged node with combined values
         */
        private Node merge(Node a, Node b) {

            // Combine sums and squareSums from both children
            // lazy is set to 0 (no pending updates in merged node)
            return new Node(0, add(a.squareSum, b.squareSum), add(a.sum, b.sum));
        }
    }


    /**
     * Node class representing a segment in the segment tree.
     *
     * Tracks three values: - lazy: Pending increment value to be applied to this range - squareSum: Sum of (distinct
     * count)² for all positions in this range - sum: Sum of distinct counts for all positions in this range
     */
    class Node {

        int lazy;       // Pending increment value (lazy propagation)
        int squareSum;  // Sum of squares of distinct counts
        int sum;        // Sum of distinct counts

        /**
         * Constructor for a segment tree node.
         *
         * @param lazy Pending lazy value
         * @param squareSum Sum of squared counts
         * @param sum Sum of counts
         */
        public Node(int lazy, int squareSum, int sum) {

            this.lazy = lazy;
            this.squareSum = squareSum;
            this.sum = sum;
        }
    }
}

