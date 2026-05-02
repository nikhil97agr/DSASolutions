package Leetcode;

import java.util.ArrayDeque;
import java.util.Arrays;

//Problem Link: https://leetcode.com/problems/minimum-partition-score/

/**
 * Solution for minimizing partition score when splitting array into k parts.
 *
 * Problem: Given an array nums and integer k, partition the array into k non-empty contiguous subarrays. The score of a
 * partition is: - For each subarray with sum S, add S*(S+1)/2 to the score - Minimize the total score
 *
 * Example: nums = [1, 2, 1, 2], k = 2 - Partition 1: [1,2] [1,2] → sum=[3,3] → score = 3*4/2 + 3*4/2 = 6+6 = 12 -
 * Partition 2: [1] [2,1,2] → sum=[1,5] → score = 1*2/2 + 5*6/2 = 1+15 = 16 - Partition 3: [1,2,1] [2] → sum=[4,2] →
 * score = 4*5/2 + 2*3/2 = 10+3 = 13 - Minimum: 12
 *
 * Key insight: DP + Convex Hull Trick
 *
 * DP formulation: - dp[j][i] = minimum score to partition nums[0..j-1] into i parts - Transition: dp[j+1][i] = min over
 * all p (dp[p][i-1] + cost of subarray [p, j]) - cost(p, j) = sum[p, j] * (sum[p, j] + 1) / 2
 *
 * Cost function expansion: Let S = sum[p, j] = pre[j+1] - pre[p] cost = S * (S + 1) / 2 = S² / 2 + S / 2
 *
 * Rewriting DP transition: dp[j+1][i] = min_p (dp[p][i-1] + (pre[j+1] - pre[p])² / 2 + (pre[j+1] - pre[p]) / 2)
 *
 * Expanding: = min_p (dp[p][i-1] + pre[j+1]²/2 - pre[j+1]*pre[p] + pre[p]²/2 + pre[j+1]/2 - pre[p]/2)
 *
 * Rearranging (grouping by p and j+1): = pre[j+1]²/2 + pre[j+1]/2 + min_p (dp[p][i-1] + pre[p]²/2 - pre[p]/2 -
 * pre[j+1]*pre[p]) = val(pre[j+1]) + min_p ((pre[p]²/2 - pre[p]/2 + dp[p][i-1]) - pre[j+1]*pre[p])
 *
 * Convex Hull Trick format: y = mx + c - m = -pre[p] - x = pre[j+1] - c = pre[p]²/2 - pre[p]/2 + dp[p][i-1] =
 * val(-pre[p]) + dp[p][i-1]
 *
 * For each j+1, we want the line with minimum value at x = pre[j+1]. Since pre[] is increasing (all non-negative), we
 * can use convex hull trick to maintain a set of "useful" lines.
 *
 * Convex Hull Trick: - Maintain lines in a deque sorted by slope (decreasing) - For query at x, remove lines from front
 * that are dominated - For insertion, remove lines from back that become dominated
 *
 * Time Complexity: O(n * k) - each element added/removed from deque once per k Space Complexity: O(n * k) for DP table
 */
public class MinimumPartitionScore {

    /**
     * Finds minimum partition score when splitting into k parts.
     *
     * @param nums Array of integers
     * @param k Number of partitions
     * @return Minimum score
     */
    public long minPartitionScore(int[] nums, int k) {

        var n = nums.length;

        // ================================================================
        // STEP 1: Build prefix sum array
        // ================================================================
        // pre[i] = sum of nums[0..i-1]
        var pre = new long[n + 1];
        for (var i = 0; i < n; i++) {
            pre[i + 1] = pre[i] + nums[i];
        }

        // ================================================================
        // STEP 2: Initialize DP table
        // ================================================================
        // dp[j][i] = min score to partition nums[0..j-1] into i parts
        var dp = new long[n + 1][k + 1];
        for (var d : dp) {
            Arrays.fill(d, Long.MAX_VALUE);
        }
        var max = Long.MAX_VALUE;
        dp[0][0] = 0;  // Base case: 0 elements in 0 parts

        // ================================================================
        // STEP 3: Fill DP table using convex hull trick
        // ================================================================
        for (int i = 1; i <= k; i++) {
            // For each number of partitions i, maintain convex hull of lines
            var que = new ArrayDeque<long[]>();  // Deque of [slope, intercept]

            for (var j = i - 1; j < n; j++) {
                // ========================================================
                // Add new line to convex hull
                // ========================================================
                // Can transition from dp[j][i-1] to dp[j+1][i]
                if (dp[j][i - 1] != max) {
                    var sum = pre[j];
                    // Line: y = (-sum) * x + (val(-sum) + dp[j][i-1])
                    // slope = -sum (negative, so slopes are decreasing as sum increases)
                    // intercept = val(-sum) + dp[j][i-1]
                    que.offer(new long[]{-sum, val(-sum) + dp[j][i - 1]});

                    // ====================================================
                    // Remove lines from back that are now dominated
                    // ====================================================
                    // Convex hull maintenance: remove middle line if it's above
                    // the line connecting first and third
                    while (que.size() >= 3) {
                        var last = que.pollLast();
                        var secondLast = que.pollLast();
                        var thirdLast = que.pollLast();

                        // Check if secondLast is useless (dominated by thirdLast and last)
                        if (check(thirdLast, secondLast, last)) {
                            // secondLast is dominated, remove it
                            que.offer(thirdLast);
                            que.offer(last);
                        } else {
                            // secondLast is useful, keep all three
                            que.offerLast(thirdLast);
                            que.offerLast(secondLast);
                            que.offerLast(last);
                            break;
                        }
                    }
                }
                // ====================================================
                // Query: find minimum value at x = pre[j+1]
                // ====================================================
                var sum = pre[j + 1];  // This is x in the linear function

                // Remove lines from front that are dominated by the next line
                while (que.size() >= 2) {
                    var a = que.pollFirst();  // Current best line
                    var b = que.pollFirst();  // Next line

                    // Evaluate both lines at x = sum
                    // If a gives worse value than b, remove a
                    if (a[0] * sum + a[1] >= b[0] * sum + b[1]) {
                        que.offerFirst(b);  // Keep b, discard a
                    } else {
                        // a is still better, keep both and stop
                        que.offerFirst(b);
                        que.offerFirst(a);
                        break;
                    }

                }

                // Best line is at front: evaluate at x = sum and add val(sum)
                dp[j + 1][i] = que.peekFirst()[0] * sum + que.peekFirst()[1] + val(sum);

            }
        }

        return dp[n][k];
    }

    /**
     * Checks if middle line (b) is dominated by lines a and c.
     *
     * Used to maintain convex hull: we want to keep only lines that form a convex lower hull.
     *
     * Mathematical explanation: - Lines: y = a[0]*x + a[1], y = b[0]*x + b[1], y = c[0]*x + c[1] - Slopes: a[0] > b[0]
     * > c[0] (decreasing slopes in our setup) - Line b is dominated if it's always above the line connecting a and c
     *
     * Intersection points: - Line a intersects line b at x = (b[1] - a[1]) / (a[0] - b[0]) - Line b intersects line c
     * at x = (c[1] - b[1]) / (b[0] - c[0])
     *
     * Line b is useful only if the first intersection happens before the second: (b[1] - a[1]) / (a[0] - b[0]) < (c[1]
     * - b[1]) / (b[0] - c[0])
     *
     * Cross-multiplying (slopes are negative, so inequality flips): (c[1] - a[1]) * (a[0] - b[0]) <= (b[1] - a[1]) *
     * (a[0] - c[0])
     *
     * If this holds, line b is dominated and should be removed.
     *
     * @param a First line [slope, intercept]
     * @param b Middle line [slope, intercept]
     * @param c Third line [slope, intercept]
     * @return true if b is dominated (should be removed)
     */
    private boolean check(long a[], long b[], long c[]) {

        // Check if middle line b is dominated
        var s1 = (c[1] - a[1]) * (a[0] - b[0]);
        var s2 = (b[1] - a[1]) * (a[0] - c[0]);
        return s1 <= s2;
    }


    /**
     * Calculates the cost function for a subarray sum.
     *
     * Formula: sum * (sum + 1) / 2
     *
     * This is the score contribution of a subarray with sum S. It equals 1 + 2 + 3 + ... + S.
     *
     * Can be expanded as: S²/2 + S/2
     *
     * @param sum Sum of a subarray
     * @return sum * (sum + 1) / 2
     */
    private long val(long sum) {

        return (sum * (sum + 1)) / 2;
    }
}