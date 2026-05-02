package Leetcode;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

//Problem Link: https://leetcode.com/problems/alternating-groups-iii/

/**
 * Solution for counting alternating groups in a circular array with dynamic updates.
 *
 * Problem: Given a circular array of colors (0s and 1s) and queries: - Type 1 [1, size]: Count how many alternating
 * groups of given size exist - Type 2 [2, index, color]: Update colors[index] = color
 *
 * An alternating group of size k is a contiguous subsequence where adjacent elements differ. The array is circular:
 * colors[n-1] is adjacent to colors[0].
 *
 * Example: colors = [0, 1, 0, 1], size = 3 - Groups: [0,1,0], [1,0,1], [0,1,0], [1,0,1] (wrapping around) - Count: 4
 *
 * Key insight: Segment tracking + Segment tree
 *
 * Observation 1: Maximal alternating segments - Break array into maximal alternating segments - Segment [l, r]: all
 * adjacent elements differ - Track segment boundaries in TreeMap
 *
 * Observation 2: Counting groups within segment - Segment of length L has max(0, L - size + 1) groups of size 'size' -
 * Example: segment [0,1,0,1,0] (length 5), size=3 → groups: 3 * [0,1,0], [1,0,1], [0,1,0]
 *
 * Observation 3: Circular array handling - If first and last segments can merge (different colors at boundary): *
 * Combined length = len(first) + len(last) * Account for wraparound groups * Adjust count to avoid double-counting
 *
 * Observation 4: Segment tree for length tracking - Store segments by length: how many segments of each length exist -
 * For query with size k: sum contributions from all segment lengths ≥ k - Quick range query using segment tree
 *
 * Data structures: - TreeMap<start, end>: maps segment start index to end index - SegmentTree: tracks count and total
 * contribution of segments by length - Node: (sum, cnt) where sum = Σ(len), cnt = count of segments
 *
 * Algorithm: 1. Build initial maximal alternating segments 2. For Type 1 query: compute groups using segment tree 3.
 * For Type 2 query: update color and adjust affected segments
 *
 * Time Complexity: O(n + q log n) Space Complexity: O(n)
 */
public class AlternateGroupsIII {

    /**
     * Processes queries on circular alternating groups.
     *
     * @param colors Initial color array (0s and 1s)
     * @param queries Array of queries [type, params...]
     * @return List of results for Type 1 queries
     */
    public List<Integer> numberOfAlternatingGroups(int[] colors, int[][] queries) {

        var n = colors.length;
        var result = new ArrayList<Integer>();

        // ================================================================
        // Initialize data structures
        // ================================================================
        var map = new TreeMap<Integer, Integer>();  // segment start → end
        var tree = new SegmentTree(n + 1);          // segments by length

        // ================================================================
        // STEP 1: Build initial maximal alternating segments
        // ================================================================
        for (int i = 0; i < n; i++) {
            int r = i + 1;
            // Extend segment while adjacent colors differ
            while (r < n && colors[r] != colors[r - 1]) {
                r++;
            }
            // Store segment [i, r-1]
            map.put(i, r - 1);
            var len = r - i;
            i = r - 1;  // Move to end of segment
            // Add segment of this length to tree
            update(tree, len, n);
        }

        // ================================================================
        // STEP 2: Process queries
        // ================================================================
        for (var q : queries) {
            if (q[0] == 1) {
                // ============================================================
                // Type 1: Count groups of given size
                // ============================================================
                result.add(compute(tree, q[1], n, map, colors));
            } else {
                // ============================================================
                // Type 2: Update color at index
                // ============================================================
                update(tree, q[1], q[2], colors, map, n);
            }
        }

        return result;
    }

    /**
     * Updates color at index and adjusts affected segments.
     *
     * When color changes at index, we need to: 1. Check if segment splits (new color equals neighbor) 2. Check if
     * segments merge (new color differs from neighbor) 3. Update segment tree with changes
     *
     * Two cases to handle: - Right neighbor (ind+1): may split or merge with segment containing ind+1 - Left neighbor
     * (ind-1): may split or merge with segment containing ind-1
     *
     * @param tree Segment tree tracking lengths
     * @param ind Index to update
     * @param val New color value
     * @param colors Color array
     * @param map Segment boundaries
     * @param n Array length
     */
    private void update(SegmentTree tree, int ind, int val, int[] colors, TreeMap<Integer, Integer> map, int n) {

        // No change needed
        if (colors[ind] == val) {
            return;
        }

        colors[ind] = val;

        // ================================================================
        // Handle right neighbor (ind+1)
        // ================================================================
        if (ind < n - 1) {
            if (val == colors[ind + 1]) {
                // ========================================================
                // SPLIT: Same color as right neighbor
                // ========================================================
                // Current segment containing ind breaks at ind
                var start = map.floorKey(ind);
                var end = map.get(start);

                // Remove old segment
                update(tree, -(end - start + 1), n);

                // Create two new segments: [start, ind] and [ind+1, end]
                map.put(start, ind);
                update(tree, ind - start + 1, n);

                map.put(ind + 1, end);
                update(tree, end - ind, n);
            } else {
                // ========================================================
                // MERGE: Different color from right neighbor
                // ========================================================
                // Segments containing ind and ind+1 can merge
                var start1 = ind + 1;
                var end1 = map.get(start1);
                update(tree, -(end1 - start1 + 1), n);

                map.remove(start1);
                var start2 = map.floorKey(ind);
                var end2 = map.get(start2);
                map.remove(start2);
                update(tree, -(end2 - start2 + 1), n);

                // Create merged segment [start2, end1]
                map.put(start2, end1);
                update(tree, end1 - start2 + 1, n);

            }
        }

        // ================================================================
        // Handle left neighbor (ind-1)
        // ================================================================
        if (ind > 0) {
            if (val == colors[ind - 1]) {
                // ========================================================
                // SPLIT: Same color as left neighbor
                // ========================================================
                // Segment containing ind breaks at ind
                var start = map.floorKey(ind);
                var end = map.get(start);

                // Remove old segment
                update(tree, -(end - start + 1), n);

                // Create two segments: [start, ind-1] and [ind, end]
                map.put(start, ind - 1);
                update(tree, ind - start, n);

                map.put(ind, end);
                update(tree, end - ind + 1, n);
            } else {
                // ========================================================
                // MERGE: Different color from left neighbor
                // ========================================================
                // Segments containing ind-1 and ind can merge
                var start = map.floorKey(ind - 1);
                update(tree, -(ind - start), n);
                var end2 = map.get(ind);
                update(tree, -(end2 - ind + 1), n);
                map.remove(ind);

                // Create merged segment [start, end2]
                map.put(start, end2);
                update(tree, end2 - start + 1, n);

            }
        }


    }

    /**
     * Computes number of alternating groups of given size.
     *
     * Strategy: 1. Query segment tree for all segments of length ≥ size 2. For each segment of length L: contributes
     * max(0, L - size + 1) groups 3. Handle circular wraparound if first and last segments can merge
     *
     * Formula for segment contribution: - Segment length L, group size k - Positions where group can start: 0, 1, ...,
     * L-k - Count: L - k + 1 (if L ≥ k, else 0)
     *
     * Circular wraparound: - If colors[0] ≠ colors[n-1]: first and last segments are adjacent in circular array -
     * Combined length: len(first) + len(last) - Add groups from combined segment - Subtract groups already counted from
     * individual segments
     *
     * @param tree Segment tree
     * @param size Group size to count
     * @param n Array length
     * @param map Segment boundaries
     * @param colors Color array
     * @return Count of alternating groups of given size
     */
    private int compute(SegmentTree tree, int size, int n, TreeMap<Integer, Integer> map, int colors[]) {

        // ================================================================
        // Query segments of length ≥ size
        // ================================================================
        // node.sum = total length of all segments
        // node.cnt = count of segments
        var node = tree.query(1, size, n, 0, n);

        // For each segment of length L: groups = L - size + 1
        // Total groups = Σ(L - size + 1) = Σ(L) - cnt*(size-1)
        int ans = node.sum - (node.cnt * (size - 1));

        // ================================================================
        // Handle circular wraparound
        // ================================================================
        if (colors[0] != colors[n - 1]) {
            // First and last segments can merge in circular array
            var first = map.firstEntry();
            var last = map.lastEntry();

            var l1 = last.getValue() - last.getKey() + 1;   // Last segment length
            var l2 = first.getValue() - first.getKey() + 1; // First segment length

            // Combined segment contribution: max(0, l1+l2 - size + 1)
            // Subtract individual contributions already counted
            // Add combined contribution
            ans = ans + Math.max(0, l1 + l2 - size + 1) - Math.max(0, l1 - size + 1) - Math.max(0, l2 - size + 1);
        }

        return ans;
    }

    /**
     * Updates segment tree with segment of given length.
     *
     * Adds or removes a segment (positive len = add, negative len = remove).
     *
     * @param tree Segment tree
     * @param len Segment length (positive to add, negative to remove)
     * @param n Array length
     */
    public void update(SegmentTree tree, int len, int n) {

        tree.add(1, Math.abs(len), len, 0, n);
    }

    /**
     * Segment Tree for tracking segments by length.
     *
     * Stores segments indexed by their length. Each position i tracks segments of length i.
     *
     * Node stores: - sum: total length contribution (sum of all segment lengths at this position) - cnt: count of
     * segments at this position
     */
    class SegmentTree {

        Node tree[];  // Array storing segment tree nodes

        /**
         * Initializes segment tree.
         *
         * @param n Maximum segment length (array size)
         */
        public SegmentTree(int n) {

            tree = new Node[n * 4];  // Standard segment tree size
        }

        /**
         * Adds or removes a segment at position i.
         *
         * Updates the count and sum for segments of length i. Position i represents segments of length i.
         *
         * @param ind Current node index in segment tree
         * @param i Position to update (segment length)
         * @param val Value to add: positive = add segment, negative = remove segment
         * @param l Left boundary of current segment
         * @param r Right boundary of current segment
         */
        public void add(int ind, int i, int val, int l, int r) {

            if (i < l || r < i) {
                // Lazy initialization for nodes we pass through
                if (tree[ind] == null) {
                    tree[ind] = new Node(0, 0);
                }

                return;
            }

            // Leaf node: update sum and count
            if (l == r) {
                if (tree[ind] == null) {
                    tree[ind] = new Node(0, 0);
                }

                // val represents total length contribution (can be negative)
                tree[ind].sum += val;
                // cnt = number of segments; val/i gives count (since val = length * count)
                tree[ind].cnt += val / i;
                return;
            }

            // Recursively update appropriate child
            var mid = (l + r) >> 1;
            add(2 * ind, i, val, l, mid);
            add(2 * ind + 1, i, val, mid + 1, r);

            // Merge children to update current node
            tree[ind] = tree[ind * 2].merge(tree[ind * 2 + 1]);
        }

        /**
         * Queries sum and count for segment lengths in range [ql, qr].
         *
         * Returns aggregate Node with: - sum: total length of all segments with length in [ql, qr] - cnt: total count
         * of segments with length in [ql, qr]
         *
         * @param ind Current node index in segment tree
         * @param ql Query left boundary (minimum segment length)
         * @param qr Query right boundary (maximum segment length)
         * @param l Left boundary of current segment
         * @param r Right boundary of current segment
         * @return Node with aggregated sum and count
         */
        public Node query(int ind, int ql, int qr, int l, int r) {

            // Current segment is outside query range
            if (qr < l || r < ql) {
                return new Node(0, 0);
            }

            // Current segment is fully within query range
            if (ql <= l && r <= qr) {
                return tree[ind];
            }

            // Partial overlap: query both children and merge
            var mid = (l + r) >> 1;
            var left = query(2 * ind, ql, qr, l, mid);
            var right = query(2 * ind + 1, ql, qr, mid + 1, r);

            // Handle null nodes (lazy initialization)
            if (left == null) {
                left = new Node(0, 0);
            }
            if (right == null) {
                right = new Node(0, 0);
            }

            return left.merge(right);
        }
    }

    /**
     * Node class for segment tree.
     *
     * Stores aggregate information about segments: - sum: total length (sum of all segment lengths at this
     * position/range) - cnt: count of segments at this position/range
     */
    class Node {

        int sum;  // Total length contribution
        int cnt;  // Count of segments

        /**
         * Constructor for Node.
         *
         * @param sum Total length
         * @param cnt Count of segments
         */
        public Node(int sum, int cnt) {

            this.sum = sum;
            this.cnt = cnt;
        }

        /**
         * Merges this node with another node.
         *
         * @param node Node to merge with
         * @return New merged node
         */
        public Node merge(Node node) {

            return new Node(sum + node.sum, cnt + node.cnt);
        }
    }

}