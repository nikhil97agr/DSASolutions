package Leetcode;//Problem Link: https://leetcode.com/problems/elevator-requests-iv/description/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Problem: Minimum Time to Fulfill All Elevator Requests
 *
 * Given: - n: number of floors (0 to n-1) - start: initial elevator position at time 0 - requests[i] = [arrival_time,
 * floor]: request for floor at arrival_time
 *
 * Constraints: - Elevator moves 1 floor per second (up/down/stay) - Request fulfilled when elevator reaches requested
 * floor at or after arrival time
 *
 * Approach: This solution uses Binary Search on the answer combined with Dynamic Programming.
 *
 * Key Insights: 1. Multiple requests for same floor can be consolidated - only track the latest arrival time 2. Binary
 * search on total time: if we can fulfill all requests in time T, we can in T+1 3. For each candidate time, use DP to
 * check if visiting all unique floors is feasible 4. The elevator must visit floors optimally - either left-to-right or
 * with detours
 */
public class ElevatorRequestsIV {

    public long elevatorRequests(int n, int start, int[][] requests) {

        // Step 1: Consolidate requests by floor
        // For each floor, we only need to track the LATEST arrival time
        // (earlier requests are automatically satisfied when we visit the floor later)
        Map<Integer, Long> map = new HashMap<>();
        for (int r[] : requests) {
            // Keep the maximum (latest) arrival time for this floor
            map.merge(r[1], (long) r[0], Math::max);
        }
        int m = map.size();

        // Step 2: Sort the unique floors to visit
        // This allows us to reason about visiting floors in sorted order
        List<Integer> floors = new ArrayList<>(map.keySet());
        floors.sort(Integer::compareTo);

        // Step 3: Determine binary search bounds
        // Lower bound (s): The latest arrival time among all requests
        // We cannot finish before the last request even arrives
        long s = map.values().stream().max(Long::compareTo).get();

        // Upper bound (e): Time to visit all floors sequentially in sorted order
        // This is a naive approach that guarantees feasibility
        long e = 0;
        long curr = start;
        for (var floor : floors) {
            // Time = max(current_time + travel_distance, request_arrival_time)
            e = Math.max(e + Math.abs(floor - curr), map.get(floor));
            curr = floor;
        }

        // Step 4: Binary search on the minimum time
        long ans = -1;
        while (s <= e) {
            var mid = (s + e) / 2;

            // Check if we can fulfill all requests within 'mid' time
            if (possible(mid, floors, start, map)) {
                ans = mid;
                e = mid - 1;  // Try to find a smaller time
            } else {
                s = mid + 1;  // Need more time
            }
        }

        return ans;
    }

    /**
     * Check if all requests can be fulfilled within the given time limit.
     *
     * Uses Dynamic Programming with interval DP approach: - State: For each contiguous range of floors [l, r], track
     * minimum time to visit all floors in that range, ending at either the left boundary or right boundary - left[l]:
     * minimum time to visit floors[l...r] and end at floors[l] - right[l]: minimum time to visit floors[l...r] and end
     * at floors[r]
     *
     * @param mid The time limit to check
     * @param floors Sorted list of unique floors to visit
     * @param start Starting floor position
     * @param map Map of floor -> latest arrival time for that floor
     * @return true if all requests can be fulfilled within 'mid' time units
     */
    private boolean possible(Long mid, List<Integer> floors, int start, Map<Integer, Long> map) {

        // Base case: only one floor to visit
        if (floors.size() == 1) {
            // Can we reach the floor from start position within time limit?
            return Math.abs(floors.getFirst() - start) <= mid;
        }

        // Use a large value instead of MAX_VALUE to avoid overflow in calculations
        long max = Long.MAX_VALUE / 4;
        var m = floors.size();

        // DP arrays: storing minimum time for intervals
        var left = new long[m];   // left[i]: min time to visit interval, ending at left boundary
        var right = new long[m];  // right[i]: min time to visit interval, ending at right boundary

        // Build up intervals of increasing size using bottom-up DP
        // i represents the size of the interval (number of floors)
        for (var i = 2; i <= m; i++) {
            int total = m - i + 1;  // number of possible intervals of size i

            // New DP arrays for current interval size
            var nl = new long[total];
            var nr = new long[total];
            Arrays.fill(nl, max);  // Initialize with max (impossible state)
            Arrays.fill(nr, max);

            // Try each interval of size i
            for (var l = 0; l < total; l++) {
                var r = l + i - 1;  // right boundary of current interval

                // Option 1: Visit floors[l] last (end on the left boundary)
                // We came from the sub-interval [l+1...r], arriving from either:
                // - left[l+1]: came from floors[l+1], move left to floors[l]
                // - right[l+1]: came from floors[r], move all the way left to floors[l]
                var leftTime = Math.min(
                        left[l + 1] + floors.get(l + 1) - floors.get(l),
                        right[l + 1] + floors.get(r) - floors.get(l)
                );

                // Check if this satisfies the arrival time constraint for floors[l]
                if (leftTime + map.get(floors.get(l)) <= mid) {
                    nl[l] = leftTime;
                }

                // Option 2: Visit floors[r] last (end on the right boundary)
                // We came from the sub-interval [l...r-1], arriving from either:
                // - left[l]: came from floors[l], move all the way right to floors[r]
                // - right[l]: came from floors[r-1], move right to floors[r]
                var rightTime = Math.min(
                        left[l] + floors.get(r) - floors.get(l),
                        right[l] + floors.get(r) - floors.get(r - 1)
                );

                // Check if this satisfies the arrival time constraint for floors[r]
                if (rightTime + map.get(floors.get(r)) <= mid) {
                    nr[l] = rightTime;
                }
            }

            // Update DP arrays for next iteration
            left = nl;
            right = nr;
        }

        // Final check: Can we visit all floors [0...m-1] starting from 'start'?
        // Two options:
        // 1. Start -> floors[0], then visit all, ending at floors[0]
        // 2. Start -> floors[m-1], then visit all, ending at floors[m-1]
        return Math.min(
                left[0] + Math.abs(floors.getFirst() - start),
                right[0] + Math.abs(floors.getLast() - start)
        ) <= mid;

    }
}
