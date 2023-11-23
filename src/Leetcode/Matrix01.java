package Leetcode;

import java.util.LinkedList;
import java.util.Queue;

// Problem Link : https://leetcode.com/problems/01-matrix/

public class Matrix01 {
    public int[][] updateMatrix(int[][] mat) {
        Queue<int[]> que = new LinkedList<>();
        int m = mat.length;
        int n = mat[0].length;
        boolean visited[][] = new boolean[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (mat[i][j] == 0) {

                    visited[i][j] = true;
                    que.add(new int[] { i, j });
                }
            }
        }

        int result[][] = new int[m][n];
        int di[] = new int[] { 0, 1, 0, -1 };
        int dj[] = new int[] { 1, 0, -1, 0 };
        while (!que.isEmpty()) {
            int point[] = que.poll();
            for (int i = 0; i < 4; i++) {
                int row = point[0] + di[i];
                int col = point[1] + dj[i];

                if (row >= m || row < 0 || col >= n || col < 0 || visited[row][col]) {
                    continue;
                }

                result[row][col] = 1 + result[point[0]][point[1]];
                visited[row][col] = true;
                que.add(new int[] { row, col });

            }
        }
        return result;
    }
}
