package Leetcode;

import java.util.Arrays;

//Problem Link: https://leetcode.com/problems/largest-local-values-in-a-matrix-ii/description/

/**
 * Q3 - Count Local Maximums in a Matrix
 *
 * This class solves the problem of counting local maximums in a 2D matrix. A cell at position (i,j) with value x is
 * considered a local maximum if: - The value x is greater than 0 - Within a diamond-shaped region of Manhattan distance
 * x from (i,j), all other values are less than or equal to x
 *
 * The solution uses Segment Trees to efficiently query maximum values in ranges.
 */
public class LargestLocalValuesInAMatrixII {

    /**
     * Counts the number of local maximums in the given matrix.
     *
     * Algorithm: 1. Build a segment tree for each row to enable efficient range maximum queries 2. For each cell (i,j)
     * with value x > 0: - Check if x is the maximum value in its diamond region - A diamond region has Manhattan
     * distance <= x from (i,j) 3. Count cells where the value is >= all values in their diamond region
     *
     * Time Complexity: O(n * m * x_max * log(m)) where x_max is the maximum value in matrix Space Complexity: O(n * m)
     * for the segment trees
     *
     * @param matrix the input 2D matrix
     * @return the count of local maximums
     */
    public int countLocalMaximums(int[][] matrix) {

        int n = matrix.length;      // Number of rows
        int m = matrix[0].length;   // Number of columns

        // Create a segment tree for each row to support range maximum queries
        var tree = new SegmentTree[n];

        // Initialize segment trees with matrix values
        for (int i = 0; i < n; i++) {
            tree[i] = new SegmentTree(m);
            for (int j = 0; j < m; j++) {
                // Add each element to the segment tree for this row
                tree[i].add(1, j, matrix[i][j], 0, m - 1);
            }
        }

        int ans = 0;  // Counter for local maximums

        // Check each cell to see if it's a local maximum
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int x = matrix[i][j];

                // Skip cells with value 0 (they can't be local maximums)
                if (x == 0) {
                    continue;
                }

                // Find the maximum value in the diamond region of size x around (i,j)
                int val = check(tree, x, i, j, n, m);

                // If current value x is >= maximum in its region, it's a local maximum
                if (val <= x) {
                    ans++;
                }

            }
        }

        return ans;
    }

    /**
     * Checks the maximum value in a diamond-shaped region around position (i,j). The diamond has Manhattan distance
     * exactly equal to x from (i,j).
     *
     * For a cell at (i,j) with value x, we check: - All rows from (i-x) to (i+x) - For each row, columns within
     * Manhattan distance x - Special handling for corner rows (top and bottom of diamond) to exclude the exact corner
     * cells which are outside the strict diamond boundary
     *
     * @param tree array of segment trees (one per row)
     * @param x the value at current cell (defines diamond size)
     * @param i current row index
     * @param j current column index
     * @param n total number of rows
     * @param m total number of columns
     * @return maximum value found in the diamond region
     */
    private int check(SegmentTree[] tree, int x, int i, int j, int n, int m) {

        int max = -1;
        int s = i - x;  // Start row of diamond
        int e = i + x;  // End row of diamond

        // Iterate through all rows in the diamond region
        for (int start = Math.max(i - x, 0); start <= Math.min(i + x, n - 1); start++) {

            // Special case: For top and bottom rows of diamond (corners)
            // We need to exclude the exact corner points to maintain diamond shape
            if (start != i && (start == s || start == e)) {
                int c1 = max(j - x, 0);      // Left boundary
                int c2 = min(j + x, m - 1);  // Right boundary

                // If left boundary is exactly at distance x, move it inward
                if (abs(j - c1) == x) {
                    c1++;
                }
                // If right boundary is exactly at distance x, move it inward
                if (abs(j - c2) == x) {
                    c2--;
                }

                // Query maximum in this adjusted range
                max = max(max, get(tree[start], c1, c2, 0, m - 1));
                continue;

            }

            // Regular case: For middle rows, include full width of diamond at this row
            max = max(
                    max,
                    get(tree[start], max(j - x, 0), min(j + x, m - 1), 0, m - 1)
            );
        }

        return max;
    }

    /**
     * Helper method to get absolute value.
     *
     * @param a the integer value
     * @return absolute value of a
     */
    private int abs(int a) {

        return Math.abs(a);
    }

    /**
     * Helper method to query maximum value in a range using segment tree.
     *
     * @param tree the segment tree to query
     * @param start query range start (inclusive)
     * @param end query range end (inclusive)
     * @param i segment tree range start (0)
     * @param j segment tree range end (m-1)
     * @return maximum value in the queried range
     */
    private int get(SegmentTree tree, int start, int end, int i, int j) {

        return tree.search(1, start, end, i, j);
    }

    /**
     * Helper method to get minimum of two integers.
     *
     * @param a first integer
     * @param b second integer
     * @return minimum of a and b
     */
    private int min(int a, int b) {

        return Math.min(a, b);
    }

    /**
     * Helper method to get maximum of two integers.
     *
     * @param a first integer
     * @param b second integer
     * @return maximum of a and b
     */
    private int max(int a, int b) {

        return Math.max(a, b);
    }

    /**
     * Segment Tree implementation for Range Maximum Query (RMQ).
     *
     * This data structure supports: - Point update: Update value at a specific index - Range query: Find maximum value
     * in a given range [l, r]
     *
     * Time Complexity: - Build: O(n log n) - Query: O(log n) - Update: O(log n)
     *
     * Space Complexity: O(4n) for the tree array
     */
    class SegmentTree {

        int tree[];  // Array to store segment tree nodes

        /**
         * Constructor to initialize segment tree.
         *
         * @param n size of the array to be represented
         */
        public SegmentTree(int n) {
            // Allocate 4*n space for segment tree (safe upper bound)
            tree = new int[n * 4];
            // Initialize all values to -1 (representing empty/invalid)
            Arrays.fill(tree, -1);
        }

        /**
         * Adds/updates a value at a specific index in the segment tree.
         *
         * @param ind current node index in segment tree
         * @param i target index where value should be added
         * @param val value to be added at index i
         * @param l left boundary of current segment
         * @param r right boundary of current segment
         */
        public void add(int ind, int i, int val, int l, int r) {

            // Base case: target index is outside current segment
            if (i < l || r < i) {
                return;
            }

            // Base case: reached leaf node (single element segment)
            if (l == r) {
                tree[ind] = val;
                return;
            }

            // Recursive case: split into left and right children
            int mid = (l + r) / 2;

            // Update left child (covers range [l, mid])
            add(2 * ind, i, val, l, mid);
            // Update right child (covers range [mid+1, r])
            add(2 * ind + 1, i, val, mid + 1, r);

            // Update current node with maximum of its children
            tree[ind] = Math.max(tree[ind * 2], tree[2 * ind + 1]);
        }

        /**
         * Searches for maximum value in a given query range [ql, qr].
         *
         * @param ind current node index in segment tree
         * @param ql query range left boundary
         * @param qr query range right boundary
         * @param l left boundary of current segment
         * @param r right boundary of current segment
         * @return maximum value in range [ql, qr], or -1 if range is invalid
         */
        public int search(int ind, int ql, int qr, int l, int r) {

            // Base case: invalid range or no overlap with query range
            if (ql > qr || qr < l || r < ql) {
                return -1;
            }

            // Base case: current segment is completely within query range
            if (ql <= l && r <= qr) {
                return tree[ind];
            }

            // Recursive case: partial overlap, check both children
            int mid = (l + r) / 2;

            // Return maximum from left and right subtrees
            return Math.max(search(2 * ind, ql, qr, l, mid), search(2 * ind + 1, ql, qr, mid + 1, r));
        }

    }
}

/*
 * EXAMPLE TEST CASE:
 *
 * Matrix:
 * [0 2 1]
 * [1 1 0]
 *
 * Explanation:
 * - Cell (0,1) has value 2
 *   - Diamond of size 2 includes cells at Manhattan distance <= 2
 *   - Maximum in that region (excluding itself) should be checked
 * - Cell (0,2) has value 1
 * - Cell (1,0) has value 1
 * - Cell (1,1) has value 1
 *
 * The algorithm checks each non-zero cell to see if it's a local maximum
 * within its diamond-shaped neighborhood.
 */