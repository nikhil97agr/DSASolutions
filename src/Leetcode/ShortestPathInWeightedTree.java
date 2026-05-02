package Leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Problem Link: https://leetcode.com/problems/shortest-path-in-a-weighted-tree

/**
 * Solution for handling shortest path queries in a weighted tree with edge updates.
 *
 * Problem: Given a weighted tree rooted at node 1, support two types of queries: 1. Update query [1, u, v, w]: Change
 * the weight of edge (u, v) to w 2. Distance query [2, node]: Find the distance from root (node 1) to the given node
 *
 * Key technique: Euler Tour + Segment Tree
 *
 * Euler Tour representation: - Flatten the tree into an array by DFS traversal - When entering a node, add the edge
 * weight leading to it - When exiting a node, subtract the edge weight (add negative value) - This creates a
 * "difference array" where the sum from root to any node gives the distance
 *
 * Example tree: 1 / \ (5) (3) 2     3
 *
 * Euler tour: [0, 5, -5, 3, -3] - Enter root (weight 0), enter node 2 (weight 5), exit node 2 (-5), enter node 3
 * (weight 3), exit node 3 (-3) - Distance to node 2: sum[0..tin[2]] = 0 + 5 = 5 - Distance to node 3: sum[0..tin[3]] =
 * 0 + 5 + (-5) + 3 = 3
 *
 * Why this works: - The Euler tour ensures that the path from root to any node in the flat array includes all edges on
 * the tree path exactly once - Edge weights are added when entering and subtracted when leaving, canceling out for
 * non-ancestor nodes
 *
 * Time Complexity: O(n + q log n) where n = nodes, q = queries Space Complexity: O(n) for the tree and auxiliary
 * structures
 */
public class ShortestPathInWeightedTree {

    Map<Integer, Map<Integer, Integer>> map;  // Adjacency list: node -> (neighbor -> weight)
    List<Integer> flatTree;                   // Euler tour representation of the tree
    int tin[];                                 // tin[node] = entry time in Euler tour
    int tout[];                                // tout[node] = exit time in Euler tour

    /**
     * Processes tree queries for distance calculation with edge updates.
     *
     * @param n Number of nodes in the tree
     * @param edges Array of edges where edges[i] = [u, v, weight]
     * @param queries Array of queries: - [1, u, v, w]: Update edge (u, v) to weight w - [2, node]: Query distance from
     * root to node
     * @return Array of results for type 2 queries
     */
    public int[] treeQueries(int n, int[][] edges, int[][] queries) {

        List<Integer> result = new ArrayList<>();
        flatTree = new ArrayList<>();
        tin = new int[n + 1];   // Entry times (1-indexed for nodes)
        tout = new int[n + 1];  // Exit times
        map = new HashMap<>();

        // Build adjacency list (bidirectional edges)
        for (var e : edges) {
            var u = e[0];
            var v = e[1];
            var w = e[2];

            map.computeIfAbsent(u, x -> new HashMap<>()).put(v, w);
            map.computeIfAbsent(v, x -> new HashMap<>()).put(u, w);
        }

        // Perform DFS from root (node 1) to build Euler tour
        // Start with weight 0 for root, no parent (-1)
        dfs(1, 0, -1);

        // Build segment tree on the Euler tour array
        // This enables efficient range sum queries and point updates
        var tree = new SegmentTree(flatTree);

        // Process each query
        for (var q : queries) {
            // Type 1: Update edge weight
            if (q[0] == 1) {

                int u = q[1];
                int v = q[2];
                int w = q[3];  // New weight

                // Determine which node is the child (higher node number = deeper in tree typically)
                // This assumes v is the child of u in the tree structure
                v = Math.max(u, v);

                // Get entry and exit positions for node v in Euler tour
                var in = tin[v];
                var out = tout[v];

                // Update: set new weight at entry and negative weight at exit
                // This replaces the old edge weight with the new one
                tree.update(1, in, w, 0, flatTree.size() - 1);
                tree.update(1, out, -w, 0, flatTree.size() - 1);

            }
            // Type 2: Query distance from root to node
            else {
                var node = q[1];

                // Sum the Euler tour from root (tin[1]) to target node (tin[node])
                // This gives the distance along the tree path
                result.add(tree.query(1, 0, flatTree.size() - 1, tin[1], tin[node]));
            }
        }

        // Convert result list to array
        var ans = new int[result.size()];
        for (int i = 0; i < result.size(); i++) {
            ans[i] = result.get(i);
        }

        return ans;

    }

    /**
     * DFS traversal to build the Euler tour representation.
     *
     * The Euler tour records: 1. When entering a node: add the edge weight leading to it 2. After visiting all
     * children: add the negative of the edge weight
     *
     * This creates a structure where: - The sum from root entry to any node entry = distance to that node - The
     * positive and negative weights cancel out for non-ancestor paths
     *
     * Example: 1 --(5)-- 2 | (3) | 3
     *
     * Euler tour: [0, 5, -5, 3, -3] - tin[1] = 0, tout[1] = 4 - tin[2] = 1, tout[2] = 2 - tin[3] = 3, tout[3] = 4
     *
     * @param curr Current node being visited
     * @param w Weight of the edge leading to this node (0 for root)
     * @param par Parent node (to avoid revisiting)
     */
    private void dfs(int curr, int w, int par) {

        // Enter node: add edge weight
        flatTree.add(w);
        tin[curr] = flatTree.size() - 1;  // Record entry position

        // Visit all children
        for (var entry : map.getOrDefault(curr, new HashMap<>()).entrySet()) {
            int v = entry.getKey();      // Neighbor node
            int d = entry.getValue();    // Edge weight

            // Only visit children (not parent)
            if (v != par) {
                dfs(v, d, curr);
            }
        }

        // Exit node: subtract edge weight
        // This ensures the weight only contributes to paths that actually use this edge
        flatTree.add(-w);
        tout[curr] = flatTree.size() - 1;  // Record exit position

    }

    /**
     * Segment Tree for efficient range sum queries and point updates.
     *
     * Supports: - Point update: Change value at a specific index - Range query: Sum of values in a range [ql, qr]
     *
     * Used here to: - Store the Euler tour array - Update edge weights (by updating entry and exit positions) - Query
     * distance from root to any node (sum along path in Euler tour)
     */
    class SegmentTree {

        int tree[];  // Segment tree array (4*n size for safety)

        /**
         * Constructor to build segment tree from initial values.
         *
         * @param list Initial array values (Euler tour)
         */
        public SegmentTree(List<Integer> list) {

            tree = new int[4 * list.size()];

            // Initialize tree with all values from the list
            for (int i = 0; i < list.size(); i++) {
                update(1, i, list.get(i), 0, list.size() - 1);
            }
        }

        /**
         * Updates a single position in the segment tree.
         *
         * @param ind Current node index in segment tree (1-indexed)
         * @param i Index in original array to update
         * @param val New value to set at index i
         * @param l Left bound of current segment
         * @param r Right bound of current segment
         */
        public void update(int ind, int i, int val, int l, int r) {

            // Index is outside current segment
            if (i < l || r < i) {
                return;
            }

            // Reached the leaf node for index i
            if (l == r) {
                tree[ind] = val;  // Set the value
                return;
            }

            // Recursively update children
            int mid = (l + r) / 2;

            update(2 * ind, i, val, l, mid);           // Update left child
            update(2 * ind + 1, i, val, mid + 1, r);   // Update right child

            // Update current node as sum of children
            tree[ind] = tree[ind * 2] + tree[2 * ind + 1];
        }

        /**
         * Queries the sum of values in a range.
         *
         * @param ind Current node index in segment tree
         * @param l Left bound of current segment
         * @param r Right bound of current segment
         * @param ql Query left bound
         * @param qr Query right bound
         * @return Sum of values in range [ql, qr]
         */
        public int query(int ind, int l, int r, int ql, int qr) {

            // Current segment is completely outside query range
            if (qr < l || ql > r) {
                return 0;
            }

            // Current segment is completely inside query range
            if (ql <= l && r <= qr) {
                return tree[ind];
            }

            // Partial overlap: query both children and combine results
            int mid = (l + r) / 2;

            return query(2 * ind, l, mid, ql, qr) + query(2 * ind + 1, mid + 1, r, ql, qr);
        }
    }
}