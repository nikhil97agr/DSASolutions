package Leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//Problem Link: https://leetcode.com/problems/maximum-sum-queries/

/**
 * Solution for finding maximum sums for queries on two arrays.
 * For each query [x, y], finds the maximum value of nums1[i] + nums2[i]
 * where nums1[i] >= x and nums2[i] >= y.
 *
 * Uses a segment tree and sorting strategy to efficiently process queries.
 */
public class MaximumSumQueries {

    /**
     * Processes multiple queries to find maximum sums.
     *
     * @param nums1   First array of integers
     * @param nums2   Second array of integers
     * @param queries 2D array where each query is [x, y]
     * @return Array of answers where ans[i] is the maximum sum for queries[i], or -1 if no valid index exists
     */
    public int[] maximumSumQueries(int[] nums1, int[] nums2, int[][] queries) {

        int n = nums1.length;
        int q = queries.length;
        int ans[] = new int[q];

        // Segment tree to efficiently find maximum sum in a range
        SegmentTree tree = new SegmentTree(n);

        // Maps original index to its position after sorting by nums1
        Map<Integer, Integer> sortedIndexMap = new HashMap<>();

        // Create arrays pairing each value with its original index
        int a[][] = new int[n][2];  // [nums1[i], original_index]
        int b[][] = new int[n][2];  // [nums2[i], original_index]
        for (int i = 0; i < n; i++) {
            a[i] = new int[]{nums1[i], i};
            b[i] = new int[]{nums2[i], i};
        }

        // Sort by nums1 values to enable binary search
        Arrays.sort(a, (x, y) -> x[0] - y[0]);

        // Sort by nums2 values to process queries in order
        Arrays.sort(b, (x, y) -> x[0] - y[0]);

        // Initialize segment tree with all sums and build the index mapping
        for (int i = 0; i < n; i++) {
            int ind = a[i][1];  // Original index
            sortedIndexMap.put(ind, i);  // Map original index to sorted position
            tree.add(1, nums1[ind] + nums2[ind], i, 0, n - 1);
        }

        // Augment queries with their original indices to track results
        int query[][] = new int[q][];
        for (int i = 0; i < q; i++) {
            query[i] = new int[]{queries[i][0], queries[i][1], i};
        }

        // Initialize all answers to -1 (no valid index found)
        Arrays.fill(ans, -1);

        // Sort queries by y value (nums2 threshold) to process in ascending order
        Arrays.sort(query, (x, y) -> x[1] - y[1]);

        // Process queries with a two-pointer approach
        // i1 tracks position in sorted nums2 array
        for (int i = 0, i1 = 0; i < q; i++) {
            int x = query[i][0];  // nums1 threshold
            int y = query[i][1];  // nums2 threshold
            int ind = query[i][2]; // Original query index

            // Remove all indices where nums2[i] < y from consideration
            // by setting their values to -1 in the segment tree
            while (i1 < n && b[i1][0] < y) {
                int ii = sortedIndexMap.get(b[i1][1]);  // Get sorted position
                tree.add(1, -1, ii, 0, n - 1);  // Invalidate this position
                i1++;
            }

            // If all elements have been removed, no valid answers for remaining queries
            if (i1 == n) {
                break;
            }

            // Find the first index in sorted nums1 array where nums1[i] >= x
            int searchIndex = binarySearch(a, x);
            if (searchIndex == -1) {
                ans[ind] = -1;
                continue;
            }

            // Query segment tree for maximum sum in range [searchIndex, n-1]
            // This range contains all valid indices where nums1[i] >= x and nums2[i] >= y
            ans[ind] = tree.search(1, searchIndex, n - 1, 0, n - 1);
        }

        return ans;
    }

    /**
     * Binary search to find the first index where arr[i][0] >= val.
     *
     * @param arr 2D array sorted by first element
     * @param val Target value to search for
     * @return Index of first element >= val, or -1 if no such element exists
     */
    private int binarySearch(int arr[][], int val) {

        int ans = -1;
        int start = 0;
        int end = arr.length - 1;

        // Standard binary search for lower bound
        while (start <= end) {
            int mid = (start + end) / 2;

            if (arr[mid][0] < val) {
                // Value at mid is too small, search right half
                start = mid + 1;
            } else {
                // Value at mid is >= val, this could be our answer
                ans = mid;
                // But search left half to find first occurrence
                end = mid - 1;
            }
        }

        return ans;
    }

    /**
     * Segment Tree implementation for range maximum queries.
     * Supports point updates and range maximum queries.
     */
    class SegmentTree {

        int tree[];  // Array representation of segment tree

        /**
         * Constructor to initialize the segment tree.
         *
         * @param n Size of the input array
         */
        public SegmentTree(int n) {

            tree = new int[n * 4];  // Segment tree needs 4*n space
            Arrays.fill(tree, -1);  // Initialize with -1 (invalid/no answer)
        }

        /**
         * Update a specific index in the segment tree.
         *
         * @param ind   Current node index in the segment tree
         * @param val   Value to set at position i
         * @param i     Array index to update
         * @param start Start of the range represented by current node
         * @param end   End of the range represented by current node
         */
        public void add(int ind, int val, int i, int start, int end) {

            // Index i is outside current range
            if (i < start || i > end) {
                return;
            }

            // Reached the leaf node corresponding to index i
            if (start == end) {
                tree[ind] = val;
                return;
            }

            // Recursively update the appropriate child
            int mid = (start + end) / 2;

            add(2 * ind, val, i, start, mid);
            add(2 * ind + 1, val, i, mid + 1, end);

            // Update current node with maximum of children
            tree[ind] = Math.max(tree[2 * ind], tree[2 * ind + 1]);


        }

        /**
         * Query the segment tree for the maximum value in a range.
         *
         * @param ind Current node index in the segment tree
         * @param ql  Query range left bound
         * @param qr  Query range right bound
         * @param l   Left bound of range represented by current node
         * @param r   Right bound of range represented by current node
         * @return Maximum value in the query range, or -1 if no valid value exists
         */
        public int search(int ind, int ql, int qr, int l, int r) {

            // Current range is completely outside query range
            if (ql > r || qr < l) {
                return -1;
            }

            // Current range is completely within query range
            if (ql <= l && r <= qr) {
                return tree[ind];
            }

            // Partial overlap - query both children and return maximum
            int mid = (l + r) / 2;

            return Math.max(search(2 * ind, ql, qr, l, mid), search(2 * ind + 1, ql, qr, mid + 1, r));
        }
    }
}