package Leetcode;

//Problem Link: https://leetcode.com/problems/maximum-sum-of-subsequence-with-non-adjacent-elements/

/**
 * Solution for finding maximum sum of non-adjacent elements with updates.
 *
 * Problem: Given an array, process queries that update elements, and after each query, find the maximum sum of a
 * subsequence where no two elements are adjacent. Return the sum of all query results.
 *
 * Constraint: Cannot select adjacent elements in the subsequence.
 *
 * Key insight: Segment Tree with DP states - Classic "House Robber" problem but with updates - Need to efficiently
 * recompute max sum after each update - Use segment tree where each node tracks 4 DP states
 *
 * DP States for a range [l, r]: - firstLast: Max sum if we INCLUDE element at l AND INCLUDE element at r -
 * firstNotLast: Max sum if we INCLUDE element at l AND EXCLUDE element at r - notFirstLast: Max sum if we EXCLUDE
 * element at l AND INCLUDE element at r - notFirstNotLast: Max sum if we EXCLUDE element at l AND EXCLUDE element at r
 *
 * Why 4 states? - When merging two ranges [l, m] and [m+1, r]: * If we include element at m from left range, we CANNOT
 * include element at m+1 from right * If we exclude element at m, we CAN include or exclude element at m+1 * Need to
 * track all combinations to make optimal choice
 *
 * Example: nums = [1, 2, 3, 4], then query updates index 1 to 5 - Initial: max subsequence = [1, 3] or [2, 4] = 4 or 6
 * → answer = 6 - After update to [1, 5, 3, 4]: max subsequence = [5, 4] = 9 - Total = 6 + 9 = 15
 *
 * Merge logic: - Left range ends at position m, right range starts at m+1 - If left includes m (firstLast or
 * notFirstLast), right must exclude m+1 (notFirstLast or notFirstNotLast) - If left excludes m (firstNotLast or
 * notFirstNotLast), right can include or exclude m+1
 *
 * Time Complexity: O(n + q log n) where q = number of queries Space Complexity: O(n) for segment tree
 */
public class MaximumSumOfSubsequenceWithNonAdjacentElements {

    /**
     * Processes queries and returns sum of maximum subsequence sums.
     *
     * @param nums Initial array
     * @param queries Array of [index, value] updates
     * @return Sum of all maximum subsequence sums after each query, modulo 10^9+7
     */
    public int maximumSumSubsequence(int[] nums, int[][] queries) {

        int ans = 0;
        var n = nums.length;
        var tree = new SegmentTree(n);

        // Build initial segment tree with original values
        for (int i = 0; i < n; i++) {
            tree.insert(1, i, nums[i], 0, n - 1);
        }

        // Process each query
        for (var q : queries) {
            var ind = q[0];  // Index to update
            var val = q[1];  // New value

            // Update the value at index
            tree.insert(1, ind, val, 0, n - 1);

            // Get the root node data (represents entire array)
            Data data = tree.data[1];

            // The maximum subsequence sum is the max of all 4 states
            // (we can choose to include/exclude first and last elements)
            ans = add(ans,
                    max(
                            data.firstLast,
                            data.firstNotLast,
                            data.notFirstLast,
                            data.notFirstNotLast
                    ));
        }

        return ans;
    }

    /**
     * Adds two numbers with modulo arithmetic.
     *
     * @param a First number
     * @param b Second number
     * @return (a + b) % (10^9 + 7)
     */
    private int add(long a, long b) {

        int mod = 1_000_000_007;

        return (int) ((a + b) % mod);
    }

    /**
     * Finds the maximum value from a variable number of integers.
     *
     * @param arr Variable number of integers
     * @return Maximum value
     */
    private long max(long... arr) {

        long ans = 0;
        for (var x : arr) {
            ans = Math.max(ans, x);
        }

        return ans;

    }


    /**
     * Segment Tree for efficient range queries with DP state tracking.
     *
     * Each node stores 4 DP states representing different inclusion/exclusion combinations for the first and last
     * elements of the range.
     */
    class SegmentTree {

        Data data[];  // Array storing DP data for each segment tree node
        long min = (long) -1e18;  // Sentinel value for invalid states

        /**
         * Constructor to initialize segment tree.
         *
         * @param n Size of the original array
         */
        public SegmentTree(int n) {

            data = new Data[4 * n];  // Standard segment tree size
        }

        /**
         * Inserts/updates a value at position i in the array.
         *
         * Updates the segment tree to reflect the new value and recomputes all affected DP states up to the root.
         *
         * @param ind Current segment tree node index
         * @param i Position to update in original array
         * @param val New value to insert
         * @param l Left boundary of current segment
         * @param r Right boundary of current segment
         */
        public void insert(int ind, int i, int val, int l, int r) {

            // Position i is outside current segment
            if (i < l || r < i) {
                return;
            }

            // Reached leaf node (single element)
            if (l == r) {
                // For a single element, only 2 valid states:
                // - Include it: firstLast = notFirstLast = val (same element is both first and last)
                // - Exclude it: firstNotLast = notFirstNotLast = 0
                // Invalid states (can't include only first/last when they're the same): min
                data[ind] = new Data(min, 0, min, val);
                return;

            }

            int mid = (l + r) / 2;

            // Recursively update left and right children
            insert(2 * ind, i, val, l, mid);
            insert(2 * ind + 1, i, val, mid + 1, r);

            // Merge children's DP states to compute current node's states
            data[ind] = new Data();
            data[ind].merge(data[2 * ind], data[2 * ind + 1]);
        }


    }

    /**
     * Data class storing 4 DP states for a range.
     *
     * States represent maximum sum with different inclusion/exclusion of boundary elements: - firstLast: Include BOTH
     * first and last elements of range - firstNotLast: Include first, EXCLUDE last - notFirstLast: EXCLUDE first,
     * include last - notFirstNotLast: EXCLUDE both first and last
     *
     * These states allow proper merging when combining ranges while respecting the non-adjacent constraint.
     */
    class Data {

        long firstNotLast;      // Max sum: include first, exclude last
        long notFirstNotLast;   // Max sum: exclude both
        long notFirstLast;      // Max sum: exclude first, include last
        long firstLast;         // Max sum: include both

        /**
         * Default constructor initializing all states to 0.
         */
        public Data() {

            this.firstNotLast = 0;
            this.notFirstNotLast = 0;
            this.notFirstLast = 0;
            this.firstLast = 0;
        }

        /**
         * Constructor with explicit state values.
         *
         * @param firstNotLast Max sum including first, excluding last
         * @param notFirstNotLast Max sum excluding both
         * @param notFirstLast Max sum excluding first, including last
         * @param firstLast Max sum including both
         */
        public Data(long firstNotLast, long notFirstNotLast, long notFirstLast, long firstLast) {

            this.firstNotLast = firstNotLast;
            this.notFirstNotLast = notFirstNotLast;
            this.notFirstLast = notFirstLast;
            this.firstLast = firstLast;
        }

        /**
         * Merges DP states from two child ranges.
         *
         * When combining left range [l, m] and right range [m+1, r]: - Left range ends at position m, right range
         * starts at m+1 (adjacent!) - If left includes m, right CANNOT include m+1 (adjacency constraint) - If left
         * excludes m, right CAN include or exclude m+1
         *
         * The merge considers all valid combinations ensuring no two adjacent elements are selected across the
         * boundary.
         *
         * Transition rules: - left ends with INCLUDE (firstLast/notFirstLast) → right must start with EXCLUDE
         * (notFirstLast/notFirstNotLast) - left ends with EXCLUDE (firstNotLast/notFirstNotLast) → right can start with
         * INCLUDE or EXCLUDE (any state)
         *
         * @param left DP states for left child range
         * @param right DP states for right child range
         */
        public void merge(Data left, Data right) {

            // Include first of merged range, exclude last of merged range
            // Options:
            // 1. left.firstLast + right.notFirstNotLast: left includes m, so right must exclude m+1 and last
            // 2. left.firstNotLast + right.firstNotLast: left excludes m, right can include m+1, exclude last
            // 3. left.firstNotLast + right.notFirstNotLast: left excludes m, right excludes both
            firstNotLast = max(
                    left.firstLast + right.notFirstNotLast,
                    left.firstNotLast + right.firstNotLast,
                    left.firstNotLast + right.notFirstNotLast
            );

            // Exclude both first and last of merged range
            // Options:
            // 1. left.notFirstLast + right.notFirstNotLast: left includes m, right must exclude both
            // 2. left.notFirstNotLast + right.firstNotLast: left excludes both, right can include m+1, exclude last
            // 3. left.notFirstNotLast + right.notFirstNotLast: left excludes both, right excludes both
            notFirstNotLast = max(
                    left.notFirstLast + right.notFirstNotLast,
                    left.notFirstNotLast + right.firstNotLast,
                    left.notFirstNotLast + right.notFirstNotLast
            );

            // Exclude first of merged range, include last of merged range
            // Options:
            // 1. left.notFirstLast + right.notFirstLast: left includes m, right must exclude m+1, include last
            // 2. left.notFirstNotLast + right.firstLast: left excludes both, right can include m+1 and last
            // 3. left.notFirstNotLast + right.notFirstLast: left excludes both, right excludes m+1, includes last
            notFirstLast = max(
                    left.notFirstLast + right.notFirstLast,
                    left.notFirstNotLast + right.firstLast,
                    left.notFirstNotLast + right.notFirstLast
            );

            // Include both first and last of merged range
            // Options:
            // 1. left.firstNotLast + right.notFirstLast: left excludes m, right can exclude m+1, include last
            // 2. left.firstNotLast + right.firstLast: left excludes m, right can include m+1 and last
            // 3. left.firstLast + right.notFirstLast: left includes m, right must exclude m+1, include last
            firstLast = max(
                    left.firstNotLast + right.notFirstLast,
                    left.firstNotLast + right.firstLast,
                    left.firstLast + right.notFirstLast
            );
        }


    }
}