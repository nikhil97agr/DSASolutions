package Leetcode;

import java.util.ArrayList;
import java.util.List;

//Problem Link: https://leetcode.com/problems/subtree-inversion-sum

/**
 * Solution for maximizing the sum of a tree by inverting subtrees.
 *
 * Problem: Given a tree with values at each node, you can perform "inversions" on subtrees: - An inversion flips the
 * sign of all values in a subtree (multiply by -1) - Inversions can only be performed every k distance from the root -
 * Goal: Maximize the total sum of all node values after performing optimal inversions
 *
 * Key constraints: - After inverting at distance d from root, the next inversion can only happen at distance d+k or
 * later - An inversion at a node affects all descendants - Need to track both the distance from last inversion and the
 * current sign multiplier
 *
 * Algorithm approach: 1. Use tree DP with DFS traversal from root (node 0) 2. State:
 * dp[node][distance_from_last_inversion][multiplier_sign] - distance: How far we are from the last inversion (-1 means
 * no inversion yet) - multiplier: Current sign multiplier (1 or -1) affecting this subtree 3. At each node, decide: -
 * Option A: Don't invert here (if allowed), continue with current multiplier - Option B: Invert here (if distance == k
 * or no previous inversion), flip multiplier 4. Return maximum sum achievable for the subtree
 *
 * Example: Tree: 0(5) - 1(-3) - 2(2), k=1 - Without inversion: 5 + (-3) + 2 = 4 - Invert at node 1: 5 + 3 + (-2) = 6
 * (better!)
 *
 * Time Complexity: O(n * k) where n = nodes, k = inversion distance constraint Space Complexity: O(n * k) for DP
 * memoization
 */
public class SubtreeInversionSum {

    int nums[];                  // Node values
    int k;                       // Minimum distance between inversions
    List<Integer> adjList[];     // Adjacency list for the tree
    long ans = 0;                // (Unused in this solution)

    Long db[][][];               // DP table: db[node][distance+1][multiplier_index]
    // multiplier_index: 0 for -1, 1 for +1

    long invalid = Long.MIN_VALUE;  // Sentinel value for invalid/uncomputed states

    /**
     * Computes the maximum sum achievable by strategically inverting subtrees.
     *
     * @param edges Array of edges defining the tree structure
     * @param nums Array of node values
     * @param k Minimum distance between consecutive inversions
     * @return Maximum sum achievable
     */
    public long subtreeInversionSum(int[][] edges, int[] nums, int k) {

        int n = edges.length + 1;  // Number of nodes (tree with n-1 edges)
        this.nums = nums;
        this.k = k;

        // Build adjacency list for the tree
        adjList = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            adjList[i] = new ArrayList<>();
        }

        // Add edges (undirected tree)
        for (var e : edges) {
            var u = e[0];
            var v = e[1];
            adjList[u].add(v);
            adjList[v].add(u);
        }

        // Initialize DP table
        // db[node][distance+1][multiplier_sign]
        // - distance ranges from -1 to k, so we use [0, k+1] indices
        // - multiplier_sign: 0 for negative (-1), 1 for positive (+1)
        db = new Long[n][k + 2][2];

        // Start DFS from node 0, no parent, distance -1 (no inversion yet), multiplier 1
        return dfs(0, -1, -1, 1, k);

    }

    /**
     * DFS function to compute maximum sum for a subtree rooted at curr.
     *
     * @param curr Current node being processed
     * @param par Parent node (to avoid revisiting in tree traversal)
     * @param dist Distance from the last inversion (-1 if no inversion yet)
     * @param multiplier Current sign multiplier (1 or -1) affecting this subtree
     * @param k Minimum distance between inversions
     * @return Maximum sum achievable for the subtree rooted at curr
     */
    private long dfs(int curr, int par, int dist, int multiplier, int k) {

        List<Integer> adjList[] = this.adjList;

        // Map multiplier to array index: -1 → 0, 1 → 1
        // Math.min(multiplier + 1, 1) converts: -1 → 0, 1 → 1 (clamping at 1)
        int m = Math.min(multiplier + 1, 1);

        // Check memoization (dist is shifted by +1 since it can be -1)
        if (db[curr][dist + 1][m] != null) {
            return db[curr][dist + 1][m];
        }

        // Track sums for two decisions: invert at current node or not
        long withInversion = invalid;      // Sum if we invert at curr
        long withOutInversion = invalid;   // Sum if we don't invert at curr

        // Process all children
        for (int child : adjList[curr]) {
            // Skip parent to avoid going back up the tree
            if (child == par) {
                continue;
            }

            // ================================================================
            // OPTION 1: Don't invert at current node
            // ================================================================
            // Continue with same multiplier, increment distance (if inversion was done before)
            // If dist == -1 (no inversion yet), keep it -1
            // Otherwise increment distance, but cap at k (we only care if dist >= k)
            long without = dfs(child, curr, dist == -1 ? -1 : Math.min(dist + 1, k), multiplier, k);

            // ================================================================
            // OPTION 2: Invert at current node (only if allowed)
            // ================================================================
            // We can invert if:
            // - dist == k: Enough distance has passed since last inversion
            // - dist == -1: No inversion has been done yet
            if (dist == k || dist == -1) {
                // Invert: flip the multiplier and reset distance to 1
                long with = dfs(child, curr, 1, multiplier * -1, k);

                // Accumulate the sum from this child (with inversion)
                if (with != invalid) {
                    if (withInversion == invalid) {
                        withInversion = with;  // First valid child contribution
                    } else {
                        withInversion += with;  // Add to existing sum
                    }
                }
            }

            // Accumulate the sum from this child (without inversion at curr)
            if (without != invalid) {
                if (withOutInversion == invalid) {
                    withOutInversion = without;  // First valid child contribution
                } else {
                    withOutInversion += without;  // Add to existing sum
                }
            }

        }

        // ================================================================
        // Add current node's contribution
        // ================================================================

        // Without inversion: Add current node's value with current multiplier
        if (withOutInversion == invalid) {
            withOutInversion = (long) multiplier * nums[curr];
        } else {
            withOutInversion += (long) multiplier * nums[curr];
        }

        // With inversion: Add current node's value with flipped multiplier
        // Only compute this if inversion is allowed at current node
        if (dist == k || dist == -1) {
            if (withInversion == invalid) {
                // Flip the sign: if multiplier is 1, it becomes -1; if -1, it becomes 1
                withInversion = -1L * multiplier * nums[curr];
            } else {
                withInversion += -1L * multiplier * nums[curr];
            }
        }

        // Return and memoize the maximum of both options
        return db[curr][dist + 1][m] = Math.max(withInversion, withOutInversion);

    }

}