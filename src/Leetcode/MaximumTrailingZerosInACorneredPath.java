package Leetcode;

import java.util.Arrays;

//Problem Link: https://leetcode.com/problems/maximum-trailing-zeros-in-a-cornered-path/

/**
 * Solution for finding the maximum trailing zeros in a path through a grid. A path can go horizontally and vertically,
 * with exactly one turn (L-shaped or straight). Trailing zeros are determined by min(count of 2s, count of 5s) in the
 * product. Uses prefix sums to efficiently calculate factor counts for all possible paths.
 */
public class MaximumTrailingZerosInACorneredPath {

    /**
     * Finds the maximum number of trailing zeros in any valid path through the grid. Valid paths: horizontal line,
     * vertical line, or L-shaped (one turn).
     *
     * @param grid 2D array of positive integers
     * @return Maximum trailing zeros possible in the product of any path
     */
    public int maxTrailingZeros(int[][] grid) {

        int n = grid.length;      // Number of rows
        int m = grid[0].length;   // Number of columns
        int ans = 0;              // Maximum trailing zeros found

        // Prefix sum arrays for horizontal and vertical directions
        // hCnt[i][j] = cumulative count of 2s and 5s from grid[i][0] to grid[i][j-1]
        Node hCnt[][] = new Node[n][m + 1];
        // vCnt[i][j] = cumulative count of 2s and 5s from grid[0][j] to grid[i-1][j]
        Node vCnt[][] = new Node[n + 1][m];

        // Initialize horizontal prefix arrays
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= m; j++) {
                hCnt[i][j] = new Node(0, 0);
            }
        }

        // Initialize vertical prefix arrays
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j < m; j++) {
                vCnt[i][j] = new Node(0, 0);
            }
        }

        // Build prefix sum arrays
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                // Count factors of 2 and 5 in current cell
                Node cnt = getCnt(grid[i][j]);

                // Update horizontal prefix sum (includes current cell)
                hCnt[i][j + 1] = new Node(cnt);
                // Update vertical prefix sum (includes current cell)
                vCnt[i + 1][j] = new Node(cnt);
                // Add previous horizontal sum
                hCnt[i][j + 1].add(hCnt[i][j]);
                // Add previous vertical sum
                vCnt[i + 1][j].add(vCnt[i][j]);
            }
        }

        // Try each cell as a potential corner/turning point
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                // v1: vertical path from top to current cell (inclusive)
                Node v1 = vCnt[i + 1][j];
                // h1: horizontal path from left to current cell (inclusive)
                Node h1 = hCnt[i][j + 1];
                // v2: vertical path from current cell (exclusive) to bottom
                Node v2 = vCnt[n][j].sub(vCnt[i][j]);
                // h2: horizontal path from current cell (exclusive) to right
                Node h2 = hCnt[i][m].sub(hCnt[i][j]);
                // sub: current cell (to avoid double counting when combining paths)
                Node sub = vCnt[i + 1][j].sub(vCnt[i][j]);

                // Try all 4 L-shaped paths with current cell as corner:
                // 1. Down then right: v1 + h2 - current
                // 2. Down then left: v1 + h1 - current
                // 3. Up then right: v2 + h2 - current
                // 4. Up then left: v2 + h1 - current
                ans = max(
                        ans,
                        Math.min(v1.two + h1.two - sub.two, v1.five + h1.five - sub.five),
                        Math.min(v1.two + h2.two - sub.two, v1.five + h2.five - sub.five),
                        Math.min(v2.two + h1.two - sub.two, v2.five + h1.five - sub.five),
                        Math.min(v2.two + h2.two - sub.two, v2.five + h2.five - sub.five)
                );
            }
        }

        return ans;
    }

    /**
     * Helper method to find the maximum value from a variable number of integers.
     *
     * @param arr Variable number of integer arguments
     * @return Maximum value among all arguments
     */
    private int max(int... arr) {

        return Arrays.stream(arr).max().getAsInt();
    }

    /**
     * Counts the number of factors of 2 and 5 in a given number. Trailing zeros = min(count of 2s, count of 5s) since
     * 10 = 2 * 5.
     *
     * @param a The number to factorize
     * @return Node containing counts of factors 2 and 5
     */
    private Node getCnt(int a) {

        int two = 0;   // Count of factor 2
        int five = 0;  // Count of factor 5

        // Count how many times 2 divides a
        while (a % 2 == 0) {
            two++;
            a /= 2;
        }

        // Count how many times 5 divides a
        while (a % 5 == 0) {
            a /= 5;
            five++;
        }

        return new Node(two, five);
    }

    /**
     * Node class to store counts of prime factors 2 and 5. Used for calculating trailing zeros since trailing zeros =
     * min(count of 2s, count of 5s).
     */
    class Node {

        int two;   // Count of factor 2
        int five;  // Count of factor 5

        /**
         * Copy constructor to create a new Node from an existing one.
         *
         * @param node Node to copy from
         */
        public Node(Node node) {

            this.two = node.two;
            this.five = node.five;
        }

        /**
         * Constructor to create a Node with specific factor counts.
         *
         * @param two Count of factor 2
         * @param five Count of factor 5
         */
        public Node(int two, int five) {

            this.two = two;
            this.five = five;
        }

        /**
         * Subtracts another node's counts from this node's counts. Used to calculate factor counts for a subrange in
         * prefix sum arrays.
         *
         * @param node Node to subtract
         * @return New Node with the difference
         */
        public Node sub(Node node) {

            return new Node(
                    two - node.two,
                    five - node.five
            );
        }

        /**
         * Adds another node's counts to this node's counts (in-place). Used to build prefix sum arrays.
         *
         * @param node Node to add
         */
        public void add(Node node) {

            two += node.two;
            five += node.five;
        }
    }
}