package Leetcode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

// Problem Link: https://leetcode.com/problems/difference-between-maximum-and-minimum-price-sum

/**
 * Solution for Difference Between Maximum and Minimum Price Sum
 *
 * Problem: Given a tree with n nodes where each node has a price, find the maximum
 * difference between two paths where:
 * - A path's price sum = sum of all node prices on the path
 * - We want: max(path_sum) - min(path_sum) where min is achieved by a leaf node
 *
 * Key Insight: The minimum price sum is always a single leaf node (price[leaf])
 * So we need to find the maximum path sum that doesn't include the endpoint leaf.
 *
 * Approach: Tree DP with Re-rooting
 * 1. First DFS: Calculate maximum path sum going down from each node to its subtree
 * 2. Second DFS: Re-root the tree to consider paths going up through parent
 * 3. For each node, track the maximum path excluding the leaf endpoint
 *
 * Example:
 *     0(5)
 *    / \
 *   1(2) 2(3)
 *
 * Path 0->1: sum = 5+2 = 7, but we exclude leaf, so contribution = 5
 * Path 0->2: sum = 5+3 = 8, but we exclude leaf, so contribution = 5
 * Maximum output = max path excluding endpoint
 */
public class DifferenceBetweenMaximumAndMinimumPriceSum {

    long ans = 0;                          // Maximum output (answer)
    LinkedList<Integer> adjList[];         // Adjacency list for the tree
    Map<Integer, Long> pathSum[];          // pathSum[u] = map of child -> max path sum to that child's subtree
    int prices[];                          // Price of each node

    /**
     * Calculates the maximum output (difference between max and min price sum)
     *
     * @param n Number of nodes in the tree
     * @param edges Array of edges [u, v] representing the tree
     * @param price Array of prices for each node
     * @return Maximum difference between path sums
     */
    public long maxOutput(int n, int[][] edges, int[] price) {

        this.prices = price;
        adjList = new LinkedList[n];
        pathSum = new HashMap[n];

        // Initialize adjacency list and pathSum maps
        for (int i = 0; i < n; i++) {
            adjList[i] = new LinkedList<>();
            pathSum[i] = new HashMap<>();
        }

        // Build the tree (undirected graph)
        for (int e[] : edges) {
            int u = e[0];
            int v = e[1];
            adjList[u].add(v);
            adjList[v].add(u);
        }

        // First DFS: Calculate maximum path sums going down from each node
        dfs(0, -1);

        // Second DFS: Re-root and calculate maximum output considering all paths
        dfs(0, -1, 0l);

        return ans;
    }

    /**
     * Second DFS: Re-rooting DFS to calculate maximum output
     *
     * This DFS considers paths that go through the current node, including:
     * - Paths going down to children's subtrees
     * - Paths coming from parent (via 'sum' parameter)
     *
     * @param u Current node
     * @param par Parent node (-1 if root)
     * @param sum Maximum path sum coming from parent direction (excluding endpoint)
     */
    private void dfs(int u, int par, long sum) {

        // Base case: u is a leaf node (no children in the rooted tree)
        // The maximum output is the path coming from parent
        if (pathSum[u].isEmpty()) {
            ans = Math.max(ans, sum);
            return;
        }

        // Find the top 2 maximum path sums among children
        // We need top 2 because when we recurse to a child, we can't use that child's path
        long max1 = -1;  // Maximum path sum to any child
        long max2 = -1;  // Second maximum path sum to any child
        long u1 = -1;    // Child node with max1
        long u2 = -1;    // Child node with max2

        for (var entry : pathSum[u].entrySet()) {
            int child = entry.getKey();
            long s = entry.getValue();

            if (s > max1) {
                // New maximum found, shift previous max1 to max2
                max2 = max1;
                u2 = u1;
                max1 = s;
                u1 = child;
            } else if (s > max2) {
                // New second maximum found
                max2 = s;
                u2 = child;
            }
        }

        // Update answer: maximum of path from parent or path to best child
        ans = Math.max(ans, Math.max(sum, max1));

        // Recurse to all children
        for (int child : adjList[u]) {
            if (child == par) {
                continue;  // Skip parent
            }

            // When recursing to a child, we pass the maximum path that doesn't use that child
            // This is either:
            // - The path from parent (sum)
            // - The path to the best sibling (max1 if child != u1, otherwise max2)

            if (child != u1 && child != u2) {
                // This child is not in top 2, so use max1
                dfs(child, u, Math.max(sum, max1) + prices[u]);
            } else if (child != u1) {
                // This child is u2, so use max1
                dfs(child, u, Math.max(sum, max1) + prices[u]);
            } else if (child != u2) {
                // This child is u1, so use max2 (can't use max1 as it comes from this child)
                dfs(child, u, Math.max(sum, max2) + prices[u]);
            }
        }
    }

    /**
     * First DFS: Calculate maximum path sum from each node to its subtree
     *
     * This DFS computes for each node u:
     * - The maximum path sum from u to any leaf in its subtree
     * - Stores the path sum to each child in pathSum[u]
     *
     * @param u Current node
     * @param par Parent node (-1 if root)
     * @return Maximum path sum from u to any leaf in its subtree (including u's price)
     */
    private long dfs(int u, int par) {

        long max = 0;  // Maximum path sum to any child's subtree

        // Process all children
        for (int child : adjList[u]) {
            if (child == par) {
                continue;  // Skip parent in undirected tree
            }

            // Recursively get maximum path sum from child to its subtree
            long next = dfs(child, u);

            // Store the path sum to this child
            // This will be used in the second DFS for re-rooting
            pathSum[u].put(child, next);

            // Track the maximum among all children
            max = Math.max(max, next);
        }

        // Return maximum path sum including current node's price
        // This represents the path from u to the best leaf in its subtree
        return max + prices[u];
    }


}