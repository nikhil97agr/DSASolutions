package Leetcode;

import java.util.ArrayList;

//Problem Link: https://leetcode.com/problems/color-the-triangle-red/

/**
 * Solution for coloring a triangle such that no two adjacent cells have the same color.
 *
 * Problem: Given a triangle with n rows where row i has 2i-1 cells, color some cells red such that: 1. Every cell can
 * be determined (red or not red) by looking at its red neighbors 2. Minimize the number of red cells
 *
 * Triangle structure (n=5): Row 1:     (1,1) Row 2:   (2,1) (2,2) (2,3) Row 3: (3,1) (3,2) (3,3) (3,4) (3,5) Row 4:
 * (4,1) (4,2) ... (4,7) Row 5: (5,1) (5,2) ... (5,9)
 *
 * Adjacent cells: - Cell (i,j) is adjacent to (i-1,j) and (i-1,j+1) in row above - Cell (i,j) is adjacent to (i+1,j-1)
 * and (i+1,j) in row below - Cell (i,j) is adjacent to (i,j-1) and (i,j+1) in same row
 *
 * Key insight: Pattern-based coloring in 4-row cycles
 *
 * The optimal solution uses a repeating pattern every 4 rows: - Row n (mod 4 = 0): Color all odd positions (1, 3, 5,
 * ...) - Row n-1 (mod 4 = 3): Color only position 2 - Row n-2 (mod 4 = 2): Color odd positions starting from 3 (3, 5,
 * 7, ...) - Row n-3 (mod 4 = 1): Color only position 1
 *
 * Visual example for n=6: Row 1: (1,1) ← RED Row 2: (2,1) RED  (2,3) RED Row 3: (3,2) ← RED Row 4: (4,3) RED (4,5) RED
 * (4,7) RED Row 5: (5,1) ← RED Row 6: (6,1) RED (6,3) RED (6,5) RED (6,7) RED (6,9) RED (6,11) RED
 *
 * Why this pattern works: - Each non-red cell has a unique configuration of red neighbors - The pattern ensures
 * determinability: we can deduce non-red cells from red ones - The 4-row cycle covers all possible row positions (mod
 * 4)
 *
 * Pattern explanation: - Phase 1 (i ≡ 0 mod 4): Color odd positions - creates a sparse pattern - Phase 2 (i ≡ 3 mod 4):
 * Color position 2 - fills gaps in determinability - Phase 3 (i ≡ 2 mod 4): Color odd positions from 3 - complements
 * phase 1 - Phase 4 (i ≡ 1 mod 4): Color position 1 - completes the cycle
 *
 * Time Complexity: O(n²) - proportional to total number of cells Space Complexity: O(n²) for result array
 */
public class ColorTheTriangleRed {

    /**
     * Generates the optimal red cell coloring for a triangle with n rows.
     *
     * @param n Number of rows in the triangle
     * @return Array of [row, column] positions to color red
     */
    public int[][] colorRed(int n) {

        var result = new ArrayList<int[]>();

        // ================================================================
        // ALWAYS color the top cell (1,1) - apex of triangle
        // ================================================================
        result.add(make(1, 1));

        // ================================================================
        // PHASE 1: Rows where (i mod 4) = (n mod 4)
        // Color all odd positions: 1, 3, 5, ...
        // ================================================================
        // Start from row n and go backwards in steps of 4
        // For row i, positions are 1, 2, 3, ..., 2*i-1
        // We color positions 1, 3, 5, ..., (2*i-1)
        for (int i = n; i > 1; i -= 4) {
            for (int j = 1; j < 2 * i; j += 2) {
                result.add(make(i, j));
            }
        }

        // ================================================================
        // PHASE 2: Rows where (i mod 4) = (n-1 mod 4)
        // Color only position 2
        // ================================================================
        // This creates a specific pattern to ensure determinability
        // Position 2 helps distinguish between different non-red cells
        for (int i = n - 1; i > 1; i -= 4) {
            result.add(make(i, 2));
        }

        // ================================================================
        // PHASE 3: Rows where (i mod 4) = (n-2 mod 4)
        // Color odd positions starting from 3: 3, 5, 7, ...
        // ================================================================
        // Skip positions 1 and 2, color 3, 5, 7, ...
        // This complements Phase 1 pattern
        for (int i = n - 2; i >= 1; i -= 4) {
            for (int j = 3; j < 2 * i; j += 2) {
                result.add(make(i, j));
            }
        }

        // ================================================================
        // PHASE 4: Rows where (i mod 4) = (n-3 mod 4)
        // Color only position 1
        // ================================================================
        // Similar to Phase 2, but for position 1
        // Completes the 4-row cycle pattern
        for (int i = n - 3; i > 1; i -= 4) {
            result.add(make(i, 1));
        }

        return result.toArray(int[][]::new);
    }

    /**
     * Helper method to create a [row, column] pair.
     *
     * @param a Row number
     * @param b Column number
     * @return Array containing [a, b]
     */
    private int[] make(int a, int b) {

        return new int[]{a, b};
    }
}