package Leetcode;

import java.util.Map;
import java.util.TreeMap;

// Problem Link: https://leetcode.com/problems/count-stable-subarrays/

/**
 * Solution for counting stable subarrays.
 *
 * A subarray is "stable" if the maximum element in the subarray equals the last element. Given an array and multiple
 * range queries [l, r], count the total number of stable subarrays contained within nums[l..r].
 *
 * Key insights: 1. For each position i, find the leftmost position last[i] such that all subarrays ending at i with
 * start >= last[i] are stable 2. Use a sliding window to maintain elements where max equals current element 3.
 * Precompute prefix sums of stable subarray counts for efficient query answering 4. Use binary search to handle query
 * boundaries
 *
 * Time Complexity: O(n log n) for preprocessing + O(q log n) for queries Space Complexity: O(n)
 */
public class CountStableSubarrays {

    /**
     * Counts stable subarrays for each query range.
     *
     * @param nums Input array
     * @param queries Array of queries where queries[i] = [l, r]
     * @return Array where result[i] is the count of stable subarrays in nums[l..r]
     */
    public long[] countStableSubarrays(int[] nums, int[][] queries) {

        var n = nums.length;
        var q = queries.length;
        var ans = new long[q];

        // pre[i] = total count of stable subarrays ending at or before position i
        var pre = new long[n];

        // last[i] = leftmost position where a stable subarray can start and end at i
        var last = new int[n];

        // TreeMap to track frequency of elements in current window
        // The lastKey() gives us the maximum element in the window
        var map = new TreeMap<Integer, Integer>();

        // Sliding window: maintain window where max element equals nums[end]
        for (int start = 0, end = 0; end < n; end++) {
            add(nums[end], map);

            // Shrink window from left while max > nums[end]
            // We need max == nums[end] for stable subarrays ending at end
            while (map.lastKey() > nums[end]) {
                remove(nums[start], map);
                start++;

            }

            // All subarrays [start..end], [start+1..end], ..., [end..end] are stable
            // Count = end - start + 1
            long sub = end - start + 1;

            // Build prefix sum of stable subarray counts
            pre[end] = sub + (end - 1 >= 0 ? pre[end - 1] : 0);

            // Record the leftmost start position for stable subarrays ending at end
            last[end] = start;
        }

        // Process each query
        for (int i = 0; i < q; i++) {
            int l = queries[i][0];
            var r = queries[i][1];

            // Find the largest index in [l, r] where last[index] < l
            // This represents positions where stable subarrays might extend beyond l
            int ind = binarySearch(last, l, r);

            if (ind == -1) {
                // All positions in [l, r] have last[i] >= l
                // So all stable subarrays are completely within [l, r]
                ans[i] = pre[r] - (l - 1 >= 0 ? pre[l - 1] : 0);
            } else {
                // Positions [ind+1..r] have their stable subarrays within [l, r]
                ans[i] = pre[r] - pre[ind];

                // Positions [l..ind] have last[i] < l, so we count only subarrays
                // that start at or after l
                // For each position in [l..ind], count subarrays starting from l
                int len = ind - l + 1;

                // For a contiguous range of length len, where all can be end positions,
                // and all must start at position l or later:
                // Count = len + (len-1) + (len-2) + ... + 1 = len*(len+1)/2
                ans[i] += (1l * len * (len + 1)) / 2;
            }
        }

        return ans;
    }

    /**
     * Binary search to find the largest index in [l, r] where last[index] < s.
     *
     * This helps identify positions where stable subarrays extend before the query start.
     *
     * @param last Array where last[i] is the leftmost start for stable subarrays ending at i
     * @param l Query start position (also used as threshold value s)
     * @param r Query end position
     * @return Largest index in [l, r] where last[index] < l, or -1 if no such index exists
     */
    private int binarySearch(int last[], int l, int r) {

        int ind = -1;  // Result: largest index where last[index] < s
        int s = l;     // Threshold value to compare against

        while (l <= r) {
            int mid = (l + r) / 2;

            if (last[mid] < s) {
                // last[mid] < s, so mid is a candidate
                // Search right half for potentially larger index
                ind = mid;
                l = mid + 1;
            } else {
                // last[mid] >= s, search left half
                r = mid - 1;
            }
        }

        return ind;
    }

    /**
     * Adds an element to the frequency map.
     *
     * @param a Element value to add
     * @param map Frequency map
     */
    private void add(int a, Map<Integer, Integer> map) {

        map.merge(a, 1, Integer::sum);  // Increment frequency
    }

    /**
     * Removes an element from the frequency map.
     *
     * @param a Element value to remove
     * @param map Frequency map
     */
    private void remove(int a, Map<Integer, Integer> map) {

        map.merge(a, -1, Integer::sum);  // Decrement frequency
        if (map.get(a) == 0) {
            map.remove(a);  // Remove key if frequency becomes 0
        }
    }
}