package Leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Problem Link: https://leetcode.com/problems/subtree-inversion-sum-ii/description/

/**
 * Solution to the Tree Subtree Inversion Problem.
 *
 * Problem: Maximize the sum of node values in a tree by selectively inverting subtrees, where inverting a subtree
 * multiplies all values in that subtree by -1.
 *
 * Constraint: Any two inverted nodes must be at least k edges apart.
 *
 * Approach: Tree DP where we track the maximum/minimum sums achievable for different "distances to the nearest inverted
 * ancestor" in the path from root to current node.
 */
public class SubtreeInversionSumII {

    int nums[];              // Node values
    List<Integer> adjList[]; // Adjacency list representation of the tree
    int k;                   // Minimum distance constraint between inverted nodes

    /**
     * Computes the maximum possible sum after applying subtree inversions.
     *
     * @param edges 2D array where edges[i] = [u, v] represents an undirected edge
     * @param nums array where nums[i] is the value at node i
     * @param k minimum distance required between any two inverted nodes
     * @return maximum achievable sum
     */
    public int subtreeInversionSum(int[][] edges, int[] nums, int k) {

        this.k = k;
        adjList = new ArrayList[nums.length];
        this.nums = nums;

        // Build adjacency list
        for (int i = 0; i < nums.length; i++) {
            adjList[i] = new ArrayList<>();
        }

        for (var e : edges) {
            var u = e[0];
            var v = e[1];

            adjList[u].add(v);
            adjList[v].add(u);
        }

        // Start DFS from root (node 0) with no parent (-1)
        // Return max[0]: maximum sum when distance to nearest inverted ancestor >= 0
        return (int) (dfs(0, -1).max[0]);
    }

    /**
     * DFS to compute maximum and minimum sums for the subtree rooted at curr.
     *
     * Key DP State: max[i] = maximum sum achievable when the distance from curr to the nearest inverted ancestor on the
     * path to root is at least i min[i] = minimum sum achievable under the same condition
     *
     * The "distance to nearest inverted ancestor" determines whether we're allowed to invert the current node (we need
     * distance >= k to satisfy the constraint).
     *
     * @param curr current node
     * @param par parent node (-1 if curr is root)
     * @return Pair containing max[] and min[] arrays for this subtree
     */
    public Pair dfs(int curr, int par) {

        // Initialize: max[i] and min[i] start with just the current node's value
        // This represents the base case before considering any children
        long max[] = new long[k];
        long min[] = new long[k];
        Arrays.fill(max, nums[curr]);
        Arrays.fill(min, nums[curr]);

        // Process all children and merge their results
        for (int child : adjList[curr]) {
            if (child == par) {
                continue;
            }

            Pair childRes = dfs(child, curr);

            long cMax[] = childRes.max;
            long cMin[] = childRes.min;

            // Merge child results with current results
            // For each distance state i, we need to combine curr's state with child's state

            // Case 1: For i < k/2, we can potentially swap which subtree (current or child)
            // has the inverted node closer to the root
            for (int i = 0; i < k / 2; i++) {
                // max[i] + cMax[k-i-2]: curr at distance i, child at distance k-i-2
                // max[k-i-2] + cMax[i]: curr at distance k-i-2, child at distance i
                max[i] = Math.max(max[i] + cMax[k - i - 2], max[k - i - 2] + cMax[i]);

                min[i] = Math.min(min[i] + cMin[k - i - 2], min[k - i - 2] + cMin[i]);
            }

            // Case 2: For i >= k/2, the distance is large enough that we don't swap
            // Simply add the child's contribution
            for (int i = k / 2; i < k; i++) {
                min[i] += cMin[i];
                max[i] += cMax[i];

            }

            // Suffix maximization: if we can achieve a good sum at distance i+1,
            // we can also achieve it at distance i (since i is less restrictive)
            // This ensures max[i] >= max[i+1] and min[i] <= min[i+1]
            for (int i = k - 2; i >= 0; i--) {
                min[i] = Math.min(min[i], min[i + 1]);
                max[i] = Math.max(max[i], max[i + 1]);
            }
        }

        // Prepare return values: adjust distances by 1 as we move up the tree
        // Also consider the option of inverting the current node
        long rMin[] = new long[k];
        long rMax[] = new long[k];

        // At index 0: we can either not invert curr (use min[0]) or invert curr
        // If we invert curr, the entire subtree is negated, so we use -max[k-1]
        // (k-1 ensures no ancestor within distance k is also inverted)
        rMin[0] = Math.min(min[0], -max[k - 1]);
        rMax[0] = Math.max(max[0], -min[k - 1]);

        // For i >= 1: shift the distance values
        // rMax[i] represents "distance to inverted ancestor is at least i"
        // This comes from max[i-1] (distance was i-1, now it's i after moving up)
        for (int i = 1; i < k; i++) {
            rMin[i] = min[i - 1];
            rMax[i] = max[i - 1];
        }

        return new Pair(rMax, rMin);
    }


    /**
     * Helper class to store DP results for a subtree.
     *
     * Stores two arrays of length k: - max[i]: maximum sum when distance to nearest inverted ancestor >= i - min[i]:
     * minimum sum when distance to nearest inverted ancestor >= i
     */
    class Pair {

        long max[];  // Maximum sums for different distance states
        long min[];  // Minimum sums for different distance states

        public Pair(long max[], long min[]) {

            this.max = max;
            this.min = min;
        }
    }

}