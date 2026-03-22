package Leetcode;//Problem Link: https://leetcode.com/problems/kth-smallest-instructions

/**
 * Solution class for finding the kth lexicographically smallest instruction path. The problem requires finding a path
 * from (0,0) to destination using 'H' (horizontal/right) and 'V' (vertical/down) moves, where paths are ordered
 * lexicographically.
 */
public class KthSmallestInstructions {

    /**
     * Finds the kth smallest path in lexicographical order to reach the destination. Uses dynamic programming to count
     * possible paths and greedy selection to build the result.
     *
     * @param dest Destination coordinates [row, column] where row = vertical moves, column = horizontal moves
     * @param k The kth smallest path to find (1-indexed)
     * @return The kth lexicographically smallest instruction string
     */
    public String kthSmallestPath(int[] dest, int k) {

        // DP table: pathSize[i][j] = number of paths from (i,j) to destination
        int pathSize[][] = new int[dest[0] + 1][dest[1] + 1];

        // Current position: x = remaining vertical moves, y = remaining horizontal moves
        int x = dest[0];
        int y = dest[1];

        // Build DP table to count number of paths from each position to destination
        for (int i = 0; i <= x; i++) {
            for (int j = 0; j <= y; j++) {
                if (i == 0 && j == 0) {
                    // Base case: one way to reach destination from destination
                    pathSize[i][j] = 1;
                } else if (i == 0) {
                    // Only horizontal moves left: one path (all H's)
                    pathSize[i][j] = pathSize[i][j - 1];
                } else if (j == 0) {
                    // Only vertical moves left: one path (all V's)
                    pathSize[i][j] = pathSize[i - 1][j];
                } else {
                    // Total paths = paths going down + paths going right
                    pathSize[i][j] = pathSize[i - 1][j] + pathSize[i][j - 1];
                }
            }
        }

        // Build the result string greedily
        StringBuilder res = new StringBuilder();

        // While we still have both horizontal and vertical moves remaining
        while (x != 0 && y != 0) {
            // Check if we can choose 'H' (lexicographically smaller than 'V')
            // If paths available by going horizontal >= k, choose 'H'
            if (pathSize[x][y - 1] >= k) {
                res.append("H");
                y--; // Move right (reduce horizontal moves)
            } else {
                // Otherwise, must choose 'V'
                res.append("V");
                // Update k by subtracting paths that would have started with 'H'
                k -= pathSize[x][y - 1];
                x--; // Move down (reduce vertical moves)

            }
        }

        // Append remaining vertical moves (if any)
        while (x-- > 0) {
            res.append("V");
        }

        // Append remaining horizontal moves (if any)
        while (y-- > 0) {
            res.append("H");
        }

        return res.toString();

    }
}