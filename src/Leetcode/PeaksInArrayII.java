package Leetcode;

import java.util.ArrayList;
import java.util.List;

//Problem Link: https://leetcode.com/problems/peaks-in-array-ii/

/**
 * Solution for counting peak subarrays with dynamic updates.
 *
 * Problem: Given an array and queries, we need to:
 * 1. Count peak subarrays in a given range [l, r]
 * 2. Update array values and handle subsequent queries
 *
 * A peak subarray must:
 * - Have length >= 3
 * - Contain at least one peak element (nums[k] > nums[k-1] && nums[k] > nums[k+1])
 *
 * Approach:
 * - Precompute all peak positions in the array
 * - Use a Segment Tree to efficiently query peak counts in any range
 * - For updates, check if neighbors become peaks or stop being peaks
 *
 * Time Complexity: O(n + q*log(n)) where n = array length, q = number of queries
 * Space Complexity: O(n) for segment tree storage
 */
public class PeaksInArrayII{

    /**
     * Main method to process queries on the array.
     *
     * @param nums The input integer array
     * @param queries 2D array where each query is either:
     *                - [1, l, r]: Count peak subarrays in range [l, r]
     *                - [2, index, val]: Update nums[index] = val
     * @return Array of results for all type-1 queries
     */
    public long[] countOfPeaks(int[] nums, int[][] queries) {

        int n = nums.length;
        List<Long> result = new ArrayList<>();

        // Step 1: Precompute all peak positions in the array
        // peak[i] = true if nums[i] is a peak element
        boolean peak[] = new boolean[n];
        for (int i = 1; i < n - 1; i++) {
            if (isPeak(i, nums)) {
                peak[i] = true;
            }
        }

        // Step 2: Build a segment tree to efficiently query peak information
        var tree = new SegmentTree(n, peak);

        // Step 3: Process each query
        for (var q : queries) {
            var type = q[0];
            var l = q[1];
            var r = q[2];

            if (type == 1) {
                // Type 1: Count peak subarrays in range [l, r]

                // Edge case: If range length < 3, no peak subarray possible
                if (r - l < 2) {
                    result.add(0l);
                    continue;
                }

                // Query the segment tree for peak information in range [l, r]
                var res = tree.query(1, l, r, 0, n - 1);

                // No peaks found in the range
                if (res.first == -1) {
                    result.add(0l);
                    continue;
                }

                // Case 1: Only one peak in the range
                // Count subarrays: all combinations where the peak is in the middle
                // Formula: (positions before peak) * (positions after peak)
                if (res.first == res.last) {
                    long cnt = res.first;
                    result.add((cnt - l) * (r - cnt));
                    continue;
                }

                // Case 2: Multiple peaks in the range
                // We need to count all valid subarrays containing at least one peak
                long a = res.first;  // Position of first peak
                long b = res.last;   // Position of last peak

                // Total count formula:
                // 1. Subarrays starting before first peak and ending at/after it: (a - l) * (r - a)
                // 2. Subarrays between peaks: r * (b - a) - res.sum
                //    where res.sum accounts for overlapping counts
                long total = (a - l) * (r - a) + 1l * r * (b - a) - res.sum;
                result.add(total);
            } else {
                // Type 2: Update nums[index] = val

                // No change needed if value is already equal
                if (nums[l] == r) {
                    continue;
                }

                // Update the array value
                nums[l] = r;

                // Check if the update affects peak status of nearby elements
                // We need to check positions [l-1, l, l+1] because:
                // - l-1 might become/stop being a peak (if l was its right neighbor)
                // - l might become/stop being a peak (direct update)
                // - l+1 might become/stop being a peak (if l was its left neighbor)
                for (int i = l - 1; i <= l + 1; i++) {
                    var state = isPeak(i, nums);
                    // If peak status changed, update the segment tree
                    if (i >= 0 && i <= nums.length - 1 && state != peak[i]) {
                        peak[i] = state;
                        tree.update(1, 0, n - 1, i, peak);
                    }
                }
            }
        }

        // Convert result list to array
        long ans[] = new long[result.size()];
        for (int i = 0; i < result.size(); i++) {
            ans[i] = result.get(i);
        }

        return ans;
    }

    /**
     * Helper method to check if position i is a peak.
     *
     * A peak is an element that is strictly greater than both its neighbors.
     * Edge elements (first and last) cannot be peaks.
     *
     * @param i The index to check
     * @param nums The array
     * @return true if nums[i] is a peak, false otherwise
     */
    private boolean isPeak(int i, int nums[]) {
        // Edge elements cannot be peaks
        if (i <= 0 || i >= nums.length - 1) {
            return false;
        }
        // Check if element is strictly greater than both neighbors
        return nums[i] > nums[i - 1] && nums[i] > nums[i + 1];
    }

    /**
     * Node class for the Segment Tree.
     *
     * Each node stores information about peaks in its range:
     * - first: Index of the first peak in the range (-1 if no peaks)
     * - last: Index of the last peak in the range (-1 if no peaks)
     * - sum: Cumulative sum used to calculate subarray counts
     *        This represents the sum of (peak_position * count) for all peaks
     *        Used in the formula to avoid double-counting overlapping subarrays
     */
    class Node {
        int first = -1;  // First peak index in this range
        int last = -1;   // Last peak index in this range
        long sum = 0;    // Cumulative sum for counting calculations
    }

    /**
     * Segment Tree implementation for efficient range queries on peaks.
     *
     * The segment tree allows us to:
     * 1. Query peak information in O(log n) time
     * 2. Update peak status in O(log n) time
     *
     * Each node stores aggregated information about peaks in its range.
     */
    class SegmentTree {

        Node[] tree;  // Array-based segment tree

        /**
         * Constructor to build the segment tree.
         *
         * @param n Size of the array
         * @param isPeak Boolean array marking peak positions
         */
        public SegmentTree(int n, boolean isPeak[]) {
            // Allocate 4*n space for the segment tree (sufficient for all nodes)
            tree = new Node[n * 4];
            build(1, 0, n - 1, isPeak);
        }

        /**
         * Recursively build the segment tree.
         *
         * @param ind Current node index in the tree
         * @param l Left boundary of current range
         * @param r Right boundary of current range
         * @param isPeak Array marking peak positions
         */
        private void build(int ind, int l, int r, boolean isPeak[]) {
            // Base case: Leaf node (single element)
            if (l == r) {
                tree[ind] = new Node();
                if (isPeak[l]) {
                    // This position is a peak
                    tree[ind].first = l;
                    tree[ind].last = l;
                    tree[ind].sum = 0;  // Single peak has sum 0
                }
                return;
            }

            // Recursive case: Build left and right children
            var mid = (l + r) / 2;
            build(2 * ind, l, mid, isPeak);           // Left child
            build(2 * ind + 1, mid + 1, r, isPeak);   // Right child

            // Merge information from children
            tree[ind] = merge(tree[2 * ind], tree[2 * ind + 1]);
        }

        /**
         * Merge two segment tree nodes.
         *
         * Combines information from left and right child nodes to create
         * the parent node's aggregated information.
         *
         * @param left Left child node
         * @param right Right child node
         * @return Merged node containing combined information
         */
        private Node merge(Node left, Node right) {
            // If left has no peaks, return right
            if (left.first == -1) {
                return right;
            }
            // If right has no peaks, return left
            if (right.first == -1) {
                return left;
            }

            // Both have peaks - merge them
            Node ans = new Node();
            ans.first = left.first;  // First peak comes from left
            ans.last = right.last;   // Last peak comes from right

            // Calculate contribution of subarrays between left.last and right.first
            // This represents the count of subarrays that span across the boundary
            long total = 1l * (right.first - left.last) * right.first;
            ans.sum = left.sum + right.sum + total;

            return ans;
        }

        /**
         * Update a single position in the segment tree.
         *
         * When an array value changes, we need to update the segment tree
         * to reflect the new peak status at that position.
         *
         * @param ind Current node index in the tree
         * @param l Left boundary of current range
         * @param r Right boundary of current range
         * @param i Index to update
         * @param isPeak Updated peak status array
         */
        public void update(int ind, int l, int r, int i, boolean isPeak[]) {
            // Position i is outside this node's range
            if (i < l || r < i) {
                return;
            }

            // Base case: Leaf node (single element)
            if (l == r) {
                if (isPeak[l]) {
                    // Position is now a peak
                    tree[ind].first = l;
                    tree[ind].last = l;
                    tree[ind].sum = 0;
                } else {
                    // Position is not a peak
                    tree[ind].first = -1;
                    tree[ind].last = -1;
                    tree[ind].sum = 0;
                }
                return;
            }

            // Recursive case: Update appropriate child
            var mid = (l + r) / 2;
            update(2 * ind, l, mid, i, isPeak);
            update(2 * ind + 1, mid + 1, r, i, isPeak);

            // Recompute current node by merging children
            tree[ind] = merge(tree[2 * ind], tree[2 * ind + 1]);
        }

        /**
         * Query peak information in a given range [ql, qr].
         *
         * Returns a node containing aggregated peak information for the query range.
         *
         * @param ind Current node index in the tree
         * @param ql Left boundary of query range
         * @param qr Right boundary of query range
         * @param l Left boundary of current node's range
         * @param r Right boundary of current node's range
         * @return Node containing peak information for the query range
         */
        public Node query(int ind, int ql, int qr, int l, int r) {
            // No overlap between query range and current range
            if (qr < l || r < ql) {
                return new Node();  // Return empty node
            }

            // Current range is completely within query range
            if (ql <= l && r <= qr) {
                return tree[ind];  // Return this node's data
            }

            // Partial overlap: Query both children
            int mid = (l + r) / 2;
            Node left = query(2 * ind, ql, qr, l, mid);
            Node right = query(2 * ind + 1, ql, qr, mid + 1, r);

            // Merge results from both children
            return merge(left, right);
        }
    }
}