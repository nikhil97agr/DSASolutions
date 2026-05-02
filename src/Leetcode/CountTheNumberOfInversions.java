package Leetcode;

import java.util.HashMap;
import java.util.Map;

//Problem Link: https://leetcode.com/problems/count-the-number-of-inversions/

/**
 * Solution for counting permutations with specific inversion count requirements.
 *
 * Problem: Count permutations of [0, 1, 2, ..., n-1] that satisfy inversion constraints. - An inversion is a pair (i,
 * j) where i < j but perm[i] > perm[j] - requirements[i] = [end, cnt] means: after placing first (end+1) elements,
 * there must be exactly cnt inversions
 *
 * Key concepts:
 *
 * What are inversions? - In permutation [2, 1, 0], inversions are: (0,1), (0,2), (1,2) → 3 total - When we place a new
 * number at position i, it can create 0 to i inversions depending on where we insert it in the relative order
 *
 * Example: Building permutation step by step - Start: [] - Place 0: [0] → 0 inversions - Place 1: Can insert as [0,1]
 * (0 new inversions) or [1,0] (1 new inversion) - Place 2: If we have [0,1], can insert as: * [0,1,2] → 0 new
 * inversions (total = 0) * [0,2,1] → 1 new inversion (total = 1) * [2,0,1] → 2 new inversions (total = 2)
 *
 * Algorithm approach: Dynamic Programming - State: dp[i][cnt] = number of ways to place first i elements with cnt
 * inversions - When placing element i, we can add 0 to i inversions - Transition: For each valid new inversion count,
 * recurse - Constraints: Must satisfy requirements at specific positions
 *
 * Key insight: - When placing the i-th smallest element (0-indexed), we've already placed i elements - This new element
 * can be inserted at any of (i+1) positions - Inserting at position k from the right adds k inversions - So we can add
 * 0, 1, 2, ..., i new inversions
 *
 * Example with requirements: - n = 3, requirements = [[2, 2]] - After placing first 3 elements (indices 0, 1, 2), must
 * have exactly 2 inversions - Valid permutations: [1, 2, 0], [2, 0, 1] → Answer = 2
 *
 * Time Complexity: O(n * maxCnt * n) where maxCnt is max required inversion count Space Complexity: O(n * maxCnt) for
 * memoization
 */
public class CountTheNumberOfInversions {

    Integer dp[][];  // dp[i][cnt] = ways to place first i elements with cnt inversions

    /**
     * Counts permutations satisfying inversion requirements.
     *
     * @param n Length of permutation (permutation of 0 to n-1)
     * @param requirements Array of [end_index, required_inversions] constraints
     * @return Count of valid permutations modulo 10^9+7
     */
    public int numberOfPermutations(int n, int[][] requirements) {

        // Build map of requirements: position → required inversion count
        var map = new HashMap<Integer, Integer>();
        var maxCnt = 0;  // Maximum inversion count needed (for DP array sizing)

        for (var r : requirements) {
            map.put(r[0], r[1]);  // After placing r[0]+1 elements, need r[1] inversions
            maxCnt = Math.max(maxCnt, r[1]);
        }

        // Edge case: First element (index 0) must have 0 inversions
        // If requirement says otherwise, impossible
        if (map.containsKey(0) && map.get(0) != 0) {
            return 0;
        }

        // Initialize DP table
        // dp[i][cnt] = number of ways to place first i elements with cnt inversions
        dp = new Integer[n][maxCnt + 1];

        // Start recursion: placing element at index 1 (second element)
        // Already have element 0 placed with 0 inversions
        return solve(1, n, 0, maxCnt, map);


    }

    /**
     * Recursive DP to count permutations with inversion constraints.
     *
     * State: - i: Number of elements placed so far (next element to place is i-th smallest, 0-indexed) - cnt: Current
     * inversion count
     *
     * Transition: - When placing element i, we can add 0 to i new inversions - This is because we can insert it at any
     * of (i+1) positions relative to existing elements - Position from right determines inversions: rightmost = 0,
     * leftmost = i
     *
     * Example: Placing element 2 when we have 2 elements already placed - Can add 0, 1, or 2 inversions - If current
     * inversions = 1, new count can be 1, 2, or 3
     *
     * @param i Index of next element to place (also = number of elements already placed)
     * @param n Total number of elements in permutation
     * @param cnt Current inversion count
     * @param maxCnt Maximum inversion count (for bounds checking)
     * @param map Requirements: index → required inversion count
     * @return Number of valid ways to complete the permutation
     */
    private int solve(int i, int n, int cnt, int maxCnt, Map<Integer, Integer> map) {

        // Base case: All n elements placed successfully
        if (i == n) {
            return 1;  // Found one valid permutation
        }

        // Memoization: Return cached result if available
        if (dp[i][cnt] != null) {
            return dp[i][cnt];
        }

        int ans = 0;

        // Check if there's a requirement for position i
        int req = map.getOrDefault(i, -1);  // -1 means no requirement

        // Try all possible new inversion counts
        // We can add 0 to i inversions by placing element i in different positions
        // newCnt ranges from cnt (add 0 inversions) to cnt+i (add i inversions)
        // Also bounded by maxCnt to avoid array out of bounds
        for (int newCnt = cnt; newCnt <= Math.min(cnt + i, maxCnt); newCnt++) {
            // Check if this newCnt satisfies the requirement (if any)
            if (req == -1 || req == newCnt) {
                // No requirement, or requirement satisfied
                // Recurse to place next element
                ans = add(ans, solve(i + 1, n, newCnt, maxCnt, map));
            }
            // If requirement exists but not satisfied, skip this branch
        }

        // Cache and return result
        return dp[i][cnt] = ans;
    }

    /**
     * Adds two numbers with modulo arithmetic.
     *
     * Uses long to prevent overflow before applying modulo.
     *
     * @param a First number
     * @param b Second number
     * @return (a + b) % (10^9 + 7)
     */
    private int add(int a, int b) {

        int mod = 1_000_000_007;
        return (int) ((1l * a + b) % mod);
    }
}