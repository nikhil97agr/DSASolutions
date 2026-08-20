package Leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

//Problem Link: https://leetcode.com/problems/minimum-moves-to-balance-circular-array-ii/description/

/**
 * Solution for the Circular Array Balance Problem
 *
 * Problem: Given a circular array where balance[i] represents the net balance of person i, find the minimum number of
 * moves to make all balances non-negative. In one move, a person can transfer exactly 1 unit to their left or right
 * neighbor.
 *
 * Approach: Minimum Cost Maximum Flow (MCMF) - Model the problem as a flow network where flow represents balance
 * transfers - Use source node to supply positive balances and sink node to absorb negative balances - Each person can
 * transfer to left/right neighbors with cost 1 per unit - Find the minimum cost to route all flow from source to sink
 */
public class MinimumMovesToBalanceCircularArrayII {

    // Adjacency list to store the flow network graph
    List<Edge> adjList[];

    /**
     * Calculates minimum moves required to balance the circular array
     *
     * @param balance - circular array where balance[i] is net balance of person i
     * @return minimum number of moves, or -1 if impossible
     */
    public long minMoves(int[] balance) {

        var n = balance.length;

        // Check if solution is possible: total balance must be non-negative
        // If sum < 0, we cannot distribute to make everyone non-negative
        long sum = 0;
        for (int x : balance) {
            sum += x;
        }
        if (sum < 0) {
            return -1;
        }

        // Initialize adjacency list for n people + source + sink
        adjList = new ArrayList[n + 2];
        for (int i = 0; i < n + 2; i++) {
            adjList[i] = new ArrayList<>();
        }

        // s = source node (supplies positive balances)
        // t = sink node (absorbs negative balances)
        int s = n;
        int t = n + 1;
        long max = Long.MAX_VALUE / 3;  // Large capacity for transfer edges

        // Build the flow network
        for (int i = 0; i < n; i++) {
            // If person i has positive balance, connect source to person i
            // Capacity = positive balance, cost = 0 (no cost to supply)
            if (balance[i] > 0) {
                add(s, i, balance[i], 0);
            }
            // If person i has negative balance, connect person i to sink
            // Capacity = absolute value of negative balance, cost = 0 (no cost to absorb)
            else if (balance[i] < 0) {
                add(i, t, -balance[i], 0);
            }

            // Add edges to neighbors in circular array
            // Person i can transfer to right neighbor (i+1) with cost 1 per unit
            add(i, (i + 1) % n, max, 1);
            // Person i can transfer to left neighbor (i-1) with cost 1 per unit
            add(i, (i - 1 + n) % n, max, 1);
        }

        // Total cost (minimum number of moves)
        long ans = 0;

        // Minimum Cost Maximum Flow algorithm using Bellman-Ford variant (SPFA)
        // Repeatedly find shortest (minimum cost) path and push flow
        while (true) {
            // dist[i] = minimum cost to reach node i from source
            long dist[] = new long[n + 2];
            Arrays.fill(dist, max);

            // Track parent node and parent edge for path reconstruction
            int[] pNode = new int[n + 2];  // parent node
            int pEdge[] = new int[n + 2];  // parent edge index
            boolean[] inQueue = new boolean[n + 2];  // for SPFA optimization

            // SPFA (Shortest Path Faster Algorithm) - variant of Bellman-Ford
            var que = new LinkedList<Integer>();
            dist[s] = 0;
            que.add(s);
            inQueue[s] = true;

            // Find shortest path from source to sink based on cost
            while (!que.isEmpty()) {
                int u = que.poll();
                inQueue[u] = false;

                // Relax all edges from current node
                for (int i = 0; i < adjList[u].size(); i++) {
                    var edge = adjList[u].get(i);

                    // Only consider edges with remaining capacity
                    if (edge.cap > 0 && dist[edge.to] > dist[u] + edge.cost) {
                        dist[edge.to] = dist[u] + edge.cost;
                        pNode[edge.to] = u;
                        pEdge[edge.to] = i;
                        if (!inQueue[edge.to]) {
                            que.add(edge.to);
                            inQueue[edge.to] = true;
                        }
                    }
                }
            }

            // If no path from source to sink exists, we're done
            if (dist[t] == max) {
                break;
            }

            // Find minimum capacity (bottleneck) along the path from source to sink
            long mm = max;
            int curr = t;

            while (curr != s) {
                int p = pNode[curr];
                int ind = pEdge[curr];
                mm = Math.min(mm, adjList[p].get(ind).cap);
                curr = p;
            }

            // Push flow along the path and update costs
            curr = t;
            while (curr != s) {
                int p = pNode[curr];
                int ind = pEdge[curr];
                Edge e = adjList[p].get(ind);

                // Decrease capacity of forward edge
                e.cap -= mm;
                // Increase capacity of reverse edge (for flow cancellation)
                adjList[curr].get(e.rev).cap += mm;
                // Add cost: flow amount * cost per unit
                ans += mm * e.cost;
                curr = p;
            }

        }

        return ans;
    }

    /**
     * Adds a directed edge to the flow network
     *
     * @param u - source node of the edge
     * @param v - destination node of the edge
     * @param cap - capacity of the edge (max flow that can pass through)
     * @param cost - cost per unit of flow on this edge
     */
    public void add(int u, int v, long cap, long cost) {

        // Add forward edge from u to v
        // rev = index of the reverse edge in v's adjacency list
        adjList[u].add(new Edge(v, adjList[v].size(), cap, cost));

        // Add reverse edge from v to u with 0 capacity and negative cost
        // This allows flow cancellation in the algorithm
        adjList[v].add(new Edge(u, adjList[u].size() - 1, 0, -cost));
    }

    /**
     * Edge class representing a directed edge in the flow network
     */
    class Edge {

        int to;      // destination node
        int rev;     // index of reverse edge in destination's adjacency list
        long cap;    // remaining capacity of this edge
        long cost;   // cost per unit of flow

        public Edge(int to, int rev, long cap, long cost) {

            this.to = to;
            this.rev = rev;
            this.cap = cap;
            this.cost = cost;
        }
    }
}
