package Leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Problem Link: https://leetcode.com/problems/finish-time-of-tasks-ii/

/**
 * Solution - Minimum Finish Time in Tree with Rerooting Technique
 *
 * PROBLEM STATEMENT: Given a tree with n nodes, where each node has a base processing time, find the minimum finish
 * time if we start from the optimal root node.
 *
 * The finish time for a node is calculated based on: - Its own base time - The maximum and minimum times from its
 * children/neighbors - Formula: finishTime = 2 * max - min + baseTime
 *
 * APPROACH - TREE REROOTING (ALL ROOTS DP):
 *
 * The challenge is to efficiently compute the answer for all possible root nodes without running DFS n times (which
 * would be O(n^2)).
 *
 * KEY TECHNIQUE - REROOTING IN TWO PASSES:
 *
 * 1. FIRST DFS (Downward Pass): - Root the tree at node 0 - For each node, compute "down[u]" = finish time considering
 * only its subtree - down[u] = 2 * max(down[children]) - min(down[children]) + baseTime[u]
 *
 * 2. SECOND DFS (Rerooting / Upward Pass): - For each node, compute the answer considering it as the root - Pass
 * "parent message" = what the parent contributes when reversed - answer[u] = 2 * max(all_neighbors) -
 * min(all_neighbors) + baseTime[u] - Where all_neighbors includes both children and the "rerooted parent"
 *
 * KEY INSIGHT - FORMULA EXPLANATION: finishTime = 2 * max - min + baseTime
 *
 * Why this formula? - We need to wait for the slowest path (max) - But we can parallelize, so we also consider the
 * fastest path (min) - The difference (2*max - min) represents the optimal scheduling strategy - Add our own baseTime
 * for final processing
 *
 * ALGORITHM STEPS: 1. Build adjacency list (undirected tree) 2. DFS1: Compute downward finish times (treating node 0 as
 * root) 3. DFS2: Reroot and compute answer for each node as potential root 4. Return the minimum finish time across all
 * nodes
 *
 * TIME COMPLEXITY: O(n) - DFS1: O(n) - visit each node once - DFS2: O(n) - visit each node once - Overall: O(n)
 *
 * SPACE COMPLEXITY: O(n) - Adjacency list: O(n) - DP arrays (down, answer): O(n) - Recursion stack: O(h) where h is
 * tree height, worst case O(n)
 */
public class FinishTimeOfTaskII {

    // Global data structures for tree and DP
    List<Integer>[] graph;           // graph[u] = adjacency list of node u
    int[] baseTime;                  // baseTime[i] = base processing time for node i
    long[] downwardFinishTime;       // downwardFinishTime[u] = finish time from u considering only subtree
    long[] finalAnswer;              // finalAnswer[u] = finish time when u is the root

    /**
     * Finds the minimum finish time across all possible root nodes.
     *
     * @param n number of nodes in the tree
     * @param edges array of undirected edges: [[u1, v1], [u2, v2], ...]
     * @param baseTime array of base processing times for each node
     * @return minimum finish time achievable by choosing optimal root
     */
    public long finishTime(int n, int[][] edges, int[] baseTime) {

        // Initialize global data structures
        graph = new ArrayList[n];
        this.baseTime = baseTime;
        downwardFinishTime = new long[n];
        finalAnswer = new long[n];

        // Create adjacency list for the tree
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
        }

        // Build undirected tree (edges are bidirectional)
        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];
            graph[u].add(v);
            graph[v].add(u);
        }

        // STEP 1: First DFS - Compute downward finish times
        // Root the tree at node 0, treat -1 as the parent of root
        computeDownwardFinishTimes(0, -1);

        // STEP 2: Second DFS - Reroot and compute answer for each node
        // Start from node 0 with no parent message (Long.MIN_VALUE as sentinel)
        computeAllAnswersWithRerooting(0, -1, Long.MIN_VALUE);

        return Arrays.stream(finalAnswer).min().getAsLong();
    }

    /**
     * First DFS - Compute downward finish times (subtree only).
     *
     * This DFS roots the tree at node 0 and computes for each node u: downwardFinishTime[u] = finish time if we only
     * consider u's subtree
     *
     * ALGORITHM: 1. Recursively process all children 2. Track min and max finish times among children 3. Apply formula:
     * finishTime = 2 * max - min + baseTime[u]
     *
     * BASE CASE: - Leaf nodes (no children): finishTime = baseTime[u]
     *
     * RECURSIVE CASE: - Internal nodes: finishTime = 2 * max(children) - min(children) + baseTime[u]
     *
     * @param currentNode current node being processed
     * @param parent parent of current node (-1 for root)
     */
    private void computeDownwardFinishTimes(int currentNode, int parent) {

        long minChildTime = Long.MAX_VALUE;  // Minimum finish time among children
        long maxChildTime = Long.MIN_VALUE;  // Maximum finish time among children
        int childCount = 0;                  // Number of children

        // Process all neighbors (in undirected tree)
        for (int neighbor : graph[currentNode]) {
            // Skip the parent to avoid going back up
            if (neighbor == parent) {
                continue;
            }

            // Recursively compute downward finish time for this child
            computeDownwardFinishTimes(neighbor, currentNode);
            childCount++;

            // Track min and max among all children
            minChildTime = Math.min(minChildTime, downwardFinishTime[neighbor]);
            maxChildTime = Math.max(maxChildTime, downwardFinishTime[neighbor]);
        }

        // Compute finish time for current node
        if (childCount == 0) {
            // LEAF NODE: No children, so finish time is just our base time
            downwardFinishTime[currentNode] = baseTime[currentNode];
        } else {
            // INTERNAL NODE: Apply the formula considering all children
            // Formula: 2 * max - min + baseTime
            // This represents optimal scheduling considering child dependencies
            downwardFinishTime[currentNode] = 2L * maxChildTime - minChildTime + baseTime[currentNode];
        }
    }

    /**
     * Second DFS - Reroot and compute answer for each node as potential root.
     *
     * This DFS computes finalAnswer[u] for each node u, which represents the finish time if we treat u as the root of
     * the tree.
     *
     * REROOTING TECHNIQUE: - When we move from parent to child, we need to pass information about what the "rerooted
     * parent" contributes - parentMessage = the finish time contribution from the parent direction
     *
     * ALGORITHM: 1. Collect all neighbor contributions (children + rerooted parent) 2. Track top 2 min and max values
     * (needed for exclusion when computing child messages) 3. Compute answer[u] using all neighbors 4. For each child,
     * compute what message to pass (excluding that child's contribution) 5. Recursively process children with their
     * respective messages
     *
     * WHY TOP 2 MIN/MAX? When computing the message to pass to child v, we exclude v's contribution. So we need the
     * min/max among all OTHER neighbors, which is: - If v has the min, use min2; otherwise use min1 - If v has the max,
     * use max2; otherwise use max1
     *
     * @param currentNode current node being processed
     * @param parent parent of current node in the rooted tree
     * @param parentMessage finish time contribution from parent direction (when rerooted)
     */
    private void computeAllAnswersWithRerooting(int currentNode, int parent, long parentMessage) {

        int neighborCount = graph[currentNode].size();

        // Track top 2 minimum and maximum values among all neighbors
        long min1 = Long.MAX_VALUE, min2 = Long.MAX_VALUE;  // Smallest and 2nd smallest
        long max1 = Long.MIN_VALUE, max2 = Long.MIN_VALUE;  // Largest and 2nd largest

        // Store the finish time contribution from each neighbor
        Map<Integer, Long> neighborContributions = new HashMap<>();

        // STEP 1: Collect contributions from all neighbors
        for (int neighbor : graph[currentNode]) {
            // Determine the contribution from this neighbor
            long contribution;
            if (neighbor == parent) {
                // This is the parent direction - use the message passed from parent
                contribution = parentMessage;
            } else {
                // This is a child - use its downward finish time
                contribution = downwardFinishTime[neighbor];
            }

            neighborContributions.put(neighbor, contribution);

            // Update top 2 minimums
            if (contribution <= min1) {
                min2 = min1;
                min1 = contribution;
            } else if (contribution < min2) {
                min2 = contribution;
            }

            // Update top 2 maximums
            if (contribution >= max1) {
                max2 = max1;
                max1 = contribution;
            } else if (contribution > max2) {
                max2 = contribution;
            }
        }

        // STEP 2: Compute answer for current node as root
        if (neighborCount == 0) {
            // LEAF NODE: No neighbors, so answer is just base time
            finalAnswer[currentNode] = baseTime[currentNode];
        } else {
            // INTERNAL NODE: Use formula with all neighbors
            // answer = 2 * max(all_neighbors) - min(all_neighbors) + baseTime
            finalAnswer[currentNode] = 2L * max1 - min1 + baseTime[currentNode];
        }

        // STEP 3: Recursively process all children with appropriate messages
        for (int neighbor : graph[currentNode]) {
            long neighborContribution = neighborContributions.get(neighbor);

            int otherNeighborCount = neighborCount - 1;  // Number of neighbors excluding current one

            long messageToSendToChild;

            if (otherNeighborCount == 0) {
                // Current node becomes a leaf when we remove this neighbor
                messageToSendToChild = baseTime[currentNode];
            } else {
                // Compute min/max EXCLUDING the current neighbor
                // This represents what the parent contributes when child becomes root
                long minExcludingNeighbor = (neighborContribution == min1) ? min2 : min1;
                long maxExcludingNeighbor = (neighborContribution == max1) ? max2 : max1;

                // Message = what parent contributes in the rerooted tree
                messageToSendToChild = 2L * maxExcludingNeighbor - minExcludingNeighbor + baseTime[currentNode];
            }

            // Only recurse on actual children (not the parent)
            if (neighbor != parent) {
                computeAllAnswersWithRerooting(neighbor, currentNode, messageToSendToChild);
            }
        }
    }
}

/*
 * EXAMPLE WALKTHROUGH:
 *
 * Tree structure:
 *       0 (base=10)
 *      / \
 *   1(5)  2(8)
 *         |
 *        3(6)
 *
 * Edges: [[0,1], [0,2], [2,3]]
 * baseTime: [10, 5, 8, 6]
 *
 * STEP 1 - First DFS (downward):
 * - Node 3 (leaf): down[3] = 6
 * - Node 2: down[2] = 2*6 - 6 + 8 = 14
 * - Node 1 (leaf): down[1] = 5
 * - Node 0: down[0] = 2*14 - 5 + 10 = 33
 *
 * STEP 2 - Second DFS (rerooting):
 * At node 0:
 * - Neighbors: [1, 2] with contributions [5, 14]
 * - answer[0] = 2*14 - 5 + 10 = 33
 * - Message to 1: 2*14 - 14 + 10 = 24
 * - Message to 2: 2*5 - 5 + 10 = 15
 *
 * At node 1 (via parent message 24):
 * - Neighbors: [0] with contribution [24]
 * - answer[1] = 2*24 - 24 + 5 = 29
 *
 * At node 2 (via parent message 15):
 * - Neighbors: [0, 3] with contributions [15, 6]
 * - answer[2] = 2*15 - 6 + 8 = 32
 * - Message to 3: 2*15 - 15 + 8 = 23
 *
 * At node 3 (via parent message 23):
 * - Neighbors: [2] with contribution [23]
 * - answer[3] = 2*23 - 23 + 6 = 29
 *
 * RESULT: min(33, 29, 32, 29) = 29
 * Optimal roots: nodes 1 or 3
 */