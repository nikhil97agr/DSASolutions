package Leetcode;

import java.util.Stack;

//Problem Link: https://leetcode.com/problems/find-sorted-submatrices-with-maximum-element-at-most-k

/**
 * Solution for counting sorted submatrices with maximum element at most k.
 *
 * Problem: Given a 2D grid and integer k, count how many submatrices satisfy: 1. All elements <= k 2. Each row in the
 * submatrix is sorted (non-decreasing from left to right)
 *
 * Key insight: Column-by-column processing with histogram technique
 *
 * Algorithm approach: 1. Process columns from left to right (fixing the right boundary) 2. For each column as the right
 * boundary, build a histogram where: - histogram[row] = length of valid sorted sequence ending at grid[row][col] -
 * "Valid" means: all elements <= k and sequence is non-decreasing 3. For each histogram, count the number of rectangles
 * (uses stack-based technique)
 *
 * Example: grid = [[1,2,3], [2,3,4], [3,4,5]], k = 4
 *
 * Column 0 (i=0): histogram = [1, 1, 1] (all single elements) - Submatrices: [1], [2], [3], [1,2], [1,2,3] = 5 total
 *
 * Column 1 (i=1): histogram = [2, 2, 2] (sequences [1,2], [2,3], [3,4]) - Many more submatrices including multi-column
 * ones
 *
 * Column 2 (i=2): histogram = [3, 3, 0] (grid[2][2]=5 > k=4, so histogram[2]=0)
 *
 * Histogram counting technique: - For a histogram, count all possible rectangles - Uses monotonic stack to efficiently
 * compute contributions
 *
 * Time Complexity: O(n * m) where n = rows, m = columns Space Complexity: O(n) for histogram and stack
 */
public class FindSortedSubmatricesWithMaximumElementAtMostK {

    /**
     * Counts the number of valid sorted submatrices.
     *
     * @param grid 2D grid of integers
     * @param k Maximum allowed element value
     * @return Count of submatrices that are sorted and have all elements <= k
     */
    public long countSubmatrices(int[][] grid, int k) {

        int n = grid.length;    // Number of rows
        int m = grid[0].length; // Number of columns
        long ans = 0;

        // Histogram array: histogram[row] = length of valid sorted sequence
        // ending at the current column for that row
        int histogram[] = new int[n];

        // Process each column as the potential right boundary of submatrices
        for (int i = 0; i < m; i++) {
            // Build histogram for column i
            for (int j = 0; j < n; j++) {
                // Case 1: Current element exceeds k
                // Can't be part of any valid submatrix, reset to 0
                if (grid[j][i] > k) {
                    histogram[j] = 0;

                }
                // Case 2: First column OR sequence is strictly increasing
                // Start a new sequence of length 1
                else if (i == 0 || grid[j][i - 1] < grid[j][i]) {
                    histogram[j] = 1;
                }
                // Case 3: Sequence continues (grid[j][i-1] >= grid[j][i] is impossible
                // due to sorted row requirement, so grid[j][i-1] == grid[j][i])
                // Extend the sequence
                else {
                    histogram[j]++;
                }
            }

            // Count all valid submatrices with column i as the right boundary
            // using the histogram technique
            ans += calcArea(histogram, n);
        }

        return ans;
    }

    /**
     * Counts the number of rectangles in a histogram.
     *
     * Uses a monotonic stack technique to efficiently count all possible rectangles.
     *
     * Key idea: - For each bar in the histogram, count how many rectangles have their bottom-right corner at this bar -
     * The stack maintains bars in increasing order of height - For each bar, we can extend rectangles from all previous
     * bars that are shorter
     *
     * Example: histogram = [2, 3, 1] - At index 0 (height=2): 2 rectangles (width 1, heights 1 and 2) - At index 1
     * (height=3): 3 new + 2 extended = 5 more rectangles - At index 2 (height=1): 1 new rectangle
     *
     * The algorithm uses dynamic programming on the stack: - curr = number of rectangles ending at current position -
     * curr = previous_curr + current_height * (distance_from_previous_shorter_bar)
     *
     * @param histogram Array where histogram[i] = height at position i
     * @param n Length of histogram
     * @return Total count of rectangles in the histogram
     */
    private long calcArea(int histogram[], int n) {

        long ans = 0;  // Total count of rectangles

        // Stack stores Node(index, cumulative_count, height)
        // Maintains bars in increasing order of height
        var stack = new Stack<Node>();

        // Sentinel node: index -1, count 0, height -1 (always at bottom of stack)
        // This simplifies boundary conditions
        stack.push(new Node(-1, 0, -1));

        // Process each bar in the histogram
        for (int i = 0; i < n; i++) {
            int currHeight = histogram[i];

            // Pop all bars that are >= current height
            // We only care about the closest bar that is shorter (for extension)
            while (stack.peek().height >= currHeight) {
                stack.pop();
            }

            // peek is now the closest previous bar with height < currHeight
            var peek = stack.peek();

            // Calculate count of rectangles ending at position i:
            // - peek.current = count of rectangles ending at peek.ind
            // - currHeight * (i - peek.ind) = new rectangles formed by extending
            //   from peek.ind to i with heights 1, 2, ..., currHeight
            //
            // Explanation:
            // - We can form rectangles of height h (for h = 1 to currHeight)
            // - Each such rectangle can start from any column in (peek.ind, i]
            // - That's (i - peek.ind) starting positions for each height
            // - Total new rectangles: currHeight * (i - peek.ind)
            // - Plus all rectangles that were ending at peek (can be extended)
            int curr = peek.current + currHeight * (i - peek.ind);

            ans += curr;  // Add count of rectangles ending at position i

            // Push current position with its cumulative count
            stack.push(new Node(i, curr, currHeight));
        }

        return ans;
    }

    /**
     * Node class for the monotonic stack in histogram processing.
     *
     * Stores information about a bar in the histogram and the cumulative count of rectangles ending at that bar.
     */
    public class Node {

        int ind;      // Index of the bar in the histogram
        int current;  // Cumulative count of rectangles ending at this bar
        int height;   // Height of this bar

        /**
         * Constructor for Node.
         *
         * @param ind Index in histogram
         * @param current Cumulative rectangle count
         * @param height Bar height
         */
        public Node(int ind, int current, int height) {

            this.ind = ind;
            this.height = height;
            this.current = current;
        }
    }

}

