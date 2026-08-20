package Leetcode;

import java.util.HashMap;
import java.util.Map;

// Problem Link: https://leetcode.com/problems/smallest-unique-subarray/description/

/**
 * Solution - Find Smallest Unique Subarray Length
 *
 * This class solves the problem of finding the smallest subarray length
 * such that there exists at least one subarray of that length with no duplicates
 * in the entire array.
 *
 * The solution uses:
 * 1. Binary search on the answer (subarray length)
 * 2. Polynomial rolling hash to efficiently detect duplicate subarrays
 * 3. Double hashing (two different moduli) to minimize hash collisions
 */
public class SmallestUniqueSubarray {

    // Two large prime moduli for double hashing to reduce collision probability
    int m1 = (int) 1e9 + 7;    // First modulus
    int m2 = (int) 1e9 + 9;    // Second modulus
    int p = 1_000_003;         // Base for polynomial rolling hash

    /**
     * Finds the smallest subarray length that appears uniquely in the array.
     *
     * Strategy:
     * - Use binary search on the length (from 1 to n-1)
     * - For each candidate length, check if all subarrays of that length are duplicates
     * - If all are duplicates, we need a larger length
     * - If at least one is unique (appears only once), we can try smaller length
     *
     * Time Complexity: O(n log n) where n is array length
     * Space Complexity: O(n) for the hash map
     *
     * @param nums input array of integers
     * @return smallest length of a unique subarray
     */
    public int smallestUniqueSubarray(int[] nums) {

        int n = nums.length;
        int ans = n;  // Default answer is full array length

        // Binary search on the subarray length
        int start = 1;
        int end = n - 1;
        while (start <= end) {
            int mid = (start + end) / 2;

            // Check if all subarrays of length 'mid' are duplicates
            if (duplicate(nums, mid, n)) {
                // All subarrays of this length are duplicates, need larger length
                start = mid + 1;
            } else {
                // Found at least one unique subarray, try smaller length
                ans = mid;
                end = mid - 1;
            }
        }

        return ans;

    }

    /**
     * Checks if all subarrays of given length are duplicates (appear more than once).
     *
     * Uses polynomial rolling hash technique:
     * - Hash = (a[0] * p^(len-1) + a[1] * p^(len-2) + ... + a[len-1]) % m
     * - When sliding window, remove leftmost element and add new rightmost element
     * - Uses double hashing (two different moduli) to minimize false positives
     *
     * @param nums input array
     * @param len length of subarrays to check
     * @param n total length of nums array
     * @return true if all subarrays of this length appear more than once (all duplicates)
     *         false if at least one subarray is unique (appears exactly once)
     */
    private boolean duplicate(int nums[], int len, int n) {

        // Map to store hash -> count of subarrays with that hash
        Map<String, Integer> map = new HashMap<>();

        int h1 = 0;  // First hash value (modulo m1)
        int h2 = 0;  // Second hash value (modulo m2)

        // Pre-compute p^(len-1) for both moduli (used for removing leftmost element)
        int p1 = pow(p, len - 1, m1);
        int p2 = pow(p, len - 1, m2);

        // Sliding window approach with rolling hash
        for (int i = 0, j = 0; i < n; i++) {
            // Add new element to the right of window
            // h = h * p + nums[i] (multiply existing hash by base, add new element)
            h1 = add(prod(h1, p, m1), nums[i], m1);
            h2 = add(prod(h2, p, m2), nums[i], m2);

            // Once we have a complete window of size 'len'
            if (i >= len - 1) {
                // Combine both hashes as a string key
                String h = h1 + ":" + h2;
                // Increment count for this hash (subarray pattern)
                map.merge(h, 1, Integer::sum);

                // Remove leftmost element from window for next iteration
                // h = h - nums[j] * p^(len-1)
                h1 = add(h1, -prod(p1, nums[j], m1), m1);
                h2 = add(h2, -prod(p2, nums[j], m2), m2);

                j++;  // Move left pointer of window
            }
        }

        // Find the minimum count among all subarray hashes
        int min = map.values().stream().min(Integer::compareTo).get();

        // If min > 1, all subarrays appeared more than once (all duplicates)
        // If min == 1, at least one subarray is unique
        return min > 1;
    }

    /**
     * Fast modular exponentiation using binary exponentiation (exponentiation by squaring).
     * Computes (a^b) % m efficiently in O(log b) time.
     *
     * Algorithm:
     * - If b is odd, multiply result by a and reduce b by 1
     * - Square a and halve b in each iteration
     * - Example: a^13 = a^1 * a^4 * a^8 (binary: 13 = 1101)
     *
     * @param a base
     * @param b exponent
     * @param m modulus
     * @return (a^b) % m
     */
    private int pow(int a, int b, int m) {

        int ans = 1;
        while (b > 0) {
            // If b is odd, multiply current power of a into result
            if (b % 2 == 1) {
                ans = prod(ans, a, m);
            }

            // Square the base for next iteration
            a = prod(a, a, m);
            // Halve the exponent
            b >>= 1;
        }
        return ans;
    }

    /**
     * Modular addition: computes (a + b) % m safely.
     * Adds m to ensure result is positive even if a or b is negative.
     *
     * @param a first operand
     * @param b second operand
     * @param m modulus
     * @return (a + b) % m (always non-negative)
     */
    private int add(int a, int b, int m) {

        // Add m to handle negative values (important for hash removal operations)
        long s = (1l * a + b + m);

        return (int) (s % m);
    }

    /**
     * Modular multiplication: computes (a * b) % m safely.
     * Uses long to prevent integer overflow.
     *
     * @param a first operand
     * @param b second operand
     * @param m modulus
     * @return (a * b) % m
     */
    private int prod(int a, int b, int m) {

        // Use long to prevent overflow
        long p = 1l * a * b;

        return (int) (p % m);
    }
}