package Leetcode;

import java.util.Arrays;

//Problem Link: https://leetcode.com/problems/beautiful-pairs/

/**
 * Solution for finding the most beautiful pair of indices. A pair (i, j) is beautiful if |nums1[i] - nums1[j]| +
 * |nums2[i] - nums2[j]| is minimized. Uses segment trees to efficiently find optimal pairs by processing elements in
 * sorted order.
 */
public class BeautifulPairs {

    /**
     * Finds the most beautiful pair of indices from two arrays.
     *
     * @param nums1 First array of integers
     * @param nums2 Second array of integers
     * @return Array containing the two indices [i, j] where i < j that form the most beautiful pair
     */
    public int[] beautifulPair(int[] nums1, int[] nums2) {

        // Track the best pair found so far
        int i1 = Integer.MAX_VALUE;  // First index of best pair
        int i2 = Integer.MAX_VALUE;  // Second index of best pair
        int diff = Integer.MAX_VALUE; // Minimum difference found
        int n = nums1.length;
        int len = n + 1;
        var nodes = new Node[n + 1];

        // Create nodes pairing values from both arrays with their original indices
        for (int i = 0; i < n; i++) {
            nodes[i] = new Node(nums1[i], nums2[i], i);
        }

        // Two segment trees to track different cases:
        // 'smaller' handles cases where nums1[i] < nums1[j]
        // 'larger' handles cases where nums1[i] >= nums1[j]
        SegmentTree smaller = new SegmentTree(n);
        SegmentTree larger = new SegmentTree(n);

        // Sort nodes by second value (nums2), then by first value (nums1)
        // This allows us to process elements in order and simplify the absolute value calculation
        Arrays.sort(nodes, (a, b) -> {
            if (a.second == b.second) {
                return a.first - b.first;
            }

            return a.second - b.second;
        });

        // Process each node in sorted order
        for (var node : nodes) {

            // Case 1: Current node has larger nums1 value than the pair candidate
            // For this case: |nums1[i] - nums1[j]| + |nums2[i] - nums2[j]|
            //              = (node.first - other.first) + (node.second - other.second)
            //              = (node.first + node.second) - (other.first + other.second)
            int s1 = node.first + node.second;

            // Query segment tree for nodes with nums1 values smaller than current
            Node query = smaller.query(1, 0, n, 0, node.first);

            // If a valid candidate was found
            if (query.ind != len + 1) {
                int s2 = query.first;  // This stores the sum (nums1 + nums2) for the candidate
                int d = s1 - s2;       // Calculate the distance
                int min = Math.min(node.ind, query.ind);
                int max = Math.max(query.ind, node.ind);

                // Update best pair if this is better
                if (d < diff) {
                    diff = d;
                    i1 = Math.min(node.ind, query.ind);
                    i2 = Math.max(node.ind, query.ind);
                } else if (d == diff) {
                    // If distance is same, prefer lexicographically smaller pair
                    if (min < i1) {
                        i1 = min;
                        i2 = max;
                    } else if (min == i1 && max < i2) {
                        i2 = max;
                    }
                }
            }

            // Update segment tree with current node's sum
            smaller.update(1, node.ind, s1, node.first, 0, n);

            // Case 2: Current node has smaller nums1 value than the pair candidate
            // For this case: |nums1[i] - nums1[j]| + |nums2[i] - nums2[j]|
            //              = (other.first - node.first) + (node.second - other.second)
            //              = (node.second - node.first) - (other.second - other.first)
            s1 = node.second - node.first;

            // Query segment tree for nodes with nums1 values larger than or equal to current
            query = larger.query(1, 0, n, node.first, n);

            if (query.ind != len + 1) {
                int s2 = query.first;  // This stores the difference (nums2 - nums1) for the candidate
                int d = s1 - s2;       // Calculate the distance
                int min = Math.min(node.ind, query.ind);
                int max = Math.max(query.ind, node.ind);

                // Update best pair if this is better
                if (d < diff) {
                    diff = d;
                    i1 = Math.min(node.ind, query.ind);
                    i2 = Math.max(node.ind, query.ind);
                } else if (d == diff) {
                    // If distance is same, prefer lexicographically smaller pair
                    if (min < i1) {
                        i1 = min;
                        i2 = max;
                    } else if (min == i1 && max < i2) {
                        i2 = max;
                    }
                }
            }

            // Update segment tree with current node's difference
            larger.update(1, node.ind, s1, node.first, 0, n);
        }

        return new int[]{i1, i2};

    }

    /**
     * Node class to store a pair of values along with their original index. Used both for storing input data and for
     * segment tree nodes.
     */
    class Node {

        int first;   // Value from nums1 array, or computed sum/difference in segment tree
        int second;  // Value from nums2 array
        int ind;     // Original index in the input arrays

        /**
         * Constructor for Node.
         *
         * @param first Value from nums1 or computed value
         * @param second Value from nums2
         * @param ind Original index
         */
        public Node(int first, int second, int ind) {

            this.first = first;
            this.second = second;
            this.ind = ind;
        }
    }

    /**
     * Segment Tree implementation for range maximum queries. Stores nodes with maximum 'first' value in each range. In
     * case of ties, prefers the node with smaller index.
     */
    class SegmentTree {

        Node tree[];  // Array representation of segment tree
        int n;        // Size of the input array

        /**
         * Constructor to initialize the segment tree.
         *
         * @param n Size of the input array
         */
        public SegmentTree(int n) {

            this.n = n;
            tree = new Node[4 * n];  // Segment tree needs 4*n space

            // Initialize all nodes with sentinel values
            // -3*n ensures these are smaller than any valid sum/difference
            // n+1 as index indicates an invalid/uninitialized node
            for (int i = 0; i < tree.length; i++) {
                tree[i] = new Node(-3 * n, -3 * n, n + 1);
            }

        }

        /**
         * Query the segment tree for the node with maximum 'first' value in a range.
         *
         * @param treeInd Current node index in the segment tree
         * @param start Start of the range represented by current node
         * @param end End of the range represented by current node
         * @param qs Query range start
         * @param qe Query range end
         * @return Node with maximum 'first' value in the query range
         */
        public Node query(int treeInd, int start, int end, int qs, int qe) {

            // Current range is completely within query range
            if (qs <= start && end <= qe) {
                return tree[treeInd];
            }

            // Current range is completely outside query range
            if (qe < start || qs > end) {
                return new Node(-3 * n, -3 * n, n + 1);
            }

            // Partial overlap - query both children
            int mid = (start + end) / 2;

            Node left = query(2 * treeInd, start, mid, qs, qe);
            Node right = query(2 * treeInd + 1, mid + 1, end, qs, qe);

            // Return node with larger 'first' value
            if (left.first > right.first) {
                return left;
            } else if (left.first < right.first) {
                return right;
            } else {
                // If 'first' values are equal, prefer smaller index
                if (left.ind < right.ind) {
                    return left;
                } else {
                    return right;
                }
            }
        }

        /**
         * Update the segment tree at a specific index.
         *
         * @param treeInd Current node index in the segment tree
         * @param index Original array index to update
         * @param val New value to store
         * @param ind Position in nums1 array (used for range identification)
         * @param start Start of the range represented by current node
         * @param end End of the range represented by current node
         */
        public void update(int treeInd, int index, int val, int ind, int start, int end) {

            // Current range doesn't contain the update index
            if (end < ind || start > ind) {
                return;
            }

            // Reached the leaf node corresponding to the update index
            if (start == end) {
                Node node = tree[treeInd];

                // Update if new value is larger, or same value but smaller index
                if (node.first < val) {
                    node.first = val;
                    node.ind = index;
                } else if (node.first == val && node.ind > index) {
                    node.ind = index;
                }
                return;
            }

            // Recursively update the appropriate child
            int mid = (start + end) / 2;

            if (ind <= mid) {
                update(2 * treeInd, index, val, ind, start, mid);
            } else {
                update(2 * treeInd + 1, index, val, ind, mid + 1, end);
            }

            // Update current node based on children
            Node left = tree[2 * treeInd];
            Node right = tree[2 * treeInd + 1];

            // Store the child with larger 'first' value
            if (left.first > right.first) {
                tree[treeInd] = left;
            } else if (left.first < right.first) {
                tree[treeInd] = right;
            } else {
                // If 'first' values are equal, prefer smaller index
                if (left.ind < right.ind) {
                    tree[treeInd] = left;
                } else {
                    tree[treeInd] = right;
                }
            }
        }

    }
}