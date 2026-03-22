package Leetcode;

import java.util.ArrayList;
import java.util.List;

//Problem Link: https://leetcode.com/problems/count-ways-to-build-rooms-in-an-ant-colony

/**
 * Solution for counting the number of valid orderings to build rooms in an ant colony. Rooms must be built respecting
 * the dependency tree (parent before children). Uses combinatorics with modular arithmetic: answer = n! / (product of
 * subtree sizes). Applies Fermat's Little Theorem for modular division.
 */
public class CountWaysToBuildRoomsInAntColony {

    int mod = 1_000_000_007;  // Modulo for the result
    List<Integer> adjList[];   // Adjacency list representing the tree
    int n;                     // Number of rooms

    /**
     * Calculates the number of ways to build rooms respecting dependencies.
     *
     * @param prevRoom Array where prevRoom[i] is the prerequisite room for room i
     * @return Number of valid build orderings modulo 10^9 + 7
     */
    public int waysToBuildRooms(int[] prevRoom) {

        n = prevRoom.length;

        // Build adjacency list from parent-child relationships
        adjList = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            adjList[i] = new ArrayList<>();
        }

        // Add edges from parent to children
        for (int i = 1; i < n; i++) {
            adjList[prevRoom[i]].add(i);
        }

        // DFS to compute the product of modular inverses of subtree sizes
        Result res = dfs(0);

        // Calculate n! (factorial of total rooms)
        int fact = 1;
        for (int i = 2; i <= n; i++) {
            fact = prod(fact, i);
        }

        // Final answer: n! * (product of modular inverses)
        // This equals: n! / (product of subtree sizes)
        return prod(fact, res.prod);
    }

    /**
     * DFS to compute subtree size and product of modular inverses. For each subtree, we divide by the subtree size to
     * account for the ways to arrange nodes within that subtree.
     *
     * @param curr Current node being processed
     * @return Result containing the product of modular inverses and subtree size
     */
    private Result dfs(int curr) {

        int p = 1;    // Product of modular inverses
        int cnt = 1;  // Count of nodes in subtree (including current)

        // Process all children
        for (int child : adjList[curr]) {
            Result res = dfs(child);

            // Multiply the products from child subtrees
            p = prod(p, res.prod);

            // Add child subtree size to current subtree size
            cnt += res.cnt;
        }

        // Divide by current subtree size using modular inverse
        // pow(cnt, mod - 2) computes cnt^(mod-2) which is the modular inverse of cnt
        // This is based on Fermat's Little Theorem: a^(p-1) ≡ 1 (mod p), so a^(p-2) ≡ a^(-1) (mod p)
        p = prod(p, pow(cnt, mod - 2));

        return new Result(p, cnt);
    }


    /**
     * Computes (a * b) % mod to prevent overflow.
     *
     * @param a First number
     * @param b Second number
     * @return (a * b) mod 10^9 + 7
     */
    private int prod(long a, long b) {

        return (int) ((a * b) % mod);
    }

    /**
     * Computes (a^b) % mod using fast exponentiation (binary exponentiation). Time complexity: O(log b)
     *
     * @param a Base
     * @param b Exponent
     * @return (a^b) mod 10^9 + 7
     */
    private int pow(long a, long b) {

        // Base cases
        if (a == 0 || b == 1) {
            return prod(a, 1);
        }
        if (b == 0 || a == 1) {
            return 1;
        }

        // Recursive case: a^b = (a^(b/2))^2 if b is even
        int p = pow(a, b / 2);
        p = prod(p, p);

        // If b is odd, multiply by a one more time
        if (b % 2 == 1) {
            p = prod(p, a);
        }

        return p;
    }

    /**
     * Result class to store DFS computation results.
     */
    class Result {

        int prod;  // Product of modular inverses for the subtree
        int cnt;   // Number of nodes in the subtree

        /**
         * Constructor for Result.
         *
         * @param prod Product of modular inverses
         * @param cnt Subtree size (node count)
         */
        public Result(int prod, int cnt) {

            this.prod = prod;
            this.cnt = cnt;
        }
    }
}