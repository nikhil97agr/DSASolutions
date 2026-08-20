package Leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

//Problem Link: https://leetcode.com/problems/minimum-time-to-reach-target-with-limited-power/

/**
 * Leetcode.MinimumTimeToReachTargetWithLimitedPower - Modified Dijkstra with Power Constraint
 *
 * PROBLEM STATEMENT: Given a directed graph with n nodes and weighted edges, find the minimum time to reach the target
 * node from the source node, subject to a power constraint. - Each node has a cost[i] (power required to leave that
 * node) - Start with initial power - Power decreases by cost[node] when leaving each node - If multiple paths have the
 * same minimum time, choose the one with maximum remaining power
 *
 * APPROACH - MODIFIED DIJKSTRA'S ALGORITHM WITH 2D STATE:
 *
 * KEY INSIGHT: Standard Dijkstra tracks only (node, distance), but here we need to track (node, power, time) because
 * the same node can be reached with different power levels, leading to different future possibilities.
 *
 * STATE SPACE: - dp[node][remainingPower] = minimum time to reach 'node' with 'remainingPower' - Priority queue
 * processes states in order of (time, -power)
 *
 * WHY 2D DP? - Reaching a node with more power might allow access to more future nodes - We need to track all (node,
 * power) combinations that might lead to optimal solution
 *
 * ALGORITHM: 1. Build adjacency list from edges (directed graph) 2. Initialize priority queue with (source,
 * initialPower, time=0) 3. Process states in order of increasing time (and decreasing power for ties) 4. For each
 * state, try all outgoing edges if we have enough power 5. Track the best (time, power) pair that reaches the target
 *
 * PRIORITY QUEUE ORDERING: - Primary: Minimize time - Secondary: Maximize power (for tie-breaking)
 *
 * TIME COMPLEXITY: O((n * power) * log(n * power)) - Each (node, power) state can be visited once - Priority queue
 * operations are O(log(states))
 *
 * SPACE COMPLEXITY: O(n * power) - DP table: O(n * power) - Priority queue: O(n * power) in worst case
 */
public class MinimumTimeToReachTargetWithLimitedPower {

    // Adjacency list representation of the graph
    // list[u] = list of edges from node u
    List<Edge> list[];

    /**
     * Find minimum time to reach target from source with power constraint.
     *
     * @param n number of nodes in the graph
     * @param edges array of directed edges: [[u, v, time], ...]
     * @param power initial power available
     * @param cost array where cost[i] = power required to leave node i
     * @param source starting node
     * @param target destination node
     * @return [minTime, maxPower] or [-1, -1] if target unreachable
     */
    public long[] minTimeMaxPower(int n, int[][] edges, int power, int[] cost, int source, int target) {

        // STEP 1: Build adjacency list (directed graph)
        list = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            list[i] = new ArrayList<>();
        }

        // Add directed edges to adjacency list
        for (var edge : edges) {
            var u = edge[0];      // Source node of edge
            var v = edge[1];      // Destination node of edge
            var t = edge[2];      // Time to traverse edge

            list[u].add(new Edge(v, t));
        }

        // STEP 2: Initialize priority queue with custom comparator
        //
        // COMPARATOR LOGIC:
        // - Primary: Sort by time (ascending) - we want minimum time
        // - Secondary: Sort by power (descending) - for ties, prefer more power
        //
        // This ensures we process states in optimal order for Dijkstra's algorithm
        PriorityQueue<Path> que = new PriorityQueue<>((p1, p2) -> {
            if (p1.time == p2.time) {
                // Tie-breaker: prefer higher power (p2.power - p1.power for descending)
                return p2.power - p1.power;
            }

            // Primary: sort by time (ascending)
            return Long.compare(p1.time, p2.time);
        });

        // STEP 3: Initialize starting state and result variables
        que.offer(new Path(source, power, 0));  // Start at source with full power and 0 time

        // Variables to track the best solution that reaches target
        long time = Long.MAX_VALUE / 4;  // Best time found to reach target
        long p = Long.MIN_VALUE / 4;     // Power remaining at best time

        // DP table: dp[node][remainingPower] = minimum time to reach 'node' with 'remainingPower'
        // Initialized to infinity (unreachable by default)
        long dp[][] = new long[n][power + 1];
        for (var d : dp) {
            Arrays.fill(d, Long.MAX_VALUE / 4);  // Use MAX_VALUE/4 to avoid overflow
        }

        // Base case: we start at source with full power at time 0
        dp[source][power] = 0;
        // STEP 4: Modified Dijkstra's algorithm
        //
        // Process states in order of (time, -power) until queue is empty
        while (!que.isEmpty()) {
            var next = que.poll();  // Get state with minimum time (max power if tied)
            var src = next.dest;    // Current node
            var pow = next.power;   // Current remaining power
            var t = next.time;      // Current time elapsed

            // PRUNING: Skip if we've found a better path to this (node, power) state
            // This is the standard Dijkstra optimization
            if (t > dp[src][pow]) {
                continue;
            }

            // GOAL CHECK: Have we reached the target?
            if (src == target) {
                // Update best solution if:
                // 1. We found a faster path, OR
                // 2. Same time but more power remaining
                if (t < time) {
                    time = t;    // New best time
                    p = pow;     // Power at this time
                } else if (t == time && pow > p) {
                    p = pow;     // Better power at same time
                }

                // Continue searching (might find better solutions)
                continue;
            }

            // POWER CHECK: Can we afford to leave this node?
            // We need at least cost[src] power to depart from src
            if (pow < cost[src]) {
                continue;  // Insufficient power, can't proceed
            }

            // RELAXATION: Try all outgoing edges from current node
            for (var child : list[src]) {
                var dest = child.dest;   // Destination node of edge
                var tt = child.time;     // Time to traverse edge

                // Calculate new state after taking this edge
                var nextime = t + tt;           // New time = current time + edge time
                var nextpow = pow - cost[src];  // New power = current power - cost to leave src

                // RELAXATION: Update if we found a better path to (dest, nextpow)
                if (nextime < dp[dest][nextpow]) {
                    dp[dest][nextpow] = nextime;  // Update DP table
                    que.offer(new Path(dest, nextpow, nextime));  // Add to queue
                }
            }
        }

        // STEP 5: Return result
        // If time was never updated, target is unreachable
        if (time == Long.MAX_VALUE / 4) {
            return new long[]{-1, -1};  // No valid path found
        }

        // Return [minimum time, maximum power at that time]
        return new long[]{time, p};
    }

    /**
     * Path class represents a state in the search space.
     *
     * A state consists of: - dest: current node - power: remaining power at this node - time: total time elapsed to
     * reach this state
     *
     * States are compared in the priority queue based on (time, -power)
     */
    class Path {

        int dest;      // Current node
        int power;     // Remaining power at this node
        long time;     // Time elapsed to reach this state

        /**
         * Constructor for Path state.
         *
         * @param dest current node
         * @param power remaining power
         * @param time time elapsed
         */
        public Path(int dest, int power, long time) {

            this.dest = dest;
            this.power = power;
            this.time = time;
        }
    }

    /**
     * Edge class represents a directed edge in the graph.
     *
     * Each edge has: - dest: destination node - time: time required to traverse this edge
     */
    class Edge {

        int dest;      // Destination node
        long time;     // Time to traverse edge

        /**
         * Constructor for Edge.
         *
         * @param dest destination node
         * @param time time to traverse edge
         */
        public Edge(int dest, long time) {

            this.dest = dest;
            this.time = time;
        }
    }
}

/*
 * EXAMPLE WALKTHROUGH:
 *
 * Graph:
 *   0 ---(10)---> 1 ---(5)---> 2
 *   |                          ^
 *   +---------(20)-------------+
 *
 * Edges: [[0,1,10], [1,2,5], [0,2,20]]
 * power: 100
 * cost: [10, 5, 0]  (cost to leave each node)
 * source: 0, target: 2
 *
 * STEP 1: Build adjacency list
 * list[0] = [(1, 10), (2, 20)]
 * list[1] = [(2, 5)]
 * list[2] = []
 *
 * STEP 2: Initialize
 * Queue: [(dest=0, power=100, time=0)]
 * dp[0][100] = 0
 *
 * STEP 3: Process states
 *
 * Iteration 1: Process (dest=0, power=100, time=0)
 * - Not target, has enough power (100 >= cost[0]=10)
 * - Try edge to node 1: nextime=10, nextpow=90
 *   - dp[1][90] = 10, add (dest=1, power=90, time=10)
 * - Try edge to node 2: nextime=20, nextpow=90
 *   - dp[2][90] = 20, add (dest=2, power=90, time=20)
 *
 * Iteration 2: Process (dest=1, power=90, time=10)
 * - Not target, has enough power (90 >= cost[1]=5)
 * - Try edge to node 2: nextime=15, nextpow=85
 *   - dp[2][85] = 15, add (dest=2, power=85, time=15)
 *
 * Iteration 3: Process (dest=2, power=85, time=15)
 * - Target reached! Update: time=15, p=85
 *
 * Iteration 4: Process (dest=2, power=90, time=20)
 * - Target reached! But time=20 > 15, don't update
 *
 * RESULT: [15, 85]
 * - Minimum time: 15 (path: 0 → 1 → 2)
 * - Maximum power at that time: 85
 *
 * WHY 2D DP IS NECESSARY:
 * Both (2, power=85, time=15) and (2, power=90, time=20) are valid states.
 * We need to track both because they represent different trade-offs.
 * The algorithm correctly identifies (2, 85, 15) as optimal.
 */