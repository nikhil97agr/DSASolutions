package Leetcode;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

//Problem Link: https://leetcode.com/problems/minimum-operations-to-equalize-subarrays/

/**
 * Solution for finding minimum operations to equalize subarrays.
 *
 * Given an array and multiple range queries, for each query [l, r], find the minimum number of operations to make all
 * elements in nums[l..r] equal. Each operation increases or decreases an element by k.
 *
 * Key insights: 1. For a subarray to be equalizable, all elements must have the same remainder mod k 2. The optimal
 * target value is the median (minimizes total distance) 3. Uses Mo's Algorithm to efficiently handle multiple range
 * queries 4. Maintains two balanced sets (like two heaps) to track median efficiently
 *
 * Time Complexity: O((n + q) * sqrt(n) * log(n)) Space Complexity: O(n)
 */
public class MinimumOperationToEqualizeSubarrays {

    // Mo's Algorithm state: current window [left, right]
    int left = 0;
    int right = -1;
    long sum = 0;

    // Two-set median tracking (similar to two heaps approach)
    int leftCnt = 0;      // Count of elements in left half (smaller elements)
    int rightCnt = 0;     // Count of elements in right half (larger elements)
    TreeMap<Long, Long> leftMap = new TreeMap<>();   // Left half: smaller values
    TreeMap<Long, Long> rightMap = new TreeMap<>();  // Right half: larger values
    long leftSum = 0;     // Sum of all elements in left half
    long rightSum = 0;    // Sum of all elements in right half

    /**
     * Processes range queries to find minimum operations for each.
     *
     * @param nums Input array
     * @param k Operation step size (can add/subtract multiples of k)
     * @param queries Array of queries where queries[i] = [l, r]
     * @return Array of results where result[i] is minimum operations for queries[i], or -1 if impossible
     */
    public long[] minOperations(int[] nums, int k, int[][] queries) {

        int n = nums.length;
        int q = queries.length;
        long res[] = new long[q];

        // Mo's Algorithm: block size is sqrt(n) for optimal time complexity
        int block = (int) Math.sqrt(n);

        // Create query objects with original indices to track answers
        Query qs[] = IntStream.range(0, q).boxed().map(x -> new Query(queries[x][0], queries[x][1], x))
                .toArray(Query[]::new);

        // Sort queries using Mo's Algorithm comparator:
        // 1. Primary: group by left endpoint block
        // 2. Secondary: sort by right endpoint within same block
        Arrays.sort(qs, (q1, q2) -> {
            int b1 = q1.l / block;
            int b2 = q2.l / block;

            if (b1 != b2) {
                return b1 - b2;  // Different blocks: sort by block number
            }

            return q1.r - q2.r;  // Same block: sort by right endpoint
        });

        // Segment tree to check if all remainders are equal in a range
        ReminderSegmenetTree tree = new ReminderSegmenetTree(n);
        for (int i = 0; i < n; i++) {
            tree.add(1, i, nums[i] % k, 0, n - 1);
        }

        // Process queries in sorted order (Mo's Algorithm)
        for (Query quer : qs) {
            int l = quer.l;
            int r = quer.r;
            int ind = quer.i;  // Original query index

            // Check if all elements in range have same remainder mod k
            int temp[] = tree.search(1, l, r, 0, n - 1);  // Returns [min, max] of remainders
            if (temp[0] != temp[1]) {
                // Different remainders -> impossible to equalize
                res[ind] = -1;
                continue;
            }

            int c = r - l + 1;  // Size of current query range

            // Expand window to the left
            while (left > l) {
                left--;
                add(nums[left], k);
            }

            // Expand window to the right
            while (right < r) {
                right++;
                add(nums[right], k);
            }

            // Shrink window from the left
            while (left < l) {
                remove(nums[left], k);
                left++;
            }

            // Shrink window from the right
            while (right > r) {
                remove(nums[right], k);
                right--;
            }

            // Calculate minimum operations using median
            // For even-sized arrays, we try both middle elements
            // For odd-sized arrays, we use the median
            if (c % 2 == 0) {
                // Even size: try both middle values and take minimum
                res[ind] = Math.min(getCnt(leftMap.lastKey()), getCnt(rightMap.firstKey()));
            } else {
                // Odd size: median is the first element in right half
                res[ind] = getCnt(rightMap.firstKey());
            }

        }

        return res;
    }

    /**
     * Calculates the total cost to move all elements to target value.
     *
     * Uses the formula: - Left half cost: each element needs (val - element) operations - Right half cost: each element
     * needs (element - val) operations
     *
     * @param val Target value (nums[i] / k) to equalize to
     * @return Total number of operations needed
     */
    private long getCnt(long val) {

        // Cost for left half: sum of (val - each_element)
        // = leftCnt * val - sum(all elements in left)
        long left = leftCnt * val - leftSum;

        // Cost for right half: sum of (each_element - val)
        // = sum(all elements in right) - rightCnt * val
        long right = rightSum - rightCnt * val;

        return left + right;
    }

    /**
     * Adds an element to the current window. Maintains the two-set structure where leftMap has smaller values and
     * rightMap has larger values (median is at boundary).
     *
     * @param val Element value to add
     * @param k Operation step size
     */
    private void add(int val, int k) {

        // Transform value: we work with div = nums[i] / k
        // Since all nums have same remainder, we only care about quotient
        long div = val / k;

        // Decide which half to add to
        if (!leftMap.isEmpty() && div < leftMap.lastKey()) {
            // Smaller than largest in left half -> goes to left
            leftMap.merge(div, 1l, Long::sum);
            leftCnt++;
            leftSum += div;
        } else {
            // Larger or equal -> goes to right
            rightMap.merge(div, 1l, Long::sum);
            rightCnt++;
            rightSum += div;
        }

        // Rebalance to maintain median property
        balance();
    }

    /**
     * Helper to remove one occurrence of a value from a map.
     *
     * @param map TreeMap to remove from
     * @param val Value to remove
     */
    private void remove(Map<Long, Long> map, long val) {

        map.merge(val, -1l, Long::sum);  // Decrement count
        if (map.get(val) == 0) {
            map.remove(val);  // Remove key if count becomes 0
        }

    }

    /**
     * Balances the two sets to maintain the median property. Ensures: - leftCnt <= rightCnt <= leftCnt + 1 - All values
     * in leftMap <= all values in rightMap
     *
     * This keeps the median at the boundary between the two sets.
     */
    private void balance() {

        // If left has more elements than right, move largest from left to right
        while (leftCnt > rightCnt) {
            long first = leftMap.lastKey();
            leftCnt--;
            leftSum -= first;
            remove(leftMap, first);

            rightCnt++;
            rightSum += first;
            rightMap.merge(first, 1l, Long::sum);
        }

        // If right has more than 1 extra element, move smallest from right to left
        while (rightCnt > leftCnt + 1) {
            long x = rightMap.firstKey();
            rightCnt--;
            rightSum -= x;
            remove(rightMap, x);

            leftCnt++;
            leftSum += x;
            leftMap.merge(x, 1L, Long::sum);
        }
    }

    /**
     * Removes an element from the current window.
     *
     * @param val Element value to remove
     * @param k Operation step size
     */
    private void remove(int val, int k) {

        long div = val / k;

        // Find which half contains the element and remove it
        if (rightMap.containsKey(div)) {
            remove(rightMap, div);
            rightCnt--;
            rightSum -= div;
        } else {
            remove(leftMap, div);
            leftCnt--;
            leftSum -= div;
        }

        // Rebalance after removal
        balance();


    }

    /**
     * Helper class to store query information with original index. Used for Mo's Algorithm to process queries in
     * optimal order.
     */
    class Query {

        int l;  // Left endpoint of query range
        int r;  // Right endpoint of query range
        int i;  // Original query index (to place answer in correct position)

        /**
         * Constructor for Query.
         *
         * @param l Left endpoint
         * @param r Right endpoint
         * @param i Original index
         */
        public Query(int l, int r, int i) {

            this.l = l;
            this.i = i;
            this.r = r;
        }
    }

    /**
     * Segment Tree to efficiently query min and max of remainders (nums[i] % k) in a range. Used to check if all
     * elements in a range have the same remainder mod k.
     */
    class ReminderSegmenetTree {

        int min[];  // Min remainder in each segment
        int max[];  // Max remainder in each segment

        /**
         * Constructor to initialize the segment tree.
         *
         * @param n Size of the input array
         */
        public ReminderSegmenetTree(int n) {

            min = new int[n * 4];
            Arrays.fill(min, Integer.MAX_VALUE);  // Initialize to max
            max = new int[n * 4];                 // Initialize to 0
        }

        /**
         * Updates a position in the segment tree with a remainder value.
         *
         * @param treeInd Current node index in segment tree
         * @param ind Array index to update
         * @param val Remainder value (nums[ind] % k)
         * @param start Start of range represented by current node
         * @param end End of range represented by current node
         */
        public void add(int treeInd, int ind, int val, int start, int end) {

            // Index outside current range
            if (ind < start || ind > end) {
                return;
            }

            // Reached leaf node
            if (start == end) {
                min[treeInd] = val;
                max[treeInd] = val;
                return;
            }

            // Recursively update children
            int mid = (start + end) / 2;
            add(2 * treeInd, ind, val, start, mid);
            add(2 * treeInd + 1, ind, val, mid + 1, end);

            // Update current node from children
            min[treeInd] = Math.min(min[2 * treeInd], min[2 * treeInd + 1]);
            max[treeInd] = Math.max(max[2 * treeInd], max[2 * treeInd + 1]);
        }

        /**
         * Queries the segment tree for min and max remainder in a range.
         *
         * @param treeInd Current node index in segment tree
         * @param qs Query range start
         * @param qe Query range end
         * @param start Start of range represented by current node
         * @param end End of range represented by current node
         * @return Array [min, max] of remainders in query range
         */
        public int[] search(int treeInd, int qs, int qe, int start, int end) {

            // Range completely outside query
            if (qe < start || end < qs) {
                return new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE};
            }

            // Range completely inside query
            if (qs <= start && end <= qe) {
                return new int[]{min[treeInd], max[treeInd]};
            }

            // Partial overlap - query both children
            int mid = (start + end) / 2;
            int left[] = search(2 * treeInd, qs, qe, start, mid);
            int right[] = search(2 * treeInd + 1, qs, qe, mid + 1, end);

            // Return combined min and max
            return new int[]{Math.min(left[0], right[0]), Math.max(left[1], right[1])};
        }
    }
}