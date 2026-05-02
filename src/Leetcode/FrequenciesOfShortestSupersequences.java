package Leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Problem Link: https://leetcode.com/problems/frequencies-of-shortest-supersequences/

/**
 * Solution for finding frequencies of characters in shortest supersequences.
 *
 * Problem: Given an array of 2-character words, find all shortest supersequences and return the character frequency
 * arrays for each.
 *
 * A supersequence of words is a string that contains each word as a subsequence. We want the shortest such
 * supersequence.
 *
 * Example: words = ["ab", "bc", "ca"] - "ab" requires: a appears before b - "bc" requires: b appears before c - "ca"
 * requires: c appears before a - This creates a cycle: a → b → c → a - Shortest supersequence needs 2 copies: "abcabc"
 * or similar - One valid answer: [2,2,2,0,0,...] (a,b,c each appear twice)
 *
 * Example 2: words = ["ab", "bc"] - "ab" requires: a before b - "bc" requires: b before c - No cycle: topological order
 * is a → b → c - Shortest supersequence: "abc" - Answer: [1,1,1,0,0,...] (a,b,c each appear once)
 *
 * Key insight: Graph + Cycle Detection + Bitmask Enumeration
 *
 * Observation 1: Model as directed graph - Each character is a node - Word "xy" creates edge x → y (x must appear
 * before y) - Shortest supersequence corresponds to topological ordering
 *
 * Observation 2: Cycle detection - If graph has cycle, we need to duplicate some characters - To break all cycles,
 * duplicate at least one character from each cycle - Minimum length = m (no cycles) or m + k (k characters duplicated)
 *
 * Observation 3: Bitmask enumeration - Try all subsets of characters to duplicate (2^m possibilities) - For each
 * subset, check if removing those nodes breaks all cycles - If removing duplicated nodes creates DAG, it's valid -
 * Among all valid subsets, find those with minimum size
 *
 * Algorithm: 1. Build character index mapping 2. Construct directed graph (adjacency list) 3. Check if graph has cycles
 * without duplication 4. If no cycles: minimum length = m (each character once) 5. If cycles exist: a. Try all bitmasks
 * (which characters to duplicate) b. For each mask, check if it breaks all cycles c. Track minimum number of
 * duplications needed d. Collect all configurations with minimum duplications
 *
 * Cycle detection with mask: - Mask indicates which nodes are "removed" (duplicated) - Check if remaining graph
 * (non-masked nodes) has cycles - Use DFS with backtracking
 *
 * Time Complexity: O(2^m × m²) where m is number of distinct characters Space Complexity: O(m) for graph and visited
 * arrays
 */
public class FrequenciesOfShortestSupersequences {

    List<Integer> adjList[] = new ArrayList[26];  // Adjacency list for character graph

    /**
     * Finds all shortest supersequence frequency configurations.
     *
     * @param words Array of 2-character words
     * @return List of frequency arrays (one per valid shortest supersequence)
     */
    public List<List<Integer>> supersequences(String[] words) {

        // ================================================================
        // STEP 1: Build character index mapping
        // ================================================================
        // Map each distinct character to an index [0, m-1]
        Map<Character, Integer> map = new HashMap<>();
        int ind = 0;
        for (String s : words) {
            char c1 = s.charAt(0), c2 = s.charAt(1);
            if (!map.containsKey(c1)) {
                map.put(c1, ind++);
            }

            if (!map.containsKey(c2)) {
                map.put(c2, ind++);
            }
        }

        // ================================================================
        // STEP 2: Build reverse mapping (index to character)
        // ================================================================
        int m = map.size();  // Number of distinct characters
        char inp[] = new char[m];
        for (var entry : map.entrySet()) {
            inp[entry.getValue()] = entry.getKey();
        }

        // ================================================================
        // STEP 3: Build directed graph (adjacency list)
        // ================================================================
        // Edge i → j means character i must appear before character j
        adjList = new ArrayList[m];
        for (int i = 0; i < m; i++) {
            adjList[i] = new ArrayList<>();
        }
        for (String s : words) {
            int i1 = map.get(s.charAt(0));
            int i2 = map.get(s.charAt(1));
            adjList[i1].add(i2);  // s.charAt(0) must come before s.charAt(1)
        }

        List<List<Integer>> res = new ArrayList<>();

        // ================================================================
        // STEP 4: Check if graph has cycles without duplication
        // ================================================================
        // mask = 0 means no nodes are removed (no duplication)
        if (!cycle(m, 0)) {
            // No cycles: each character appears exactly once
            int arr[] = new int[26];
            for (int i = 0; i < m; i++) {
                arr[inp[i] - 'a'] = 1;
            }

            res.add(Arrays.stream(arr).boxed().toList());

            return res;
        }

        // ================================================================
        // STEP 5: Graph has cycles - find minimum duplication set
        // ================================================================
        int minLen = Integer.MAX_VALUE;  // Minimum number of characters to duplicate

        // Try all possible subsets of characters to duplicate
        for (int mask = 1; mask < (1 << m); mask++) {
            int len = Integer.bitCount(mask);  // Number of characters duplicated

            // Pruning: skip if more duplications than current minimum
            if (len > minLen) {
                continue;
            }

            // ============================================================
            // Check if duplicating masked characters breaks all cycles
            // ============================================================
            // mask bit i = 1 means duplicate character i
            // When duplicated, node i is "removed" from cycle detection
            if (!cycle(m, mask)) {
                // Valid configuration: no cycles remain after duplication

                if (len < minLen) {
                    // Found smaller duplication set
                    minLen = len;
                    res = new ArrayList<>();  // Clear previous results

                }

                // ========================================================
                // Build frequency array for this configuration
                // ========================================================
                int freq[] = new int[26];
                for (int j = 0; j < m; j++) {
                    ind = inp[j] - 'a';

                    if ((mask & (1 << j)) != 0) {
                        // Character j is duplicated
                        freq[ind] = 2;
                    } else {
                        // Character j appears once
                        freq[ind] = 1;
                    }

                }

                res.add(Arrays.stream(freq).boxed().toList());
            }
        }

        return res;
    }

    /**
     * Checks if graph has cycles considering masked nodes as removed.
     *
     * Strategy: - Masked nodes (bit = 1) are considered "duplicated" and removed from cycle detection - Check remaining
     * nodes for cycles using DFS - Start DFS from each non-masked node
     *
     * Why this works: - If we duplicate a node, we can place one copy before and one after in sequence - This
     * effectively "breaks" any cycle passing through that node - We check if remaining graph (non-duplicated nodes)
     * forms a DAG
     *
     * @param n Number of nodes (distinct characters)
     * @param mask Bitmask indicating which nodes are duplicated (removed from graph)
     * @return true if cycles exist in non-masked subgraph, false otherwise
     */
    private boolean cycle(int n, int mask) {

        // Try starting DFS from each non-masked node
        for (int i = 0; i < n; i++) {
            int bit = (mask >> i) & 1;
            if (bit == 1) {
                continue;  // Skip masked (duplicated) nodes
            }

            // Fresh visited array for each starting node
            boolean[] vis = new boolean[n];
            if (check(i, mask, vis)) {
                return true;  // Cycle found
            }
        }

        return false;  // No cycles in non-masked subgraph
    }

    /**
     * DFS-based cycle detection with backtracking.
     *
     * Uses "coloring" approach: - vis[i] = true: node i is currently in DFS path (gray) - vis[i] = false after
     * visiting: node i is completely processed (black) - If we encounter a gray node, we found a back edge (cycle)
     *
     * Mask handling: - Masked nodes are skipped (treated as if they don't exist) - This simulates removing duplicated
     * nodes from the graph
     *
     * Example: Graph a → b → c → a (cycle) - mask = 0 (no duplication): cycle detected - mask = 1 (duplicate 'a'): a
     * removed, b → c remains (no cycle)
     *
     * @param curr Current node in DFS traversal
     * @param mask Bitmask of removed (duplicated) nodes
     * @param vis Visited array (true = in current DFS path)
     * @return true if cycle found, false otherwise
     */
    private boolean check(int curr, int mask, boolean vis[]) {

        // Mark current node as in DFS path (gray)
        vis[curr] = true;

        // Explore all neighbors
        for (int child : adjList[curr]) {
            int bit = (mask >> child) & 1;
            // Skip masked (duplicated) nodes
            if (bit == 1) {
                continue;
            }

            // If child is already in current DFS path, we found a cycle
            if (vis[child]) {
                return true;  // Back edge detected
            }

            // Recursively check child
            if (check(child, mask, vis)) {
                return true;
            }
        }

        // Backtrack: mark current node as processed (black)
        vis[curr] = false;
        return false;
    }
}