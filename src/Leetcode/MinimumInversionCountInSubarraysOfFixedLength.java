package Leetcode;

import java.util.Arrays;
import java.util.HashMap;

//Problem Link: https://leetcode.com/problems/minimum-inversion-count-in-subarrays-of-fixed-length

/**
 * Solution for finding the minimum inversion count in subarrays of fixed length.
 *
 * An inversion in a subarray is a pair (i, j) where i < j and nums[i] > nums[j].
 * Given an array and a fixed length k, find the minimum number of inversions
 * in any contiguous subarray of length k.
 *
 * Algorithm:
 * 1. Use coordinate compression to map values to a smaller range [0, n-1]
 * 2. Use a sliding window of size k
 * 3. Use a segment tree (Fenwick tree style) to efficiently count inversions:
 *    - When adding an element, count how many larger elements are already in the window
 *    - When removing an element, count how many smaller elements remain
 *
 * Time Complexity: O(n log n) for sorting + O(n log n) for sliding window with segment tree
 * Space Complexity: O(n) for segment tree and compression map
 */
public class MinimumInversionCountInSubarraysOfFixedLength {

    /**
     * Finds the minimum inversion count across all subarrays of length k.
     *
     * @param nums Input array
     * @param k    Length of subarray
     * @return Minimum number of inversions in any subarray of length k
     */
    public long minInversionCount(int[] nums, int k) {

        int n = nums.length;

        // Segment tree to count elements in the current window
        var tree = new SegmentTree(n);

        // Coordinate compression: map original values to compressed indices [0, n-1]
        // This allows us to use values as indices in the segment tree
        var compressed = new HashMap<Integer, Integer>();
        int ind = 0;
        int clone[] = nums.clone();
        Arrays.sort(clone);  // Sort to assign compressed indices in order

        for (int x : clone) {
            if (!compressed.containsKey(x)) {
                compressed.put(x, ind++);  // Assign next available index
            }

        }

        long ans = Long.MAX_VALUE;  // Minimum inversions found so far
        long cnt = 0;               // Current inversion count in window

        // Sliding window: maintain window [start, end] of size k
        for (int start = 0, end = 0; end < n; end++) {
            // Add nums[end] to the window
            int val = compressed.get(nums[end]);

            // Count inversions created by adding nums[end]
            // An inversion occurs when nums[end] is smaller than elements already in window
            // Elements with compressed value > val are larger than nums[end]
            cnt += tree.search(1, val + 1, n - 1, 0, n - 1);

            // Add nums[end] to the segment tree (increment count at position val)
            tree.add(1, val, 1, 0, n - 1);

            // When window reaches size k, record answer and slide window
            if (end >= k - 1) {
                ans = Math.min(ans, cnt);

                // Remove nums[start] from the window
                val = compressed.get(nums[start]);
                tree.add(1, val, -1, 0, n - 1);  // Decrement count at position val

                // Subtract inversions removed by deleting nums[start]
                // nums[start] formed inversions with elements smaller than it
                // Elements with compressed value < val are smaller than nums[start]
                cnt -= tree.search(1, 0, val - 1, 0, n - 1);

                start++;  // Slide window forward
            }
        }

        return ans;
    }

    /**
     * Segment Tree implementation for range sum queries and point updates.
     * Used as a frequency counter to track how many elements of each compressed value
     * are currently in the sliding window.
     */
    class SegmentTree {

        int tree[];  // Array representation of segment tree

        /**
         * Constructor to initialize the segment tree.
         *
         * @param n Size of the coordinate-compressed range
         */
        public SegmentTree(int n) {

            tree = new int[n * 4];  // Segment tree needs 4*n space

        }

        /**
         * Updates a specific position in the segment tree by adding a value.
         * Used to increment/decrement the frequency of an element.
         *
         * @param treeInd Current node index in the segment tree
         * @param ind     Compressed value index to update
         * @param val     Value to add (typically +1 to add, -1 to remove)
         * @param start   Start of the range represented by current node
         * @param end     End of the range represented by current node
         */
        public void add(int treeInd, int ind, int val, int start, int end) {

            // Index is outside current range
            if (ind < start || ind > end) {
                return;
            }

            // Reached the leaf node corresponding to index ind
            if (start == end) {
                tree[treeInd] += val;  // Add value (can be positive or negative)
                return;
            }

            // Recursively update the appropriate child
            int mid = (start + end) / 2;

            add(2 * treeInd, ind, val, start, mid);
            add(2 * treeInd + 1, ind, val, mid + 1, end);

            // Update current node with sum of children
            tree[treeInd] = tree[2 * treeInd] + tree[2 * treeInd + 1];
        }

        /**
         * Queries the segment tree for the sum in a range.
         * Returns the count of elements with compressed values in [qs, qe].
         *
         * @param treeInd Current node index in the segment tree
         * @param qs      Query range start
         * @param qe      Query range end
         * @param s       Start of the range represented by current node
         * @param e       End of the range represented by current node
         * @return Sum of frequencies in the query range [qs, qe]
         */
        public int search(int treeInd, int qs, int qe, int s, int e) {

            // Current range is completely outside query range
            if (qe < s || qs > e) {
                return 0;
            }

            // Current range is completely within query range
            if (qs <= s && e <= qe) {
                return tree[treeInd];
            }

            // Partial overlap - query both children and sum results
            int mid = (s + e) / 2;

            return search(2 * treeInd, qs, qe, s, mid) + search(2 * treeInd + 1, qs, qe, mid + 1, e);
        }
    }
}