package Leetcode;

import java.util.LinkedList;
import java.util.List;

//Problem Link: https://leetcode.com/problems/count-visited-nodes-in-a-directed-graph

/**
 * Solution for counting visited nodes in a directed graph.
 *
 * Given a directed graph where each node has exactly one outgoing edge, for each node, count how many nodes will be
 * visited if we start from that node and keep following edges.
 *
 * The algorithm works in three phases: 1. Use topological sort to remove all nodes not in cycles 2. Identify and
 * process all cycles 3. Use DFS to calculate answers for nodes leading into cycles
 */
public class CountVisitedNodesInADirectedGraph {

    List<Integer> edges;  // edges[i] represents the node that node i points to
    int ans[];            // ans[i] stores the count of visited nodes starting from node i
    int n;                // Total number of nodes

    /**
     * Counts the number of nodes visited starting from each node.
     *
     * @param edges List where edges.get(i) is the node that node i points to
     * @return Array where ans[i] is the count of visited nodes starting from node i
     */
    public int[] countVisitedNodes(List<Integer> edges) {

        this.edges = edges;
        n = edges.size();
        ans = new int[n];  // Initialize answer array

        // Phase 1: Topological sort to identify nodes not in cycles
        // Calculate indegree (number of incoming edges) for each node
        var indegree = new int[n];
        for (int i = 0; i < n; i++) {
            indegree[edges.get(i)]++;
        }

        // Queue for topological sort - start with nodes that have no incoming edges
        var que = new LinkedList<Integer>();
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                que.offer(i);
            }
        }

        // Topological sort: remove nodes with indegree 0 iteratively
        // This removes all "tail" nodes that eventually lead to cycles but aren't in cycles
        while (!que.isEmpty()) {
            int next = edges.get(que.poll());
            indegree[next]--;  // Remove edge to next node

            // If next node now has no incoming edges, add it to queue
            if (indegree[next] == 0) {
                que.offer(next);
            }
        }

        // Phase 2: Process cycles
        // After topological sort, nodes with indegree > 0 are part of cycles
        for (int i = 0; i < n; i++) {
            if (indegree[i] > 0 && ans[i] == 0) {
                // Find the size of the cycle
                int size = 0;
                int curr = i;
                do {
                    curr = edges.get(curr);
                    size++;
                } while (curr != i);

                // Set answer for all nodes in the cycle
                // Each node in a cycle visits exactly 'size' nodes (the entire cycle)
                do {
                    ans[curr] = size;
                    curr = edges.get(curr);

                } while (curr != i);
            }
        }

        // Phase 3: Calculate answers for nodes that lead into cycles
        // Use DFS to compute answers based on distance to the cycle
        for (int i = 0; i < n; i++) {
            if (ans[i] == 0) {
                dfs(i);
            }
        }

        return ans;
    }

    /**
     * DFS to calculate the number of visited nodes for nodes outside cycles.
     *
     * For a node outside a cycle, the answer is: 1 (current node) + answer of the next node
     *
     * @param curr Current node being processed
     * @return Number of nodes visited starting from curr
     */
    private int dfs(int curr) {

        // Base case: answer already computed (node is in a cycle or already processed)
        if (ans[curr] != 0) {
            return ans[curr];
        }

        // Recursive case: visit current node + all nodes reachable from next node
        return ans[curr] = 1 + dfs(edges.get(curr));
    }
}