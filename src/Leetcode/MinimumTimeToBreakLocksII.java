package Leetcode;
//Problem Link: https://leetcode.com/problems/minimum-time-to-break-locks-ii

import java.util.Arrays;

/**
 * Solution for finding minimum time to break all locks using optimal assignment.
 *
 * Problem: Given n locks with strengths nums[i], break them sequentially where your energy level X starts at 1 and
 * increases by 1 after each lock. The time to break lock i with energy X is ceil(nums[i] / X).
 *
 * Key insight: This is an assignment problem! - We need to assign each lock to a specific energy level (1, 2, 3, ...,
 * n) - Each lock can be assigned to exactly one energy level - Each energy level can be used for exactly one lock -
 * Goal: Minimize the total time (sum of all breaking times)
 *
 * Solution approach: 1. Build a cost matrix: time[lock][energy] = ceil(nums[lock] / energy) 2. Use Hungarian Algorithm
 * to find minimum cost assignment 3. The assignment gives us the optimal order to break locks
 *
 * Example: nums = [3, 4, 1] - If we break in order [3, 4, 1] with energies [1, 2, 3]: Time = ceil(3/1) + ceil(4/2) +
 * ceil(1/3) = 3 + 2 + 1 = 6 - If we break in order [1, 3, 4] with energies [1, 2, 3]: Time = ceil(1/1) + ceil(3/2) +
 * ceil(4/3) = 1 + 2 + 2 = 5 (better!)
 *
 * Time Complexity: O(n³) for Hungarian Algorithm Space Complexity: O(n²) for cost matrix and auxiliary arrays
 */
public class MinimumTimeToBreakLocksII {

    int time[][];  // Cost matrix: time[lock_index][energy_level] = breaking time

    /**
     * Finds the minimum time to break all locks.
     *
     * @param nums Array where nums[i] is the strength of lock i
     * @return Minimum total time to break all locks
     */
    public int findMinimumTime(int[] nums) {

        int n = nums.length;

        // Build cost matrix (1-indexed for Hungarian algorithm convenience)
        // time[i][x] = time to break lock (i-1) with energy level x
        time = new int[n + 1][n + 1];

        // For each energy level x (1 to n)
        for (var x = 1; x <= n; x++) {
            // For each lock i (0 to n-1)
            for (var i = 0; i < n; i++) {
                // Calculate time to break lock i with energy x
                // ceil(nums[i] / x) = (nums[i] + x - 1) / x (integer division trick)
                time[i + 1][x] = (nums[i] + x - 1) / x;
            }
        }

        // Solve the assignment problem using Hungarian Algorithm
        // Find the minimum cost assignment of locks to energy levels
        return hungarian(n + 1, n + 1);


    }

    /**
     * Hungarian Algorithm (Kuhn-Munkres Algorithm) for solving the assignment problem.
     *
     * Finds the minimum cost assignment in a bipartite graph where: - Left side: locks (workers) - Right side: energy
     * levels (jobs) - Edge weights: time[lock][energy] (cost)
     *
     * The algorithm uses dual variables (potentials) u and v: - u[i]: potential for lock i - v[j]: potential for energy
     * level j - Reduced cost: time[i][j] - u[i] - v[j]
     *
     * Key idea: Maintain "tight" edges (where reduced cost = 0) and build an augmenting path to improve the matching.
     *
     * @param n Number of rows (locks + 1, 1-indexed)
     * @param m Number of columns (energy levels + 1, 1-indexed)
     * @return Minimum total cost of the optimal assignment
     */
    private int hungarian(int n, int m) {

        // Dual variables (potentials) for the assignment problem
        int[] u = new int[n];      // u[i]: potential for lock i
        int[] v = new int[m];      // v[j]: potential for energy level j
        int[] p = new int[m];      // p[j]: which lock is assigned to energy level j (matching)
        int[] way = new int[m];    // way[j]: previous column in augmenting path

        // Process each lock one by one to build the optimal assignment
        for (int i = 1; i < n; i++) {
            // Start with a dummy assignment: lock i is assigned to column 0
            p[0] = i;
            int j0 = 0;  // Current column in the search

            // min[j]: minimum reduced cost to reach column j from current matching
            int[] min = new int[m];
            Arrays.fill(min, Integer.MAX_VALUE);

            // seen[j]: whether column j has been visited in current iteration
            boolean[] seen = new boolean[m];

            // Find augmenting path using Dijkstra-like approach
            // Loop until we find an unmatched column (p[j0] == 0)
            while (p[j0] != 0) {
                seen[j0] = true;
                int i0 = p[j0];  // Lock currently matched to column j0
                int current = Integer.MAX_VALUE;  // Best reduced cost found
                int minCol = 0;  // Column with best reduced cost

                // For each unvisited column, update the minimum reduced cost
                for (int j = 1; j < m; j++) {
                    if (!seen[j]) {
                        // Calculate reduced cost: actual cost - dual variables
                        int val = time[i0][j] - u[i0] - v[j];

                        // Update minimum cost to reach column j
                        if (val < min[j]) {
                            min[j] = val;
                            way[j] = j0;  // Track path for augmentation
                        }

                        // Track the column with minimum cost
                        if (min[j] < current) {
                            current = min[j];
                            minCol = j;
                        }
                    }
                }

                // Update dual variables to maintain complementary slackness
                // This is the "delta" adjustment in Hungarian algorithm
                for (int j = 0; j < m; j++) {
                    if (seen[j]) {
                        // For visited columns: increase u and decrease v
                        u[p[j]] += current;
                        v[j] -= current;
                    } else {
                        // For unvisited columns: decrease minimum cost
                        min[j] -= current;
                    }
                }

                // Move to the column with minimum reduced cost
                j0 = minCol;
            }

            // Augment the matching along the path we found
            // Backtrack from j0 to update the assignment
            int temp = way[j0];
            p[j0] = p[temp];
            j0 = temp;

            // Continue backtracking until we reach the start (column 0)
            while (j0 != 0) {
                temp = way[j0];
                p[j0] = p[temp];
                j0 = temp;
            }
        }

        // The total cost is stored in -v[0]
        // (This is a property of the dual formulation)
        return -v[0];
    }


}