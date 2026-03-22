package Leetcode;
// Problem Link : https://leetcode.com/problems/remove-boxes

/**
 * Solution for Remove Boxes
 *
 * Problem: Given an array of colored boxes, you can remove a continuous group of boxes of the same color and get points
 * = k*k (where k is the number of boxes removed). Find the maximum points you can get.
 *
 * Example: boxes = [1, 3, 2, 2, 2, 3, 4, 3, 1]
 *
 * One optimal strategy: 1. Remove [3, 2, 2, 2, 3] -> get 1*1 = 1 point -> [1, 4, 3, 1] 2. Remove [4] -> get 1*1 = 1
 * point -> [1, 3, 1] 3. Remove [3] -> get 1*1 = 1 point -> [1, 1] 4. Remove [1, 1] -> get 2*2 = 4 points -> [] Total: 1
 * + 1 + 1 + 4 = 7 points
 *
 * Better strategy: 1. Remove middle [2, 2, 2] -> get 3*3 = 9 points -> [1, 3, 3, 4, 3, 1] 2. Remove [3, 3, 3] -> get
 * 3*3 = 9 points -> [1, 4, 1] 3. Remove [1, 1] -> get 2*2 = 4 points -> [4] 4. Remove [4] -> get 1*1 = 1 point -> []
 * Total: 9 + 9 + 4 + 1 = 23 points
 *
 * Key Insight: Sometimes it's better to remove boxes in the middle first to merge boxes of the same color that are
 * separated, creating larger groups.
 *
 * Approach: Interval DP with Extra Dimension - State: dp[start][end][cnt] = max points for boxes[start..end] with 'cnt'
 * boxes of the same color as boxes[start] attached to the left - The 'cnt' parameter tracks boxes that can be merged
 * with boxes[start]
 *
 * Strategy: 1. Remove all boxes starting from 'start' along with 'cnt' attached boxes 2. Or, find a box at position j
 * (j > start) with same color as boxes[start], remove boxes[start+1..j-1] first, then merge boxes[start] with boxes[j]
 */
public class RemoveBoxes {

    // DP memoization table: dp[start][end][count]
    // dp[i][j][k] = max points for boxes[i..j] with k boxes of color boxes[i] attached to the left
    Integer dp[][][];

    /**
     * Calculates maximum points from removing boxes
     *
     * @param boxes Array of colored boxes (represented by integers)
     * @return Maximum points achievable
     */
    public int removeBoxes(int[] boxes) {

        int n = boxes.length;

        // Initialize DP table
        // dp[start][end][cnt] where cnt can be at most n-1
        dp = new Integer[n][n][n];

        // Start with range [0, n-1] and 0 attached boxes
        return solve(boxes, 0, n - 1, 0);
    }

    /**
     * Recursive function to calculate maximum points using interval DP
     *
     * @param nums The boxes array
     * @param start Start index of current range
     * @param end End index of current range
     * @param cnt Number of boxes with same color as nums[start] attached to the left (these boxes were removed from
     * earlier positions but can be merged)
     * @return Maximum points for this subproblem
     */
    private int solve(int nums[], int start, int end, int cnt) {

        // Base case: empty range
        if (start > end) {
            return 0;
        }

        // Return memoized result if already computed
        if (dp[start][end][cnt] != null) {
            return dp[start][end][cnt];
        }

        // Optimization: merge consecutive boxes of the same color at the start
        // This reduces redundant computation
        int l = start;
        int r = end;
        int c = cnt;

        // Count consecutive boxes of the same color starting from 'start'
        while (l + 1 <= r && nums[l] == nums[l + 1]) {
            c++;  // Increment count of boxes that can be merged
            l++;  // Move to next position
        }

        // Strategy 1: Remove all boxes from start to l (inclusive) along with cnt attached boxes
        // Points = (c + 1)^2 because we have (c + 1) boxes of the same color
        // Then solve for the remaining range [l+1, r]
        int ans = (c + 1) * (c + 1) + solve(nums, l + 1, r, 0);

        // Strategy 2: Try to find boxes with same color as nums[l] later in the array
        // and merge them by removing boxes in between first
        for (int j = l + 1; j <= r; j++) {
            // If we find a box with same color as nums[l]
            if (nums[j] == nums[l]) {
                // Remove boxes[l+1..j-1] first (get points for that)
                // Then solve boxes[j..r] with (c+1) attached boxes of color nums[l]
                // This allows merging nums[l] with nums[j] later for potentially more points
                ans = Math.max(ans,
                        solve(nums, l + 1, j - 1, 0) + solve(nums, j, r, c + 1)
                );
            }
        }

        // Memoize and return the result
        return dp[start][end][cnt] = ans;
    }
}