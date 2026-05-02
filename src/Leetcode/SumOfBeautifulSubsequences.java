package Leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//Problem Link: https://leetcode.com/problems/sum-of-beautiful-subsequences

/**
 * Solution for computing sum of beautiful subsequences.
 *
 * Problem: A subsequence is beautiful if it's non-decreasing. For each beautiful subsequence, calculate
 * gcd(subsequence) and multiply by the count of such subsequences. Return the sum of all these products.
 *
 * Example: nums = [1, 2, 2] - Subsequence [1]: gcd=1, count=1 → contributes 1*1 = 1 - Subsequence [2]: gcd=2, count=2
 * (two different 2's) → contributes 2*2 = 4 - Subsequence [1,2]: gcd=1, count=2 (two different 2's) → contributes 1*2 =
 * 2 - Subsequence [2,2]: gcd=2, count=1 → contributes 2*1 = 2 - Subsequence [1,2,2]: gcd=1, count=1 → contributes 1*1 =
 * 1 - Total: 1 + 4 + 2 + 2 + 1 = 10
 *
 * Key insight: DP + Inclusion-Exclusion on GCD
 *
 * Observation 1: Non-decreasing subsequences - Process elements in original order - Count subsequences ending at each
 * position - Use segment tree to query "how many subsequences end with value ≤ current"
 *
 * Observation 2: Group by GCD - For each possible GCD value g, count subsequences with GCD exactly g - dp[g] = count of
 * non-decreasing subsequences with GCD exactly g
 *
 * Observation 3: Inclusion-Exclusion - First compute: dp'[g] = count of subsequences where all elements are divisible
 * by g - Then apply inclusion-exclusion: dp[g] = dp'[g] - Σ dp[kg] for k ≥ 2 - This removes subsequences where GCD is
 * actually a multiple of g
 *
 * Algorithm: 1. Group numbers by their divisors (gcds[d] = numbers divisible by d) 2. For each GCD value g (from max to
 * 1): a. Count non-decreasing subsequences using only numbers divisible by g b. Use DP + segment tree to count
 * efficiently c. Apply inclusion-exclusion to get exact count for GCD = g d. Add g * count to answer
 *
 * Why process GCD from max to 1? - Inclusion-exclusion requires knowing dp[kg] before computing dp[g] - Processing in
 * descending order ensures dependencies are met
 *
 * Time Complexity: O(max² + n√max log n) where max = max(nums) Space Complexity: O(max + n)
 */
public class SumOfBeautifulSubsequences {

    private int mod = 1_000_000_007;  // Modulo for result

    /**
     * Computes sum of gcd * count for all beautiful subsequences.
     *
     * @param nums Input array
     * @return Sum of (gcd * count) for all non-decreasing subsequences
     */
    public int totalBeauty(int[] nums) {

        var ans = 0;
        var n = nums.length;
        var max = Arrays.stream(nums).max().getAsInt();

        // ================================================================
        // STEP 1: Group numbers by their divisors
        // ================================================================
        // gcds[d] = list of all numbers in nums that are divisible by d
        List<Integer> gcds[] = new ArrayList[max + 1];
        for (int i = 0; i <= max; i++) {
            gcds[i] = new ArrayList<>();
        }

        // For each number, find all its divisors and add to corresponding lists
        for (int x : nums) {
            // Find all divisors of x efficiently (only check up to √x)
            for (int d = 1; d * d <= x; d++) {
                if (x % d == 0) {
                    gcds[d].add(x);  // d is a divisor
                    if (x / d != d) {
                        gcds[x / d].add(x);  // x/d is also a divisor (if different)
                    }
                }
            }
        }
        // ================================================================
        // STEP 2: Compute dp[g] for each GCD value g
        // ================================================================
        // dp[g] = count of non-decreasing subsequences with GCD exactly g
        var dp = new int[max + 1];

        // Process GCD values from largest to smallest (for inclusion-exclusion)
        for (var gcd = max; gcd >= 1; gcd--) {
            var list = gcds[gcd];  // Numbers divisible by 'gcd'

            // Create sorted version for binary search
            var sorted = new ArrayList<>(list);
            Collections.sort(sorted);
            var size = list.size();

            // ============================================================
            // Count non-decreasing subsequences using DP + Segment Tree
            // ============================================================
            // Segment tree: tree[i] = count of subsequences ending with sorted[i]
            var tree = new SegmentTree(list.size());

            // Process elements in original order (to maintain subsequence property)
            for (int i = 0; i < list.size(); i++) {
                int val = list.get(i);  // Current element value
                int ind = search(sorted, val);  // Position in sorted array

                // Count new subsequences ending at current element
                // 1: current element alone forms a subsequence
                // tree.query(0, ind-1): extend all subsequences ending with value ≤ val
                int total = add(1, tree.query(1, 0, ind - 1, 0, size - 1));

                // Add to total count for this GCD (before inclusion-exclusion)
                dp[gcd] = add(dp[gcd], total);

                // Update segment tree: add 'total' subsequences ending at this value
                tree.insert(1, ind, total, 0, size - 1);
            }

            // ============================================================
            // Apply Inclusion-Exclusion Principle
            // ============================================================
            // At this point, dp[gcd] counts subsequences where all elements
            // are divisible by 'gcd', but GCD might be larger (e.g., 2*gcd, 3*gcd)
            // Subtract counts where GCD is actually a multiple of 'gcd'
            for (var g = gcd * 2; g <= max; g += gcd) {
                // dp[g] = count with GCD exactly g (already computed since g > gcd)
                // These were incorrectly included in dp[gcd], so subtract them
                dp[gcd] = add(dp[gcd], -dp[g]);
            }

            // ============================================================
            // Add contribution to answer: gcd * count
            // ============================================================
            ans = add(ans, prod(gcd, dp[gcd]));
        }

        return ans;
    }

    /**
     * Binary search to find the leftmost index where list[index] >= val.
     *
     * Used to find position in sorted array for range queries. Returns the position where val should be inserted to
     * maintain sorted order.
     *
     * @param list Sorted list of integers
     * @param val Value to search for
     * @return Leftmost index where list[index] >= val
     */
    private int search(List<Integer> list, int val) {

        int start = 0;
        int end = list.size() - 1;
        while (start < end) {
            int mid = (start + end) >> 1;

            if (list.get(mid) < val) {
                start = mid + 1;  // Search right
            } else {

                end = mid;  // Search left (including mid)
            }
        }

        return end;
    }

    /**
     * Adds two numbers with modulo arithmetic.
     *
     * Handles potential negative values by adding mod before taking modulo.
     *
     * @param a First value
     * @param b Second value
     * @return (a + b) % mod
     */
    private int add(long a, long b) {

        return (int) ((a + b + mod) % mod);
    }

    /**
     * Multiplies two numbers with modulo arithmetic.
     *
     * Applies modulo to prevent overflow.
     *
     * @param a First value
     * @param b Second value
     * @return (a * b) % mod
     */
    private int prod(long a, long b) {

        return (int) ((a * b) % mod);
    }

    /**
     * Segment Tree for tracking count of subsequences ending at each value.
     *
     * Each node stores the sum (count) of subsequences ending with values in the corresponding range.
     *
     * Supports: - insert: Add count to a specific position - query: Get sum of counts in a range [ql, qr]
     */
    class SegmentTree {

        int tree[];  // Array storing segment tree nodes

        /**
         * Constructor to initialize segment tree.
         *
         * @param n Number of distinct values (size of sorted array)
         */
        public SegmentTree(int n) {

            tree = new int[4 * n];  // Standard segment tree size
        }

        /**
         * Inserts (adds) a value at position i.
         *
         * Used to update the count of subsequences ending with a specific value.
         *
         * @param ind Current node index in segment tree
         * @param i Position to update
         * @param val Value to add at position i
         * @param l Left boundary of current segment
         * @param r Right boundary of current segment
         */
        public void insert(int ind, int i, int val, int l, int r) {

            // Position i is outside current segment
            if (i < l || r < i) {
                return;
            }

            // Leaf node: update count
            if (l == r) {
                tree[ind] = add(tree[ind], val);
                return;
            }

            int mid = (l + r) >> 1;

            // Recursively update left or right child
            insert(2 * ind, i, val, l, mid);
            insert(2 * ind + 1, i, val, mid + 1, r);

            // Update current node: sum of both children
            tree[ind] = add(tree[2 * ind + 1], tree[2 * ind]);
        }

        /**
         * Queries the sum of counts in range [ql, qr].
         *
         * Returns the total count of subsequences ending with values in [ql, qr].
         *
         * @param ind Current node index in segment tree
         * @param ql Query left boundary
         * @param qr Query right boundary
         * @param l Left boundary of current segment
         * @param r Right boundary of current segment
         * @return Sum of counts in range [ql, qr]
         */
        public int query(int ind, int ql, int qr, int l, int r) {

            // Current segment is outside query range or invalid range
            if (ql > qr || qr < l || r < ql) {
                return 0;  // Identity for addition
            }

            // Current segment is fully within query range
            if (ql <= l && r <= qr) {
                return tree[ind];
            }

            // Partial overlap: query both children and sum results
            int mid = (l + r) >> 1;
            return add(query(2 * ind, ql, qr, l, mid), query(2 * ind + 1, ql, qr, mid + 1, r));
        }
    }
}