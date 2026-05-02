package Leetcode;

//Problem Link: https://leetcode.com/problems/number-of-effective-subsequences

/**
 * Solution for counting "effective" subsets.
 *
 * Problem: A subset is "effective" if the bitwise OR of all its elements equals the bitwise OR of the entire array.
 * Count how many non-empty subsets are effective.
 *
 * Key insight: Inclusion-Exclusion Principle with bit compression
 *
 * Example: nums = [1, 2, 3] - OR of all = 1|2|3 = 3 (binary: 11) - Effective subsets: those with OR = 3 {3}, {1,2},
 * {1,3}, {2,3}, {1,2,3} → 5 subsets
 *
 * Algorithm approach: 1. Find which bits are set in the global OR (compress bit space) 2. Map each number to compressed
 * bit representation 3. Use SOS DP (Sum Over Subsets) to count numbers with specific bit patterns 4. Apply
 * inclusion-exclusion to count subsets with OR = all bits set
 *
 * Why bit compression? - Only bits set in global OR matter - Compress from 22 bits to k bits (k = number of set bits in
 * global OR) - Reduces state space from 2^22 to 2^k
 *
 * Inclusion-Exclusion: - For subset to have OR = fullMask, it must NOT have complement bits - Count subsets that avoid
 * specific bit combinations - Use inclusion-exclusion to combine counts
 *
 * Time Complexity: O(n + k * 2^k) where k = bits in global OR Space Complexity: O(2^k)
 */
public class NumberOfEffectiveSubsequences {

    int mod = 1_000_000_007;  // Modulo for preventing overflow

    /**
     * Counts the number of effective subsets.
     *
     * @param nums Input array
     * @return Count of non-empty subsets with OR equal to global OR, modulo 10^9+7
     */
    public int countEffective(int[] nums) {

        // ================================================================
        // STEP 1: Calculate global OR and handle edge case
        // ================================================================
        var or = 0;
        var n = nums.length;

        // Compute OR of all elements
        for (var x : nums) {
            or |= x;
        }

        // Edge case: if all numbers are 0, no effective subset exists
        if (or == 0) {
            return 0;
        }

        // ================================================================
        // STEP 2: Identify and compress relevant bits
        // ================================================================
        // Only bits that are set in global OR are relevant
        // Map these bits to a compressed representation

        var bits = new int[22];  // Maps compressed bit index to original bit position
        var bitLength = 0;       // Number of relevant bits

        // Find which bits are set in global OR
        for (var i = 0; i < 22; i++) {
            var bit = (or >> i) & 1;
            if (bit == 1) {
                bits[bitLength++] = i;  // Record this bit position
            }
        }

        // Create frequency array for compressed representations
        // max = 2^bitLength (all possible combinations of relevant bits)
        var max = 1 << bitLength;
        var freq = new int[max];  // freq[mask] = count of numbers with this bit pattern

        // ================================================================
        // STEP 3: Compress each number and build frequency array
        // ================================================================
        // Map each number from original bit representation to compressed

        for (var x : nums) {
            var newOr = 0;  // Compressed representation of x

            // For each relevant bit, check if it's set in x
            for (var i = 0; i < bitLength; i++) {
                // Check if bit at position bits[i] is set in x
                var bit = (x >> bits[i]) & 1;
                if (bit == 1) {
                    // Set bit i in compressed representation
                    newOr |= (1 << i);
                }
            }

            // Increment frequency of this compressed bit pattern
            freq[newOr]++;
        }

        // ================================================================
        // STEP 4: SOS DP (Sum Over Subsets Dynamic Programming)
        // ================================================================
        // Transform freq[mask] to count numbers that are subsets of mask
        // After this, freq[mask] = count of numbers whose bits are subset of mask

        // Process each bit dimension
        for (var bit = 0; bit < bitLength; bit++) {
            // For each possible mask
            for (var mask = 0; mask < max; mask++) {
                var b = (mask >> bit) & 1;

                // If bit is set in mask, add count from mask without this bit
                if (b == 1) {
                    // freq[mask] now includes numbers with bit unset
                    freq[mask] = add(freq[mask], freq[mask ^ (1 << bit)]);
                }
            }
        }

        // ================================================================
        // STEP 5: Precompute powers of 2
        // ================================================================
        // pow[i] = 2^i mod (10^9+7)
        // Used to calculate number of subsets from i elements

        var pow = new int[n + 1];
        pow[0] = 1;  // 2^0 = 1
        for (int i = 1; i <= n; i++) {
            pow[i] = prod(pow[i - 1], 2);  // 2^i = 2^(i-1) * 2
        }

        // ================================================================
        // STEP 6: Inclusion-Exclusion to count effective subsets
        // ================================================================
        // An effective subset must have OR = maxMask (all bits set)
        // Use inclusion-exclusion: count subsets that don't miss any bits

        var ans = 0;
        var maxMask = max - 1;  // All relevant bits set (e.g., if bitLength=3, maxMask=111 in binary)

        // Iterate over all non-empty subsets of bits
        for (var mask = 1; mask <= maxMask; mask++) {
            // complement: bits that are NOT in mask
            // We're checking subsets that are missing these bits
            var complement = maxMask ^ mask;

            // freq[complement]: count of numbers that are subsets of complement
            // These numbers DON'T have the bits in mask
            var cnt = freq[complement];

            // totalSub: number of subsets we can form from cnt numbers
            // Each of the cnt numbers can be included or excluded: 2^cnt possibilities
            var totalSub = pow[cnt];

            // Inclusion-Exclusion principle:
            // - Odd number of bits in mask: ADD (inclusion)
            // - Even number of bits in mask: SUBTRACT (exclusion)
            if (Integer.bitCount(mask) % 2 == 1) {
                ans = add(ans, totalSub);
            } else {
                ans = add(ans, -totalSub);
            }
        }

        return ans;
    }

    /**
     * Multiplies two numbers with modulo arithmetic.
     *
     * @param a First number
     * @param b Second number
     * @return (a * b) % mod
     */
    private int prod(long a, long b) {

        return (int) ((a * b) % mod);
    }

    /**
     * Adds two numbers with modulo arithmetic.
     *
     * Handles negative numbers by adding mod before taking modulo. This ensures the result is always non-negative.
     *
     * @param a First number
     * @param b Second number
     * @return (a + b) % mod (always non-negative)
     */
    private int add(long a, long b) {

        // Add mod to handle negative inputs (from inclusion-exclusion)
        long sum = a + b + mod;

        return (int) (sum % mod);
    }
}