package Leetcode;

//Problem Link : https://leetcode.com/problems/shortest-path-visiting-all-nodes

import java.util.LinkedList;
import java.util.Queue;

public class ShortestPathVisitAllNodes {
    public int shortestPathLength(int[][] graph) {
        int n = graph.length;
        if (n == 1)
            return 0; // if it has only one node then no path to traverse

        // bit wise representation of each node to be visited
        int finalBits = (1 << n) - 1;

        /// Queue to hold every node that is traversed & initially it'll contains every
        /// node because we can start from anywhere
        // Index: 0 -> node number, 1-> bit representation of the nodes that are visited
        Queue<int[]> que = new LinkedList<>();

        // To track that current node is traversed or not which particular combination
        // of nodes traversed
        boolean visited[][] = new boolean[n][finalBits + 1];

        for (int i = 0; i < n; i++) {
            que.add(new int[] { i, 1 << i });
        }
        int pathSize = 0;
        while (!que.isEmpty()) {
            int totalNodes = que.size();
            pathSize++;
            for (int i = 0; i < totalNodes; i++) {
                int node[] = que.poll();
                int ind = node[0];
                int bits = node[1];

                for (int neighbor : graph[ind]) {
                    int newBits = bits | (1 << neighbor);

                    if (visited[neighbor][newBits]) {
                        continue;
                    }

                    visited[neighbor][newBits] = true;

                    if (newBits == finalBits)
                        return pathSize;

                    que.add(new int[] { neighbor, newBits });
                }
            }

        }

        return 0;
    }
}
