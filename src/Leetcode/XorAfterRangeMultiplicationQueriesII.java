package Leetcode;

import java.util.Arrays;
import java.util.HashMap;

//Problem Link: https://leetcode.com/problems/xor-after-range-multiplication-queries-ii/

/**
 * Solution for XOR after range multiplication queries.
 *
 * Given an array nums and multiple queries [l, r, k, v], each query multiplies elements at positions l, l+k, l+2k, ...
 * (up to r) by value v (mod 10^9+7). After all queries, return the XOR of all elements.
 *
 * Uses Square Root Decomposition technique: - Large k (>= sqrt(n)): Apply updates immediately (few iterations per
 * query) - Small k (< sqrt(n)): Use lazy propagation with difference arrays
 *
 * Key insights: 1. For large k, there are at most O(sqrt(n)) affected elements per query 2. For small k, there are at
 * most O(sqrt(n)) distinct k values 3. Use difference array technique for small k to batch updates 4. Use modular
 * multiplicative inverse to "undo" multiplications at range boundaries
 *
 * Time Complexity: O(q * sqrt(n) + n * sqrt(n)) Space Complexity: O(sqrt(n) * n) for lazy propagation arrays
 */
public class XorAfterRangeMultiplicationQueriesII {

    int mod = 1_000_000_007;  // Modulo for all operations

    /**
     * Processes range multiplication queries and returns XOR of final array.
     *
     * @param nums Initial array
     * @param queries Array of queries where queries[i] = [l, r, k, v]
     * @return XOR of all elements after applying all queries
     */
    public int xorAfterQueries(int[] nums, int[][] queries) {

        var n = nums.length;

        // Block limit for square root decomposition
        // k >= blockLimit -> handle immediately (large k)
        // k < blockLimit -> handle with lazy propagation (small k)
        var blockLimit = (int) (Math.sqrt(n)) + 1;

        // Map: k -> difference array for that step size k
        // For small k values, we store pending multiplications
        var map = new HashMap<Integer, int[]>();

        // Process each query
        for (int q[] : queries) {
            var l = q[0];  // Start index
            var r = q[1];  // End index
            var k = q[2];  // Step size
            var v = q[3];  // Multiplication value

            // Large k: few elements affected, apply immediately
            if (k >= blockLimit) {
                // Multiply nums[l], nums[l+k], nums[l+2k], ... up to r
                for (int i = l; i <= r; i += k) {
                    nums[i] = prod(nums[i], v);
                }
            } else {
                // Small k: use lazy propagation with difference array

                // Initialize difference array for this k if not exists
                if (!map.containsKey(k)) {
                    int arr[] = new int[n];
                    Arrays.fill(arr, 1);  // Initialize to 1 (multiplicative identity)
                    map.put(k, arr);
                }

                int arr[] = map.get(k);

                // Mark start of range: multiply at position l
                arr[l] = prod(arr[l], v);

                // Calculate position where multiplication should stop
                // We need to find the first position after r that has same remainder mod k as l
                int r2 = n;  // Default: no stop position (goes to end of array)
                int m1 = l % k;  // Remainder of l
                int m2 = r % k;  // Remainder of r

                if (m1 == m2) {
                    // l and r have same remainder, so next position with same remainder is r+k
                    r2 = r + k;
                } else if (m1 > m2) {
                    // Need to advance from r to next position with remainder m1
                    r2 = r + (m1 - m2);
                } else {
                    // m1 < m2: wrap around to next cycle
                    r2 = r + (k - m2 + m1);
                }

                // At position r2, undo the multiplication using modular inverse
                // This ensures the multiplication only affects [l, l+k, l+2k, ..., r]
                if (r2 < n) {
                    // v^(mod-2) is the modular multiplicative inverse of v
                    arr[r2] = prod(arr[r2], pow(v, mod - 2));
                }
            }
        }

        // Apply all lazy propagated updates for small k values
        for (var entry : map.entrySet()) {
            int k = entry.getKey();   // Step size
            var arr = entry.getValue();  // Difference array for this k

            // Process each residue class modulo k separately
            for (int i = 0; i < k; i++) {
                int curr = 1;  // Running product for positions with remainder i

                // Iterate through positions: i, i+k, i+2k, ...
                for (int j = i; j < n; j += k) {
                    // Accumulate the multiplication factor
                    curr = prod(curr, arr[j]);

                    // Apply accumulated multiplications to nums[j]
                    nums[j] = prod(nums[j], curr);
                }
            }
        }

        // Calculate XOR of all final values
        int ans = 0;
        for (int x : nums) {
            ans ^= x;
        }

        return ans;
    }

    /**
     * Multiplies two numbers with modulo arithmetic.
     *
     * @param a First number
     * @param b Second number
     * @return (a * b) mod (10^9 + 7)
     */
    private int prod(long a, long b) {

        return (int) ((a * b) % mod);
    }

    /**
     * Computes modular exponentiation: a^b mod (10^9 + 7). Uses fast exponentiation (exponentiation by squaring).
     *
     * Commonly used to compute modular multiplicative inverse: - By Fermat's Little Theorem: a^(p-1) ≡ 1 (mod p) for
     * prime p - Therefore: a^(-1) ≡ a^(p-2) (mod p)
     *
     * @param a Base
     * @param b Exponent
     * @return a^b mod (10^9 + 7)
     */
    private int pow(int a, int b) {

        int p = 1;
        while (b > 0) {
            if (b % 2 == 1) {
                p = prod(p, a);

            }
            a = prod(a, a);
            b >>= 1;
        }

        return p;
    }
}
