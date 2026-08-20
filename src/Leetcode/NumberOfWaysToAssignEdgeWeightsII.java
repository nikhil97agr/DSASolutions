package Leetcode;//Problem Link: https://leetcode.com/problems/number-of-ways-to-assign-edge-weights-ii

import java.util.ArrayList;
import java.util.List;

/**
 * NumberOfWaysToAssignEdgeWeightsII - Count Ways to Assign Edge Weights in Tree
 *
 * PROBLEM STATEMENT:
 * Given a tree with n nodes and (n-1) edges, count the number of ways to assign weights {1, 2}
 * to edges such that for each query path from u to v, certain constraints are satisfied.
 *
 * APPROACH - BINARY LIFTING LCA + DYNAMIC PROGRAMMING:
 *
 * The solution combines two powerful techniques:
 *
 * 1. BINARY LIFTING for efficient LCA (Lowest Common Ancestor):
 *    - Precompute ancestor pointers at powers of 2 distances
 *    - parent[node][k] = ancestor of node at distance 2^k
 *    - Enables O(log n) LCA queries
 *
 * 2. DYNAMIC PROGRAMMING for counting weight assignments:
 *    - State: dp[edgesRemaining][currentSum % 2]
 *    - Each edge can have weight 1 or 2
 *    - Count all valid assignments modulo 10^9 + 7
 *
 * KEY INSIGHT - PATH LENGTH CALCULATION:
 * For nodes u and v with LCA = lca(u, v):
 *   path_length = depth[u] + depth[v] - 2 * depth[lca]
 *
 * Why? The path goes: u → lca → v
 * - Edges from u to lca: depth[u] - depth[lca]
 * - Edges from lca to v: depth[v] - depth[lca]
 * - Total: (depth[u] - depth[lca]) + (depth[v] - depth[lca])
 *        = depth[u] + depth[v] - 2 * depth[lca]
 *
 * ALGORITHM STEPS:
 * 1. Build tree adjacency list from edges
 * 2. DFS to compute depths and build binary lifting table
 * 3. For each query [u, v]:
 *    a. Find LCA(u, v) using binary lifting
 *    b. Calculate path length (number of edges)
 *    c. Use DP to count valid weight assignments
 *
 * TIME COMPLEXITY:
 * - Preprocessing: O(n log n) for DFS and binary lifting
 * - Per query: O(log n) for LCA + O(pathLength) for DP
 * - Overall: O(n log n + Q * pathLength)
 *
 * SPACE COMPLEXITY: O(n log n) for parent table + O(pathLength) for DP memoization
 */
public class NumberOfWaysToAssignEdgeWeightsII {

    // Tree structure and LCA preprocessing data structures
    int[] depth;                  // depth[i] = depth of node i from root (0-indexed)
    int[][] parent;               // parent[node][k] = ancestor at distance 2^k from node
    int max = 16;                 // Maximum power of 2 (2^16 = 65536 > typical tree size)
    List<Integer>[] adjList;      // adjList[i] = list of neighbors of node i

    /**
     * Main method to count ways to assign edge weights for each query.
     *
     * @param edges array of tree edges in 1-indexed format: [[u1, v1], [u2, v2], ...]
     * @param queries array of path queries: [[u1, v1], [u2, v2], ...]
     * @return array of counts (number of valid weight assignments for each query path)
     */
    public int[] assignEdgeWeights(int[][] edges, int[][] queries) {

        // Tree has n nodes (since there are n-1 edges in a tree)
        int n = edges.length + 1;

        // Initialize data structures
        depth = new int[n];
        parent = new int[n][max];
        adjList = new ArrayList[n];

        // Create empty adjacency lists for all nodes
        for (int i = 0; i < n; i++) {
            adjList[i] = new ArrayList<>();
        }

        // Build undirected tree from edges
        // Convert from 1-indexed (problem input) to 0-indexed (internal representation)
        for (var edge : edges) {
            int u = edge[0] - 1;  // Convert to 0-indexed
            int v = edge[1] - 1;  // Convert to 0-indexed
            adjList[u].add(v);
            adjList[v].add(u);
        }

        // STEP 1: Preprocess tree - build depth array and binary lifting table
        // Start DFS from node 0 as root, with parent 0 and initial depth 0
        buildTreeStructure(0, 0, 0);

        // STEP 2: Initialize DP memoization table
        // dp[pathLength][currentSum % 2] = number of ways to assign weights
        Integer[][] dpMemo = new Integer[n + 1][2];

        // STEP 3: Process each query
        int[] answers = new int[queries.length];
        for (int queryIndex = 0; queryIndex < queries.length; queryIndex++) {
            int[] query = queries[queryIndex];
            int u = query[0] - 1;  // Convert to 0-indexed
            int v = query[1] - 1;  // Convert to 0-indexed

            // Find the lowest common ancestor of u and v
            int lowestCommonAncestor = findLCA(u, v);

            // Calculate path length (number of edges) between u and v
            // Formula: depth[u] + depth[v] - 2 * depth[lca]
            int pathLength = depth[u] + depth[v] - 2 * depth[lowestCommonAncestor];

            // Count the number of ways to assign weights to edges on this path
            answers[queryIndex] = countWaysDP(pathLength, 0, dpMemo);
        }

        return answers;
    }

    /**
     * Dynamic Programming to count ways to assign edge weights.
     *
     * DP STATE:
     * - edgesRemaining: number of edges left to assign weights
     * - currentSum: current sum modulo 2 (tracks parity)
     *
     * DP TRANSITION:
     * For each edge, we can assign weight 1 or 2:
     * - Assign weight 1: move to state (edgesRemaining-1, (currentSum+1)%2)
     * - Assign weight 2: move to state (edgesRemaining-1, (currentSum+2)%2)
     *
     * BASE CASE:
     * When edgesRemaining == 0, return currentSum (the final parity)
     *
     * @param edgesRemaining number of edges left to assign
     * @param currentSum current sum modulo 2
     * @param dpMemo memoization table
     * @return number of valid ways to assign weights
     */
    private int countWaysDP(int edgesRemaining, int currentSum, Integer[][] dpMemo) {

        // BASE CASE: No more edges to assign
        if (edgesRemaining == 0) {
            return currentSum;  // Return the final sum (parity)
        }

        // Check memoization table
        if (dpMemo[edgesRemaining][currentSum] != null) {
            return dpMemo[edgesRemaining][currentSum];
        }

        // RECURSIVE CASE: Try assigning weight 1 or weight 2 to current edge
        // Option 1: Assign weight 1 → new sum = (currentSum + 1) % 2
        int waysWithWeight1 = countWaysDP(edgesRemaining - 1, (currentSum + 1) % 2, dpMemo);

        // Option 2: Assign weight 2 → new sum = (currentSum + 2) % 2
        int waysWithWeight2 = countWaysDP(edgesRemaining - 1, (currentSum + 2) % 2, dpMemo);

        // Total ways = sum of both options (modulo 10^9 + 7)
        return dpMemo[edgesRemaining][currentSum] = addModulo(waysWithWeight1, waysWithWeight2);
    }

    /**
     * Helper method to add two numbers with modulo arithmetic.
     *
     * Prevents overflow by using long for intermediate calculation.
     *
     * @param a first number
     * @param b second number
     * @return (a + b) % (10^9 + 7)
     */
    private int addModulo(int a, int b) {

        int MOD = 1_000_000_007;

        // Use long to prevent overflow
        long sum = 1L * a + b;

        return (int) (sum % MOD);
    }

    /**
     * Find Lowest Common Ancestor (LCA) using Binary Lifting.
     *
     * ALGORITHM:
     * 1. Ensure u is deeper than v (swap if necessary)
     * 2. Bring u to the same level as v using binary jumps
     * 3. If u == v, we found LCA (one is ancestor of the other)
     * 4. Otherwise, binary search for LCA by jumping both nodes up simultaneously
     *
     * TIME COMPLEXITY: O(log n)
     *
     * @param u first node
     * @param v second node
     * @return lowest common ancestor of u and v
     */
    private int findLCA(int u, int v) {

        // STEP 1: Ensure u is the deeper node (swap if necessary)
        if (depth[u] < depth[v]) {
            return findLCA(v, u);  // Swap and recurse
        }

        // STEP 2: Bring u to the same level as v
        // Use binary representation of depth difference for efficient jumping
        int depthDifference = depth[u] - depth[v];

        // Process each bit of the depth difference
        for (int powerOf2 = max - 1; powerOf2 >= 0; powerOf2--) {
            // Check if this bit is set in the depth difference
            int bit = (depthDifference >> powerOf2) & 1;

            if (bit == 1) {
                // Jump u up by 2^powerOf2 steps
                u = parent[u][powerOf2];
            }
        }

        // STEP 3: Check if v is an ancestor of u
        if (u == v) {
            return u;  // v is the LCA
        }

        // STEP 4: Binary search for LCA
        // Move both u and v up simultaneously, staying just below their LCA
        for (int powerOf2 = max - 1; powerOf2 >= 0; powerOf2--) {
            // If ancestors at distance 2^powerOf2 are different, we can jump
            if (parent[u][powerOf2] != parent[v][powerOf2]) {
                u = parent[u][powerOf2];
                v = parent[v][powerOf2];
            }
        }

        // Now u and v are children of their LCA
        return parent[u][0];  // Parent of u (or v) is the LCA
    }


    /**
     * DFS to build tree structure and precompute binary lifting table.
     *
     * This method:
     * 1. Computes depth of each node
     * 2. Builds the parent table for binary lifting
     *
     * BINARY LIFTING TABLE CONSTRUCTION:
     * parent[node][0] = immediate parent
     * parent[node][k] = ancestor at distance 2^k
     *
     * Formula: parent[node][k] = parent[parent[node][k-1]][k-1]
     * Meaning: To get ancestor at distance 2^k, jump 2^(k-1) twice
     *
     * Example: parent[node][3] = ancestor at distance 2^3 = 8
     *          = jump 4 steps from parent[node][2]
     *          = parent[parent[node][2]][2]
     *
     * @param currentNode current node being processed
     * @param parentNode parent of current node
     * @param currentDepth depth of current node from root
     */
    private void buildTreeStructure(int currentNode, int parentNode, int currentDepth) {

        // Store depth of current node
        depth[currentNode] = currentDepth;

        // Store immediate parent (2^0 = 1 step up)
        parent[currentNode][0] = parentNode;

        // Build binary lifting table for this node
        // parent[currentNode][i] = ancestor at distance 2^i
        for (int powerOf2 = 1; powerOf2 < max; powerOf2++) {
            // To get ancestor at 2^i distance, jump 2^(i-1) twice
            int ancestorAtHalfDistance = parent[currentNode][powerOf2 - 1];
            parent[currentNode][powerOf2] = parent[ancestorAtHalfDistance][powerOf2 - 1];
        }

        // Recursively process all children
        for (int childNode : adjList[currentNode]) {
            // Skip the parent (to avoid going back up)
            if (childNode == parentNode) {
                continue;
            }

            // Recursively build structure for child subtree
            buildTreeStructure(childNode, currentNode, currentDepth + 1);
        }
    }
}

/*
 * EXAMPLE WALKTHROUGH:
 *
 * Tree structure (1-indexed in problem, 0-indexed internally):
 *       1 (0)
 *      / \
 *     2(1) 3(2)
 *    /
 *   4(3)
 *
 * Edges: [[1,2], [1,3], [2,4]]
 * Query: [[4, 3]] (find LCA and count weight assignments)
 *
 * Step 1: Build tree
 * depth = [0, 1, 1, 2]
 * parent[0][0] = 0, parent[1][0] = 0, parent[2][0] = 0, parent[3][0] = 1
 *
 * Step 2: Process query [4, 3] → [3, 2] in 0-indexed
 * - findLCA(3, 2):
 *   - depth[3] = 2, depth[2] = 1
 *   - Bring node 3 up by 1 level → node 1
 *   - Now compare: 1 vs 2, different
 *   - Jump both up: parent[1][0] = 0, parent[2][0] = 0
 *   - LCA = 0
 *
 * Step 3: Calculate path length
 * pathLength = depth[3] + depth[2] - 2 * depth[0]
 *            = 2 + 1 - 2 * 0 = 3 edges
 *
 * Step 4: Count ways to assign weights
 * - 3 edges, each can be weight 1 or 2
 * - Use DP to count: 2^3 = 8 total ways
 * - (Actual count depends on the constraint being checked)
 */