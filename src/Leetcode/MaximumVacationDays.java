package Leetcode;

import java.util.Arrays;

// Problem Link: https://leetcode.com/problems/maximum-vacation-days

/**
 * Solution for Maximum Vacation Days
 *
 * Problem: You are given n cities and k weeks. You start in city 0. - flights[i][j] = 1 means there's a direct flight
 * from city i to city j - days[i][j] = number of vacation days you can take in city i during week j - Each week, you
 * can either stay in the current city or fly to another city - Goal: Maximize total vacation days over k weeks
 *
 * Example: flights = [[0,1,1],    // From city 0: can fly to city 1 or 2 [1,0,1],    // From city 1: can fly to city 0
 * or 2 [1,1,0]]    // From city 2: can fly to city 0 or 1
 *
 * days = [[1,3,1],       // City 0: 1 day (week 0), 3 days (week 1), 1 day (week 2) [6,0,3],       // City 1: 6 days
 * (week 0), 0 days (week 1), 3 days (week 2) [3,3,3]]       // City 2: 3 days (week 0), 3 days (week 1), 3 days (week
 * 2)
 *
 * Optimal strategy: Week 0: Fly to city 1 -> 6 vacation days Week 1: Fly to city 0 -> 3 vacation days Week 2: Fly to
 * city 1 -> 3 vacation days Total: 6 + 3 + 3 = 12 vacation days
 *
 * Approach: Dynamic Programming - State: dp[city][week] = maximum vacation days achievable by being in 'city' at 'week'
 * - Transition: For each week, try all possible source cities and pick the best - Base case: Start at city 0 in week 0
 */
public class MaximumVacationDays {

    /**
     * Calculates maximum vacation days over k weeks
     *
     * @param flights n x n matrix where flights[i][j] = 1 means flight from city i to j exists
     * @param days n x k matrix where days[i][j] = vacation days in city i during week j
     * @return Maximum total vacation days achievable
     */
    public int maxVacationDays(int[][] flights, int[][] days) {

        int n = flights.length;      // Number of cities
        int k = days[0].length;      // Number of weeks

        // DP table: dp[city][week] = max vacation days by being in 'city' at 'week'
        int dp[][] = new int[n][k];

        // Initialize with -1 to indicate unreachable states
        for (int d[] : dp) {
            Arrays.fill(d, -1);
        }

        // Base case: Start at city 0 in week 0
        dp[0][0] = days[0][0];

        // Week 0: Check which cities we can reach from city 0
        for (int i = 1; i < n; i++) {
            if (flights[0][i] == 1) {
                // We can fly from city 0 to city i in week 0
                dp[i][0] = days[i][0];
            }
        }

        // Process each week from 1 to k-1
        for (int week = 1; week < k; week++) {
            // For each possible destination city
            for (int destination = 0; destination < n; destination++) {
                // Try all possible source cities from previous week
                for (int src = 0; src < n; src++) {
                    // Check if we can transition from src to destination
                    // Conditions:
                    // 1. dp[src][week-1] != -1: src was reachable in previous week
                    // 2. flights[src][destination] == 1: there's a flight from src to destination
                    //    OR src == destination: we can stay in the same city
                    if (dp[src][week - 1] != -1 && (flights[src][destination] == 1 || src == destination)) {
                        // Update dp[destination][week] with the maximum
                        // Previous vacation days + vacation days in destination this week
                        dp[destination][week] = Math.max(dp[destination][week],
                                dp[src][week - 1] + days[destination][week]);
                    }
                }
            }
        }

        // Return the maximum vacation days across all cities in the last week
        // We can end in any city, so we take the maximum
        return Arrays.stream(dp).mapToInt(x -> x[k - 1]).max().getAsInt();
    }
}