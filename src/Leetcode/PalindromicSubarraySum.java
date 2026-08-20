package Leetcode;

import java.util.Arrays;

//Problem Link: https://leetcode.com/problems/palindromic-subarray-sum/

/**
 * Solution to find the maximum sum of elements in any palindromic subarray. Uses Manacher's Algorithm to find all
 * palindromes in O(n) time.
 */
public class PalindromicSubarraySum {


    /**
     * Finds the maximum sum among all palindromic subarrays.
     *
     * @param nums input array of integers
     * @return maximum sum of elements in any palindromic subarray
     */
    public long getSum(int[] nums) {

        // Build Manacher's algorithm data structures
        var manacher = new ManacherAlgorithm(nums);
        var p = manacher.p;  // p[i] = radius of palindrome centered at i
        var t = manacher.t;  // transformed array with 0s inserted between elements
        int n = t.length;

        // Build prefix sum array for efficient range sum queries
        long pre[] = new long[n + 1];
        for (int i = 0; i < n; i++) {
            pre[i + 1] = t[i] + pre[i];
        }

        // Find the maximum sum among all palindromes
        long ans = -1_000_000_000_000_000_0L;
        for (int i = 0; i < n; i++) {
            int size = p[i];  // radius of palindrome at center i

            // Calculate the range of the palindrome
            int start = i - size + 1;
            int end = i + size - 1;

            // Calculate sum using prefix array: sum[start..end] = pre[end+1] - pre[start]
            ans = Math.max(ans, pre[end + 1] - pre[start]);
        }

        return ans;
    }


    /**
     * Manacher's Algorithm for finding all palindromes in O(n) time.
     *
     * The algorithm works by: 1. Transforming the array to handle even-length palindromes uniformly 2. Using previously
     * computed palindrome information to skip redundant checks 3. Maintaining a "rightmost" palindrome boundary to
     * leverage symmetry
     */
    class ManacherAlgorithm {

        public int p[];  // p[i] = radius of longest palindrome centered at i in transformed array
        int[] t;         // transformed array with 0s inserted between original elements

        /**
         * Constructs Manacher's algorithm for the given array.
         *
         * Example: [1, 2, 3] becomes [0, 1, 0, 2, 0, 3, 0] This transformation allows us to treat even and odd length
         * palindromes uniformly.
         *
         * @param arr input array
         */
        public ManacherAlgorithm(int arr[]) {

            int n = arr.length;

            // Transform array: insert 0 between each element
            // Original: [a, b, c] -> Transformed: [0, a, 0, b, 0, c, 0]
            t = new int[n * 2 + 1];
            p = new int[t.length];
            Arrays.fill(t, 0);
            for (int i = 1, j = 0; j < n; i += 2, j++) {
                t[i] = arr[j];
            }

            // Initialize all palindrome radii to 1 (every element is a palindrome of length 1)
            Arrays.fill(p, 1);

            build();
        }

        /**
         * Builds the palindrome radius array using Manacher's algorithm.
         *
         * Key Insight: If we have a palindrome centered at position c with right boundary r, then for any position i
         * within this palindrome (i < r), its mirror position i' = l + r - i (where l is the left boundary) will have
         * the same palindrome structure, which we can reuse to avoid redundant comparisons.
         *
         * Time Complexity: O(n) - each position is visited at most twice
         */
        public void build() {

            int n = t.length;
            int l = 1;  // left boundary of the rightmost palindrome found so far
            int r = 1;  // right boundary of the rightmost palindrome found so far

            for (int i = 1; i < n; i++) {
                // Optimization: if i is within a known palindrome [l, r),
                // we can use the mirror position's information
                if (i < r) {
                    // Mirror position of i with respect to the center (l+r)/2
                    int mirror = l + r - i;

                    // p[i] is at least the minimum of:
                    // 1. Distance to the right boundary (r - i)
                    // 2. Palindrome radius at the mirror position p[mirror]
                    // We take the minimum because we can't guarantee beyond the boundary
                    p[i] = Math.max(0, Math.min(r - i, p[mirror]));
                } else {
                    // If i >= r, start with max(0, r-i) which will be 0 or negative
                    p[i] = Math.max(0, r - i);
                }

                // Try to expand the palindrome centered at i
                // This while loop only runs when we discover new characters beyond known palindromes
                while (i + p[i] < n && i - p[i] >= 0 && t[i + p[i]] == t[i - p[i]]) {
                    p[i]++;
                }

                // If the palindrome centered at i extends beyond r,
                // update the rightmost palindrome boundary
                if (i + p[i] > r) {
                    l = i - p[i];  // new left boundary
                    r = i + p[i];  // new right boundary
                }
            }
        }


    }
}