package Leetcode;

import java.util.ArrayList;
import java.util.List;

// Problem Link: https://leetcode.com/problems/maximum-gcd-sum-of-a-subarray

/**
 * Solution for Maximum GCD-Sum of a Subarray problem
 *
 * Problem: Find the maximum value of (sum of subarray) * (GCD of subarray) where the subarray length is at least k.
 *
 * Approach: Dynamic Programming with GCD optimization - For each ending position i, maintain all possible GCDs of
 * subarrays ending at i - Use the property that GCD values decrease (or stay same) as we extend subarrays - The number
 * of distinct GCD values is at most log(max_element) due to GCD properties
 */
public class MaximumGcdSumOfSubarray {

    /**
     * Finds the maximum GCD-Sum value for subarrays of length at least k
     *
     * @param nums The input array
     * @param k Minimum length of subarray
     * @return Maximum value of (subarray sum) * (subarray GCD)
     */
    public long maxGcdSum(int[] nums, int k) {

        long ans = 0;
        int n = nums.length;

        // Build prefix sum array for O(1) range sum queries
        // pre[i] = sum of nums[0..i-1]
        long pre[] = new long[n + 1];
        for (int i = 0; i < n; i++) {
            pre[i + 1] = pre[i] + nums[i];
        }

        // prev stores pairs of [gcd, startIndex] for all subarrays ending at previous position
        // Each pair represents: a subarray with this GCD starting from startIndex
        List<int[]> prev = new ArrayList<>();

        // Process each position as the ending point of subarrays
        for (int i = 0; i < n; i++) {
            // next will store all distinct GCD values for subarrays ending at position i
            List<int[]> next = new ArrayList<>();
            int len = 1;

            // Add current element as a subarray of length 1
            next.add(new int[]{nums[i], i});

            // Extend all subarrays from previous position by including nums[i]
            for (int p[] : prev) {
                int g = gcd(p[0], nums[i]);

                // If this GCD is same as the last one in next, merge them
                // Keep the minimum start index to maximize subarray length
                if (g == next.get(len - 1)[0]) {
                    next.get(len - 1)[1] = Math.min(next.get(len - 1)[1], p[1]);
                }
                // Otherwise, add a new GCD group
                else {
                    next.add(new int[]{g, p[1]});
                    len++;
                }
            }

            // Calculate GCD-Sum for all valid subarrays ending at position i
            for (int p[] : next) {
                int gcd = p[0];        // GCD of the subarray
                int start = p[1];      // Starting index of the subarray
                int l = i - start + 1; // Length of the subarray

                // Skip if subarray length is less than k
                if (l < k) {
                    continue;
                }

                // Calculate sum of subarray [start, i] using prefix sum
                long sum = pre[i + 1] - pre[start];

                // Update answer with (sum * GCD)
                ans = Math.max(ans, sum * gcd);
            }

            // Update prev for next iteration
            prev = next;
        }

        return ans;
    }

    /**
     * Calculates the Greatest Common Divisor (GCD) of two numbers using Euclidean algorithm
     *
     * @param a First number
     * @param b Second number
     * @return GCD of a and b
     */
    private int gcd(int a, int b) {

        // Base case: if a is 0, GCD is b
        if (a == 0) {
            return b;
        }

        // Recursive case: gcd(a, b) = gcd(b % a, a)
        return gcd(b % a, a);
    }
}