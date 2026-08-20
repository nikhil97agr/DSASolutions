package Leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//Problem Link: https://leetcode.com/problems/maximum-score-with-co-prime-element/description/

/**
 * Problem: Maximum Score After Modifications
 *
 * You are given an integer array nums of length n and an integer maxVal.
 *
 * You may change any element in nums to any positive integer less than or equal to maxVal. Each such change costs 1.
 *
 * Two integers are co-prime if their greatest common divisor (GCD) is 1.
 *
 * After all modifications, you must choose an index i such that, nums[i] is co-prime with every other element nums[j].
 *
 * Let: - selectedValue be the final value of nums[i] after modifications. - modificationCost be the total number of
 * elements changed.
 *
 * The score is defined as: score = selectedValue - modificationCost.
 *
 * Return the maximum possible score.
 *
 * ================================================================================================
 * ALGORITHM OVERVIEW:
 * ================================================================================================
 *
 * This solution uses Number Theory (Prime Factorization + Inclusion-Exclusion Principle).
 *
 * High-Level Approach:
 * 1. Try every possible value i (from high to low) as the selected co-prime element
 * 2. For each i, calculate the minimum cost to make all other elements co-prime with i
 * 3. Score = i - cost, track the maximum score
 *
 * Key Challenge: How to efficiently count elements that are NOT co-prime with i?
 *
 * Solution: Two numbers share a common factor if they share at least one prime factor.
 *           Use Inclusion-Exclusion Principle on the prime factors of i.
 *
 * Steps:
 * 1. Precompute SPF (Smallest Prime Factor) array using Sieve of Eratosthenes
 * 2. Count frequency of each value in nums
 * 3. Precompute mult[x] = count of numbers divisible by x
 * 4. For each candidate value i:
 *    a. Get all prime factors of i
 *    b. Use Inclusion-Exclusion to count elements sharing ≥1 prime factor with i
 *    c. These elements must be changed (they're not co-prime with i)
 *    d. Calculate score = i - modificationCost
 * 5. Return maximum score
 *
 * Example: i = 6 = 2 × 3
 * - Elements divisible by 2 OR 3 are NOT co-prime with 6
 * - Count = |div_by_2| + |div_by_3| - |div_by_6| (Inclusion-Exclusion)
 * - These must be changed to values co-prime with 6
 */
public class MaximumScoreWithCoPrimeElement{

    // Array to store the smallest prime factor (SPF) for each number
    // Used for efficient prime factorization
    int spf[];

    /**
     * Computes the smallest prime factor for all numbers from 0 to max using Sieve of Eratosthenes.
     * This preprocessing enables O(log n) prime factorization later.
     *
     * Time Complexity: O(max * log(log(max)))
     * Space Complexity: O(max)
     *
     * @param max The maximum number up to which SPF needs to be computed
     */
    private void smallesPrimeFactor(int max) {

        spf = new int[max + 1];

        // Initialize: each number is its own smallest prime factor initially
        for (int i = 0; i <= max; i++) {
            spf[i] = i;
        }

        // Mark all even numbers with smallest prime factor as 2
        for (int i = 4; i <= max; i += 2) {
            spf[i] = 2;
        }

        // Sieve for odd numbers: for each prime, mark its multiples
        for (int i = 3; i * i <= max; i += 2) {
            if (spf[i] == i) {  // i is prime (its SPF is itself)
                // Mark all odd multiples of i starting from i*i
                for (int j = i * i; j <= max; j += i * 2) {
                    if (spf[j] == j) {  // Only update if not already marked by a smaller prime
                        spf[j] = i;
                    }
                }
            }
        }
    }

    /**
     * Extracts all unique prime factors of a number n using the precomputed SPF array.
     *
     * Example: getPrimes(12) returns {2, 3} because 12 = 2^2 * 3
     *
     * Time Complexity: O(log n) - number of prime factors
     *
     * @param n The number to factorize
     * @return Set of unique prime factors of n
     */
    private Set<Integer> getPrimes(int n) {

        Set<Integer> set = new HashSet<>();

        // Repeatedly divide n by its smallest prime factor
        while (n > 1) {
            set.add(spf[n]);      // Add the smallest prime factor
            n /= spf[n];          // Divide n by this prime factor
        }

        return set;
    }

    /**
     * Generates all divisors of n that are products of subsets of n's prime factors.
     * This is used for Inclusion-Exclusion Principle to count numbers NOT co-prime with n.
     *
     * Example: if n = 6 = 2 * 3, returns [1, 2, 3, 6]
     *          - mask=0 (00): 1 (empty product)
     *          - mask=1 (01): 2 (first prime)
     *          - mask=2 (10): 3 (second prime)
     *          - mask=3 (11): 6 (both primes)
     *
     * Time Complexity: O(2^k * k) where k is the number of distinct prime factors
     *
     * @param n The number whose divisor subsets to generate
     * @return Array of all products of subsets of prime factors (including 1 and n)
     */
    private int[] getSub(int n) {

        List<Integer> primes = new ArrayList<>(getPrimes(n));
        int m = primes.size();  // Number of distinct prime factors

        // Generate all 2^m subsets using bitmask
        int subs[] = new int[1 << m];
        for (int mask = 0; mask < (1 << m); mask++) {
            int val = 1;
            // For each bit set in mask, multiply the corresponding prime
            for (int j = 0; j < m; j++) {
                if ((mask & (1 << j)) != 0) {
                    val *= primes.get(j);
                }
            }
            subs[mask] = val;  // Store the product of this subset
        }

        return subs;
    }

    /**
     * Main function to calculate the maximum score.
     *
     * Strategy:
     * 1. Try each possible value i as the selected co-prime value (from high to low)
     * 2. For each i, count how many elements must be changed to make them co-prime with i
     * 3. Calculate score = i - modificationCost and track the maximum
     *
     * Key Insight: A number is co-prime with i if it shares no common prime factors with i.
     * We use Inclusion-Exclusion Principle to count numbers that share at least one prime factor with i.
     *
     * Time Complexity: O(max^2 + n * 2^k) where k is max distinct prime factors (~7 for numbers < 10^6)
     * Space Complexity: O(max)
     */
    public int maxScore(int[] nums, int maxVal) {

        // Calculate upper bound for all possible values
        int max = Math.max(maxVal, Arrays.stream(nums).max().getAsInt()) + 1;

        // Precompute smallest prime factors for efficient factorization
        smallesPrimeFactor(max);

        // cnt[x] = count of elements with value x in the original array
        int cnt[] = new int[max];
        for (int x : nums) {
            cnt[x]++;
        }

        // mult[i] = count of elements divisible by i (i.e., multiples of i)
        // This helps count elements that share prime factor i
        int mult[] = cnt.clone();
        for (int i = 2; i < max; i++) {
            for (int j = i * 2; j < max; j += i) {
                mult[i] += cnt[j];  // Add count of all multiples of i
            }
        }

        // Special case: if 1 exists in array, it can be selected with score 1
        // 1 is co-prime with everything, no changes needed if we select existing 1
        int ans = 0;
        if (cnt[1] > 0) {
            ans = 1;
        }

        // Try each value i from high to low as the potential selected value
        for (int i = max - 1; i > 1; i--) {
            // Early termination: if current answer >= i, no point checking smaller i
            if (ans >= i) {
                break;
            }

            // Get all divisors formed by subsets of i's prime factors
            int sub[] = getSub(i);

            // Use Inclusion-Exclusion to count elements NOT co-prime with i
            // Start with -cnt[i] because we'll select one element to be i
            int c = -cnt[i];

            // Inclusion-Exclusion Principle:
            // - Add counts for single prime factors (odd bit count)
            // - Subtract counts for pairs of prime factors (even bit count)
            // - Add counts for triples, etc.
            for (int mask = 1; mask < sub.length; mask++) {
                if ((Integer.bitCount(mask) & 1) == 1) {  // Odd number of primes: ADD
                    c += mult[sub[mask]];
                } else {  // Even number of primes: SUBTRACT
                    c -= mult[sub[mask]];
                }
            }

            // Case 1: i already exists in array
            if (cnt[i] > 0) {
                // Keep one i, change (c + cnt[i] - 1) other elements
                // c = elements not co-prime with i (excluding all i's)
                // cnt[i] - 1 = other i's that need to be changed
                int cost = c + cnt[i] - 1;
                ans = Math.max(ans, i - cost);
            }
            // Case 2: i doesn't exist but is within maxVal, so we can create it
            else if (i <= maxVal) {
                // Need to change one element to i, plus all elements not co-prime with i
                // If c > 0, cost = c elements not co-prime
                // If c == 0, we still need to create i from some element, cost = 1
                int cost = c > 0 ? c : 1;
                ans = Math.max(ans, i - cost);
            }
        }

        return ans;
    }
}