package Leetcode;

// Problem Link: https://leetcode.com/problems/dice-roll-simulation

/**
 * Solution for Dice Roll Simulation
 *
 * Problem: A die simulator generates a random number from 1 to 6 for each roll. However, there's a constraint: you
 * cannot roll the same number more than rollMax[i] consecutive times. Given n (number of rolls) and rollMax array,
 * count the number of distinct sequences possible.
 *
 * Example: n = 2, rollMax = [1, 1, 2, 2, 2, 3] - Face 1 (index 0): can appear at most 1 time consecutively - Face 2
 * (index 1): can appear at most 1 time consecutively - Face 3 (index 2): can appear at most 2 times consecutively -
 * etc.
 *
 * Valid sequences for n=2: - [1,2], [1,3], [1,4], [1,5], [1,6] - [2,1], [2,3], [2,4], [2,5], [2,6] - [3,1], [3,2],
 * [3,3], [3,4], [3,5], [3,6]  (3,3 is valid since rollMax[2]=2) - etc.
 *
 * Approach: Dynamic Programming with Inclusion-Exclusion - State: dp[len][face] = number of valid sequences of length
 * 'len' ending with 'face' - total[len] = total number of valid sequences of length 'len'
 *
 * Key Insight: Use inclusion-exclusion principle 1. Start with all sequences ending with face: total[len-1] 2. Subtract
 * invalid sequences (those with more than rollMax[face] consecutive occurrences) 3. Invalid sequences = sequences of
 * length (len - limit - 1) that DON'T end with face, followed by (limit + 1) consecutive occurrences of face
 *
 * Formula: dp[len][face] = total[len-1] - invalidSeq where invalidSeq = total[len-limit-1] - dp[len-limit-1][face]
 *
 * Time Complexity: O(n × 6) = O(n) Space Complexity: O(n × 6) = O(n)
 */
public class DiceRollSimulation {

    long mod = 1_000_000_007;  // Modulo for large number handling

    /**
     * Counts the number of distinct valid dice roll sequences
     *
     * @param n Number of dice rolls
     * @param rollMax Array where rollMax[i] is the max consecutive times face (i+1) can appear
     * @return Number of distinct valid sequences, modulo 10^9+7
     */
    public int dieSimulator(int n, int[] rollMax) {

        // dp[len][face] = number of valid sequences of length 'len' ending with 'face'
        int dp[][] = new int[n + 1][6];

        // total[len] = total number of valid sequences of length 'len'
        int total[] = new int[n + 1];

        // Base case: empty sequence (length 0)
        total[0] = 1;

        // Build up sequences from length 1 to n
        for (int len = 1; len <= n; len++) {

            // For each face (0-5 representing dice faces 1-6)
            for (int face = 0; face < 6; face++) {
                int limit = rollMax[face];  // Max consecutive occurrences for this face

                // Start with all sequences of length (len-1), then append current face
                // This gives us all sequences ending with 'face'
                dp[len][face] = total[len - 1];

                // Subtract invalid sequences (those with more than 'limit' consecutive faces)
                // Invalid sequences have (limit + 1) consecutive occurrences of 'face'
                // They come from sequences of length (len - limit - 1)
                int prevIndex = len - limit - 1;

                if (prevIndex >= 0) {
                    // invalidSeq = sequences at prevIndex that DON'T end with 'face'
                    // = total[prevIndex] - dp[prevIndex][face]
                    // These sequences, followed by (limit+1) consecutive 'face', are invalid
                    int invalidSeq = add(total[prevIndex], -dp[prevIndex][face]);

                    // Subtract invalid sequences from dp[len][face]
                    dp[len][face] = add(dp[len][face], -invalidSeq);
                }
            }

            // Calculate total[len] by summing all dp[len][face]
            for (int face = 0; face < 6; face++) {
                total[len] = add(total[len], dp[len][face]);
            }
        }

        // Return total number of valid sequences of length n
        return total[n];
    }

    /**
     * Adds two numbers with modulo arithmetic
     *
     * Handles negative numbers by adding mod before taking modulo. This is needed when subtracting (passing negative
     * b).
     *
     * @param a First number
     * @param b Second number (can be negative for subtraction)
     * @return (a + b) % mod, properly handling negative results
     */
    private int add(long a, long b) {

        // Add mod to handle negative results from subtraction
        return (int) ((a + b + mod) % mod);
    }
}