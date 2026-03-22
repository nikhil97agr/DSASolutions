package Leetcode;// Problem Link: https://leetcode.com/problems/count-the-number-of-incremovable-subarrays-ii

/**
 * Solution for Count the Number of Incremovable Subarrays II
 *
 * An "incremovable subarray" is a subarray that, when removed, makes the remaining array strictly increasing.
 *
 * Example: [1, 2, 3, 4] - Remove []: [1, 2, 3, 4] - strictly increasing ✓ - Remove [1]: [2, 3, 4] - strictly increasing
 * ✓ - Remove [2]: [1, 3, 4] - strictly increasing ✓ - All subarrays are incremovable
 *
 * Example: [6, 3, 7, 6] - Remove [3, 7, 6]: [6] - strictly increasing ✓ - Remove [6, 3]: [7, 6] - NOT strictly
 * increasing ✗
 *
 * Approach: Two Pointers - Find the longest strictly increasing prefix - Find the longest strictly increasing suffix -
 * Count valid removals by combining prefix and suffix
 *
 * Key Insight: The array can be split into three parts: [strictly increasing prefix] [middle to remove] [strictly
 * increasing suffix]
 */
public class CountTheNumberOfIncremovalSubarrayII {

    /**
     * Counts the number of incremovable subarrays
     *
     * @param nums The input array
     * @return Count of incremovable subarrays
     */
    public long incremovableSubarrayCount(int[] nums) {

        int n = nums.length;

        // Find the longest strictly increasing prefix
        // start points to the last element of the strictly increasing prefix
        int start = 0;
        while (start + 1 < n && nums[start] < nums[start + 1]) {
            start++;
        }

        // Special case: entire array is strictly increasing
        // Any subarray removal will keep it strictly increasing
        // Total subarrays = n*(n+1)/2 (including empty subarray)
        if (start == n - 1) {
            return 1l * n * (n + 1) / 2;
        }

        // Count removals that keep only the prefix (remove from start+1 to end)
        // We can remove:
        // - [start+1, n-1] (remove everything after prefix)
        // - [start, n-1] (remove last element of prefix and everything after)
        // - [start-1, n-1] (remove last 2 elements of prefix and everything after)
        // - ... and so on
        // Total: start + 2 options (including removing entire array)
        long ans = start + 2;

        // Process the suffix: find strictly increasing suffix and count valid combinations
        for (int i = n - 1; i >= 0; i--) {
            // Check if current element maintains strictly increasing suffix
            if (i < n - 1 && nums[i] >= nums[i + 1]) {
                break; // Suffix is no longer strictly increasing
            }

            // Adjust start pointer to ensure prefix[start] < suffix[i]
            // This ensures that when we remove middle part, the remaining array is strictly increasing
            while (start >= 0 && nums[start] >= nums[i]) {
                start--;
            }

            // Count valid removals:
            // We can remove any subarray that starts after index 'start' and ends before index 'i'
            // Options: remove [start+1, i-1], [start, i-1], [start-1, i-1], ..., [0, i-1]
            // Total: start + 2 options
            ans += start + 2;
        }

        return ans;
    }
}