package Leetcode;

import java.util.ArrayList;
import java.util.List;

//Problem Link: https://leetcode.com/problems/find-weighted-median-node-in-tree

/**
 * Solution for finding the weighted median node on a path between two nodes in a tree.
 *
 * Given a weighted tree and queries asking for the median node on the path between two nodes, the median node is
 * defined as the node where the sum of edge weights from the start to that node is >= half of the total path weight.
 *
 * Key techniques: 1. Binary Lifting (Sparse Table): Efficiently jump up the tree by powers of 2 2. LCA (Lowest Common
 * Ancestor): Find the common ancestor of two nodes 3. Binary Search: Find the median node on the path 4. DFS
 * preprocessing: Build parent pointers, depths, and distances
 *
 * Algorithm: 1. Preprocess tree with DFS to compute: - Binary lifting parent array par[node][k] (2^k-th ancestor) -
 * Depth of each node - Distance from root to each node 2. For each query (u, v): - Find LCA of u and v - Calculate
 * total path weight - Binary search on path positions to find median node - A node is the median if distance from u >=
 * total/2
 *
 * Time Complexity: O(n log n) preprocessing + O(q log n log n) for queries Space Complexity: O(n log n) for binary
 * lifting table
 */
public class WeightedMedianNodeInTree {

    List<int[]> adjList[];  // Adjacency list: adjList[u] = [[v1, w1], [v2, w2], ...]
    int depth[];            // depth[i] = depth of node i from root
    long dist[];            // dist[i] = sum of edge weights from root to node i
    int par[][];            // par[i][k] = 2^k-th ancestor of node i (binary lifting)
    int log = 20;           // Maximum power of 2 (supports trees up to 2^20 ≈ 1M nodes)

    /**
     * Finds the weighted median node for each query path.
     *
     * @param n Number of nodes in the tree
     * @param edges Array of edges where edges[i] = [u, v, weight]
     * @param queries Array of queries where queries[i] = [u, v]
     * @return Array of median nodes for each query
     */
    public int[] findMedian(int n, int[][] edges, int[][] queries) {

        // Initialize data structures
        adjList = new ArrayList[n];
        dist = new long[n];
        depth = new int[n];
        par = new int[n][log];  // Binary lifting table

        for (int i = 0; i < n; i++) {
            adjList[i] = new ArrayList<>();
        }

        // Build adjacency list (undirected tree)
        for (int e[] : edges) {
            int u = e[0];
            int v = e[1];
            int w = e[2];
            adjList[u].add(new int[]{v, w});
            adjList[v].add(new int[]{u, w});
        }

        // Preprocess tree: build binary lifting table, compute depths and distances
        dfs(0, 0, 1, 0);

        int ans[] = new int[queries.length];

        // Process each query
        for (var i = 0; i < queries.length; i++) {
            int u = queries[i][0];
            int v = queries[i][1];

            // Trivial case: same node
            if (u == v) {
                ans[i] = u;
                continue;
            }

            // Find lowest common ancestor
            int lca = lca(u, v);

            // Calculate total path weight from u to v
            // Path = u -> lca -> v
            // Weight = dist[u] + dist[v] - 2*dist[lca]
            long total = dist[u] + dist[v] - 2 * dist[lca];

            // Binary search on the path position
            // Path has (depth[u] - depth[lca]) + (depth[v] - depth[lca]) edges
            int start = 0;
            int end = depth[u] + depth[v] - 2 * depth[lca];
            int res = v;

            // Binary search for the median node
            // We're searching for the first node where distance from u >= total/2
            while (start <= end) {
                int mid = (start + end) / 2;

                // Get the node at position mid on the path from u to v
                int midNode = getMedian(u, v, lca, mid);

                // Calculate distance from u to midNode
                int lcaMid = lca(u, midNode);
                long midWeight = dist[u] + dist[midNode] - 2 * dist[lcaMid];

                // Check if midNode qualifies as median (distance >= total/2)
                if (midWeight * 2 >= total) {
                    res = midNode;
                    end = mid - 1;  // Try to find an earlier node
                } else {
                    start = mid + 1;  // Need to go further along the path
                }
            }

            ans[i] = res;
        }

        return ans;
    }

    /**
     * Gets the node at a specific position along the path from u to v.
     *
     * The path goes: u -> ... -> lca -> ... -> v midIndex=0 means node u, midIndex=1 is one step from u toward v, etc.
     *
     * @param u Start node of the path
     * @param v End node of the path
     * @param lca Lowest common ancestor of u and v
     * @param midIndex Position along the path (0-indexed)
     * @return The node at position midIndex on the path
     */
    private int getMedian(int u, int v, int lca, int midIndex) {

        // Special case: when lca == v, path goes from u up to v
        // Need to flip perspective to handle correctly
        if (depth[u] > depth[v] && lca == v) {
            // Convert index from u's perspective to v's perspective
            // If path is u -> ... -> v and we want position midIndex from u,
            // it's equivalent to position (totalLength - midIndex) from v
            return getMedian(v, u, lca, depth[u] - depth[v] - midIndex);
        }

        // Length of path segment from u to lca (inclusive of both endpoints)
        int lenUl = depth[u] - depth[lca] + 1;

        // Case 1: midIndex is on the u -> lca segment
        if (midIndex < lenUl) {
            // Lift u up by midIndex steps toward lca
            return liftUp(u, midIndex);

        }

        // Case 2: midIndex is on the lca -> v segment
        // Calculate remaining steps after reaching lca
        int remain = midIndex - (lenUl - 1);

        // Length of path from v to lca (not including lca)
        int lenVl = depth[v] - depth[lca];

        // How many steps from v do we need to go up to reach the target?
        // If remain=1, we want the node just below lca on v's side
        // So we lift v by (lenVl - remain) steps
        int diff = lenVl - remain;
        return liftUp(v, diff);


    }

    /**
     * Lifts a node up the tree by a specified distance using binary lifting.
     *
     * Uses the binary representation of dist to efficiently jump by powers of 2. Example: To go up 13 steps, we jump by
     * 8 (2^3), then 4 (2^2), then 1 (2^0).
     *
     * @param u Starting node
     * @param dist Number of steps to go up toward root
     * @return The ancestor of u that is dist steps up
     */
    private int liftUp(int u, int dist) {

        // Process each bit of dist from highest to lowest
        for (int i = log - 1; i >= 0; i--) {
            // Check if the i-th bit is set in dist
            int bit = (dist >> i) & 1;
            if (bit == 1) {
                // Jump by 2^i steps using the binary lifting table
                u = par[u][i];
            }
        }

        return u;
    }

    /**
     * Finds the Lowest Common Ancestor (LCA) of two nodes using binary lifting.
     *
     * Algorithm: 1. Bring both nodes to the same depth 2. If they're the same, that's the LCA 3. Otherwise, lift both
     * nodes simultaneously until their parents differ 4. The parent of either node is the LCA
     *
     * @param u First node
     * @param v Second node
     * @return The lowest common ancestor of u and v
     */
    private int lca(int u, int v) {

        // Ensure u is the deeper node (or equal depth)
        if (depth[u] < depth[v]) {
            return lca(v, u);  // Swap and recurse
        }

        // Step 1: Bring u to the same depth as v
        u = liftUp(u, depth[u] - depth[v]);

        // If they're the same node now, that's the LCA
        if (u == v) {
            return u;
        }

        // Step 2: Lift both nodes simultaneously, jumping as high as possible
        // without making them equal (binary search for LCA)
        for (int i = log - 1; i >= 0; i--) {
            // If their 2^i-th ancestors are different, we can jump
            if (par[u][i] != par[v][i]) {
                u = par[u][i];
                v = par[v][i];
            }
        }

        // Now u and v are children of the LCA
        // Return their parent
        return par[u][0];
    }

    /**
     * DFS to preprocess the tree and build the binary lifting table.
     *
     * Computes: - dist[node]: cumulative edge weight from root to node - depth[node]: number of edges from root to node
     * - par[node][k]: the 2^k-th ancestor of node
     *
     * Binary lifting table construction: - par[node][0] = direct parent - par[node][1] = grandparent (2^1 = 2 steps up)
     * - par[node][k] = par[par[node][k-1]][k-1] (jump 2^(k-1) twice)
     *
     * @param curr Current node being processed
     * @param p Parent of current node
     * @param d Depth of current node
     * @param distance Cumulative distance from root to current node
     */
    private void dfs(int curr, int p, int d, long distance) {

        // Store distance from root
        dist[curr] = distance;

        // Store depth from root
        depth[curr] = d;

        // Direct parent (2^0 = 1 step up)
        par[curr][0] = p;

        // Build binary lifting table using dynamic programming
        // par[curr][i] = 2^i-th ancestor
        for (int i = 1; i < log; i++) {
            // To jump 2^i steps, first jump 2^(i-1) steps, then another 2^(i-1)
            int pp = par[curr][i - 1];  // 2^(i-1)-th ancestor
            par[curr][i] = par[pp][i - 1];  // Jump another 2^(i-1) from there
        }

        // Recursively process all children
        for (var child : adjList[curr]) {
            int v = child[0];  // Child node
            int w = child[1];  // Edge weight

            // Don't revisit parent (tree is undirected in adjacency list)
            if (v == p) {
                continue;
            }

            // Recurse on child with updated depth and distance
            dfs(v, curr, d + 1, distance + w);
        }
    }
}