package Leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

//Problem Link: https://leetcode.com/problems/total-sum-of-interaction-cost-in-tree-groups-ii/

/**
 * Solution to calculate the sum of interaction costs for all pairs of nodes in the same group.
 *
 * Algorithm Overview: This solution uses a Virtual Tree approach combined with Binary Lifting for efficient LCA
 * queries. For each group, we build a virtual tree containing only the relevant nodes and their LCAs, then calculate
 * the total distance contribution efficiently using tree DP.
 *
 * Time Complexity: O(n log n + k * m log m) where k is the number of groups and m is the average group size Space
 * Complexity: O(n log n) for the binary lifting table
 */
public class TotalSumOfInteractionCostInTreeGroupsII {

    // Adjacency list representation of the tree
    List<Integer> adjList[];

    // Number of nodes in the tree
    int n;

    // Group assignment for each node
    int[] group;

    // Depth of each node (distance from root node 0)
    int depth[];

    // Binary lifting table: parent[i][j] = 2^j-th ancestor of node i
    // Used for efficient LCA (Lowest Common Ancestor) queries in O(log n) time
    int parent[][];

    // Euler tour start time for each node (used to check ancestor relationship)
    int tin[];

    // Euler tour end time for each node (used to check ancestor relationship)
    int tout[];

    // Maximum power of 2 for binary lifting (2^18 = 262144 nodes max)
    int log = 18;

    // Timer for Euler tour timestamps
    int timer = 0;

    /**
     * Main method to calculate total interaction costs.
     *
     * @param n Number of nodes in the tree
     * @param edges Tree edges (n-1 edges for n nodes)
     * @param group Group assignment for each node
     * @return Sum of distances between all pairs of nodes in the same group
     */
    public long interactionCosts(int n, int[][] edges, int[] group) {

        // Initialize member variables
        this.n = n;
        this.group = group;
        depth = new int[n];
        tin = new int[n];
        tout = new int[n];
        parent = new int[n][log];
        adjList = new ArrayList[n];

        for (int i = 0; i < n; i++) {
            adjList[i] = new ArrayList<>();
        }

        // Build adjacency list from edges (undirected tree)
        for (var e : edges) {
            var u = e[0];
            var v = e[1];
            adjList[u].add(v);
            adjList[v].add(u);
        }

        // Preprocess the tree: compute depths, binary lifting table, and Euler tour timestamps
        dfs(0, 1, 0);
        // Group nodes by their group ID
        Map<Integer, List<Integer>> map = new HashMap<>();

        for (int i = 0; i < n; i++) {
            map.computeIfAbsent(group[i], x -> new ArrayList<>()).add(i);
        }

        long res = 0;

        // Process each group independently
        for (var entry : map.entrySet()) {
            var key = entry.getKey();
            var list = entry.getValue();

            // Skip groups with only one node (no pairs to calculate)
            if (list.size() == 1) {
                continue;
            }

            // VIRTUAL TREE CONSTRUCTION
            // Step 1: Sort nodes by Euler tour start time (DFS order)
            Collections.sort(list, (a, b) -> tin[a] - tin[b]);

            // Step 2: Add all LCAs of adjacent nodes in DFS order
            // This ensures the virtual tree contains all necessary internal nodes
            List<Integer> lca = new ArrayList<>(list);
            for (int i = 1; i < list.size(); i++) {
                lca.add(lca(list.get(i - 1), list.get(i)));
            }

            // Step 3: Sort all nodes (original + LCAs) by DFS order
            Collections.sort(lca, (a, b) -> tin[a] - tin[b]);

            // Step 4: Remove duplicates to get the final virtual tree nodes
            List<Integer> distinct = new ArrayList<>();
            int prev = -1;
            for (int x : lca) {
                if (prev != x) {
                    prev = x;
                    distinct.add(x);
                }
            }

            int m = distinct.size();

            // BUILD PARENT RELATIONSHIPS IN VIRTUAL TREE
            // Use a stack to efficiently find parent of each node in DFS order
            int parent[] = new int[m];
            Arrays.fill(parent, -1);

            var stack = new Stack<Integer>();
            for (int i = 0; i < m; i++) {
                // Pop all nodes from stack that are not ancestors of current node
                while (!stack.isEmpty() && !ancestor(distinct.get(stack.peek()), distinct.get(i))) {
                    stack.pop();
                }

                // Top of stack is the parent of current node in the virtual tree
                if (!stack.isEmpty()) {
                    parent[i] = stack.peek();
                }
                stack.push(i);
            }

            // COUNT NODES IN SUBTREES (TREE DP)
            // cnt[i] = number of nodes from the current group in subtree rooted at distinct[i]
            int cnt[] = new int[m];

            // Initialize: only count original group nodes (not LCA nodes)
            for (int i = 0; i < m; i++) {
                if (group[distinct.get(i)] == key) {
                    cnt[i] = 1;
                }
            }

            // CALCULATE CONTRIBUTION OF EACH EDGE
            // Process nodes in reverse order (bottom-up) to aggregate subtree counts
            for (int i = m - 1; i >= 0; i--) {
                int p = parent[i];
                if (p != -1) {
                    long c = cnt[i];

                    // Edge contribution formula:
                    // - c nodes below this edge, (list.size() - c) nodes above
                    // - Each of c nodes pairs with each of (list.size() - c) nodes
                    // - Distance = depth difference (each edge on path is counted once)
                    res += c * (list.size() - c) * (depth[distinct.get(i)] - depth[distinct.get(p)]);

                    // Propagate count to parent for next iteration
                    cnt[p] += cnt[i];

                }
            }

        }

        return res;
    }

    /**
     * Check if node u is an ancestor of node v. Uses Euler tour timestamps: u is ancestor of v if v's tour is within
     * u's tour.
     *
     * @param u Potential ancestor node
     * @param v Potential descendant node
     * @return true if u is an ancestor of v (or u == v)
     */
    private boolean ancestor(int u, int v) {

        return tin[u] <= tin[v] && tout[v] <= tout[u];
    }

    /**
     * DFS to preprocess the tree. Computes: 1. Depth of each node 2. Binary lifting table (parent[i][j] = 2^j-th
     * ancestor of node i) 3. Euler tour timestamps (tin/tout) for ancestor checking
     *
     * @param curr Current node being visited
     * @param d Depth of current node
     * @param par Parent of current node
     */
    private void dfs(int curr, int d, int par) {

        depth[curr] = d;
        parent[curr][0] = par;  // Direct parent

        // Record Euler tour entry time
        tin[curr] = timer++;

        // Build binary lifting table: parent[curr][i] = 2^i-th ancestor
        for (int i = 1; i < log; i++) {
            int pp = parent[curr][i - 1];
            parent[curr][i] = parent[pp][i - 1];
        }

        // Recursively visit all children
        for (var child : adjList[curr]) {
            if (child == par) {
                continue;
            }

            dfs(child, d + 1, curr);
        }

        // Record Euler tour exit time
        tout[curr] = timer++;
    }

    /**
     * Find Lowest Common Ancestor (LCA) of two nodes using Binary Lifting. Time Complexity: O(log n)
     *
     * Algorithm: 1. Bring both nodes to the same depth by lifting the deeper node 2. If they're the same, that's the
     * LCA 3. Otherwise, lift both nodes simultaneously until their parents differ 4. The parent of either node is the
     * LCA
     *
     * @param u First node
     * @param v Second node
     * @return LCA of u and v
     */
    private int lca(int u, int v) {

        // Ensure u is the deeper node (or same depth)
        if (depth[u] < depth[v]) {
            return lca(v, u);
        }

        // Step 1: Bring u to the same depth as v using binary lifting
        int diff = depth[u] - depth[v];
        for (int i = log - 1; i >= 0; i--) {
            int bit = (1 << i) & diff;
            if (bit != 0) {
                u = parent[u][i];  // Jump 2^i levels up
            }
        }

        // Step 2: If nodes are now the same, that's the LCA
        if (u == v) {
            return u;
        }

        // Step 3: Binary search for LCA - lift both nodes until parents differ
        for (int i = log - 1; i >= 0; i--) {
            if (parent[u][i] != parent[v][i]) {
                u = parent[u][i];
                v = parent[v][i];
            }
        }

        // Step 4: Direct parent is the LCA
        return parent[u][0];
    }


}