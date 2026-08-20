package Leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Problem Link: https://leetcode.com/problems/create-grid-with-exactly-k-paths-ii/description/

public class CreateGridWithExactlyKPathsII {

    /**
     * Creates a 25x25 grid with exactly k valid paths from top-left to bottom-right.
     *
     * Strategy: Use binary representation of k to construct the grid. - Since we can only move right or down, we use a
     * pattern of "blocks" - Each block represents a bit position and contributes 2^bit paths - By selectively opening
     * paths based on k's binary representation, we can create exactly k paths
     *
     * The grid structure: - 10 blocks arranged diagonally (supporting k up to 2^10 - 1 = 1023) - Each block is a 2x2
     * free cell area - Blocks are connected vertically to create a base path - Each block can optionally have a
     * horizontal path to the right edge - When a horizontal path is opened for bit b, it adds 2^b paths
     *
     * Time Complexity: O(1) - fixed 25x25 grid Space Complexity: O(1) - fixed 25x25 grid
     */
    public List<String> createGrid(int k) {

        // Initialize 25x25 grid with all obstacles
        char[][] grid = new char[25][25];
        for (int i = 0; i < 25; i++) {
            Arrays.fill(grid[i], '#');
        }

        // Create 10 blocks of 2x2 free cells along the diagonal
        // Block b is positioned at (2*b, 2*b) to (2*b+1, 2*b+1)
        // Each block provides a foundation for path construction
        for (int b = 0; b < 10; b++) {
            int r = b * 2;      // Row position of block b
            int c = b * 2;      // Column position of block b
            grid[r][c] = '.';
            grid[r][c + 1] = '.';
            grid[r + 1][c] = '.';
            grid[r + 1][c + 1] = '.';
        }

        // Connect blocks vertically to create a base path
        // Each connector links block b to block b+1 vertically
        // This ensures there's always at least one path from start to the last block
        for (int b = 0; b < 9; b++) {
            int r = b * 2;      // Row position of block b
            int c = b * 2;      // Column position of block b
            grid[r + 2][c + 1] = '.';  // Connect to next block below
        }

        // Create a vertical corridor on the rightmost column (column 24)
        // This allows paths to reach the bottom-right corner (24, 24)
        for (int i = 0; i < 25; i++) {
            grid[i][24] = '.';
        }

        // Process each bit of k to determine which horizontal paths to open
        // If bit b is set in k, open a horizontal path from block b to the right edge
        // This adds 2^b additional paths to the total count
        for (int b = 0; b < 10; b++) {
            if (((k >> b) & 1) == 1) {  // Check if bit b is set in k
                int r = b * 2;           // Row position of block b (top row)
                int c = b * 2;           // Column position of block b
                // Open horizontal path from block b to the right edge
                // This creates 2^b paths because:
                // - At block b, there are 2^b ways to reach the top-left of the block
                // - Opening this horizontal path allows bypassing all lower blocks
                for (int j = c; j <= 24; j++) {
                    grid[r][j] = '.';
                }
            }
        }

        return transform(grid);
    }

    /**
     * Converts the 2D character grid to a list of strings.
     *
     * @param grid 2D character array representing the grid
     * @return List of strings where each string is a row of the grid
     */
    private List<String> transform(char[][] grid) {

        List<String> list = new ArrayList<>();
        for (char[] ch : grid) {
            list.add(new String(ch));
        }
        return list;
    }
}