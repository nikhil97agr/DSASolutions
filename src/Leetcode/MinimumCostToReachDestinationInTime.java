package Leetcode;

import java.util.Arrays;

//Problem Link: https://leetcode.com/problems/minimum-cost-to-reach-destination-in-time/

/**
 * Solution for finding the minimum cost to reach destination within a time limit. Uses dynamic programming where
 * dp[time][city] represents the minimum cost to reach a city at a specific time. The graph is undirected, and each city
 * has a passing fee.
 */
public class MinimumCostToReachDestinationInTime {

    /**
     * Calculates the minimum cost to reach the destination city (city n-1) from city 0 within the given time limit.
     *
     * @param maxTime Maximum time allowed to reach destination
     * @param edges Array of edges where edges[i] = [u, v, time] represents an undirected edge between cities u and v
     * that takes 'time' units to traverse
     * @param passingFees Array where passingFees[i] is the fee to pass through city i
     * @return Minimum cost to reach destination within maxTime, or -1 if impossible
     */
    public int minCost(int maxTime, int[][] edges, int[] passingFees) {

        int n = passingFees.length;

        // DP table: dp[time][city] = minimum cost to reach 'city' at exactly 'time' units
        int dp[][] = new int[maxTime + 1][n];

        // Initialize all states with a large value (infinity)
        for (int d[] : dp) {
            Arrays.fill(d, 1_000_000_0);
        }

        // Base case: start at city 0 at time 0 with its passing fee
        dp[0][0] = passingFees[0];

        // Iterate through all time units from 0 to maxTime
        for (int currTime = 0; currTime <= maxTime; currTime++) {
            // Try all edges to transition to next states
            for (var e : edges) {
                int u = e[0];        // First city of the edge
                int v = e[1];        // Second city of the edge
                int time = e[2];     // Time to traverse this edge
                int nextTime = currTime + time;

                // Skip if this transition exceeds the time limit
                if (nextTime > maxTime) {
                    continue;
                }

                // Update cost to reach city v at nextTime (traveling from u to v)
                dp[nextTime][v] = Math.min(dp[nextTime][v], dp[currTime][u] + passingFees[v]);

                // Update cost to reach city u at nextTime (traveling from v to u)
                // This handles the undirected nature of the graph
                dp[nextTime][u] = Math.min(dp[nextTime][u], dp[currTime][v] + passingFees[u]);
            }
        }

        // Find the minimum cost to reach destination (city n-1) at any time <= maxTime
        int ans = 1_000_000_0;
        for (int i = 0; i <= maxTime; i++) {
            ans = Math.min(ans, dp[i][n - 1]);
        }

        // Return -1 if destination is unreachable within time limit
        return ans == 1_000_000_0 ? -1 : ans;
    }

}