package Leetcode;//Problem Link: https://leetcode.com/problems/maximum-score-from-grid-operations

/**
 * Solution for maximizing score from grid operations.
 *
 * Problem: Given an n×n grid, process columns from left to right. For each column, you can select a contiguous segment
 * (possibly empty). The score is the sum of black cells (cells not in any selected segment).
 *
 * Constraint: Between adjacent columns, the selected segments must be "compatible": - If column i has segment [a, b]
 * and column i+1 has segment [c, d] - They must not create a "valley" or "peak" pattern (specific adjacency rules)
 *
 * Example: grid = [[0,0,0,0,0], [0,0,3,0,0], [0,1,0,0,0], [5,0,0,3,0], [0,0,0,0,2]] - Select segments optimally to
 * maximize sum of unselected cells
 *
 * Key insight: Dynamic Programming with state tracking
 *
 * State definition: - dp[c][prev1][prev2] = maximum score from column c onwards - prev1: in previous column (c-1), we
 * took cells from row prev1 onwards - prev2: in column before that (c-2), we took cells from row prev2 onwards - Why
 * track two previous columns? To enforce adjacency constraints
 *
 * Observation 1: Column processing - Process columns left to right - For each column, decide which contiguous segment
 * to select (if any) - Segment options: empty, [0,k], [1,k], ..., [i,n-1] for various i,k
 *
 * Observation 2: Scoring - Score = sum of cells NOT in selected segments - For current column c with segment starting
 * from row h: * Black cells in column c: rows [0, h-1] * But if previous column had segment from prev2, some are
 * already counted
 *
 * Observation 3: Adjacency constraint - Need to track previous two columns to ensure valid transitions - prev1 and
 * prev2 encode the segment boundaries
 *
 * Algorithm: 1. For each column c, try all possible segment starting positions 2. Calculate score from black cells
 * (considering overlaps with previous) 3. Recursively solve for remaining columns 4. Use memoization to avoid
 * recomputation
 *
 * Time Complexity: O(n⁴) - n columns × n² states × n transitions Space Complexity: O(n³) for DP table
 */
public class MaximumScoreFromGridOperations {

    Long dp[][][];  // Memoization table: dp[column][prev1][prev2]

    /**
     * Computes maximum score from grid operations.
     *
     * @param grid n×n grid of integers
     * @return Maximum achievable score
     */
    public long maximumScore(int[][] grid) {

        int n = grid.length;
        // Initialize DP table
        // dp[c][prev1][prev2] = max score from column c onwards
        dp = new Long[n + 1][n + 1][n + 1];

        // Start from column 0 with no previous selections
        return solve(n, 0, 0, 0, grid);
    }

    /**
     * Recursive DP solver with memoization.
     *
     * State: - c: current column being processed - prev1: in column c-1, selected segment starts from row prev1 (0
     * means no segment) - prev2: in column c-2, selected segment starts from row prev2 (0 means no segment)
     *
     * Returns: maximum score from column c onwards given the constraints
     *
     * @param n Grid size (n×n)
     * @param c Current column index
     * @param prev1 Starting row of segment in column c-1
     * @param prev2 Starting row of segment in column c-2
     * @param grid The input grid
     * @return Maximum score from column c onwards
     */
    private long solve(int n, int c, int prev1, int prev2, int grid[][]) {

        // ================================================================
        // Base case: processed all columns
        // ================================================================
        if (c >= n) {
            return 0;
        }

        // ================================================================
        // Memoization: return cached result if available
        // ================================================================
        if (dp[c][prev1][prev2] != null) {
            return dp[c][prev1][prev2];
        }

        // ================================================================
        // OPTION 1: Select empty segment in column c (take no cells)
        // ================================================================
        // If we take no cells from column c, all cells are "black" (contribute to score)
        // But we only count cells in rows [0, prev2-1] because:
        // - These are the cells that were NOT selected in column c-2
        // - Due to adjacency constraints, these contribute to current score
        long s2 = 0;
        for (int i = 0; i < prev2; i++) {
            s2 += grid[i][c];
        }

        // Transition: move to next column with:
        // - prev1 becomes 0 (no segment in current column)
        // - prev2 becomes prev1 (current column's prev1 becomes next column's prev2)
        long ans = s2 + solve(n, c + 1, 0, prev1, grid);

        // ================================================================
        // OPTION 2: Select segment starting from row prev1 onwards
        // ================================================================
        // Try extending segment downward from row prev1
        // This enforces that we respect the constraint from previous column
        long s1 = 0;

        if (c + 1 < n) {
            // Try all possible ending positions for the segment
            for (int i = prev1; i < n; i++) {
                s1 += grid[i][c];  // Add current cell to segment

                // If segment is [prev1, i], then black cells are [0, prev1-1] and [i+1, n-1]
                // Score contribution is handled implicitly through state transitions
                // Next column: segment can start from row i+1 onwards (prev1 = i+1)
                // prev2 becomes 0 (no constraint from two columns back)
                ans = Math.max(ans, s1 + solve(n, c + 1, i + 1, 0, grid));
            }
        }

        // ================================================================
        // OPTION 3: Select segment starting from various positions
        // ================================================================
        // Try all possible starting positions for segment in current column
        // Independent of prev1 constraint (exploring all possibilities)
        for (int i = 0; i < n; i++) {
            // Adjust s2 based on current starting position
            // If starting from row i+1, black cells are [0, i]
            // But only count cells [0, prev2-1] that weren't in c-2's segment
            if (i < prev2) {
                s2 -= grid[i][c];  // Remove this cell from black cell count
            }

            // Transition to next column with:
            // - prev1 = 0 (no segment or segment starts from 0)
            // - prev2 = i+1 (segment in current column starts from i+1)
            ans = Math.max(ans, s2 + solve(n, c + 1, 0, i + 1, grid));
        }

        // ================================================================
        // Memoize and return result
        // ================================================================
        return dp[c][prev1][prev2] = ans;


    }
}