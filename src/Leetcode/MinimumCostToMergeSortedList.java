package Leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Problem Link: https://leetcode.com/problems/minimum-cost-to-merge-sorted-lists/

/**
 * Solution for finding minimum cost to merge multiple sorted lists into one.
 *
 * Problem: Given n sorted lists, merge them into one sorted list. Each merge operation of two groups has a cost: - cost
 * = size1 + size2 + |median1 - median2| where size = total number of elements, median = middle element when sorted
 *
 * Key insight: Bitmask DP with optimal substructure - Use bitmask to represent which lists are merged together - For
 * each subset of lists, find the optimal way to partition and merge - The cost of merging two groups depends on their
 * sizes and medians
 *
 * Why this approach? - Different merge orders give different costs (not associative) - Need to try all possible ways to
 * partition lists - Bitmask DP ensures we compute each subset exactly once
 *
 * Example: lists = [[1,3], [2,4], [5,6]] - Merge [1,3] + [2,4] first: cost = 2 + 2 + |2-3| = 5, result = [1,2,3,4] -
 * Then merge [1,2,3,4] + [5,6]: cost = 4 + 2 + |3-5.5| = 8.5 - Total cost depends on merge order
 *
 * Algorithm: 1. Precompute for each subset (mask): - size[mask]: Total elements in lists represented by mask -
 * median[mask]: Median value of all elements in mask 2. DP: For each mask representing a subset of lists: - Try all
 * ways to partition into two non-empty groups - Cost = dp[group1] + dp[group2] + size[group1] + size[group2] +
 * |median[group1] - median[group2]| - Take minimum over all partitions 3. Answer: dp[all lists]
 *
 * Time Complexity: O(3^n + n*2^n*log(max_value)) where n = number of lists 3^n for iterating submasks, log for median
 * calculation Space Complexity: O(2^n) for DP array and precomputed values
 */
public class MinimumCostToMergeSortedList {

    /**
     * Finds the minimum cost to merge all sorted lists into one.
     *
     * @param lists Array of sorted lists where lists[i] is a sorted array
     * @return Minimum total cost to merge all lists
     */
    public long minMergeCost(int[][] lists) {

        int n = lists.length;

        // maxMask = 2^n, representing all possible subsets of lists
        int maxMask = (1 << n);

        // Precompute size and median for each subset of lists
        int size[] = new int[maxMask];      // size[mask] = total elements in subset
        long median[] = new long[maxMask];  // median[mask] = median value in subset

        // ================================================================
        // STEP 1: Precompute size and median for all subsets
        // ================================================================
        for (int mask = 1; mask < maxMask; mask++) {
            size[mask] = getSize(lists, mask, n);
            median[mask] = getMedian(lists, mask, n, size[mask]);

        }

        // dp[mask] = minimum cost to merge all lists in subset represented by mask
        long dp[] = new long[maxMask];

        // ================================================================
        // STEP 2: DP to compute minimum merge cost for each subset
        // ================================================================
        for (int mask = 0; mask < maxMask; mask++) {
            // Skip single lists (no merge needed, cost = 0)
            // (mask & (mask - 1)) == 0 checks if mask is a power of 2 (single bit set)
            if ((mask & (mask - 1)) == 0) {
                continue;
            }

            long min = Long.MAX_VALUE;

            // Iterate through all non-empty proper subsets of mask
            // This technique generates all submasks
            int subMask = (mask - 1) & mask;

            while (subMask > 0) {
                // prev = complement of subMask within mask
                // mask = subMask | prev (partition into two groups)
                int prev = mask ^ subMask;

                // Avoid counting same partition twice (subMask, prev) and (prev, subMask)
                if (subMask < prev) {
                    // Cost to merge these two groups:
                    // 1. dp[subMask]: cost to form group 1
                    // 2. dp[prev]: cost to form group 2
                    // 3. size[subMask] + size[prev]: merging cost based on sizes
                    // 4. |median[prev] - median[subMask]|: penalty for median difference
                    long c = dp[subMask] + dp[prev] + size[subMask] + size[prev] + Math.abs(
                            median[prev] - median[subMask]);
                    min = Math.min(min, c);
                }

                // Generate next submask
                // This efficiently iterates through all submasks of mask
                subMask = (subMask - 1) & mask;
            }

            dp[mask] = min;


        }

        // Return cost to merge all lists (mask with all bits set)
        return dp[maxMask - 1];
    }

    /**
     * Finds the median value of all elements in lists represented by mask.
     *
     * Uses binary search on the answer instead of merging all lists. For a sorted merged list, the median is at
     * position (size+1)/2.
     *
     * Strategy: - Binary search on the value range [min, max] - For each candidate value, count how many elements are
     * <= it - If count >= (size+1)/2, the median is <= candidate
     *
     * Why this works: - Lists are sorted, so we can use binary search within each list - Count of elements <= value
     * gives us position in merged list - Find minimum value with count >= median position
     *
     * @param lists All lists
     * @param mask Bitmask representing which lists to consider
     * @param n Number of lists
     * @param size Total number of elements in selected lists
     * @return Median value of all elements in selected lists
     */
    private int getMedian(int lists[][], int mask, int n, int size) {

        // Collect all lists represented by mask
        List<int[]> list = new ArrayList<>();
        int requiredSize = (size + 1) / 2;  // Position of median in sorted order
        int max = Integer.MIN_VALUE;  // Maximum value across all selected lists
        int min = Integer.MAX_VALUE;  // Minimum value across all selected lists

        for (int i = 0; i < n; i++) {
            int bit = (mask >> i) & 1;
            if (bit == 1) {
                // List i is included in this subset
                list.add(lists[i]);
                max = Math.max(max, Arrays.stream(lists[i]).max().getAsInt());
                min = Math.min(min, Arrays.stream(lists[i]).min().getAsInt());
            }
        }

        // Binary search for the median value in range [min, max]
        while (min < max) {
            int mid = min + (max - min) / 2;

            // Count how many elements are <= mid
            int cnt = getCnt(list, mid);

            if (cnt >= requiredSize) {
                // At least requiredSize elements are <= mid
                // So median must be <= mid
                max = mid;
            } else {
                // Fewer than requiredSize elements are <= mid
                // So median must be > mid
                min = mid + 1;
            }
        }

        return max;  // The median value


    }

    /**
     * Counts how many elements across all lists are <= val.
     *
     * Uses binary search within each sorted list to count efficiently.
     *
     * @param list Collection of sorted lists
     * @param val Value to compare against
     * @return Total count of elements <= val across all lists
     */
    private int getCnt(List<int[]> list, int val) {

        var total = 0;

        // For each sorted list, use binary search to find count of elements <= val
        for (var l : list) {
            var start = 0;
            var end = l.length - 1;
            var size = 0;  // Count of elements <= val in this list

            // Binary search for the rightmost element <= val
            while (start <= end) {
                var mid = (start + end) >> 1;

                if (l[mid] <= val) {
                    // mid is <= val, count includes all elements up to mid
                    size = mid + 1;
                    start = mid + 1;  // Search right for potentially more elements
                } else {
                    // mid is > val, search left
                    end = mid - 1;
                }
            }

            total += size;
        }

        return total;
    }


    /**
     * Calculates the total number of elements in lists represented by mask.
     *
     * @param lists All lists
     * @param mask Bitmask representing which lists to include
     * @param n Number of lists
     * @return Total number of elements in selected lists
     */
    private int getSize(int[][] lists, int mask, int n) {

        int size = 0;

        // Sum up lengths of all lists where corresponding bit is set
        for (int i = 0; i < n; i++) {
            int bit = (mask >> i) & 1;
            if (bit == 1) {
                // List i is included in this subset
                size += lists[i].length;
            }
        }

        return size;
    }


}