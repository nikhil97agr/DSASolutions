package Leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

//Problem Link: https://leetcode.com/problems/maximum-area-rectangle-with-point-constraints-ii/

/**
 * Solution for finding maximum area rectangle with point constraints.
 *
 * Problem: Given n points in 2D plane, find the maximum area rectangle that:
 * 1. Has sides parallel to axes (axis-aligned)
 * 2. Four corners are formed by exactly 4 points from the input
 * 3. Interior and boundary of rectangle contain NO other points except the 4 corners
 *
 * Example: points = [(1,1), (1,3), (3,1), (3,3), (2,2)]
 * - Cannot use rectangle with corners (1,1), (1,3), (3,1), (3,3)
 * - Reason: point (2,2) is inside the rectangle
 * - Answer: -1 (no valid rectangle)
 *
 * Example 2: points = [(1,1), (1,3), (3,1), (3,3)]
 * - Valid rectangle: corners at all 4 points
 * - Area = (3-1) × (3-1) = 4
 *
 * Key insight: Sweep line algorithm + Segment tree
 *
 * Observation 1: Rectangle properties
 * - Rectangle defined by 4 corners: (x1, y1), (x1, y2), (x2, y1), (x2, y2)
 * - Two vertical edges at x1 and x2
 * - Two horizontal edges at y1 and y2
 * - For valid rectangle: no points inside or on edges (except corners)
 *
 * Observation 2: Sweep line approach
 * - Process points by x-coordinate (left to right)
 * - For each x, consider all pairs of y-coordinates at this x
 * - Check if there exists a previous x with the same pair of y-coordinates
 * - And no points in between on these y-coordinates
 *
 * Observation 3: Segment tree optimization
 * - For each y-coordinate, track the most recent x where we saw a point
 * - When we see edge (y1, y2) at x, query segment tree for max x in range [y1, y2]
 * - If max x equals the x where we last saw both y1 and y2, we have a candidate rectangle
 *
 * Algorithm:
 * 1. Compress y-coordinates to indices for segment tree
 * 2. Group points by x-coordinate
 * 3. Process x-coordinates from left to right (sweep line)
 * 4. For each x, iterate through consecutive pairs of y-coordinates
 * 5. Query segment tree for max x in range [y1, y2]
 * 6. If this matches previous x with same edge (y1, y2), compute area
 * 7. Update segment tree with current x for processed y-coordinates
 *
 * Time Complexity: O(n log n + n² log n) where n is number of points
 * Space Complexity: O(n) for data structures
 */
public class MaximumAreaRectangleWithPointConstraintsII {

    /**
     * Finds maximum area of valid rectangle.
     *
     * @param xCoord Array of x-coordinates
     * @param yCoord Array of y-coordinates
     * @return Maximum rectangle area, or -1 if no valid rectangle exists
     */
    public long maxRectangleArea(int[] xCoord, int[] yCoord) {

        var n = xCoord.length;

        // ================================================================
        // STEP 1: Coordinate compression for y-coordinates
        // ================================================================
        // Collect all unique y-coordinates and map them to indices
        TreeSet<Integer> yList = new TreeSet<>();
        for (int y : yCoord) {
            yList.add(y);
        }

        // Map y-coordinate to compressed index (for segment tree)
        var yMap = new HashMap<Integer, Integer>();
        int ind = 0;
        for (int y : yList) {
            yMap.put(y, ind++);
        }

        // ================================================================
        // STEP 2: Build x-coordinate map
        // ================================================================
        // xMap: x-coordinate → sorted set of y-coordinates at that x
        var xMap = buildXMap(n, xCoord, yCoord);

        long ans = -1;  // Result: -1 if no valid rectangle

        // ================================================================
        // STEP 3: Initialize segment tree
        // ================================================================
        // Segment tree tracks: for each y-index, what is the most recent x we saw?
        var segmentTree = new SegmentTree(yMap.size());

        // ================================================================
        // STEP 4: Sweep line - process x-coordinates from left to right
        // ================================================================
        for (var entry : xMap.entrySet()) {
            var tree = entry.getValue();  // TreeSet of y-coordinates at this x
            var x = entry.getKey();       // Current x-coordinate

            // ============================================================
            // Process consecutive pairs of y-coordinates at current x
            // ============================================================
            var currY = tree.first();  // Start with smallest y
            while (tree.higher(currY) != null) {
                var nextY = tree.higher(currY);  // Next y-coordinate

                // Map y-coordinates to compressed indices
                var ind1 = yMap.get(currY);
                var ind2 = yMap.get(nextY);

                // ========================================================
                // Query: Find maximum x in range [ind1, ind2]
                // ========================================================
                // This tells us the most recent x where we saw a point
                // in the y-range [currY, nextY]
                int prevX = segmentTree.query(1, 0, yMap.size() - 1, ind1, ind2);

                // ========================================================
                // Check if we can form a valid rectangle
                // ========================================================
                if (prevX != -1) {
                    // Get points at prevX
                    var set = xMap.get(prevX);

                    // Valid rectangle conditions:
                    // 1. prevX has a point at currY
                    // 2. prevX has a point immediately after currY
                    // 3. That next point is exactly at nextY
                    // This ensures:
                    //   - 4 corners exist: (prevX, currY), (prevX, nextY), (x, currY), (x, nextY)
                    //   - No points on edges between corners (consecutive y's)
                    //   - No points inside (segment tree query confirmed no x in between)
                    if (set.contains(currY) && set.higher(currY) != null && Objects.equals(set.higher(currY), nextY)) {
                        long width = x - prevX;
                        long height = nextY - currY;
                        ans = Math.max(ans, width * height);
                    }
                }

                // ========================================================
                // Update segment tree: mark currY as seen at x
                // ========================================================
                segmentTree.update(1, 0, yMap.size() - 1, ind1, x);

                currY = nextY;  // Move to next y-coordinate
            }

            // Update last y-coordinate
            segmentTree.update(1, 0, yMap.size() - 1, yMap.get(currY), x);
        }

        return ans;


    }

    /**
     * Builds a map from x-coordinates to sorted sets of y-coordinates.
     *
     * This groups all points by their x-coordinate and sorts y-coordinates
     * at each x. TreeMap ensures x's are sorted, TreeSet ensures y's are sorted.
     *
     * Example: points [(1,2), (1,5), (3,2)]
     * - xMap[1] = {2, 5}
     * - xMap[3] = {2}
     *
     * @param n      Number of points
     * @param xCoord Array of x-coordinates
     * @param yCoord Array of y-coordinates
     * @return TreeMap: x-coordinate → TreeSet of y-coordinates at that x
     */
    private TreeMap<Integer, TreeSet<Integer>> buildXMap(int n, int xCoord[], int yCoord[]) {

        var xMap = new TreeMap<Integer, TreeSet<Integer>>();
        for (var i = 0; i < n; i++) {
            var x = xCoord[i];
            var y = yCoord[i];

            // Add y to the set of y-coordinates at x
            xMap.computeIfAbsent(x, k -> new TreeSet<>()).add(y);


        }

        return xMap;
    }

    /**
     * Segment Tree for range maximum queries and point updates.
     *
     * Each node stores the maximum value in its range.
     * Used to track the most recent x-coordinate for each y-index range.
     *
     * Operations:
     * - update(ind, val): Set value at index ind to val
     * - query(ql, qr): Get maximum value in range [ql, qr]
     */
    class SegmentTree {

        int tree[];  // Tree array storing maximum values

        /**
         * Constructor to initialize segment tree.
         *
         * @param n Number of elements (compressed y-coordinates)
         */
        public SegmentTree(int n) {

            tree = new int[n * 4];  // Standard segment tree size
            Arrays.fill(tree, -1);   // -1 indicates no x seen yet
        }

        /**
         * Updates value at position ind to val.
         *
         * Point update: sets tree[ind] = val and propagates max upward.
         *
         * @param treeInd Current node index in segment tree
         * @param l       Left boundary of current segment
         * @param r       Right boundary of current segment
         * @param ind     Position to update
         * @param val     New value (x-coordinate)
         */
        public void update(int treeInd, int l, int r, int ind, int val) {

            // Position ind is outside current segment
            if (ind < l || ind > r) {
                return;
            }

            // Leaf node: update with new value
            if (l == r) {
                tree[treeInd] = val;
                return;
            }

            // Recursively update left or right child
            int mid = (l + r) >> 1;
            update(2 * treeInd, l, mid, ind, val);
            update(2 * treeInd + 1, mid + 1, r, ind, val);

            // Recompute current node: max of both children
            tree[treeInd] = Math.max(tree[2 * treeInd], tree[2 * treeInd + 1]);
        }

        /**
         * Queries maximum value in range [ql, qr].
         *
         * Returns the maximum x-coordinate seen in the y-index range.
         *
         * @param ind Current node index in segment tree
         * @param l   Left boundary of current segment
         * @param r   Right boundary of current segment
         * @param ql  Query left boundary
         * @param qr  Query right boundary
         * @return Maximum value in range [ql, qr], or -1 if no values
         */
        public int query(int ind, int l, int r, int ql, int qr) {

            // Invalid query range
            if (ql > qr) {
                return -1;
            }

            // Current segment is outside query range
            if (qr < l || r < ql) {
                return -1;
            }

            // Current segment is fully within query range
            if (ql <= l && r <= qr) {
                return tree[ind];
            }

            // Partial overlap: query both children and return max
            int mid = (l + r) >> 1;
            return Math.max(query(ind * 2, l, mid, ql, qr), query(2 * ind + 1, mid + 1, r, ql, qr));
        }
    }
}