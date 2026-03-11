// Problem Link: https://leetcode.com/problems/count-ways-to-make-array-with-product

package Leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Solution for Count Ways to Make Array With Product
 *
 * Problem: Given queries [n, k], count ways to fill an array of length n
 * with positive integers such that their product equals k.
 *
 * Approach: Combinatorics with Prime Factorization
 * - Factorize k into prime factors
 * - For each prime p with exponent e, distribute e occurrences among n positions
 * - Use stars and bars: C(n+e-1, e) ways to distribute
 * - Multiply results for all prime factors
 */
public class CountWaysToMakeArrayWithProduct {

    // Modulo for large number handling
    long mod = (long) 1e9 + 7;

    /**
     * Calculates the number of ways to fill arrays for each query
     *
     * @param queries Array of [n, k] pairs where n is array length and k is target product
     * @return Array of answers for each query
     */
    public int[] waysToFillArray(int[][] queries) {

        int q = queries.length;
        int ans[] = new int[q];
        Arrays.fill(ans, 1);

        // Precompute factorials and inverse factorials for combinations
        int fact[] = new int[100000];
        int invFact[] = new int[100000];

        fact[0] = fact[1] = 1;
        invFact[0] = invFact[1] = 1;

        // Build factorial and inverse factorial arrays
        for (int i = 2; i < 100000; i++) {
            fact[i] = prod(fact[i - 1], i);
            invFact[i] = pow(fact[i], mod - 2); // Fermat's little theorem for modular inverse
        }

        // Process each query
        for (int i = 0; i < q; i++) {
            int n = queries[i][0]; // Array length
            int k = queries[i][1];
            Map<Integer, Integer> factors = factors(k);
            for (int val : factors.values()) {
                ans[i] = prod(ans[i], comb(n + val - 1, val, fact, invFact));
            }
        }

        return ans;
    }

    private int comb(int n, int r, int fact[], int invFact[]) {

        return prod(fact[n], prod(invFact[r], invFact[n - r]));
    }

    private Map<Integer, Integer> factors(int k) {

        Map<Integer, Integer> map = new HashMap<>();
        while (k % 2 == 0) {
            map.merge(2, 1, Integer::sum);
            k /= 2;
        }

        for (int i = 3; i <= Math.sqrt(k); i++) {
            while (k % i == 0) {
                k /= i;
                map.merge(i, 1, Integer::sum);
            }
        }
        if (k > 1) {
            map.merge(k, 1, Integer::sum);
        }

        return map;
    }

    private int add(long a, long b) {

        return (int) ((a % mod + b % mod) % mod);
    }

    private int prod(long a, long b) {

        return (int) ((a * b) % mod);
    }

    private int pow(long a, long b) {

        if (b == 1 || a == 0) {
            return prod(a, 1);
        }
        if (b == 0) {
            return 1;
        }

        int p = pow(a, b / 2);
        p = prod(p, p);
        if (b % 2 == 1) {
            p = prod(p, a);
        }

        return p;
    }
}