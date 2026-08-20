package Leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//Problem Link: https://leetcode.com/problems/lexicographically-maximum-mex-array/

/**
 * Solution to find maximum MEX (Minimum EXcluded) values for array partitions. MEX is the smallest non-negative integer
 * not present in a set.
 */
public class LexicographicallyMaximumMexArray {

    /**
     * Computes the maximum MEX value achievable for each partition of the array. Strategy: Greedily partition the array
     * to maximize MEX at each step.
     *
     * @param nums the input array
     * @return array containing maximum MEX value for each partition
     */
    public int[] maximumMEX(int[] nums) {

        int n = nums.length;
        int max = Arrays.stream(nums).max().getAsInt();

        // suff[i] = MEX of elements from index i to n-1
        int suff[] = new int[n];
        Set<Integer> vis = new HashSet<>();
        int mex = 0;

        // Build suffix MEX array by traversing from right to left
        for (int i = n - 1; i >= 0; i--) {
            vis.add(nums[i]);
            // Find MEX: smallest non-negative integer not in vis
            while (vis.contains(mex)) {
                mex++;
            }
            suff[i] = mex;
        }

        List<Integer> result = new ArrayList<>();

        // Greedily partition array to maximize MEX at each step
        for (int i = 0; i < n; ) {
            // If remaining elements have MEX 0, all partitions will have MEX 0
            if (suff[i] == 0) {
                while (i < n) {
                    result.add(0);
                    i++;
                }
                break;
            }

            // Try to achieve the maximum possible MEX for this partition
            Set<Integer> set = new HashSet<>();
            max = suff[i];  // Target MEX value
            int val = 0;    // Count of distinct values [0, max-1] found

            // Extend partition until we have all values [0, max-1]
            while (i < n && val < max) {
                // Only count values that contribute to MEX = max
                if (nums[i] < max && !set.contains(nums[i])) {
                    val++;
                    set.add(nums[i]);
                }
                i++;
            }

            // This partition achieves MEX = max
            result.add(max);
        }

        // Convert List<Integer> to int[]
        return result.stream().mapToInt(x -> x).toArray();
    }
}