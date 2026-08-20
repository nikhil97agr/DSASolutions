package Leetcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Problem Link: https://leetcode.com/problems/count-subarrays-with-even-odd-ratio-ii/

/*
 * PROBLEM STATEMENT:
 * Given an integer array nums and two integers a and b, find the number of valid subarrays.
 *
 * For a subarray with x even elements and y odd elements:
 * - The subarray is valid if y > 0 and x/y <= a/b
 *
 * APPROACH:
 * This solution transforms the ratio comparison into a linear inequality:
 *
 * 1. Mathematical Transformation:
 *    x/y <= a/b  (where y > 0)
 *    => x*b <= y*a  (cross multiply, b > 0)
 *    => x*b - y*a <= 0
 *
 * 2. Key Insight:
 *    For a subarray [i, j], we need: (x[j] - x[i-1])*b - (y[j] - y[i-1])*a <= 0
 *    Rearranging: x[j]*b - y[j]*a <= x[i-1]*b - y[i-1]*a
 *
 *    So we can define val[i] = x[i]*b - y[i]*a and count pairs where val[j] <= val[i-1]
 *
 * 3. Grouping Strategy:
 *    Since y > 0 is required, we group indices by their odd count (y value).
 *    We only consider pairs (i, j) where y[j] > y[i-1], ensuring y > 0 for the subarray.
 *
 * 4. Efficient Counting:
 *    Maintain a sorted list of val values from previous odd count groups.
 *    For each position, binary search to count how many previous values are >= current val.
 *
 * TIME COMPLEXITY: O(n^2 log n) in worst case, O(n log n) on average
 * SPACE COMPLEXITY: O(n)
 */
public class CountSubarraysWithEvenOddRatioII {

    /**
     * Counts the number of valid subarrays where x/y <= a/b and y > 0
     *
     * @param nums - input integer array
     * @param a - numerator of the ratio threshold
     * @param b - denominator of the ratio threshold
     * @return count of valid subarrays
     */
    public long countRatioSubarrays(int[] nums, int a, int b) {

        long ans = 0;           // Total count of valid subarrays
        long prev = 0;          // Previous odd count (y value) to detect group changes

        // temp: stores val values for the current odd count group
        List<Long> temp = new ArrayList<>();
        // list: stores sorted val values from all previous odd count groups
        List<Long> list = new ArrayList<>();

        long x = 0;             // Running count of even elements
        long y = 0;             // Running count of odd elements

        // Initialize with val = 0 for empty prefix (to count subarrays starting at index 0)
        temp.add(0L);

        // Process each element in the array
        for (int num : nums) {
            // Update running counts based on parity
            if (num % 2 == 0) {
                x++;            // Even element
            } else {
                y++;            // Odd element
            }

            // Calculate the transformed value: x*b - y*a
            // A subarray ending here is valid if this val <= previous val from earlier odd count
            long val = x * b - y * a;

            // Check if we're still in the same odd count group
            if (y == prev) {
                // Same group - just add the current val to temp
                temp.add(val);
            } else {
                // New odd count group detected - merge previous group into main list
                update(temp, list);
                prev = y;
                temp.clear();
                temp.add(val);
            }

            // Count how many previous values (from earlier odd counts) are >= current val
            // These represent valid subarrays ending at current position
            int cnt = search(list, val);

            ans += cnt;
        }

        return ans;
    }

    /**
     * Merges values from temp into the main sorted list while maintaining sort order. This is called when transitioning
     * to a new odd count group.
     *
     * For each value in temp, uses binary search to find the correct insertion position in the already-sorted list,
     * then inserts it there.
     *
     * @param temp - list of values from the previous odd count group
     * @param list - main sorted list accumulating all values from completed groups
     */
    private void update(List<Long> temp, List<Long> list) {

        // Insert each value from temp into the sorted list
        for (var x : temp) {
            // Binary search returns index if found, or (-(insertion point) - 1) if not found
            var ind = Collections.binarySearch(list, x);

            if (ind < 0) {
                // Value not found - convert to insertion point
                // Formula: insertion_point = -ind - 1
                ind = -ind - 1;
            }

            // Insert the value at the correct position to maintain sorted order
            if (ind == list.size()) {
                // Insert at end
                list.add(x);
            } else {
                // Insert at specific position
                list.add(ind, x);
            }
        }
    }

    /**
     * Performs binary search to count how many elements in the sorted list are >= val.
     *
     * This finds the leftmost position where list[i] >= val, then returns the count of all elements from that position
     * to the end.
     *
     * Why count >= val? For a subarray [i, j] to be valid: - val[j] <= val[i-1]  (from our transformation) -
     * Equivalently: val[i-1] >= val[j] So we count previous positions with val >= current val.
     *
     * @param list - sorted list of val values from previous odd count groups
     * @param val - current transformed value (x*b - y*a)
     * @return count of values in list that are >= val
     */
    private int search(List<Long> list, long val) {

        int n = list.size();
        int start = 0;
        int end = n - 1;
        int ans = 0;  // Count of elements >= val

        // Binary search to find the leftmost element >= val
        while (start <= end) {
            int mid = (start + end) / 2;

            if (list.get(mid) >= val) {
                // Found a valid position - all elements from mid to end are >= val
                ans = n - mid;
                // Try to find an even earlier position
                end = mid - 1;
            } else {
                // list[mid] < val, search in the right half
                start = mid + 1;
            }
        }

        return ans;
    }
}