package Leetcode;//Problem Link: https://leetcode.com/problems/minimum-operations-to-make-all-grid-elements-equal

/**
 * Solution for making all grid elements equal using minimum operations.
 *
 * Problem: Given an m×n grid and parameter k, you can add a value x to any k×k subgrid. Find the minimum number of
 * operations to make all elements equal, or -1 if impossible.
 *
 * Example: grid = [[1, 2],      k = 2 [3, 4]] - Add -1 to entire 2×2 subgrid → all become equal - Operations = 1
 *
 * Key insight: Sliding window + Linear equation solving
 *
 * Observation 1: Operation effect - Adding x to a k×k subgrid affects k² cells - Multiple subgrids can overlap - Each
 * cell can be affected by multiple operations
 *
 * Observation 2: Final value - All cells must reach the same final value V - For each cell: initial_value +
 * operations_sum = V - We need to find V and the minimum operations
 *
 * Observation 3: Top-left corner pattern - Consider cells at positions (i*k, j*k) - these are top-left corners of k×k
 * blocks - Each such cell is affected by exactly one operation (the block starting there) - Let op[i][j] = operation
 * value for block starting at (i*k, j*k) - Then: grid[i*k][j*k] + op[i][j] = V - So: op[i][j] = V - grid[i*k][j*k] -
 * Sum of operations = Σop[i][j] = (count * V) - Σgrid[i*k][j*k] - This is linear in V: operations = constant + coeff *
 * V
 *
 * Observation 4: Other cells constrain V - Cells not at top-left corners are affected by multiple operations - Their
 * constraints determine valid values of V - Some cells might give unique constraint on V - Others might be
 * automatically satisfied
 *
 * Algorithm: 1. Process grid row by row with sliding window of size k 2. For cells in "controllable" region (top-left
 * k×k blocks), track operations 3. Compute coefficient and constant for linear equation: ops = constant + coeff * V 4.
 * For cells in "edge" regions, extract constraints on V 5. Solve for V and compute minimum operations
 *
 * Key variables: - col[j]: cumulative operations affecting column j (sliding window) - ops[i][j]: operation value for
 * k×k block at relative position (i,j) - coeff, constant: coefficients of linear equation - possibleValue: the unique
 * value V that satisfies constraints
 *
 * Time Complexity: O(m × n) Space Complexity: O(k × n)
 */
public class MinimumOperationToMakeAllGridElementsEqual {

    /**
     * Finds minimum operations to make all grid elements equal.
     *
     * @param grid m×n grid of integers
     * @param k Size of square subgrid for operations
     * @return Minimum operations needed, or -1 if impossible
     */
    public long minOperations(int[][] grid, int k) {

        var ans = 0;
        var m = grid.length;
        var n = grid[0].length;

        // ================================================================
        // Initialize data structures
        // ================================================================
        var col = new long[n];        // col[j] = sum of operations affecting column j in current window
        var ops = new long[k][n];     // ops[i][j] = operation for block at relative position (i,j)

        long possibleValue = 0;       // The target value V (to be determined)
        boolean found = false;        // Whether we've found a constraint on V
        long coeff = 0;               // Coefficient in linear equation
        long constant = 0;            // Constant in linear equation
        long min = Long.MIN_VALUE;    // Minimum allowed value of V

        // ================================================================
        // STEP 1: Process grid row by row with sliding window
        // ================================================================
        for (int i = 0; i < m; i++) {
            // ============================================================
            // Update sliding window: remove operations from row i-k
            // ============================================================
            if (i >= k) {
                var row = i % k;  // Which row in ops[][] to clear
                for (int j = 0; j < n; j++) {
                    col[j] -= ops[row][j];  // Remove old operation from column sum
                    ops[row][j] = 0;        // Clear for reuse
                }
            }

            // ============================================================
            // Process current row with horizontal sliding window
            // ============================================================
            long preSum = 0;  // Sum of operations in current k-width window
            for (int j = 0; j < n; j++) {
                // Add column's vertical contribution to window
                preSum += col[j];
                // ====================================================
                // Calculate required operation to make cell equal to V
                // ====================================================
                // Current cell value: grid[i][j]
                // Operations already affecting it: preSum
                // To reach target V: grid[i][j] + preSum + req = V
                // Therefore: req = V - grid[i][j] - preSum = -grid[i][j] - preSum + V
                // We'll express req in terms of V later
                long req = -grid[i][j] - preSum;

                // ====================================================
                // CASE 1: Cell is in "controllable" region
                // ====================================================
                // Controllable: top-left of a k×k block exists
                // i <= m-k and j <= n-k ensures block fits in grid
                if (i <= m - k && j <= n - k) {
                    // Store operation for this position
                    ops[i % k][j] = req;
                    col[j] += req;    // Add to column's operation sum
                    preSum += req;    // Add to window sum

                    // Check if this is top-left corner of a block
                    // Top-left: i%k==0 and j%k==0 (multiples of k)
                    long co = (i % k == 0 && j % k == 0) ? 1 : 0;
                    coeff += req;     // Accumulate coefficient
                    constant += co;   // Accumulate constant

                    if (co == 1) {
                        // Top-left corner: req = V - grid[i][j]
                        // So V = grid[i][j] + req, or V >= -req (since we want min ops)
                        // Track minimum constraint: V >= -req
                        if (-req > min) {
                            min = -req;
                        }
                    } else {
                        // Not a top-left corner: req should be non-negative
                        // (These cells are affected by multiple blocks)
                        if (req < 0) {
                            return -1;  // Impossible: would need negative operation
                        }
                    }
                } else {
                    // ================================================
                    // CASE 2: Cell is in "edge" region
                    // ================================================
                    // Edge region: cells that don't have complete k×k block
                    // These cells constrain what V can be

                    // Check if this cell is in "boundary" (affected by incomplete blocks)
                    // r1 = 1 if the block starting at (i/k)*k, (j/k)*k doesn't fit
                    long r1 = ((i / k) * k > m - k || (j / k) * k > n - k) ? 1 : 0;

                    if (r1 != 0) {
                        // Cell is in boundary region
                        // For these cells, req determines V uniquely
                        // req = -grid[i][j] - preSum (no additional operation)
                        // So: grid[i][j] + preSum = V
                        // Therefore: V = -req
                        long x = -req;
                        if (!found) {
                            possibleValue = x;  // First constraint on V
                            found = true;
                        } else if (possibleValue != x) {
                            // Conflicting constraints: impossible
                            return -1;
                        }
                    } else {
                        // Cell is covered by complete blocks but not in controllable region
                        // These should already be satisfied (req should be 0)
                        if (req != 0) {
                            return -1;  // Inconsistent: impossible
                        }
                    }
                }

                // ================================================
                // Slide horizontal window: remove leftmost column
                // ================================================
                if (j >= k - 1) {
                    preSum -= col[j - k + 1];  // Remove column that's now outside window
                }
            }
        }

        // ================================================================
        // STEP 2: Determine target value V
        // ================================================================
        if (found) {
            // Edge cells gave us a specific constraint on V
            // Validate it satisfies minimum bound
            if (possibleValue < min) {
                return -1;  // Constraint violated: impossible
            }
        } else {
            // No explicit constraint from edge cells
            // Use minimum bound: V = min
            possibleValue = min;
        }

        // ================================================================
        // STEP 3: Compute total operations using linear equation
        // ================================================================
        // We have: total_ops = coeff + constant * V
        // Where:
        // - constant = number of top-left corner cells (blocks)
        // - coeff = sum of all operation values accumulated
        // - V = possibleValue (target value for all cells)
        //
        // The formula comes from:
        // For each top-left corner at (i*k, j*k):
        //   op[i][j] = V - grid[i*k][j*k]
        // Sum of all operations = Σop[i][j] = constant*V - Σgrid[i*k][j*k]
        //
        // Our accumulated coeff already incorporates the grid values,
        // so final answer is: coeff + constant * possibleValue
        return coeff + constant * possibleValue;

    }
}