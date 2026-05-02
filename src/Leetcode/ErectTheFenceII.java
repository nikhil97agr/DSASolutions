package Leetcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

//Problem Link: https://leetcode.com/problems/erect-the-fence-ii/

/**
 * Solution for finding the smallest enclosing circle for a set of points.
 *
 * Problem: Given n trees (points on a 2D plane), find the smallest circle that encloses all trees. Return [center_x,
 * center_y, radius].
 *
 * Key insight: Welzl's Algorithm (Randomized Incremental Algorithm)
 *
 * The smallest enclosing circle has a unique property: - The circle is determined by at most 3 points on its boundary -
 * If 2 points define it: they are diametrically opposite (circle passes through both) - If 3 points define it: they
 * form a triangle with the circle as its circumcircle
 *
 * Algorithm intuition: - Build the circle incrementally, adding one point at a time - If a new point is outside the
 * current circle, the circle must pass through this point - Use recursion with a "boundary" set: points that MUST be on
 * the circle's boundary - When we have 3 boundary points, the circle is uniquely determined
 *
 * Welzl's algorithm steps: 1. If all points processed OR boundary has 3 points → generate circle from boundary 2. Pick
 * a random unprocessed point p 3. Recursively solve for remaining points (without p) 4. If p is inside the result
 * circle → return that circle 5. If p is outside → p MUST be on boundary, recurse with p added to boundary
 *
 * Why randomization? - Expected O(n) time complexity (without randomization, could be O(n⁴)) - Random order makes it
 * unlikely that many points are outside intermediate circles - On average, only a few points trigger boundary updates
 *
 * Example: points = [(0,0), (1,0), (0,1), (1,1)] - Smallest circle: center = (0.5, 0.5), radius = √2/2 ≈ 0.707 - Passes
 * through all 4 points (they're on a circle)
 *
 * Time Complexity: O(n) expected (worst case O(n⁴) but very rare with randomization) Space Complexity: O(n) for
 * recursion stack
 */
public class ErectTheFenceII {

    /**
     * Finds the smallest circle enclosing all trees.
     *
     * @param trees Array of [x, y] coordinates for each tree
     * @return [center_x, center_y, radius] of smallest enclosing circle
     */
    public double[] outerTrees(int[][] trees) {

        // Convert input to Point objects
        var points = new ArrayList<Point>();
        for (var t : trees) {
            points.add(new Point(t[0], t[1]));
        }
        var n = trees.length;

        // Randomize point order for expected O(n) performance
        // Random order ensures we're unlikely to repeatedly pick outliers
        Collections.shuffle(points);

        // Solve using Welzl's algorithm
        // Start with empty boundary (no points forced on circle boundary yet)
        Circle circle = solve(points, new ArrayList<>(), n);

        return new double[]{circle.center.x, circle.center.y, circle.radius};
    }

    /**
     * Generates the minimum circle that encloses all points in the boundary set.
     *
     * The boundary set contains points that MUST lie on the circle's boundary. Based on the number of boundary points:
     * - 0 points: Trivial circle (no constraints) - 1 point: Circle centered at that point with radius 0 - 2 points:
     * Circle with these as diameter endpoints - 3 points: Circumcircle of the triangle formed by these points
     *
     * Special case with 3 points: - Sometimes 2 of the 3 points define the circle (the third is inside) - Try all pairs
     * first to see if a smaller circle works - If no pair works, all 3 points are on the boundary (use circumcircle)
     *
     * Why try pairs when we have 3 boundary points? - The boundary set means these points MUST be on or inside the
     * circle - But not all of them need to be exactly on the boundary - Example: boundary = [(0,0), (1,0), (0.5, 0.1)]
     * * Circle through (0,0) and (1,0) might already contain (0.5, 0.1) * This gives a smaller circle than the
     * circumcircle of all 3
     *
     * @param boundary List of points that must be on/inside the circle
     * @return Smallest circle enclosing all boundary points
     */
    private Circle generateMinCircle(List<Point> boundary) {

        // Case 0: No boundary constraints
        if (boundary.isEmpty()) {
            return new Circle(new Point(0, 0), 0);  // Trivial circle
        }

        // Case 1: Single boundary point
        if (boundary.size() == 1) {
            return new Circle(boundary.getFirst(), 0);  // Point circle
        }

        // Case 2: Two boundary points
        if (boundary.size() == 2) {
            // Circle with these two points as diameter endpoints
            return buildCircleFromPoints(boundary.getFirst(), boundary.getLast());
        }

        // Case 3: Three boundary points
        // Try all pairs of points to see if they define a valid circle
        for (int i = 0; i < boundary.size(); i++) {
            for (int j = i + 1; j < boundary.size(); j++) {
                // Build circle with points i and j as diameter
                Circle circle = buildCircleFromPoints(boundary.get(i), boundary.get(j));

                // Check if this circle contains all boundary points
                if (circle.isValid(boundary)) {
                    return circle;  // Found a valid circle with just 2 points
                }
            }
        }

        // No pair works, all 3 points must be on the boundary
        // Return the circumcircle of the triangle
        return buildCircleFromPoints(boundary.getFirst(), boundary.get(1), boundary.getLast());
    }

    /**
     * Calculates the circumcenter of a triangle using coordinate transformation.
     *
     * Given three points forming a triangle, finds the center of the circle passing through all three points
     * (circumcircle).
     *
     * Mathematical approach: - Translate the triangle so p1 is at origin - Points become: (0,0), (bx, by), (cx, cy) -
     * Use perpendicular bisector method to find center
     *
     * Formula derivation: - The circumcenter is equidistant from all three vertices - Set |center - p1|² = |center -
     * p2|² = |center - p3|² - Solve the system of linear equations - Result: center = ((cy*a - by*b)/(2d), (bx*b -
     * cx*a)/(2d)) where a = bx² + by², b = cx² + cy², d = bx*cy - by*cx
     *
     * The determinant d = bx*cy - by*cx is twice the signed area of the triangle. If d = 0, the points are collinear
     * (no unique circle).
     *
     * @param bx X-coordinate of second point relative to first
     * @param by Y-coordinate of second point relative to first
     * @param cx X-coordinate of third point relative to first
     * @param cy Y-coordinate of third point relative to first
     * @return Center point of circumcircle (relative to origin)
     */
    private Point getCenter(double bx, double by, double cx, double cy) {

        double a = bx * bx + by * by;  // |p2 - p1|² in transformed coordinates
        double b = cx * cx + cy * cy;  // |p3 - p1|² in transformed coordinates
        double d = bx * cy - by * cx;  // Cross product (2 × triangle area)

        // Calculate circumcenter in transformed coordinates
        return new Point((cy * a - by * b) / (2 * d), (bx * b - cx * a) / (2 * d));
    }

    /**
     * Builds a circle passing through three points (circumcircle).
     *
     * Strategy: 1. Translate so p1 is at origin 2. Calculate center in transformed coordinates 3. Translate back to
     * original coordinates 4. Calculate radius as distance from center to any of the three points
     *
     * @param p1 First point on circle
     * @param p2 Second point on circle
     * @param p3 Third point on circle
     * @return Circle passing through all three points
     */
    private Circle buildCircleFromPoints(Point p1, Point p2, Point p3) {

        // Get center in coordinates relative to p1
        Point center = getCenter(p2.x - p1.x, p2.y - p1.y, p3.x - p1.x, p3.y - p1.y);

        // Translate back to original coordinate system
        center.x += p1.x;
        center.y += p1.y;

        // Radius is distance from center to any point (we use p1)
        return new Circle(center, center.dist(p1));
    }

    /**
     * Builds a circle with two points as diameter endpoints.
     *
     * When a circle is defined by two points, they must be diametrically opposite. - Center is the midpoint of the two
     * points - Radius is half the distance between them
     *
     * @param p1 First endpoint of diameter
     * @param p2 Second endpoint of diameter
     * @return Circle with p1 and p2 as diameter endpoints
     */
    private Circle buildCircleFromPoints(Point p1, Point p2) {

        // Center is midpoint of the two points
        Point p = new Point((p1.x + p2.x) / 2d, (p1.y + p2.y) / 2d);

        // Radius is half the distance between the points
        return new Circle(p, p1.dist(p2) / 2d);
    }


    /**
     * Recursive solver implementing Welzl's algorithm.
     *
     * Builds the smallest enclosing circle incrementally by processing points one at a time.
     *
     * Base cases: 1. n == 0: All points processed → generate circle from boundary constraints 2. boundary.size() == 3:
     * Maximum boundary points → circle is uniquely determined
     *
     * Recursive case: 1. Pick a random unprocessed point (randomization ensures expected O(n) time) 2. Swap it to
     * position n-1 (so we can exclude it from recursive call) 3. Recursively solve for remaining n-1 points with
     * current boundary 4. If the picked point is inside the result circle → done (return that circle) 5. If the picked
     * point is outside → it MUST be on the boundary - Add it to boundary set - Recursively solve again with updated
     * boundary
     *
     * Why this works: - If a point is outside the minimal circle for other points, the final circle MUST pass through
     * this point (otherwise we could shrink it) - The boundary set accumulates all such critical points - At most 3
     * points can define a circle, so recursion depth is limited
     *
     * Example trace: points = [A, B, C], boundary = [] 1. Pick C randomly 2. Solve for [A, B] with boundary = [] →
     * Circle₁ through A and B 3. Check if C inside Circle₁: - If yes: return Circle₁ - If no: recurse with boundary =
     * [C] * Solve for [A, B] with boundary = [C] * Pick B * Solve for [A] with boundary = [C] → Circle₂ through A and C
     * * If B inside Circle₂: return Circle₂ * If B outside: solve for [A] with boundary = [C, B] → Circle₃ through A,
     * B, C
     *
     * @param points List of all points (only first n are considered)
     * @param boundary Points that MUST be on the circle boundary
     * @param n Number of unprocessed points
     * @return Smallest circle enclosing first n points and passing through boundary points
     */
    private Circle solve(List<Point> points, List<Point> boundary, int n) {

        // ================================================================
        // BASE CASES
        // ================================================================

        // Base case 1: All points processed
        // Base case 2: Already have 3 boundary points (circle is determined)
        if (n == 0 || boundary.size() == 3) {
            return generateMinCircle(boundary);
        }

        // ================================================================
        // RECURSIVE CASE: Process one more point
        // ================================================================

        // Pick a random point from the first n points
        var randomIndex = new Random().nextInt(n);
        var point = points.get(randomIndex);

        // Swap it to position n-1 (so we can process points[0..n-2] recursively)
        Collections.swap(points, randomIndex, n - 1);

        // ============================================================
        // Recursively solve for n-1 points (excluding current point)
        // ============================================================
        Circle circle = solve(points, boundary, n - 1);

        // ============================================================
        // Check if current point is inside the circle
        // ============================================================
        if (point.inside(circle)) {
            // Point is already enclosed, no need to update circle
            return circle;
        }

        // ============================================================
        // Point is outside! It must be on the boundary of final circle
        // ============================================================
        // Add current point to boundary set
        List<Point> newBoundary = new ArrayList<>(boundary);
        newBoundary.add(point);

        // Recurse with updated boundary (point must be on circle now)
        return solve(points, newBoundary, n - 1);
    }

    /**
     * Represents a 2D point with double precision coordinates.
     */
    class Point {

        double x;  // X-coordinate
        double y;  // Y-coordinate

        /**
         * Constructor for a 2D point.
         *
         * @param x X-coordinate
         * @param y Y-coordinate
         */
        public Point(double x, double y) {

            this.x = x;
            this.y = y;
        }

        /**
         * Checks if this point is inside or on the boundary of a circle.
         *
         * A point is inside if its distance to center ≤ radius. Uses <= comparison which naturally handles boundary
         * points.
         *
         * @param circle Circle to check against
         * @return true if point is inside or on circle boundary
         */
        public boolean inside(Circle circle) {

            Point center = circle.center;
            double radius = circle.radius;
            double dist = this.dist(center);

            // Point is inside if distance ≤ radius
            return dist <= radius;
        }

        /**
         * Calculates Euclidean distance to another point.
         *
         * Uses Math.hypot for better numerical stability and precision. hypot(a, b) computes √(a² + b²) while avoiding
         * overflow/underflow.
         *
         * @param point Other point
         * @return Distance to the point
         */
        public double dist(Point point) {

            return Math.hypot(this.x - point.x, this.y - point.y);
        }
    }

    /**
     * Represents a circle defined by a center point and radius.
     */
    class Circle {

        Point center;   // Center point of the circle
        double radius;  // Radius of the circle

        /**
         * Constructor for a circle.
         *
         * @param center Center point
         * @param radius Radius (distance from center to boundary)
         */
        public Circle(Point center, double radius) {

            this.center = center;
            this.radius = radius;
        }

        /**
         * Checks if this circle encloses all given points.
         *
         * A circle is valid for a set of points if every point is inside or on the boundary of the circle.
         *
         * Used in generateMinCircle to verify if a circle defined by 2 points can also enclose a third boundary point.
         *
         * @param points List of points to check
         * @return true if all points are inside or on the circle
         */
        public boolean isValid(List<Point> points) {

            // Check each point
            for (var p : points) {
                if (!p.inside(this)) {
                    return false;  // Found a point outside the circle
                }
            }

            return true;  // All points are inside or on the circle
        }
    }
}