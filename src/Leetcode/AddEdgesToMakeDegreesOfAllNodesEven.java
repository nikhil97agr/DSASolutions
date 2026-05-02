package Leetcode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//Problem Link: https://leetcode.com/problems/add-edges-to-make-degrees-of-all-nodes-even

/**
 * Solution for determining if it's possible to add at most 2 edges to make all node degrees even.
 * Key insight: The number of odd-degree nodes must be even (handshaking lemma).
 * We can add 0, 1, or 2 edges to fix odd-degree nodes.
 *
 * Cases:
 * - 0 odd nodes: Already valid
 * - 2 odd nodes: Add 1 edge between them, or 2 edges through an intermediate node
 * - 4 odd nodes: Add 2 edges to pair them up
 * - 1, 3, or 5+ odd nodes: Impossible (violates handshaking lemma or exceeds 2 edges)
 */
public class AddEdgesToMakeDegreesOfAllNodesEven {

    /**
     * Determines if it's possible to add at most 2 edges to make all node degrees even.
     *
     * @param n     Number of nodes (1-indexed in input, converted to 0-indexed)
     * @param edges List of existing edges as [u, v] pairs (1-indexed)
     * @return true if possible to make all degrees even with at most 2 edges, false otherwise
     */
    public boolean isPossible(int n, List<List<Integer>> edges) {

        // Build adjacency list using sets for O(1) edge existence checks
        Set<Integer> adjList[] = new HashSet[n];
        for (int i = 0; i < n; i++) {
            adjList[i] = new HashSet<>();
        }

        // Add all edges to adjacency list (convert from 1-indexed to 0-indexed)
        for (var e : edges) {
            int u = e.get(0) - 1;
            int v = e.get(1) - 1;
            adjList[u].add(v);
            adjList[v].add(u);
        }

        // Collect all nodes with odd degree
        List<Integer> set = new ArrayList<>();

        for (var i = 0; i < n; i++) {
            var s = adjList[i];
            if (s.size() % 2 == 1) {
                set.add(i);
            }
        }

        // Impossible cases:
        // - More than 4 odd nodes: would need more than 2 edges
        // - Odd number of odd nodes: violates handshaking lemma
        if (set.size() >= 5 || set.size() % 2 == 1) {
            return false;
        }

        // Case 0: No odd-degree nodes - already valid
        if (set.isEmpty()) {
            return true;
        }

        // Case 1: Exactly 2 odd-degree nodes
        if (set.size() == 2) {
            int a = set.get(0);
            int b = set.get(1);

            // Option 1: Add edge directly between a and b (if it doesn't exist)
            if (!adjList[a].contains(b)) {
                return true;
            }

            // Option 2: Add 2 edges through an intermediate node i: a-i and b-i
            // Node i must not already be connected to a or b
            for (int i = 0; i < n; i++) {
                if (!adjList[i].contains(a) && !adjList[i].contains(b)) {
                    return true;
                }
            }
            return false;
        }

        // Case 2: Exactly 4 odd-degree nodes
        // Need to add 2 edges to pair them up: (a-b and c-d)
        // Try all possible pairings of the 4 nodes
        for (int a : set) {
            for (int b : set) {
                if (a == b) {
                    continue;
                }
                for (int c : set) {
                    if (a == c || b == c) {
                        continue;
                    }
                    for (int d : set) {
                        if (a == d || b == d || c == d) {
                            continue;
                        }
                        // Check if we can add edges a-b and c-d (both must not exist)
                        if (!adjList[a].contains(b) && !adjList[c].contains(d)) {
                            return true;
                        }
                    }
                }
            }
        }

        // No valid pairing found for 4 odd nodes
        return false;

    }
}