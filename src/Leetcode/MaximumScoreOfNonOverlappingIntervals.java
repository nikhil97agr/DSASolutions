package Leetcode;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

//Problem Link: https://leetcode.com/problems/maximum-score-of-non-overlapping-intervals/

/**
 * Solution for selecting up to 4 non-overlapping weighted intervals with maximum total weight.
 *
 * Given a list of intervals where each interval has [left, right, weight], select at most 4 non-overlapping intervals
 * to maximize the total weight. If there are ties, return the lexicographically smallest set of indices.
 *
 * Algorithm: 1. Sort intervals by left endpoint (then by right endpoint for consistency) 2. Use dynamic programming:
 * dp[i][cnt] = best result using intervals from index i onwards with cnt slots remaining 3. For each interval, choose
 * to either skip it or take it 4. If taking it, find the next non-overlapping interval using binary search 5. Handle
 * tie-breaking by comparing indices lexicographically
 *
 * Time Complexity: O(n log n) for sorting + O(n * 4) for DP = O(n log n) Space Complexity: O(n * 4) for DP table
 */
public class MaximumScoreOfNonOverlappingIntervals {

    Result dp[][];  // dp[i][cnt] = best result starting from interval i with cnt slots left
    Node node[];    // Sorted array of intervals

    /**
     * Finds indices of up to 4 non-overlapping intervals with maximum total weight.
     *
     * @param intervals List of intervals where each is [left, right, weight]
     * @return Array of original indices of selected intervals
     */
    public int[] maximumWeight(List<List<Integer>> intervals) {

        int n = intervals.size();
        dp = new Result[n][5];  // cnt ranges from 0 to 4
        node = new Node[n];

        // Convert intervals to Node objects, preserving original indices
        for (int i = 0; i < n; i++) {
            var curr = intervals.get(i);
            node[i] = new Node(curr.get(0), curr.get(1), curr.get(2), i);
        }

        // Sort intervals by left endpoint (primary), then by right endpoint (secondary)
        // Sorting enables us to process intervals left-to-right and use binary search
        Arrays.sort(node, (a, b) -> {
            if (a.l == b.l) {
                return a.r - b.r;  // If same left endpoint, sort by right
            }

            return a.l - b.l;  // Sort by left endpoint
        });

        // Start DP: from index 0, with 4 slots available
        Result result = solve(0, 4);

        // Convert TreeSet of indices to array
        int ans[] = new int[result.indices.size()];
        int ind = 0;
        for (int x : result.indices) {
            ans[ind++] = x;
        }

        return ans;
    }

    /**
     * Recursive DP function to find the best selection of intervals.
     *
     * @param i Current interval index in sorted array
     * @param cnt Number of intervals we can still select (slots remaining)
     * @return Result containing the maximum weight and selected indices
     */
    private Result solve(int i, int cnt) {

        // Base case 1: No more slots available
        if (cnt == 0) {
            return new Result(0, new TreeSet<>());
        }

        // Base case 2: No more intervals to consider
        if (i == node.length) {
            return new Result(0, new TreeSet<>());
        }

        // Memoization: return cached result if available
        if (dp[i][cnt] != null) {
            return new Result(dp[i][cnt]);  // Return a copy
        }

        // Option 1: Skip the current interval
        Result skip = solve(i + 1, cnt);

        // Option 2: Take the current interval
        // Find the next non-overlapping interval (left >= current.right + 1)
        int ind = search(i + 1, node[i].r + 1);

        if (ind == -1) {
            // No more non-overlapping intervals available after taking current
            // So we can only take the current interval
            Result res = new Result(node[i].w, new TreeSet<>());
            res.indices.add(node[i].ind);  // Add current interval's original index
            return dp[i][cnt] = response(res, skip);  // Compare take vs skip
        }

        // There are more non-overlapping intervals available
        // Recursively solve for remaining intervals with one less slot
        Result ans = solve(ind, cnt - 1);
        Result res = new Result(ans);  // Copy the result

        // Add current interval's weight (if the recursive result is valid)
        if (res.sum != Integer.MIN_VALUE) {
            res.sum += node[i].w;

        }
        res.indices.add(node[i].ind);  // Add current interval's original index

        // Compare taking current interval vs skipping it
        return dp[i][cnt] = response(res, skip);
    }

    /**
     * Compares two results and returns the better one.
     *
     * Priority: 1. Higher sum (weight) wins 2. If sums are equal, lexicographically smaller index set wins
     *
     * @param res First result (typically "take" option)
     * @param skip Second result (typically "skip" option)
     * @return The better result according to the comparison criteria
     */
    private Result response(Result res, Result skip) {

        // Case 1: res has higher weight
        if (res.sum > skip.sum) {
            return res;
        }

        // Case 2: skip has higher weight
        if (skip.sum > res.sum) {
            return new Result(skip);
        }

        // Case 3: Equal weights - need lexicographic comparison of indices
        // Compare indices one by one from smallest to largest
        Integer a = skip.indices.first();
        Integer b = res.indices.first();

        // Skip over common prefix of indices
        while (a != null && a.equals(b)) {
            a = skip.indices.higher(a);  // Next index in skip
            b = res.indices.higher(b);   // Next index in res
        }

        // If all indices are the same, return either one
        if (a == null && b == null) {
            return res;
        }

        // If skip has fewer indices (prefix match), prefer res (more intervals selected)
        if (a == null) {
            return res;
        }

        // If res has fewer indices (prefix match), prefer skip
        if (b == null) {
            return new Result(skip);
        }

        // First differing index: choose the result with smaller index (lexicographically smaller)
        if (a < b) {
            return new Result(skip);  // skip is lexicographically smaller
        }

        return res;  // res is lexicographically smaller
    }

    /**
     * Binary search to find the first interval with left endpoint >= val.
     *
     * Since intervals are sorted by left endpoint, this efficiently finds the next non-overlapping interval.
     *
     * @param i Starting index for search (inclusive)
     * @param val Minimum left endpoint value we're looking for
     * @return Index of first interval with left >= val, or -1 if not found
     */
    private int search(int i, int val) {

        int start = i;
        int ind = -1;  // Result: -1 means not found
        int end = node.length - 1;

        // Standard binary search for lower bound
        while (start <= end) {
            int mid = (start + end) / 2;
            if (node[mid].l < val) {
                // Mid's left endpoint is too small, search right half
                start = mid + 1;
            } else {
                // Mid's left endpoint is >= val, this is a candidate
                ind = mid;
                // But search left half for potentially earlier interval
                end = mid - 1;
            }
        }

        return ind;
    }

    /**
     * Represents an interval with its boundaries, weight, and original index.
     */
    class Node {

        int l;    // Left endpoint of interval
        int r;    // Right endpoint of interval
        int w;    // Weight of interval
        int ind;  // Original index in the input list

        /**
         * Constructor for Node.
         *
         * @param l Left endpoint
         * @param r Right endpoint
         * @param w Weight
         * @param ind Original index
         */
        public Node(int l, int r, int w, int ind) {

            this.l = l;
            this.r = r;
            this.w = w;
            this.ind = ind;
        }
    }

    /**
     * Represents a solution: total weight and set of selected interval indices. Uses TreeSet to maintain indices in
     * sorted order for lexicographic comparison.
     */
    class Result {

        long sum;                  // Total weight of selected intervals
        TreeSet<Integer> indices;  // Original indices of selected intervals (sorted)

        /**
         * Constructor for Result.
         *
         * @param sum Total weight
         * @param indices Set of selected interval indices
         */
        public Result(int sum, TreeSet<Integer> indices) {

            this.sum = sum;
            this.indices = indices;
        }

        /**
         * Copy constructor for Result. Creates a deep copy to avoid modifying the original.
         *
         * @param result Result to copy
         */
        public Result(Result result) {

            this.sum = result.sum;
            this.indices = new TreeSet<>(result.indices);  // Deep copy of TreeSet
        }
    }
}