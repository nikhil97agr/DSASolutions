package Leetcode;//Problem Link: https://leetcode.com/problems/minimize-manhattan-distances

/**
 * Solution for minimizing the maximum Manhattan distance after removing one point.
 *
 * Problem: Given n points in 2D space, remove exactly one point to minimize the maximum Manhattan distance between any
 * two remaining points.
 *
 * Manhattan distance between (x1, y1) and (x2, y2) = |x1 - x2| + |y1 - y2|
 *
 * Key mathematical insight - Coordinate transformation: Manhattan distance can be rewritten using a coordinate
 * transformation: - Let u = x + y, v = x - y - Then: |x1 - x2| + |y1 - y2| = max(|u1 - u2|, |v1 - v2|)
 *
 * Proof: Case 1: (x1 >= x2, y1 >= y2) → |x1-x2| + |y1-y2| = (x1-x2) + (y1-y2) = (x1+y1) - (x2+y2) = u1-u2 Case 2: (x1
 * >= x2, y1 < y2)  → |x1-x2| + |y1-y2| = (x1-x2) + (y2-y1) = (x1-y1) - (x2-y2) = v1-v2 Case 3: (x1 < x2, y1 >= y2)  →
 * |x1-x2| + |y1-y2| = (x2-x1) + (y1-y2) = (x2-y2) - (x1-y1) = v2-v1 Case 4: (x1 < x2, y1 < y2)   → |x1-x2| + |y1-y2| =
 * (x2-x1) + (y2-y1) = (x2+y2) - (x1+y1) = u2-u1
 *
 * In all cases: Manhattan distance = max(|u1-u2|, |v1-v2|)
 *
 * For a set of points: - Max Manhattan distance = max(max(u) - min(u), max(v) - min(v))
 *
 * Algorithm strategy: 1. Find the current maximum Manhattan distance (with all points) 2. The maximum distance is
 * determined by two points (the extremes) 3. Try removing each of these two extreme points 4. Return the minimum of the
 * two resulting maximum distances
 *
 * Why this works: - The maximum distance is always determined by points at the extremes of u or v - Removing one of
 * these extreme points gives the best reduction - We only need to try removing the two points that currently define the
 * max distance
 *
 * Time Complexity: O(n) - we scan the points a constant number of times Space Complexity: O(1)
 */
public class MinimizeManhattanDistances {

    /**
     * Finds the minimum possible maximum Manhattan distance after removing one point.
     *
     * @param points Array of 2D points where points[i] = [x, y]
     * @return Minimum possible maximum Manhattan distance
     */
    public int minimumDistance(int[][] points) {

        // Step 1: Find the current maximum distance with all points
        // curr[0] = maximum distance
        // curr[1] = index of one extreme point
        // curr[2] = index of the other extreme point
        int curr[] = findMaxDistance(points, -1);

        // Step 2: Try removing each of the two extreme points
        // The answer is the minimum of:
        // - Maximum distance after removing curr[1]
        // - Maximum distance after removing curr[2]
        return Math.min(findMaxDistance(points, curr[1])[0], findMaxDistance(points, curr[2])[0]);
    }

    /**
     * Finds the maximum Manhattan distance among points, optionally skipping one point.
     *
     * Uses coordinate transformation to simplify Manhattan distance calculation: - Transform: u = x + y, v = x - y -
     * Manhattan distance = max(max(u) - min(u), max(v) - min(v))
     *
     * @param points Array of 2D points
     * @param skipIndex Index of point to skip (-1 to include all points)
     * @return Array [maxDistance, index1, index2] where index1 and index2 are the two points that determine the maximum
     * distance
     */
    private int[] findMaxDistance(int points[][], int skipIndex) {

        // Track min and max of u = x + y (the "sum" coordinate)
        // maxAdd[0] = min(u), maxAdd[1] = max(u)
        int maxAdd[] = {Integer.MAX_VALUE, Integer.MIN_VALUE};

        // Track min and max of v = x - y (the "difference" coordinate)
        // maxSub[0] = min(v), maxSub[1] = max(v)
        int maxSub[] = {Integer.MAX_VALUE, Integer.MIN_VALUE};

        // Track indices of points with extreme u values
        // addInd[0] = index of point with min(u), addInd[1] = index with max(u)
        int addInd[] = new int[]{0, 0};

        // Track indices of points with extreme v values
        // subInd[0] = index of point with min(v), subInd[1] = index with max(v)
        int subInd[] = new int[]{0, 0};

        // Scan all points to find extremes in both coordinate systems
        for (var i = 0; i < points.length; i++) {
            // Skip the point if requested (used when testing removal)
            if (i == skipIndex) {
                continue;
            }

            // Transform coordinates
            int sum = points[i][0] + points[i][1];   // u coordinate
            int diff = points[i][0] - points[i][1];  // v coordinate

            // Update minimum u value and its index
            if (sum < maxAdd[0]) {
                maxAdd[0] = sum;
                addInd[0] = i;
            }

            // Update maximum u value and its index
            if (sum > maxAdd[1]) {
                maxAdd[1] = sum;
                addInd[1] = i;

            }

            // Update minimum v value and its index
            if (diff < maxSub[0]) {
                maxSub[0] = diff;
                subInd[0] = i;
            }

            // Update maximum v value and its index
            if (diff > maxSub[1]) {
                maxSub[1] = diff;
                subInd[1] = i;
            }
        }

        // The maximum Manhattan distance is the larger of:
        // - Range in u coordinates: max(u) - min(u)
        // - Range in v coordinates: max(v) - min(v)

        // Compare which coordinate system gives the larger distance
        if (maxAdd[1] - maxAdd[0] > maxSub[1] - maxSub[0]) {
            // u-coordinate range is larger
            // Return [distance, index_of_min_u, index_of_max_u]
            return new int[]{maxAdd[1] - maxAdd[0], addInd[0], addInd[1]};
        }

        // v-coordinate range is larger (or equal)
        // Return [distance, index_of_min_v, index_of_max_v]
        return new int[]{maxSub[1] - maxSub[0], subInd[0], subInd[1]};
    }
}