package Leetcode;

import java.util.ArrayList;
import java.util.List;

// Problem Link: https://leetcode.com/problems/maximum-xor-of-two-non-overlapping-subtrees

/**
 * Solution for Maximum XOR of Two Non-Overlapping Subtrees
 *
 * Problem: Given a tree with n nodes where each node has a value, find two non-overlapping subtrees such that the XOR
 * of their sums is maximized.
 *
 * Key Definitions: - Subtree sum = sum of all node values in that subtree - Non-overlapping = the two subtrees don't
 * share any nodes - Goal: maximize (sum of subtree1) XOR (sum of subtree2)
 *
 * Example: 1(5) / \ 2(3) 3(7) / 4(2)
 *
 * Subtree sums: - Subtree rooted at 1: 5+3+7+2 = 17 - Subtree rooted at 2: 3+2 = 5 - Subtree rooted at 3: 7 - Subtree
 * rooted at 4: 2
 *
 * Non-overlapping pairs: (subtree 3, subtree 2) -> 7 XOR 5 = 2 (subtree 3, subtree 4) -> 7 XOR 2 = 5 etc.
 *
 * Approach: DFS + Binary Trie 1. First DFS: Calculate subtree sum for each node 2. Second DFS: For each node, find
 * maximum XOR with previously seen subtree sums - Use Binary Trie to efficiently find maximum XOR - Process nodes in
 * post-order to ensure non-overlapping property 3. Binary Trie: Store numbers in binary form to maximize XOR in O(log
 * max_value) time
 *
 * Key Insight: By processing in post-order (children before parent), when we query the trie for a node, the trie only
 * contains subtrees that don't overlap with it.
 */
public class MaximumXorOfTwoNonOverlappingSubtrees {

    int maxBit = 45;                    // Maximum bit position (values can be up to 10^12)
    Trie trie;                          // Binary trie to store subtree sums
    List<Integer>[] adjList;            // Adjacency list for the tree
    int n;                              // Number of nodes
    long ans = 0;                       // Maximum XOR result
    long sum[];                         // sum[i] = sum of all values in subtree rooted at i
    int values[];                       // Node values

    /**
     * Finds the maximum XOR of two non-overlapping subtrees
     *
     * @param n Number of nodes in the tree
     * @param edges Array of edges [u, v] representing the tree
     * @param values Array of node values
     * @return Maximum XOR of two non-overlapping subtree sums
     */
    public long maxXor(int n, int[][] edges, int[] values) {

        this.n = n;
        trie = new Trie();
        adjList = new ArrayList[n];
        this.values = values;
        sum = new long[n];

        // Initialize adjacency list
        for (int i = 0; i < n; i++) {
            adjList[i] = new ArrayList<Integer>();
        }

        // Build the tree (undirected graph)
        for (int e[] : edges) {
            int u = e[0];
            int v = e[1];
            adjList[u].add(v);
            adjList[v].add(u);
        }

        // First DFS: Calculate subtree sums
        dfs(0, -1);

        // Second DFS: Find maximum XOR using binary trie
        dfs2(0, -1);

        return ans;
    }

    /**
     * Second DFS: Process nodes in post-order to find maximum XOR
     *
     * Post-order ensures that when we query for a node's subtree sum, the trie only contains subtree sums that don't
     * overlap with the current subtree.
     *
     * Process order: 1. Recursively process all children first 2. Query trie for maximum XOR with current subtree sum
     * 3. Add current subtree sum to trie
     *
     * @param u Current node
     * @param par Parent node (-1 if root)
     */
    private void dfs2(int u, int par) {

        // Query trie for maximum XOR with current subtree sum
        // At this point, trie contains subtree sums from other branches
        long res = trie.xor(sum[u], maxBit);
        ans = Math.max(ans, res);

        // Recursively process all children
        for (int child : adjList[u]) {
            if (child == par) {
                continue;  // Skip parent in undirected tree
            }
            dfs2(child, u);
        }

        // Add current subtree sum to trie AFTER processing children
        // This ensures non-overlapping property: when we add sum[u], all its
        // descendants have already been processed and added to the trie
        trie.add(sum[u], maxBit);

    }

    /**
     * First DFS: Calculate subtree sum for each node
     *
     * Computes sum[u] = sum of all node values in the subtree rooted at u
     *
     * @param u Current node
     * @param par Parent node (-1 if root)
     * @return Sum of all values in subtree rooted at u
     */
    private long dfs(int u, int par) {

        long total = 0;

        // Sum up all children's subtree sums
        for (int child : adjList[u]) {
            if (child == par) {
                continue;  // Skip parent in undirected tree
            }
            total += dfs(child, u);
        }

        // Add current node's value
        total += values[u];

        // Store the subtree sum
        sum[u] = total;

        return total;
    }

    /**
     * Binary Trie for efficient maximum XOR queries
     *
     * Stores numbers in binary representation from most significant bit to least significant bit. Each node has at most
     * 2 children: trie[0] for bit 0, trie[1] for bit 1.
     *
     * Operations: - add(num, bit): Insert a number into the trie - xor(num, bit): Find maximum XOR of num with any
     * number in the trie
     *
     * Time Complexity: O(maxBit) for both operations
     */
    class Trie {

        Trie trie[];  // trie[0] = child for bit 0, trie[1] = child for bit 1

        /**
         * Constructor: Initialize a trie node with 2 children (for bits 0 and 1)
         */
        public Trie() {

            trie = new Trie[2];
        }

        /**
         * Adds a number to the trie in binary representation
         *
         * @param num The number to add
         * @param bit Current bit position (from maxBit down to 0)
         */
        public void add(long num, int bit) {

            // Base case: processed all bits
            if (bit == -1) {
                return;
            }

            // Extract the bit at position 'bit' (0 or 1)
            int ind = (int) ((num >> bit) & 1);

            // Create child node if it doesn't exist
            if (trie[ind] == null) {
                trie[ind] = new Trie();
            }

            // Recursively add remaining bits
            trie[ind].add(num, bit - 1);
        }

        /**
         * Finds the maximum XOR of num with any number stored in the trie
         *
         * Strategy: At each bit position, try to go to the opposite bit to maximize XOR - If current bit is 0, prefer
         * path with bit 1 (XOR gives 1) - If current bit is 1, prefer path with bit 0 (XOR gives 1) - If preferred path
         * doesn't exist, take the other path (XOR gives 0)
         *
         * @param num The number to XOR with
         * @param bit Current bit position (from maxBit down to 0)
         * @return Maximum XOR value achievable
         */
        public long xor(long num, int bit) {

            // Base case: processed all bits
            if (bit == -1) {
                return 0;
            }

            // Extract the bit at position 'bit' (0 or 1)
            int ind = (int) ((num >> bit) & 1);

            // Try to go to the opposite bit to maximize XOR
            if (trie[1 - ind] != null) {
                // Opposite bit exists: XOR gives 1 at this position
                // Set bit at position 'bit' to 1 and recurse
                return (1l << bit) | (trie[1 - ind].xor(num, bit - 1));
            }
            // Opposite bit doesn't exist, try same bit
            else if (trie[ind] != null) {
                // Same bit exists: XOR gives 0 at this position
                // Don't set bit at position 'bit', just recurse
                return trie[ind].xor(num, bit - 1);
            }

            // No path exists (trie is empty at this level)
            return 0;
        }
    }
}