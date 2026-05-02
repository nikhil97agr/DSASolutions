package Leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Problem Link: https://leetcode.com/problems/maximum-points-after-collecting-coins-from-all-nodes/

/**
 * Solution for maximizing points after collecting coins from all nodes in a tree.
 *
 * Given a tree and coins at each node, we must visit all nodes starting from node 0. For each node, we can choose one
 * of two operations: 1. Collect floor(coins[i] / 2^t) - k points (where t is cumulative division count from ancestors)
 * 2. Collect floor(coins[i] / 2^(t+1)) points (divide by 2 one more time, no k subtracted)
 *
 * The key insight is that after ~log(max_coin) divisions, all values become 0, so we only need to track division count
 * up to that limit.
 *
 * Uses tree DP with memoization, where state is (node, division_count).
 */
public class MaximumPointsAfterCollectingCoinsFromAllNodes {

    List<Integer> adjList[];  // Adjacency list representation of the tree
    int maxBit;               // Maximum useful division count (log2 of max coin value)
    int n;                    // Number of nodes
    int coins[];              // Coin values at each node
    int k;                    // Penalty for operation 1
    Result dp[][];            // Memoization table: dp[node][divisions] = Result

    /**
     * Calculates the maximum points obtainable from collecting coins from all nodes.
     *
     * @param edges Array of edges representing the tree structure
     * @param coins Array where coins[i] is the initial coin value at node i
     * @param k Penalty subtracted when choosing operation 1
     * @return Maximum points achievable
     */
    public int maximumPoints(int[][] edges, int[] coins, int k) {

        this.coins = coins;
        this.k = k;
        n = coins.length;

        // Build adjacency list for the tree
        adjList = new ArrayList[n];

        // Calculate the maximum number of divisions needed
        // After log2(max_coin) divisions, all values become 0
        int max = Arrays.stream(coins).max().getAsInt();
        if (max == 0) {
            maxBit = 1;
        } else {
            maxBit = (int) (Math.log(Arrays.stream(coins).max().getAsInt()) / Math.log(2));

        }

        // Initialize DP table: dp[node][division_count]
        dp = new Result[n][maxBit + 1];

        // Initialize adjacency list
        for (int i = 0; i < n; i++) {
            adjList[i] = new ArrayList<>();
        }

        // Build the tree structure (undirected graph)
        for (var e : edges) {
            adjList[e[0]].add(e[1]);
            adjList[e[1]].add(e[0]);
        }

        System.out.println(Arrays.toString(coins));

        // Start DFS from root node 0, with no parent (-1) and 0 divisions
        Result result = solve(0, -1, 0);

        // Return the maximum of both operations (ensuring non-negative result)
        return Math.max(0, Math.max(result.first, result.second));
    }

    /**
     * Tree DP function to compute maximum points for a subtree.
     *
     * @param curr Current node being processed
     * @param par Parent node (to avoid revisiting in tree traversal)
     * @param t Number of divisions applied from ancestors (cumulative)
     * @return Result object containing points for both operation choices at current node
     */
    private Result solve(int curr, int par, int t) {

        // Return memoized result if already computed
        if (dp[curr][t] != null) {
            return dp[curr][t];
        }

        // Calculate points for both operations at current node:
        // first: Operation 1 - (coins[curr] / 2^t) - k
        // second: Operation 2 - coins[curr] / 2^(t+1)
        // Using bit shift (>> t) is equivalent to division by 2^t
        Result result = new Result((coins[curr] >> t) - k, coins[curr] >> (t + 1));

        // Process all children in the subtree
        for (int child : adjList[curr]) {
            if (child == par) {
                continue;  // Skip parent to avoid going back up the tree
            }

            // If we choose operation 1 at current node, child has same division count 't'
            Result r1 = solve(child, curr, t);

            // If we choose operation 2 at current node, child has division count 't+1'
            // Cap at maxBit since further divisions would result in 0
            Result r2 = solve(child, curr, Math.min(t + 1, maxBit));

            // Add the best result from child to current node's operations
            // Child can independently choose its best operation
            result.first += Math.max(r1.first, r1.second);
            result.second += Math.max(r2.first, r2.second);
        }

        // Memoize and return the result
        return dp[curr][t] = result;
    }

    /**
     * Helper class to store results for both operation choices. This allows us to track points for both possible
     * operations at each node.
     */
    static class Result {

        int first;   // Points if we choose operation 1 at this node: floor(coins/2^t) - k
        int second;  // Points if we choose operation 2 at this node: floor(coins/2^(t+1))

        /**
         * Constructor for Result.
         *
         * @param first Points for operation 1
         * @param second Points for operation 2
         */
        public Result(int first, int second) {

            this.first = first;
            this.second = second;
        }
    }
}
