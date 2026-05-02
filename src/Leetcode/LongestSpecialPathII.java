package Leetcode;

import java.util.ArrayList;
import java.util.List;

//Problem Link: https://leetcode.com/problems/longest-special-path-ii

/**
 * Solution for finding the longest "special" path in a weighted tree.
 * <p>
 * Problem: Given a weighted tree where each node has a value nums[i], find: 1. The longest path where node values have
 * at most 1 duplicate value 2. Among paths with the same length, return the one with minimum number of nodes
 * <p>
 * "Special path" definition: - A path is special if at most ONE value appears more than once - All other values must be
 * unique along the path
 * <p>
 * Example: Path with values [1, 2, 3, 2, 4] is special (only 2 appears twice) Path with values [1, 2, 3, 2, 3] is NOT
 * special (both 2 and 3 appear twice)
 * <p>
 * Algorithm approach (DFS with state tracking): 1. DFS traversal from root (node 0) 2. Track path state using three key
 * data structures: - nodeMap[value]: For each value, track [cumulativeWeight, depth] on current path - pathStart:
 * Starting point of the current valid special path - duplicate: The value that appears twice (if any) 3. For each node,
 * try extending the special path 4. Backtrack by restoring nodeMap state after exploring subtree
 * <p>
 * Key insight: - Use nodeMap to detect when a value appears again (becomes duplicate) - Track the "path start" as the
 * furthest point where the path is still valid - When current value appeared before, decide if it becomes the new
 * duplicate or if we need to start a new path
 * <p>
 * Time Complexity: O(n) where n = number of nodes Space Complexity: O(max_value) for nodeMap + O(n) for recursion
 * stack
 */
public class LongestSpecialPathII {

    List<int[]> adjList[];  // Adjacency list: adjList[u] = [[v1, w1], [v2, w2], ...]
    int nums[];             // Node values
    int nodeMap[][];        // nodeMap[value] = [cumulativeWeight, depth] for value on current path
    // Tracks the last occurrence of each value
    int nodeCount = 1;      // Result: minimum number of nodes in longest special path
    int length = 0;         // Result: length (total weight) of longest special path

    /**
     * Finds the longest special path in the tree.
     *
     * @param edges Array of edges where edges[i] = [u, v, weight]
     * @param nums Array of node values
     * @return [maxLength, minNodeCount] where maxLength is the longest path length and minNodeCount is the minimum
     * nodes among paths of that length
     */
    public int[] longestSpecialPath(int[][] edges, int[] nums) {

        this.nums = nums;
        int n = nums.length;
        adjList = new ArrayList[n];

        // nodeMap: for each possible value (0 to 50000), store [weight, depth]
        // This tracks where each value was last seen on the current DFS path
        nodeMap = new int[5_000_1][2];

        // Build adjacency list
        for (int i = 0; i < n; i++) {
            adjList[i] = new ArrayList<>();
        }

        // Add edges (undirected tree)
        for (var e : edges) {
            var u = e[0];
            var v = e[1];
            var w = e[2];
            adjList[u].add(new int[]{v, w});
            adjList[v].add(new int[]{u, w});
        }

        // Start DFS from node 0, parent 0 (dummy), empty pathStart and duplicate
        // pathStart[0] = cumulative weight at path start, pathStart[1] = depth at path start
        // duplicate[0] = cumulative weight where duplicate started, duplicate[1] = depth
        dfs(0, 0, new int[2], new int[2]);

        return new int[]{length, nodeCount};
    }

    /**
     * DFS traversal to find longest special path.
     *
     * State tracking: - pathStart: The starting point (weight, depth) of the current valid special path - duplicate:
     * The value that appears twice, and where it started appearing - nodeMap[val]: For each value, tracks
     * [cumulativeWeight, depth] of last occurrence
     *
     * Logic flow: 1. Check if current value appeared before (prev != [0,0]) 2. If yes, handle the duplicate: - If this
     * duplicate is "older" than current duplicate, update pathStart - Otherwise, update pathStart to after old
     * duplicate, and set new duplicate 3. Calculate path length from pathStart to current node 4. Update global best if
     * this path is better 5. Recursively explore children with updated state 6. Backtrack: restore nodeMap for current
     * value
     *
     * @param curr Current node being visited
     * @param par Parent node (to avoid revisiting)
     * @param pathStart Array [cumulativeWeight, depth] marking start of valid special path
     * @param duplicate Array [cumulativeWeight, depth] marking where duplicate value started
     */
    public void dfs(int curr, int par, int pathStart[], int duplicate[]) {

        int val = nums[curr];  // Value of current node

        // current: state from parent node
        // prev: previous occurrence of current value (if any) on this path
        int[] current = nodeMap[nums[par]], prev = nodeMap[val];

        // ================================================================
        // STEP 1: Handle duplicate detection and path start adjustment
        // ================================================================

        // Check if current value appeared before in the path
        // prev[1] != 0 means this value was seen before (depth > 0)

        if (prev[1] < duplicate[1]) {
            // Case 1: Current value's previous occurrence is BEFORE the current duplicate
            // This means current value appeared earlier in the path
            // We need to move pathStart forward to exclude the earlier occurrence

            if (prev[0] > pathStart[0]) {
                // Update pathStart to the previous occurrence of current value
                // This ensures the path from pathStart to current has only one occurrence of val
                pathStart = prev;
            }
        } else {
            // Case 2: Current value's previous occurrence is AFTER (or is) the current duplicate
            // OR this is the first duplicate we're encountering (prev[1] >= duplicate[1])

            if (duplicate[0] > pathStart[0]) {
                // Move pathStart to after the old duplicate
                pathStart = duplicate;
            }
            // Set current value as the new duplicate
            duplicate = prev;
        }

        // ================================================================
        // STEP 2: Calculate and update best path
        // ================================================================

        // Calculate path length from pathStart to current node
        int pathLength = current[0] - pathStart[0];
        int pathNodes = current[1] - pathStart[1] + 1;

        if (pathLength > length) {
            // Found a longer path
            length = pathLength;
            nodeCount = pathNodes;
        } else if (pathLength == length && pathNodes < nodeCount) {
            // Same length but fewer nodes (better)
            nodeCount = pathNodes;
        }

        // ================================================================
        // STEP 3: Update nodeMap for current value
        // ================================================================

        // Mark current value as seen at depth (current[1] + 1)
        // Weight will be updated before each child DFS call
        nodeMap[val] = new int[]{0, current[1] + 1};

        // ================================================================
        // STEP 4: Recursively explore children
        // ================================================================

        for (var child : adjList[curr]) {
            if (child[0] == par) {
                continue;  // Skip parent
            }

            // Update cumulative weight for this path
            nodeMap[val][0] = current[0] + child[1];

            // Recursively explore child
            dfs(child[0], curr, pathStart, duplicate);
        }

        // ================================================================
        // STEP 5: Backtrack - restore previous state
        // ================================================================

        // Restore nodeMap to state before visiting this node
        // This allows other paths to use this value independently
        nodeMap[val] = prev;


    }
}