package Leetcode;//Problem Link: https://leetcode.com/problems/string-transformation/

/**
 * Solution for counting ways to transform string s to string t using exactly k rotations.
 *
 * Problem: Given strings s and t of equal length, and integer k: - In one operation, rotate s by one position (move
 * first character to end) - Count the number of ways to perform exactly k operations to transform s into t
 *
 * Key insight: Markov Chain with Matrix Exponentiation
 *
 * State space: - State 0: Current rotation matches target t - State 1: Current rotation doesn't match target t
 *
 * Transitions: - From State 0 (matching): * Rotate to another matching position: (totalMatch - 1) ways * Rotate to
 * non-matching position: totalMatch ways - From State 1 (non-matching): * Rotate to matching position: totalMatch ways
 * * Rotate to another non-matching: (n - totalMatch - 1) ways
 *
 * Example: s = "abcd", t = "cdab", k = 2 - totalMatch = 1 (only rotation "cdab" matches) - n = 4 - Transition matrix:
 * [[0, 1],    State 0 can go to: 0 matches (impossible), 1 non-match [1, 2]]    State 1 can go to: 1 match, 2
 * non-matches - After k=2 rotations: mat^2 gives the answer
 *
 * Why matrix exponentiation? - Need to count paths of exactly k steps in the Markov chain - mat^k[i][j] = number of
 * ways to go from state i to state j in k steps - Use fast exponentiation to compute mat^k in O(log k) time
 *
 * Time Complexity: O(n + log k) where n = string length (for KMP), log k for matrix exp Space Complexity: O(n) for KMP
 * preprocessing
 */
public class StringTransformation {

    int mod = 1_000_000_007;  // Modulo for preventing overflow

    /**
     * Counts the number of ways to transform s to t using exactly k rotations.
     *
     * @param s Source string
     * @param t Target string
     * @param k Number of rotations (can be very large)
     * @return Number of ways to reach target in exactly k steps, modulo 10^9+7
     */
    public int numberOfWays(String s, String t, long k) {

        // Step 1: Count how many rotations of s match t
        // Use KMP algorithm to find all matching rotations efficiently
        var totalMatch = rotated(s, t);
        var n = s.length();

        // Edge case: If no rotation of s matches t, impossible to reach
        if (totalMatch == 0) {
            return 0;
        }

        // Step 2: Build transition matrix for the Markov chain
        // mat[i][j] = number of ways to go from state i to state j in one rotation
        // State 0: current rotation matches t
        // State 1: current rotation doesn't match t
        int mat[][] = {
                {totalMatch - 1, totalMatch},              // From matching state
                {n - totalMatch, n - totalMatch - 1}       // From non-matching state
        };

        // Step 3: Compute mat^k using fast matrix exponentiation
        mat = exp(mat, k);

        // Step 4: Return answer based on initial state
        if (s.equals(t)) {
            // Start in matching state (State 0)
            // Answer = mat[0][0] = ways to stay in matching state after k steps
            return mat[0][0];
        }

        // Start in non-matching state (State 1)
        // Answer = mat[1][0] = ways to reach matching state from non-matching
        // But matrix indices are [0][1] due to how we set up the matrix
        return mat[0][1];
    }

    /**
     * Fast matrix exponentiation to compute mat^k in O(log k) time.
     *
     * Uses binary exponentiation (similar to fast power algorithm): - If k is odd: result *= mat, then k-- - If k is
     * even: mat = mat * mat, then k /= 2
     *
     * Example: Computing mat^5 - k=5 (odd): res = res * mat, mat = mat^2, k=2 - k=2 (even): mat = mat^4, k=1 - k=1
     * (odd): res = res * mat^4, done
     *
     * @param mat Matrix to exponentiate
     * @param k Exponent (number of rotations)
     * @return mat^k computed efficiently
     */
    private int[][] exp(int mat[][], long k) {

        var n = mat.length;

        // Initialize result as identity matrix
        var res = new int[n][n];
        for (var i = 0; i < n; i++) {
            res[i][i] = 1;  // Identity matrix: diagonal = 1, rest = 0
        }

        // Binary exponentiation
        while (k > 0) {
            // If k is odd, multiply result by current mat
            if (k % 2 == 1) {
                res = prod(res, mat);
            }

            // Square the matrix (mat = mat * mat)
            mat = prod(mat, mat);

            // Divide k by 2 (move to next bit)
            k >>= 1;
        }

        return res;
    }

    /**
     * Multiplies two matrices with modulo arithmetic.
     *
     * Standard matrix multiplication: C[i][j] = Σ(A[i][k] * B[k][j]) All operations are done modulo 10^9+7 to prevent
     * overflow.
     *
     * @param a First matrix (rA × cA)
     * @param b Second matrix (cA × cB)
     * @return Result matrix (rA × cB)
     */
    private int[][] prod(int a[][], int b[][]) {

        var rA = a.length;       // Rows in matrix a
        var cB = b[0].length;    // Columns in matrix b
        var cA = a[0].length;    // Columns in a = rows in b
        var res = new int[rA][cB];

        // Standard matrix multiplication with three nested loops
        for (var i = 0; i < rA; i++) {
            for (var j = 0; j < cB; j++) {
                for (var k = 0; k < cA; k++) {
                    // res[i][j] += a[i][k] * b[k][j] (with modulo)
                    res[i][j] = add(res[i][j], prod(a[i][k], b[k][j]));
                }
            }
        }

        return res;
    }

    /**
     * Adds two numbers with modulo arithmetic.
     *
     * @param a First number
     * @param b Second number
     * @return (a + b) % mod
     */
    private int add(long a, long b) {

        return (int) ((1l * a + b) % mod);
    }

    /**
     * Multiplies two numbers with modulo arithmetic.
     *
     * @param a First number
     * @param b Second number
     * @return (a * b) % mod
     */
    private int prod(long a, long b) {

        return (int) ((1l * a * b) % mod);
    }

    /**
     * Counts how many rotations of string s match string t using KMP algorithm.
     *
     * Strategy: All rotations of s can be found in s+s - Example: s = "abc" → s+s = "abcabc" - Rotations: "abc" (at 0),
     * "bca" (at 1), "cab" (at 2) - Use KMP to find all occurrences of t in s+s - Only count occurrences in the first n
     * positions (valid rotations)
     *
     * Why KMP? - Efficient pattern matching in O(n) time - Can find all occurrences, not just the first one
     *
     * Example: s = "abab", t = "abab" - s+s = "abababab" - t appears at positions 0, 2, 4, 6 - Valid rotations (i - n <
     * n): positions 0, 2 → count = 2
     *
     * @param s Source string
     * @param t Target string to match
     * @return Number of rotations of s that equal t
     */
    private int rotated(String s, String t) {

        // Build KMP failure function (LPS array) for pattern t
        var lps = lps(t.toCharArray());
        var matchingIndices = 0;
        var n = t.length();

        // Concatenate s with itself to contain all rotations
        s = s + s;

        // KMP search for pattern t in text s+s
        var i = 0;  // Index in text (s+s)
        var j = 0;  // Index in pattern (t)

        while (i < s.length() && j < t.length()) {
            if (s.charAt(i) == t.charAt(j)) {
                // Characters match, advance both pointers
                i++;
                j++;

                // Complete match found
                if (j == t.length()) {
                    // Only count if this is a valid rotation (within first n positions)
                    // i - n gives the starting position of the match in original s
                    if (i - n < n) {
                        matchingIndices++;
                    }

                    // Continue searching for more matches
                    j = lps[j - 1];
                }
            } else {
                // Mismatch
                if (j == 0) {
                    // At start of pattern, just move text pointer
                    i++;
                } else {
                    // Use failure function to skip characters in pattern
                    j = lps[j - 1];
                }
            }
        }

        return matchingIndices;
    }

    /**
     * Builds the LPS (Longest Proper Prefix which is also Suffix) array for KMP algorithm.
     *
     * The LPS array is used in KMP pattern matching to avoid redundant comparisons: - lps[i] = length of longest proper
     * prefix of pattern[0..i] that is also a suffix - When mismatch occurs at position i, we can skip to lps[i-1]
     * instead of restarting
     *
     * Example: pattern = "ABABC" - lps = [0, 0, 1, 2, 0] - At index 2: "ABA", longest prefix-suffix = "A" (length 1) -
     * At index 3: "ABAB", longest prefix-suffix = "AB" (length 2)
     *
     * Algorithm: - Compare current character with character at previous prefix length - If match: extend prefix length
     * - If mismatch: fall back to previous prefix length using lps[len-1]
     *
     * @param ch Character array (pattern)
     * @return LPS array for KMP
     */
    private int[] lps(char ch[]) {

        var n = ch.length;
        var lps = new int[n];  // LPS array
        var i = 1;    // Current position being processed
        var len = 0;  // Length of previous longest prefix-suffix

        while (i < n) {
            if (ch[i] == ch[len]) {
                // Characters match: extend the prefix-suffix length
                len++;
                lps[i] = len;
                i++;
            } else {
                // Mismatch
                if (len == 0) {
                    // No previous prefix to fall back to
                    // lps[i] = 0 (implicitly, array initialized to 0)
                    i++;
                } else {
                    // Fall back to previous prefix length
                    // This is the key optimization in KMP
                    len = lps[len - 1];
                }
            }
        }

        return lps;
    }
}