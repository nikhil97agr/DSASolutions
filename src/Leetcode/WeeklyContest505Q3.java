package Leetcode;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * Leetcode.WeeklyContest505Q3 - Maximum Sum with m Subarrays
 *
 * This class solves the problem of selecting at most m non-overlapping subarrays from an array, where each subarray has
 * length between l and r (inclusive), to maximize the total sum.
 *
 * The solution uses: 1. Dynamic Programming with state optimization 2. Monotonic Deque for range maximum queries 3.
 * Prefix sums for efficient subarray sum calculation
 *
 * Key insight: Use DP where dp[k][i] = maximum sum using at most k subarrays from the first i elements.
 */
public class WeeklyContest505Q3 {

    /**
     * Finds the maximum sum by selecting at most m non-overlapping subarrays, where each subarray has length between l
     * and r (inclusive).
     *
     * Algorithm: - Use dynamic programming with monotonic deque optimization - dp[k][i] = max sum using at most k
     * subarrays from first i elements - Transition: Either skip element i, or end a subarray at position i - For each
     * ending position i, consider all valid starting positions [i-r, i-l] - Use monotonic deque to efficiently find the
     * best starting position
     *
     * Time Complexity: O(m * n) where m is max subarrays, n is array length Space Complexity: O(n) - using
     * space-optimized DP (only prev and next arrays)
     *
     * @param nums input array of integers
     * @param m maximum number of subarrays to select
     * @param l minimum length of each subarray
     * @param r maximum length of each subarray
     * @return maximum possible sum
     */
    public long maximumSum(int[] nums, int m, int l, int r) {

        // Use a large negative value to represent impossible states
        long neg = Long.MIN_VALUE / 4;
        long ans = -1_000_000_000_000_000L;  // Initialize answer to very small value
        var n = nums.length;

        // Prefix sum array: pre[i] = sum of nums[0] to nums[i-1]
        // This allows O(1) calculation of any subarray sum
        long pre[] = new long[n + 1];
        for (int i = 1; i <= n; i++) {
            pre[i] = pre[i - 1] + nums[i - 1];
        }

        // DP state: prev[i] = max sum using k-1 subarrays from first i elements
        long prev[] = new long[n + 1];

        // Iterate through number of subarrays (1 to m)
        for (int k = 1; k <= m; k++) {
            // DP state: next[i] = max sum using k subarrays from first i elements
            long next[] = new long[n + 1];
            Arrays.fill(next, neg);  // Initialize to impossible value

            // Monotonic deque to maintain maximum of (prev[j] - pre[j])
            // for valid starting positions j in the current window
            Deque<Integer> que = new ArrayDeque<>();

            // Process each position i in the array
            for (int i = 1; i <= n; i++) {
                // Option 1: Don't use element i in a subarray (carry forward previous value)
                next[i] = next[i - 1];

                // Option 2: End a subarray at position i
                // Consider all valid starting positions [i-r, i-l]

                // Add new potential starting position to deque
                int ind = i - l;  // Maximum starting position for subarray ending at i
                if (ind >= 0) {
                    // Value represents: max sum with k-1 subarrays up to position ind,
                    // minus the prefix sum at ind (will be added back when calculating)
                    long value = prev[ind] - pre[ind];

                    // Maintain monotonic decreasing deque
                    // Remove elements from back that are smaller or equal to current value
                    while (!que.isEmpty()) {
                        int last = que.peekLast();
                        long val = prev[last] - pre[last];

                        if (val <= value) {
                            que.pollLast();  // Remove worse option
                        } else {
                            break;
                        }
                    }
                    que.offerLast(ind);  // Add current index
                }

                // Remove indices from front that are out of valid range
                // Valid starting positions for ending at i: [i-r, i-l]
                int range = i - r;  // Minimum starting position
                while (!que.isEmpty() && que.peekFirst() < range) {
                    que.pollFirst();  // Remove indices that are too far left
                }

                // Calculate maximum sum by ending a subarray at position i
                if (!que.isEmpty()) {
                    int t = que.peekFirst();  // Best starting position in valid range

                    // Sum from position t to i = pre[i] - pre[t]
                    // Total value = (max sum with k-1 subarrays up to t) + (sum from t to i)
                    //             = prev[t] + (pre[i] - pre[t])
                    //             = pre[i] + (prev[t] - pre[t])
                    next[i] = Math.max(
                            next[i],
                            pre[i] + (prev[t] - pre[t])
                    );
                }
            }

            // Update answer with maximum sum using k subarrays
            ans = Math.max(ans, next[n]);

            // Prepare for next iteration (k+1 subarrays)
            prev = next;
        }

        return ans;
    }
}