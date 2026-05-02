package Leetcode;

import java.util.ArrayList;
import java.util.List;

//Problem Link: https://leetcode.com/problems/maximum-number-of-darts-inside-of-a-circular-dartboard/

/**
 * Solution for finding maximum number of darts that can be enclosed by a circle of radius r.
 *
 * Problem: Given n darts on a 2D plane and a circle of radius r, find the maximum number of darts that can be inside
 * the circle (including boundary). You can place the circle anywhere.
 *
 * Key insight: Angular sweep algorithm with fixed center - For optimal placement, the circle's boundary should pass
 * through at least one dart - For each dart as a potential "anchor point" on the circle's boundary: * Imagine the
 * circle centered at various positions where this dart is on the boundary * As we rotate the circle around the anchor,
 * different darts enter/exit * Track the angular ranges where each dart can be inside
 *
 * Geometric approach: 1. Fix one dart (anchor) on the circle's boundary 2. For each other dart within range (distance ≤
 * 2r): - Calculate the angular range where the circle center can be positioned such that both the anchor and this dart
 * are inside/on the circle 3. Use sweep line algorithm on angles to find maximum overlap
 *
 * Why distance ≤ 2r? - If two darts are farther than 2r apart, they cannot both be in a circle of radius r - Maximum
 * distance between two points in a circle of radius r is 2r (diameter)
 *
 * Angular calculation: - For anchor dart at origin and another dart at distance d: * The circle center must be at
 * distance r from anchor (on a circle around anchor) * The circle center must be within distance r from the other dart
 * * This creates an angular range (arc) on the circle of radius r around anchor
 *
 * Example visualization: Anchor dart A, other dart B, circle radius r - Circle center can be anywhere on the circle of
 * radius r around A - But must also be within distance r from B - The intersection forms an arc with angular range
 * [angleA - angleB, angleA + angleB]
 *
 * Time Complexity: O(n² log n) - for each dart, process n darts and sort angles Space Complexity: O(n²) for distance
 * matrix
 */
public class MaximumNumberofDartsInsideofACircularDartboard {

    /**
     * Finds maximum number of darts that can fit in a circle of radius r.
     *
     * @param darts Array of [x, y] dart positions
     * @param r Radius of the circle
     * @return Maximum number of darts that can be enclosed
     */
    public int numPoints(int[][] darts, int r) {

        var n = darts.length;

        // Convert to Point objects for easier manipulation
        var points = new Point[n];
        for (var i = 0; i < n; i++) {
            points[i] = new Point(darts[i][0], darts[i][1]);
        }

        // ================================================================
        // STEP 1: Precompute all pairwise distances
        // ================================================================
        // dist[i][j] = Euclidean distance between dart i and dart j
        var dist = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                var p1 = points[i];
                var p2 = points[j];
                // Math.hypot(a, b) computes sqrt(a² + b²) with better precision
                dist[i][j] = dist[j][i] = Math.hypot(p1.x - p2.x, p1.y - p2.y);
            }
        }

        // ================================================================
        // STEP 2: Try each dart as anchor point on circle boundary
        // ================================================================
        var ans = 0;
        for (var i = 0; i < n; i++) {
            // For each dart, calculate max darts when this dart is on circle boundary
            ans = Math.max(ans, getPointsInside(i, points, dist, r, n));
        }

        return ans;
    }

    /**
     * Calculates maximum darts when dart 'ind' is fixed on the circle boundary.
     *
     * Strategy: Angular sweep algorithm 1. Fix dart 'ind' as anchor on the circle's boundary 2. Imagine the circle
     * center rotating around anchor at distance r 3. For each other dart, calculate angular range where it's inside the
     * circle 4. Use sweep line to find angle with maximum dart overlap
     *
     * Geometric calculation: - Anchor dart at position A - Circle center C is at distance r from A (on a circle around
     * A) - For another dart B at distance d from A: * B is inside/on circle if distance(C, B) ≤ r * This constrains C
     * to be within distance r from both A and B * The valid positions for C form an arc on the circle around A
     *
     * Angular range calculation: - angleA = direction angle from anchor to dart B (using atan2) - angleB = half of the
     * angular width of the arc (using acos) - Formula: angleB = acos(d / (2r)) where d = distance(A, B) - Valid range:
     * [angleA - angleB, angleA + angleB]
     *
     * Why acos(d / (2r))? - Consider triangle: A (anchor), B (dart), C (circle center) - |AC| = r (center is at
     * distance r from anchor) - |BC| ≤ r (dart B must be inside/on circle) - |AB| = d (given distance) - Using law of
     * cosines: angle at C can be calculated - The angular span on the circle around A is 2 * acos(d / (2r))
     *
     * @param ind Index of anchor dart
     * @param points Array of all dart positions
     * @param dist Precomputed distance matrix
     * @param r Circle radius
     * @param n Number of darts
     * @return Maximum darts when anchor dart is on boundary
     */
    private int getPointsInside(int ind, Point points[], double dist[][], int r, int n) {

        // List of angular events (dart entering/leaving as circle rotates)
        List<Pair> list = new ArrayList<>();

        // ================================================================
        // STEP 1: Calculate angular ranges for all reachable darts
        // ================================================================
        for (int i = 0; i < n; i++) {
            // Skip anchor dart itself
            if (ind == i) {
                continue;
            }

            // Skip darts too far away (cannot both be in circle of radius r)
            // Maximum distance between two points in a circle is 2r (diameter)
            if (dist[ind][i] > 2 * r) {
                continue;
            }

            // Calculate angular range where dart i can be inside the circle

            // angleB: Half of the angular width of the arc
            // Using law of cosines on the constraint geometry
            double angleB = Math.acos(dist[ind][i] / (2 * r));

            // angleA: Direction from anchor to dart i
            // atan2(y, x) gives the angle in radians from -π to π
            double angleA = Math.atan2(points[i].y - points[ind].y, points[i].x - points[ind].x);

            // Angular range where circle center can be positioned
            // to include both anchor and dart i
            double start = angleA - angleB;  // Start of arc (dart enters)
            double end = angleA + angleB;    // End of arc (dart exits)

            // Add events: start (dart enters), end (dart exits)
            list.add(new Pair(start, true));   // Entry event
            list.add(new Pair(end, false));    // Exit event
        }

        // ================================================================
        // STEP 2: Sort events by angle
        // ================================================================
        // When two events have same angle, process entry before exit
        // This ensures we count correctly when dart exactly enters/exits
        list.sort((a, b) -> {
            if (a.first < b.first) {
                return -1;  // Sort by angle ascending
            }
            if (a.first > b.first) {
                return 1;
            }

            // Same angle: entry (true) comes before exit (false)
            return a.second ? -1 : 1;
        });

        // ================================================================
        // STEP 3: Sweep through angles to find maximum overlap
        // ================================================================
        int ans = 1;   // Start with 1 (the anchor dart is always included)
        int cnt = 1;   // Current count of darts inside circle

        for (var pair : list) {
            if (pair.second) {
                // Entry event: dart enters the circle
                cnt++;
            } else {
                // Exit event: dart leaves the circle
                cnt--;
            }

            // Track maximum darts seen at any angle
            ans = Math.max(ans, cnt);
        }

        return ans;
    }

    /**
     * Pair class representing an angular event in the sweep line algorithm.
     *
     * Used to track when darts enter or exit the circle as we sweep through angles.
     */
    class Pair {

        double first;    // Angle in radians (from -π to π)
        boolean second;  // true = entry event (dart enters), false = exit event (dart leaves)

        /**
         * Constructor for angular event.
         *
         * @param first Angle at which event occurs
         * @param second true for entry, false for exit
         */
        public Pair(double first, boolean second) {

            this.first = first;
            this.second = second;
        }
    }

    /**
     * Point class representing a 2D coordinate.
     *
     * Represents the position of a dart on the plane.
     */
    class Point {

        int x;  // X-coordinate
        int y;  // Y-coordinate

        /**
         * Constructor for a 2D point.
         *
         * @param x X-coordinate
         * @param y Y-coordinate
         */
        public Point(int x, int y) {

            this.x = x;
            this.y = y;
        }
    }
}