package Leetcode;//Problem Link: https://leetcode.com/problems/find-products-of-elements-of-big-array/

/**
 * Solution for finding products of elements in a "big array" constructed from bit decomposition.
 *
 * Problem: The "big array" is created by: - For each number i = 1, 2, 3, 4, 5, ... - Decompose i into powers of 2 (its
 * set bits) - Append these powers to the big array
 *
 * Example: i = 1 to 7 - 1 = 2^0 → [1] - 2 = 2^1 → [2] - 3 = 2^1 + 2^0 → [2, 1] - 4 = 2^2 → [4] - 5 = 2^2 + 2^0 → [4, 1]
 * - 6 = 2^2 + 2^1 → [4, 2] - 7 = 2^2 + 2^1 + 2^0 → [4, 2, 1] - Big array: [1, 2, 2, 1, 4, 4, 1, 4, 2, 4, 2, 1]
 *
 * Query: Given [left, right, mod], find product of elements big[left..right] mod mod
 *
 * Example query: [2, 5, 10^9+7] - Elements: big[2..5] = [2, 1, 4, 4] - Product: 2 × 1 × 4 × 4 = 32
 *
 * Key insight: Bit counting + Binary search
 *
 * Observation 1: Virtual array - Big array is virtual (too large to construct) - Element at position i is a power of 2
 * - Need to find which power appears at position i
 *
 * Observation 2: Count 1-bits - Total elements up to number n = total 1-bits in binary representations of [1..n] - For
 * range [0..n], count how many times bit position b is set - Example: [1..7] has 12 total 1-bits (matches big array
 * length)
 *
 * Observation 3: Product as power - Product of powers of 2: 2^a × 2^b × 2^c = 2^(a+b+c) - Big array element at position
 * i corresponds to some bit position - Product = ∏(2^bit[i]) = 2^0^(count of bit 0) × 2^1^(count of bit 1) × ...
 *
 * Algorithm: 1. For query [l, r, mod]: a. Find bit count up to position l-1: count(l-1) b. Find bit count up to
 * position r: count(r) c. Difference gives count of each bit in range [l, r] d. Compute product: ∏(2^i)^(count[i]) mod
 * mod
 *
 * 2. count(index) - find bit frequency up to position index: a. Binary search: find largest number n such that total
 * 1-bits ≤ index+1 b. Count bits in [1..n] using pattern formula c. Handle remaining elements beyond n
 *
 * Time Complexity: O(q × log²(max_index)) for q queries Space Complexity: O(1) excluding output
 */
public class FindProductsOfElementsOfBigArray {

    /**
     * Processes all queries on the big array.
     *
     * @param queries Array of [left, right, mod] queries
     * @return Array of products modulo mod for each query
     */
    public int[] findProductsOfElements(long[][] queries) {

        var q = queries.length;
        int ans[] = new int[q];

        // Process each query independently
        for (int i = 0; i < q; i++) {
            ans[i] = solve(queries[i]);
        }

        return ans;
    }

    /**
     * Solves a single query: product of big[l..r] mod mod.
     *
     * Strategy: 1. Count bit frequencies up to position l-1 2. Count bit frequencies up to position r 3. Difference
     * gives frequencies in range [l, r] 4. Compute product using exponentiation
     *
     * Example: big[2..5] = [2, 1, 4, 4] - 2 = 2^1, 1 = 2^0, 4 = 2^2, 4 = 2^2 - Bit 0 appears 1 time - Bit 1 appears 1
     * time - Bit 2 appears 2 times - Product = 2^0^1 × 2^1^1 × 2^2^2 = 1 × 2 × 16 = 32
     *
     * @param query [left, right, mod]
     * @return Product of elements in range modulo mod
     */
    private int solve(long query[]) {

        long l = query[0], r = query[1], mod = query[2];

        // ================================================================
        // Get bit frequencies for range [0, l-1] and [0, r]
        // ================================================================
        long[] lCnt = count(l - 1);  // Bit counts up to position l-1
        long[] rCnt = count(r);      // Bit counts up to position r

        // ================================================================
        // Compute product: ∏(2^i)^(count[i]) mod mod
        // ================================================================
        var ans = 1;
        for (int i = 0; i < 63; i++) {
            // Frequency of bit i in range [l, r]
            long diff = rCnt[i] - lCnt[i];
            if (diff == 0) {
                continue;  // Bit i doesn't appear in range
            }

            // Multiply ans by (2^i)^diff
            // (2^i)^diff = 2^(i × diff)
            ans = prod(ans, pow(1l << i, diff, mod), mod);
            if (ans == 0) {
                return 0;  // Result is 0 mod mod
            }
        }

        return ans;
    }

    /**
     * Counts frequency of each bit position in big array up to given index.
     *
     * Strategy: 1. Binary search to find largest number n where total 1-bits ≤ index+1 2. Count all bits in numbers
     * [1..n] 3. If we need more elements (extra), add bits from n+1 one by one
     *
     * Example: index = 5 - Need first 6 elements of big array - Big array: [1, 2, 2, 1, 4, 4, ...] - Elements come from
     * numbers: 1=>[1], 2=>[2], 3=>[2,1], 4=>[4], 5=>[4,...] - After 4, we have [1,2,2,1,4] = 5 elements - Need 1 more:
     * next bit from 5 is 2^0 = 1 - Bit frequencies: bit0=2, bit1=2, bit2=2
     *
     * @param index Position in big array (0-indexed)
     * @return Array where bitCount[i] = frequency of bit i in big[0..index]
     */
    private long[] count(long index) {

        // ================================================================
        // Binary search: find largest n such that total 1-bits ≤ index+1
        // ================================================================
        long min = 0;
        long max = index + 5;       // Upper bound estimate
        long highestPow = 0;        // Largest number fully contributing to big array
        long extra = index + 1;     // How many more elements needed beyond highestPow

        while (min <= max) {
            long mid = min + (max - min) / 2;
            long ones = cntOnes(mid);  // Total 1-bits in [1..mid]

            if (ones < index + 1) {
                // mid doesn't give us enough elements
                highestPow = mid;
                min = mid + 1;
                extra = index + 1 - ones;  // Still need this many elements
            } else if (ones > index + 1) {
                // mid gives us too many elements
                max = mid - 1;
            } else {
                // Exact match: mid gives exactly index+1 elements
                highestPow = mid;
                extra = 0;
                break;
            }
        }

        // ================================================================
        // Count bits in [1..highestPow]
        // ================================================================
        long bitCount[] = bitCount(highestPow);

        if (extra == 0) {
            return bitCount;  // We have exactly the right number of elements
        }

        // ================================================================
        // Add remaining 'extra' elements from next number (highestPow+1)
        // ================================================================
        // The next number is max+1 (or highestPow+1)
        long higher = max + 1;

        // Add bits from higher one by one (from LSB to MSB)
        for (int bit = 0; bit < 64; bit++) {
            long b = (higher >> bit) & 1;
            if (b == 1) {
                bitCount[bit]++;  // This bit appears in big array
                extra--;
                if (extra == 0) {
                    return bitCount;  // Added all needed elements
                }
            }
        }

        return bitCount;
    }


    /**
     * Counts how many times each bit position is set in range [0..num].
     *
     * Uses mathematical pattern: bits repeat in cycles of 2^(bit+1).
     *
     * Pattern for bit b in range [0..n]: - Period: 2^(b+1) numbers - In each period: first 2^b numbers have bit b = 0,
     * next 2^b have bit b = 1 - Full periods: (n+1) / 2^(b+1) → each contributes 2^b ones - Partial period: (n+1) %
     * 2^(b+1) → may contribute some ones
     *
     * Example: bit 1 (value 2) in range [0..7] - Binary: 000, 001, 010, 011, 100, 101, 110, 111 - Bit 1:    0    0    1
     *    1    0    0    1    1 - Period = 4: [0,1] have bit1=0, [2,3] have bit1=1 - In 8 numbers: 2 full periods × 2
     * ones/period = 4 ones
     *
     * @param num Upper bound (inclusive) for counting
     * @return Array where bitCnt[i] = count of bit i set in [0..num]
     */
    private long[] bitCount(long num) {

        long bitCnt[] = new long[64];

        // For each bit position
        for (int bit = 0; bit < 55; bit++) {
            // Full periods of 2^(bit+1)
            long pairs = (num + 1) / (1l << (bit + 1));

            // Each period contributes 2^bit set bits
            bitCnt[bit] = pairs * (1l << bit);

            // Handle partial period
            long left = (num + 1) % (1l << (bit + 1));

            // If partial period extends beyond 2^bit, it has some 1's
            if (left > (1l << bit)) {
                bitCnt[bit] += left - (1l << bit);
            }
        }

        return bitCnt;
    }

    /**
     * Counts total 1-bits in binary representation of all numbers in [0..num].
     *
     * Uses pattern-based counting for each bit position.
     *
     * Strategy: - For each bit position, count how many times it's set in [0..num] - Use cyclic pattern: bit b has
     * period 2^(b+1) - Sum across all bit positions
     *
     * Example: num = 7, range [0..7] - Binary: 000, 001, 010, 011, 100, 101, 110, 111 - Bit 0: 0,1,0,1,0,1,0,1 → 4
     * times - Bit 1: 0,0,1,1,0,0,1,1 → 4 times - Bit 2: 0,0,0,0,1,1,1,1 → 4 times - Total: 4 + 4 + 4 = 12 set bits
     *
     * @param num Upper bound (inclusive) for counting
     * @return Total count of 1-bits in [0..num]
     */
    private long cntOnes(long num) {

        long setBits = 0;
        long n = num + 1;  // Convert to count: [0..num] has n numbers

        // For each bit position
        for (long bit = 0; (1l << bit) <= num; bit++) {
            // Full periods: each period of 2^(bit+1) has 2^bit set bits
            long totalPairs = n / (1l << (bit + 1));
            setBits += totalPairs * (1l << bit);

            // Partial period: may have some set bits
            // If remainder > 2^bit, we have (remainder - 2^bit) set bits
            setBits += Math.max(0l, n % (1l << (bit + 1)) - (1l << bit));
        }

        return setBits;
    }

    /**
     * Computes (a × b) mod mod.
     *
     * Ensures both operands are reduced modulo mod before multiplication to prevent overflow.
     *
     * @param a First operand
     * @param b Second operand
     * @param mod Modulo value
     * @return (a × b) mod mod
     */
    private int prod(long a, long b, long mod) {

        a = a % mod;
        b = b % mod;
        return (int) ((a * b) % mod);
    }

    /**
     * Computes a^b mod mod using binary exponentiation.
     *
     * Binary exponentiation (fast power): - Represent b in binary - For each bit set in b, multiply result by
     * corresponding power of a - Square a at each step
     *
     * Example: 2^13 mod m - 13 = 1101₂ = 8 + 4 + 1 - 2^13 = 2^8 × 2^4 × 2^1 - Compute: 2^1, 2^2, 2^4, 2^8 by repeated
     * squaring - Multiply those where bit is set
     *
     * Time Complexity: O(log b)
     *
     * @param a Base
     * @param b Exponent
     * @param mod Modulo value
     * @return a^b mod mod
     */
    private int pow(long a, long b, long mod) {

        int ans = 1;
        while (b > 0) {
            // If current bit of b is set, multiply by current power of a
            if (b % 2 == 1) {
                ans = prod(ans, a, mod);
            }

            // Square a for next bit position
            a = prod(a, a, mod);
            b >>= 1;  // Move to next bit
        }

        return ans;
    }
}