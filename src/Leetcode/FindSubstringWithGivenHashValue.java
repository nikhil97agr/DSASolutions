package Leetcode;

/**
 * Solution for Find Substring With Given Hash Value
 *
 * Problem: Find a substring of length k with a specific polynomial rolling hash value
 * Hash formula: hash(s) = (val[0] * p^0 + val[1] * p^1 + ... + val[k-1] * p^(k-1)) % m
 * where val[i] = s[i] - 'a' + 1
 *
 * Approach: Reverse Rolling Hash
 * - Process string from right to left (reverse direction)
 * - This allows us to add new characters at the beginning efficiently
 * - Use rolling hash to compute hash for each window of size k
 * - Return the last (leftmost) substring that matches the hash
 */
public class FindSubstringWithGivenHashValue {

    int mod = 0;

    /**
     * Finds substring of length k with the given hash value
     *
     * @param s The input string
     * @param power The base for polynomial hash (p)
     * @param modulo The modulo value (m)
     * @param k Length of substring
     * @param hashValue Target hash value to find
     * @return The leftmost substring with the given hash value
     */
    public String subStrHash(String s, int power, int modulo, int k, int hashValue) {

        mod = modulo;

        // Precompute power^k for removing characters from the window
        int pk = pow(power, k);

        int hash = 0;
        int n = s.length();
        int ans = 0;  // Store the starting index of answer

        // Process from right to left (reverse rolling hash)
        for (int i = n - 1; i >= 0; i--) {
            // Add current character to hash (at the beginning of window)
            int ind = s.charAt(i) - 'a' + 1;
            hash = add(prod(hash, power), ind);

            // Remove character that's now outside the window (if window size > k)
            if (i + k < n) {
                int last = s.charAt(i + k) - 'a' + 1;
                hash = add(hash, -prod(pk, last));
            }

            // Check if current window matches the target hash
            if (hash == hashValue) {
                ans = i;  // Update answer (we want the leftmost match)
            }
        }

        return s.substring(ans, ans + k);
    }

    /**
     * Adds two numbers with modulo
     * Handles negative numbers by adding mod before taking modulo
     */
    private int add(long a, long b) {

        return (int) ((a + b + mod) % mod);
    }

    /**
     * Multiplies two numbers with modulo
     */
    private int prod(long a, long b) {

        return (int) ((a * b) % mod);
    }

    /**
     * Calculates (a^b) % mod using binary exponentiation
     * Time complexity: O(log b)
     */
    private int pow(long a, long b) {

        if (a == 0 || b == 1) {
            return prod(a, 1);
        }
        if (b == 0) {
            return 1;
        }

        // Divide and conquer: a^b = (a^(b/2))^2
        int p = pow(a, b / 2);
        p = prod(p, p);

        // If b is odd, multiply by a once more
        if (b % 2 == 1) {
            p = prod(p, a);
        }

        return p;
    }

}