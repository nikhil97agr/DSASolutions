package Leetcode;

// Problem Link: https://leetcode.com/problems/maximum-total-subarray-value-ii

import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * Solution - Maximum Total Value by Selecting k Subarrays
 *
 * PROBLEM STATEMENT: Given an array nums and integer k, select k subarrays (possibly overlapping in selection) to
 * maximize the total value, where the value of a subarray is |max - min| within that subarray.
 *
 * APPROACH - GREEDY WITH PRIORITY QUEUE:
 *
 * The challenge is to efficiently find the k subarrays with the highest |max - min| values. Direct enumeration of all
 * subarrays would be O(n^2), which is too slow.
 *
 * KEY INSIGHT: Use a greedy approach with a max-heap (priority queue): 1. Start with the full array [0, n-1] 2. Always
 * pick the subarray with the largest |max - min| value 3. For each picked subarray [l, r], generate two child
 * subarrays: [l+1, r] and [l, r-1] 4. Use segment tree for O(log n) range min/max queries 5. Use visited set to avoid
 * processing the same subarray twice
 *
 * WHY IT WORKS: - The subarray with max |max - min| in the entire array has the global extremes - Shrinking from either
 * end can only decrease or maintain the difference - By exploring both shrink options, we ensure we find all locally
 * optimal subarrays - Priority queue ensures we always pick the best available subarray
 *
 * ALGORITHM STEPS: 1. Build segment tree for O(log n) range min/max queries 2. Initialize priority queue with full
 * array 3. For k iterations: a. Extract subarray with maximum |max - min| b. Add its value to answer c. Generate child
 * subarrays (shrink from left/right) d. Add unvisited children to priority queue
 *
 * TIME COMPLEXITY: O(k log k + n log n) - Building segment tree: O(n log n) - k iterations, each with O(log k) priority
 * queue operations - Each iteration does O(log n) segment tree queries
 *
 * SPACE COMPLEXITY: O(n + k) - Segment tree: O(4n) - Priority queue: O(k) at most k elements - Visited set: O(k)
 */
public class MaximumTotalSubarrayValueII {

    /**
     * Finds maximum total value by selecting k subarrays with highest |max - min|.
     *
     * @param nums input array of integers
     * @param k number of subarrays to select
     * @return maximum total value (sum of |max - min| for k selected subarrays)
     */
    public long maxTotalValue(int[] nums, int k) {

        long totalValue = 0;

        // MAX-HEAP: Priority queue ordered by difference (|max - min|) in descending order
        // We always want to process the subarray with the largest difference first
        var maxHeap = new PriorityQueue<SubarrayNode>((a, b) -> Long.compare(b.difference, a.difference));

        int n = nums.length;

        // SEGMENT TREE: For efficient O(log n) range min/max queries
        var segmentTree = new SegmentTree(nums);

        // Start with the full array [0, n-1]
        // Initial difference is 0 (will be computed when processed)
        maxHeap.offer(new SubarrayNode(0, n - 1, 0));

        // VISITED SET: Track which subarrays we've already processed to avoid duplicates
        // Using string key "start:end" for simplicity
        var visitedSubarrays = new HashSet<String>();
        visitedSubarrays.add(createKey(0, n - 1));
        // MAIN LOOP: Select k subarrays greedily
        while (!maxHeap.isEmpty() && k-- > 0) {
            // STEP 1: Extract the subarray with maximum |max - min|
            var currentSubarray = maxHeap.poll();

            int left = currentSubarray.start;
            int right = currentSubarray.end;

            // STEP 2: Calculate the actual min and max for this subarray
            long minValue = segmentTree.min(1, left, right, 0, n - 1);
            long maxValue = segmentTree.max(1, left, right, 0, n - 1);

            // STEP 3: Add this subarray's value to total
            totalValue += Math.abs(minValue - maxValue);

            // STEP 4: Generate child subarrays by shrinking from left
            // Child subarray: [left + 1, right] (remove leftmost element)
            if (left + 1 <= right) {
                String childKey = createKey(left + 1, right);

                // Only process if we haven't visited this subarray before
                if (!visitedSubarrays.contains(childKey)) {
                    // Calculate the difference for this child subarray
                    minValue = segmentTree.min(1, left + 1, right, 0, n - 1);
                    maxValue = segmentTree.max(1, left + 1, right, 0, n - 1);
                    long childDifference = Math.abs(minValue - maxValue);

                    // Add to priority queue with its actual difference
                    maxHeap.offer(new SubarrayNode(left + 1, right, childDifference));
                    visitedSubarrays.add(childKey);
                }
            }

            // STEP 5: Generate child subarrays by shrinking from right
            // Child subarray: [left, right - 1] (remove rightmost element)
            if (left <= right - 1) {
                String childKey = createKey(left, right - 1);

                // Only process if we haven't visited this subarray before
                if (!visitedSubarrays.contains(childKey)) {
                    // Calculate the difference for this child subarray
                    minValue = segmentTree.min(1, left, right - 1, 0, n - 1);
                    maxValue = segmentTree.max(1, left, right - 1, 0, n - 1);
                    long childDifference = Math.abs(minValue - maxValue);

                    // Add to priority queue with its actual difference
                    maxHeap.offer(new SubarrayNode(left, right - 1, childDifference));
                    visitedSubarrays.add(childKey);
                }
            }
        }

        return totalValue;


    }

    /**
     * Creates a unique string key for a subarray range.
     *
     * Used to track visited subarrays in the HashSet. Format: "start:end"
     *
     * @param start left boundary of subarray (inclusive)
     * @param end right boundary of subarray (inclusive)
     * @return string key representing the subarray range
     */
    private String createKey(int start, int end) {

        return String.format("%d:%d", start, end);
    }

    /**
     * Segment Tree for efficient Range Minimum and Maximum Queries (RMQ).
     *
     * This data structure supports: - Range minimum query: Find minimum value in range [l, r] in O(log n) - Range
     * maximum query: Find maximum value in range [l, r] in O(log n)
     *
     * Structure: - Each node stores both min and max for its range - Tree array size is 4n (safe upper bound for
     * complete binary tree) - Node at index i has children at 2i (left) and 2i+1 (right)
     *
     * TIME COMPLEXITY: - Build: O(n log n) - Query (min/max): O(log n)
     *
     * SPACE COMPLEXITY: O(n) for both min and max arrays
     */
    static class SegmentTree {

        int[] minTree;  // minTree[i] = minimum value in the range covered by node i
        int[] maxTree;  // maxTree[i] = maximum value in the range covered by node i

        /**
         * Constructor - Builds the segment tree from input array.
         *
         * @param nums input array to build segment tree from
         */
        public SegmentTree(int[] nums) {

            int n = nums.length;

            // Allocate 4n space (safe upper bound for segment tree)
            minTree = new int[n * 4];
            maxTree = new int[n * 4];

            // Initialize with sentinel values
            Arrays.fill(minTree, Integer.MAX_VALUE);
            Arrays.fill(maxTree, Integer.MIN_VALUE);

            // Insert each element into the segment tree
            for (int i = 0; i < n; i++) {
                insert(1, i, nums[i], 0, n - 1);
            }
        }

        /**
         * Range minimum query - finds minimum value in range [queryLeft, queryRight].
         *
         * @param nodeIndex current node index in segment tree
         * @param queryLeft left boundary of query range (inclusive)
         * @param queryRight right boundary of query range (inclusive)
         * @param rangeLeft left boundary of current node's range (inclusive)
         * @param rangeRight right boundary of current node's range (inclusive)
         * @return minimum value in range [queryLeft, queryRight]
         */
        public int min(int nodeIndex, int queryLeft, int queryRight, int rangeLeft, int rangeRight) {

            // BASE CASE 1: Invalid range or no overlap with query range
            if (queryLeft > queryRight || queryRight < rangeLeft || rangeRight < queryLeft) {
                return Integer.MAX_VALUE;  // Return max value (won't affect min calculation)
            }

            // BASE CASE 2: Current node's range is completely within query range
            if (queryLeft <= rangeLeft && rangeRight <= queryRight) {
                return minTree[nodeIndex];
            }

            // RECURSIVE CASE: Partial overlap, query both children
            int mid = (rangeLeft + rangeRight) / 2;

            int leftMin = min(2 * nodeIndex, queryLeft, queryRight, rangeLeft, mid);
            int rightMin = min(2 * nodeIndex + 1, queryLeft, queryRight, mid + 1, rangeRight);

            return Math.min(leftMin, rightMin);
        }

        /**
         * Range maximum query - finds maximum value in range [queryLeft, queryRight].
         *
         * @param nodeIndex current node index in segment tree
         * @param queryLeft left boundary of query range (inclusive)
         * @param queryRight right boundary of query range (inclusive)
         * @param rangeLeft left boundary of current node's range (inclusive)
         * @param rangeRight right boundary of current node's range (inclusive)
         * @return maximum value in range [queryLeft, queryRight]
         */
        public int max(int nodeIndex, int queryLeft, int queryRight, int rangeLeft, int rangeRight) {

            // BASE CASE 1: Invalid range or no overlap with query range
            if (queryLeft > queryRight || queryRight < rangeLeft || rangeRight < queryLeft) {
                return Integer.MIN_VALUE;  // Return min value (won't affect max calculation)
            }

            // BASE CASE 2: Current node's range is completely within query range
            if (queryLeft <= rangeLeft && rangeRight <= queryRight) {
                return maxTree[nodeIndex];
            }

            // RECURSIVE CASE: Partial overlap, query both children
            int mid = (rangeLeft + rangeRight) / 2;

            int leftMax = max(2 * nodeIndex, queryLeft, queryRight, rangeLeft, mid);
            int rightMax = max(2 * nodeIndex + 1, queryLeft, queryRight, mid + 1, rangeRight);

            return Math.max(leftMax, rightMax);
        }

        /**
         * Insert/update a value at a specific index in the segment tree.
         *
         * This method updates both min and max trees for the given position.
         *
         * @param nodeIndex current node index in segment tree
         * @param targetIndex index where value should be inserted
         * @param value value to insert at targetIndex
         * @param rangeLeft left boundary of current node's range
         * @param rangeRight right boundary of current node's range
         */
        private void insert(int nodeIndex, int targetIndex, int value, int rangeLeft, int rangeRight) {

            // BASE CASE 1: Target index is outside current range
            if (targetIndex < rangeLeft || targetIndex > rangeRight) {
                return;
            }

            // BASE CASE 2: Reached leaf node (single element range)
            if (rangeLeft == rangeRight) {
                minTree[nodeIndex] = value;
                maxTree[nodeIndex] = value;
                return;
            }

            // RECURSIVE CASE: Update children and propagate up
            int mid = (rangeLeft + rangeRight) / 2;

            // Update left subtree (covers range [rangeLeft, mid])
            insert(2 * nodeIndex, targetIndex, value, rangeLeft, mid);

            // Update right subtree (covers range [mid + 1, rangeRight])
            insert(2 * nodeIndex + 1, targetIndex, value, mid + 1, rangeRight);

            // Update current node with min/max from children
            minTree[nodeIndex] = Math.min(minTree[2 * nodeIndex], minTree[2 * nodeIndex + 1]);
            maxTree[nodeIndex] = Math.max(maxTree[2 * nodeIndex], maxTree[2 * nodeIndex + 1]);
        }
    }

    /**
     * Node class representing a subarray candidate in the priority queue.
     *
     * Each node stores: - start: left boundary of the subarray (inclusive) - end: right boundary of the subarray
     * (inclusive) - difference: |max - min| value for this subarray (used for priority queue ordering)
     *
     * Priority queue ordering: Nodes with higher difference are prioritized.
     */
    class SubarrayNode {

        int start;        // Left boundary of subarray (inclusive)
        int end;          // Right boundary of subarray (inclusive)
        long difference;  // |max - min| for this subarray

        /**
         * Constructor for SubarrayNode.
         *
         * @param start left boundary of subarray
         * @param end right boundary of subarray
         * @param difference |max - min| value for this subarray
         */
        public SubarrayNode(int start, int end, long difference) {

            this.start = start;
            this.end = end;
            this.difference = difference;
        }
    }
}

/*
 * EXAMPLE WALKTHROUGH:
 *
 * Input: nums = [1, 5, 2, 8, 3], k = 3
 *
 * Initial state:
 * - Segment tree built with nums
 * - Priority queue: [(0, 4, diff=0)]  // Will compute actual diff when processed
 *
 * Iteration 1:
 * - Pop (0, 4): min=1, max=8, diff=7 → totalValue = 7
 * - Add children:
 *   - (1, 4): min=2, max=8, diff=6
 *   - (0, 3): min=1, max=8, diff=7
 * - Priority queue: [(0, 3, 7), (1, 4, 6)]
 *
 * Iteration 2:
 * - Pop (0, 3): min=1, max=8, diff=7 → totalValue = 14
 * - Add children:
 *   - (1, 3): min=2, max=8, diff=6 (already visited from (1,4) shrink? No, different)
 *   - (0, 2): min=1, max=5, diff=4
 * - Priority queue: [(1, 4, 6), (1, 3, 6), (0, 2, 4)]
 *
 * Iteration 3:
 * - Pop (1, 4, 6): min=2, max=8, diff=6 → totalValue = 20
 * - k iterations complete
 *
 * Result: 20
 *
 * WHY THIS GREEDY APPROACH WORKS:
 * - We always select the subarray with maximum |max - min|
 * - Each selected subarray contributes its full value
 * - By exploring both shrink directions, we ensure we find the best k subarrays
 * - The visited set prevents processing the same subarray twice
 */