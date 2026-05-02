package Leetcode;//Problem Link: https://leetcode.com/problems/find-x-value-of-array-ii/

/**
 * Solution for finding count of subsequences with specific product modulo k.
 * <p/>
 * Problem: Given an array nums and modulo k, process queries of form [index, value, start, x]: - Update: nums[index] =
 * value - Query: Count subsequences in nums[start...n-1] where (product of elements) % k == x
 * <p/>
 * Example: nums = [2, 3, 4], k = 6, query = [1, 5, 0, 2] - Update: nums[1] = 5, array becomes [2, 5, 4] - Count
 * subsequences in [2, 5, 4] where product % 6 == 2: - [2]: product = 2, 2 % 6 = 2 ✓ - [4]: product = 4, 4 % 6 = 4 ✗ -
 * [2, 4]: product = 8, 8 % 6 = 2 ✓ - Count = 2
 * <p/>
 * Key insight: Track remainder distribution with segment tree
 * <p/>
 * Observation 1: Product modulo k - (a * b) % k = ((a % k) * (b % k)) % k - We only need to track remainders, not
 * actual values - For each position, track: what remainder does this element have?
 * <p/>
 * Observation 2: Subsequence products - A subsequence can include or exclude each element - For range [l, r], we need
 * to track all possible product remainders - rem[x] = count of subsequences with product % k == x
 * <p/>
 * Observation 3: Merging ranges - If we have rem[] for left range and rem[] for right range - Combined: either pick
 * from left, or from right, or from both - When combining subsequences from both sides: * Left has product with
 * remainder i * Right has product with remainder j * Combined product remainder = (i * j) % k
 * <p/>
 * Algorithm: 1. Build segment tree where each node tracks rem[] array 2. rem[i] = count of subsequences in this range
 * with product % k == i 3. For point update: rebuild rem[] array for that position 4. For range query: merge rem[]
 * arrays from left to right
 * <p/>
 * Merge formula: - For each remainder from left range, combine with right range - If left has subsequence with product
 * ≡ i (mod k) - And right has subsequence with product ≡ j (mod k) - Combined subsequence has product ≡ (i * j) (mod
 * k)
 * <p/>
 * Time Complexity: O(n * k + q * k * log n) Space Complexity: O(n * k) for segment tree
 */
public class FindXValueOfArrayII {

    /**
     * Processes queries on array with product modulo tracking.
     *
     * @param nums Initial array
     * @param k Modulo value
     * @param queries Array of [index, value, start, x] queries
     * @return Count array of valid subsequences for each query
     */
    public int[] resultArray(int[] nums, int k, int[][] queries) {

        var n = nums.length;
        var q = queries.length;
        var res = new int[q];

        // ================================================================
        // STEP 1: Initialize segment tree
        // ================================================================
        // Segment tree tracks remainder distribution for each range
        var tree = new SegmentTree(n, k);

        // Build initial tree with original array values
        for (var i = 0; i < n; i++) {
            tree.add(1, i, nums[i], 0, n - 1);
        }

        // ================================================================
        // STEP 2: Process each query
        // ================================================================
        for (var i = 0; i < q; i++) {
            var ind = queries[i][0];    // Index to update
            var val = queries[i][1];    // New value
            var start = queries[i][2];  // Query range start
            var x = queries[i][3];      // Target remainder

            // ============================================================
            // Update array at index ind
            // ============================================================
            tree.add(1, ind, val, 0, n - 1);

            // ============================================================
            // Query range [start, n-1] for remainder x
            // ============================================================
            var node = tree.query(1, start, n - 1, 0, n - 1);

            // rem[x] = count of subsequences with product % k == x
            res[i] = node.rem[x];
        }

        return res;
    }

    /**
     * Node class representing a segment in the segment tree.
     * <p/>
     * Tracks: - prod: Product of all elements in this range (mod k) - rem[i]: Count of subsequences with product % k ==
     * i
     * <p/>
     * For a single element with value val: - prod = val % k - rem[] is initially all zeros - Will be set to rem[val%k]
     * = 1 after construction in add() method
     */
    static class Node {

        int prod;   // Product of all elements in range (mod k)
        int[] rem;  // rem[i] = count of subsequences with product ≡ i (mod k)

        /**
         * Constructor for a segment tree node.
         *
         * @param val Value for this node (typically element value or product)
         * @param k Modulo value
         */
        public Node(int val, int k) {

            rem = new int[k];  // Initialize remainder array
            prod = val % k;    // Store product modulo k

        }
    }

    /**
     * Segment Tree for tracking remainder distribution in ranges.
     * <p/>
     * Each node stores: - prod: Product of all elements in this range (mod k) - rem[i]: Count of subsequences with
     * product % k == i
     * <p/>
     * Supports: - Point update: Change value at specific index - Range query: Get remainder distribution for range [l,
     * r]
     */
    static class SegmentTree {

        Node[] tree;  // Array storing segment tree nodes
        int k;        // Modulo value

        /**
         * Constructor to initialize segment tree.
         *
         * @param n Size of the array
         * @param k Modulo value
         */
        public SegmentTree(int n, int k) {

            this.k = k;
            tree = new Node[4 * n];  // Standard segment tree size

            // Initialize all nodes with identity element (value = 1)
            for (int i = 0; i < 4 * n; i++) {
                tree[i] = new Node(1, k);
            }
        }

        /**
         * Queries the range [ql, qr] to get remainder distribution.
         * <p/>
         * Returns a Node containing: - prod: Product of all elements in range (mod k) - rem[i]: Count of subsequences
         * with product % k == i
         *
         * @param ind Current node index in segment tree
         * @param ql Query left boundary
         * @param qr Query right boundary
         * @param l Left boundary of current segment
         * @param r Right boundary of current segment
         * @return Node with remainder distribution for query range
         */
        public Node query(int ind, int ql, int qr, int l, int r) {

            // Current segment is outside query range
            if (qr < l || r < ql) {
                return new Node(1, k);  // Return identity element
            }

            // Current segment is fully within query range
            if (ql <= l && r <= qr) {
                return tree[ind];
            }

            // Partial overlap: query both children
            int mid = (l + r) / 2;

            Node left = query(2 * ind, ql, qr, l, mid);
            Node right = query(2 * ind + 1, ql, qr, mid + 1, r);

            // Merge results from both children
            return merge(left, right);
        }

        /**
         * Updates value at position i to val.
         * <p/>
         * Point update: changes single array element and propagates changes upward.
         *
         * @param ind Current node index in segment tree
         * @param i Position to update
         * @param val New value
         * @param l Left boundary of current segment
         * @param r Right boundary of current segment
         */
        public void add(int ind, int i, int val, int l, int r) {

            // Position i is outside current segment
            if (i < l || r < i) {
                return;
            }

            // Leaf node: update with new value
            if (l == r) {
                tree[ind] = new Node(val, k);
                // Single element: 1 subsequence with product = val
                // This subsequence has remainder val % k
                tree[ind].rem[val % k] = 1;
                return;
            }

            // Recursively update left or right child
            int mid = (l + r) / 2;

            add(2 * ind, i, val, l, mid);
            add(2 * ind + 1, i, val, mid + 1, r);

            // Recompute current node by merging children
            tree[ind] = merge(tree[2 * ind], tree[2 * ind + 1]);

        }

        /**
         * Merges two nodes by combining their remainder distributions.
         * <p/>
         * Combination strategy: - Start with all subsequences from left range - Then add subsequences that include
         * elements from right range
         * <p/>
         * Key insight: Optimization using left.prod - Instead of nested loop over all i,j combinations - Use the fact
         * that we're multiplying entire left range product - For subsequences from right with remainder i: * When
         * combined with left range, product becomes (left.prod * i) % k
         * <p/>
         * Merging logic: 1. Copy all left subsequences: node.rem[i] = left.rem[i] 2. For each remainder i from right: -
         * These are subsequences using only right elements - To combine with left, multiply by left.prod - New
         * remainder: (i * left.prod) % k - Add right.rem[i] to node.rem[newRem]
         * <p/>
         * Example: k=6, left has prod=2, right has 3 subsequences with prod%6=3 - Combined remainder: (3 * 2) % 6 = 0 -
         * Add 3 to node.rem[0]
         * <p/>
         * Why this works: - Left subsequences already counted - Right subsequences get multiplied by entire left
         * product - This efficiently computes all combinations
         *
         * @param left Left node
         * @param right Right node
         * @return Merged node with combined remainder distribution
         */
        private Node merge(Node left, Node right) {

            // Create node with combined product
            Node node = new Node(left.prod * right.prod, k);

            // Copy all subsequences from left range
            node.rem = left.rem.clone();

            // Add subsequences that include right elements
            for (int i = 0; i < k; i++) {
                // For right subsequences with product ≡ i (mod k)
                // Combined with left: product ≡ (i * left.prod) (mod k)
                int newRem = (i * left.prod) % k;
                node.rem[newRem] += right.rem[i];
            }

            return node;
        }
    }
}
