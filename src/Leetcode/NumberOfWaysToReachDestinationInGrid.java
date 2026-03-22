package Leetcode;
//Problem Link: https://leetcode.com/problems/number-of-ways-to-reach-destination-in-the-grid


/**
 * Solution for Number of Ways to Reach a Position After Exactly k Steps
 *
 * Problem: Given an n x m grid, a source position, a destination position, and k steps, count the number of ways to
 * reach the destination from the source in exactly k steps.
 *
 * Movement Rules: - Each step, you can move to an adjacent cell (up, down, left, right) - You must use exactly k steps
 * (can revisit cells)
 *
 * Example: Grid: 2 x 2, source = [0, 0], dest = [1, 1], k = 2 Possible paths: - [0,0] -> [0,1] -> [1,1] (right, down) -
 * [0,0] -> [1,0] -> [1,1] (down, right) Total: 2 ways
 *
 * Approach: Recursive DP with State Tracking - State: (sameRow, sameColumn, remainingSteps) - sameRow = 1 if currently
 * in same row as destination, 0 otherwise - sameColumn = 1 if currently in same column as destination, 0 otherwise -
 * remainingSteps = number of steps left
 *
 * Key Insight: We don't need to track exact position, only whether we're in the same row/column as the destination.
 * This reduces state space significantly.
 *
 * Transitions: - From same row: can move to (n-1) other rows - From different row: can move to destination row or (n-2)
 * other rows - Similar logic for columns
 */
public class NumberOfWaysToReachDestinationInGrid {

    int n;                              // Number of rows in the grid
    int m;                              // Number of columns in the grid
    long mod = (long) 1e9 + 7;          // Modulo for large number handling

    /**
     * Counts the number of ways to reach destination from source in exactly k steps
     *
     * @param n Number of rows in the grid
     * @param m Number of columns in the grid
     * @param k Number of steps (must use exactly k steps)
     * @param source Starting position [row, col]
     * @param dest Destination position [row, col]
     * @return Number of ways to reach dest from source in k steps, modulo 10^9+7
     */
    public int numberOfWays(int n, int m, int k, int[] source, int[] dest) {

        // Initialize grid dimensions
        this.n = n;
        this.m = m;

        // Start recursion with initial state
        // sameRow = 1 if source and dest are in same row, 0 otherwise
        // sameColumn = 1 if source and dest are in same column, 0 otherwise
        return solve(
                source[0] == dest[0] ? 1 : 0,
                source[1] == dest[1] ? 1 : 0,
                k
        );
    }

    /**
     * Recursive function to count ways to reach destination
     *
     * @param sameRow 1 if currently in same row as destination, 0 otherwise
     * @param sameColumn 1 if currently in same column as destination, 0 otherwise
     * @param k Number of steps remaining
     * @return Number of ways to reach destination in exactly k steps
     */
    private int solve(int sameRow, int sameColumn, int k) {

        // Recursive case: more than 1 step remaining
        if (k > 1) {
            int rowMove = 0;  // Ways to move vertically (change row)
            int colMove = 0;  // Ways to move horizontally (change column)

            // Calculate ways to move vertically (up or down)
            if (sameRow == 1) {
                // Currently in same row as destination
                // Moving vertically takes us to a different row (n-1 choices)
                // After moving, sameRow becomes 0
                rowMove = prod(n - 1, solve(0, sameColumn, k - 1));
            } else {
                // Currently in different row from destination
                // Option 1: Move to destination row (1 way) -> sameRow becomes 1
                // Option 2: Move to other rows (n-2 ways) -> sameRow stays 0
                rowMove = add(solve(1, sameColumn, k - 1), prod(n - 2, solve(0, sameColumn, k - 1)));
            }

            // Calculate ways to move horizontally (left or right)
            if (sameColumn == 1) {
                // Currently in same column as destination
                // Moving horizontally takes us to a different column (m-1 choices)
                // After moving, sameColumn becomes 0
                colMove = prod(m - 1, solve(sameRow, 0, k - 1));
            } else {
                // Currently in different column from destination
                // Option 1: Move to destination column (1 way) -> sameColumn becomes 1
                // Option 2: Move to other columns (m-2 ways) -> sameColumn stays 0
                // Note: Bug in original code - should be m-2, not n-2
                colMove = add(solve(sameRow, 1, k - 1), prod(m - 2, solve(sameRow, 0, k - 1)));
            }

            // Total ways = ways to move vertically + ways to move horizontally
            return add(rowMove, colMove);
        }
        // Base case: exactly 1 step remaining
        else {
            // We can reach destination in 1 step if we're in same row OR same column
            // (but not both, as that would mean we're already at destination)
            if ((sameRow == 1 && sameColumn == 0) || (sameRow == 0 && sameColumn == 1)) {
                return 1;  // One move reaches destination
            }

            // If we're at destination (sameRow=1, sameColumn=1) or too far away
            // (sameRow=0, sameColumn=0), we can't reach in exactly 1 step
            return 0;
        }
    }

    /**
     * Adds two numbers with modulo arithmetic
     *
     * @param a First number
     * @param b Second number
     * @return (a + b) % mod
     */
    private int add(long a, long b) {

        return (int) ((a % mod + b % mod) % mod);
    }

    /**
     * Multiplies two numbers with modulo arithmetic
     *
     * @param a First number
     * @param b Second number
     * @return (a * b) % mod
     */
    private int prod(long a, long b) {

        return (int) ((a % mod * b % mod) % mod);
    }
}