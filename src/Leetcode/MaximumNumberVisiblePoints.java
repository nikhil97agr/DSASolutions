package Leetcode;

import java.util.ArrayList;
import java.util.List;

//Problem Link: https://leetcode.com/problems/maximum-number-of-visible-points/

/**
 * Solution class for finding the maximum number of visible points within a given viewing angle. This problem uses polar
 * coordinates and a sliding window approach.
 */
public class MaximumNumberVisiblePoints {

    private static int getCnt(int angle, ArrayList<Double> degrees) {

        int cnt = 0;

        // Use sliding window to find the maximum number of points within the angle range
        for (int start = 0, end = 0; end < degrees.size(); end++) {
            // Shrink the window from the left while the angle difference exceeds the limit
            while (degrees.get(end) - degrees.get(start) > angle) {
                start++;
            }

            // Update the maximum count of points in the current valid window
            cnt = Math.max(cnt, end - start + 1);
        }
        return cnt;
    }

    /**
     * Calculates the maximum number of points visible from a given location within a specified angle.
     *
     * @param points List of 2D points represented as [x, y] coordinates
     * @param angle The viewing angle in degrees
     * @param location The observer's location as [x, y] coordinates
     * @return The maximum number of points visible within the given angle
     */
    public int visiblePoints(List<List<Integer>> points, int angle, List<Integer> location) {

        // Store the angular positions of all points relative to the location
        var degrees = new ArrayList<Double>();

        // Count points that overlap with the observer's location (always visible)
        int overlap = 0;

        // Convert each point to its angular position relative to the location
        for (var p : points) {
            // If point is at the same location as observer, it's always visible
            if (p.equals(location)) {
                overlap++;
                continue;
            }

            // Calculate the relative position (delta x and delta y)
            var dx = p.get(0) - location.get(0);
            var dy = p.get(1) - location.get(1);

            // Convert Cartesian coordinates to polar angle using atan2
            var degree = Math.toDegrees(Math.atan2(dx, dy));

            // Add the angle and also add angle + 360 to handle circular wrapping
            // This allows us to handle cases where the viewing angle crosses 0/360 degrees
            degrees.add(degree);
            degrees.add(degree + 360);
        }

        // Sort all angles to enable sliding window approach
        degrees.sort(Double::compareTo);

        // Track the maximum count of visible points
        int cnt = getCnt(angle, degrees);

        // Return the maximum visible points plus those overlapping with the location
        return cnt + overlap;
    }
}