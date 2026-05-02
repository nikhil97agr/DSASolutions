package Leetcode;

import java.util.Arrays;

/**
 * Solution for maximizing alternating sum with distance constraint.
 *
 * Problem: Given array nums and distance k, find maximum alternating sum where: - Select a subsequence where adjacent
 * selected elements are at least k positions apart - Alternating sum: a₁ - a₂ + a₃ - a₄ + ... (odd positions add, even
 * positions subtract) - Can also choose subsequences with pattern: -a₁ + a₂ - a₃ + ... (start with subtraction)
 *
 * Example: nums = [5, 2, 8, 3], k = 2 - Can select indices [0, 2]: sum = 5 - 8 = -3 (or 8 - 5 = 3) - Can select indices
 * [0, 3]: sum = 5 - 3 = 2 (or 3 - 5 = -2) - Can select single element: max = 8 - Maximum = 8
 *
 * Key insight: DP with coordinate compression + Segment tree
 *
 * Observation 1: Two alternating patterns - "Down" pattern (d): last element is subtracted (...+ x - y) - "Up" pattern
 * (u): last element is added (...- x + y) - We track best sum ending with each pattern
 *
 * Observation 2: Transition rules - To extend "down" (add element with subtraction): * Need previous "up" with smaller
 * value (to maintain alternation) * d[val] = max(u[smaller_values]) + val - To extend "up" (add element with addition):
 * * Need previous "down" with larger value * u[val] = max(d[larger_values]) + val
 *
 * Observation 3: Distance constraint - Only consider previous elements at least k positions away - Use sliding window:
 * add elements to segment tree when they're k positions behind
 *
 * Observation 4: Coordinate compression - Compress values to small range for segment tree efficiency - Map original
 * values to indices in sorted unique array
 *
 * Data structures: - d[val]: best "down" sum ending with value val - u[val]: best "up" sum ending with value val -
 * down: segment tree tracking max d[] values - up: segment tree tracking max u[] values
 *
 * Algorithm: 1. Compress coordinates (remove duplicates, sort) 2. Process elements left to right 3. Add elements k
 * positions back to segment trees 4. For current element: - Query smaller values for "up" extension - Query larger
 * values for "down" extension - Update DP arrays and answer
 *
 * Time Complexity: O(n log n) for sorting and segment tree operations Space Complexity: O(n) for arrays and segment
 * trees
 */
public class MaximumAlternatingSubseqSum {

    /**
     * Finds maximum alternating sum with distance constraint.
     *
     * @param nums Array of integers
     * @param k Minimum distance between selected elements
     * @return Maximum alternating sum achievable
     */
    public long maxAlternatingSum(int[] nums, int k) {

        int n = nums.length;

        // ================================================================
        // STEP 1: Coordinate compression
        // ================================================================
        // Create sorted array of unique values for efficient indexing
        int clone[] = nums.clone();
        Arrays.sort(clone);

        // Remove duplicates in-place
        int len = 0;
        for (int i = 0; i < n; i++) {
            if (len == 0 || clone[len - 1] != clone[i]) {
                clone[len++] = clone[i];
            }
        }

        // ================================================================
        // STEP 2: Initialize DP arrays
        // ================================================================
        long d[] = new long[len];  // d[i] = best "down" sum ending with clone[i]
        long u[] = new long[len];  // u[i] = best "up" sum ending with clone[i]

        // ================================================================
        // STEP 3: Initialize segment trees
        // ================================================================
        SegmentTree down = new SegmentTree(len);  // Tracks max d[] values by position
        SegmentTree up = new SegmentTree(len);    // Tracks max u[] values by position
        long ans = 0;

        // ================================================================
        // STEP 4: Process elements with sliding window
        // ================================================================
        for (int i = 0; i < n; i++) {
            int j = i - k;  // Position that becomes available (k positions behind)

            // ============================================================
            // Add element at position j to segment trees (if valid)
            // ============================================================
            if (j >= 0) {
                // Find compressed index of nums[j]
                int ind = Arrays.binarySearch(clone, 0, len, nums[j]);

                // Add to segment trees: track best values for this element
                // max(d[ind], nums[j]): consider both ending "down" sum and single element
                down.add(1, ind, max(d[ind], nums[j]), 0, len - 1);
                up.add(1, ind, max(u[ind], nums[j]), 0, len - 1);
            }

            // ============================================================
            // Process current element nums[i]
            // ============================================================
            int ind = Arrays.binarySearch(clone, 0, len, nums[i]);

            // ========================================================
            // Extend "up" pattern: query smaller values from "down"
            // ========================================================
            // To add nums[i] with +, we need previous element with - that's larger
            // Query down[0..ind-1] for best "down" sum with smaller values
            long a = down.query(1, 0, ind - 1, 0, len - 1);
            if (a != -1) {
                // Update u[ind]: best "up" sum ending with nums[i]
                u[ind] = max(u[ind], a + nums[i]);
            }

            // ========================================================
            // Extend "down" pattern: query larger values from "up"
            // ========================================================
            // To add nums[i] with -, we need previous element with + that's smaller
            // Query up[ind+1..len-1] for best "up" sum with larger values
            long b = up.query(1, ind + 1, len - 1, 0, len - 1);
            if (b != -1) {
                // Update d[ind]: best "down" sum ending with nums[i]
                d[ind] = max(d[ind], b + nums[i]);
            }

            // ========================================================
            // Update answer with best value so far
            // ========================================================
            // Consider: down pattern, up pattern, or single element
            ans = max(ans, d[ind], u[ind], nums[i]);
        }

        return ans;

    }


    /**
     * Helper method to find maximum among variable number of arguments.
     *
     * @param arr Variable arguments of long values
     * @return Maximum value among all arguments
     */
    private long max(long... arr) {

        long ans = -1;
        for (long x : arr) {
            ans = Math.max(ans, x);
        }

        return ans;
    }


    /**
     * Segment Tree for range maximum queries and point updates.
     *
     * Tracks maximum values in ranges to efficiently query best DP values for elements with values in specific ranges.
     *
     * Operations: - add(i, val): Update position i to max(tree[i], val) - query(l, r): Get maximum value in range [l,
     * r]
     */
    class SegmentTree {

        long tree[];  // Tree array storing maximum values

        /**
         * Constructor to initialize segment tree.
         *
         * @param n Size of the coordinate-compressed array
         */
        public SegmentTree(int n) {

            tree = new long[n * 4];  // Standard segment tree size
            Arrays.fill(tree, -1);    // -1 indicates no value yet
        }

        /**
         * Updates position i to maximum of current value and val.
         *
         * Point update that maintains maximum value seen at position.
         *
         * @param ind Current node index in segment tree
         * @param i Position to update
         * @param val New value to consider
         * @param l Left boundary of current segment
         * @param r Right boundary of current segment
         */
        public void add(int ind, int i, long val, int l, int r) {

            // Position i is outside current segment
            if (i < l || r < i) {
                return;
            }

            // Leaf node: update with maximum
            if (l == r) {
                tree[ind] = Math.max(tree[ind], val);
                return;
            }

            int mid = (l + r) / 2;

            // Recursively update appropriate child
            add(2 * ind, i, val, l, mid);
            add(2 * ind + 1, i, val, mid + 1, r);

            // Update current node: max of both children
            tree[ind] = Math.max(tree[2 * ind], tree[2 * ind + 1]);
        }

        /**
         * Queries maximum value in range [ql, qr].
         *
         * Returns the maximum DP value among elements with compressed indices in the query range.
         *
         * @param ind Current node index in segment tree
         * @param ql Query left boundary
         * @param qr Query right boundary
         * @param l Left boundary of current segment
         * @param r Right boundary of current segment
         * @return Maximum value in range [ql, qr], or -1 if no valid values
         */
        public long query(int ind, int ql, int qr, int l, int r) {

            // Current segment is outside query range
            if (qr < l || r < ql) {
                return -1;
            }

            // Current segment is fully within query range
            if (ql <= l && r <= qr) {
                return tree[ind];
            }

            int mid = (l + r) / 2;

            // Query both children and return maximum
            return Math.max(query(2 * ind, ql, qr, l, mid), query(2 * ind + 1, ql, qr, mid + 1, r));
        }
    }

}