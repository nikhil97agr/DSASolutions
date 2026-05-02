package Leetcode;

import java.util.ArrayList;
import java.util.List;

//Problem Link: https://leetcode.com/problems/partition-array-for-maximum-xor-and-and/

/**
 * Solution for partitioning array to maximize XOR and AND operations.
 *
 * Problem: Divide array nums into 3 subsequences A, B, C (possibly empty) such that: - Each element belongs to exactly
 * one subsequence - Score = XOR(A) + AND(B) + XOR(C) - Maximize the score
 *
 * Where: - XOR(A) = bitwise XOR of all elements in A (0 if empty) - AND(B) = bitwise AND of all elements in B
 * (Integer.MAX_VALUE if empty, 0 for consistency) - XOR(C) = bitwise XOR of all elements in C (0 if empty)
 *
 * Key insights:
 *
 * 1. XOR property for A and C: - totalXor = XOR(all elements) - XOR(A) XOR XOR(B) XOR XOR(C) = totalXor - If XOR(A) = x
 * and XOR(B) = y, then XOR(C) = totalXor XOR x XOR y - Since we only care about XOR(A) and XOR(C), we can combine them
 *
 * 2. Strategy - Use bitmask for B (AND partition): - AND operation is most restrictive (all bits must be 1) - Choose
 * which elements go to B (for AND operation) - Remaining elements can be optimally split between A and C for XOR
 *
 * 3. Optimizing XOR(A) + XOR(C): - Let remaining elements (not in B) be in set R - We can partition R into A and C in
 * any way - Goal: maximize XOR(A) + XOR(C) subject to XOR(A) XOR XOR(C) = XOR(R) - This is equivalent to: choose subset
 * S ⊆ R for A, rest goes to C - XOR(A) = XOR(S), XOR(C) = XOR(R) XOR XOR(S)
 *
 * 4. Key observation for XOR maximization: - For any subset S: XOR(S) + (XOR(R) XOR XOR(S)) = ? - Let x = XOR(S), then
 * we want to maximize: x + (XOR(R) XOR x) - We can choose any x that can be formed by XORing elements from R - Use XOR
 * basis to generate all possible x values efficiently - For each possible x, compute x + (XOR(R) XOR x) and take
 * maximum
 *
 * 5. Simplification using bitwise properties: - Let totalXorRemaining = XOR(all elements not in B) - For partition into
 * A and C: XOR(A) XOR XOR(C) = totalXorRemaining - To maximize XOR(A) + XOR(C), we use XOR basis - Mask elements with
 * ~totalXorRemaining to avoid redundant bits - Build XOR basis and find configuration that maximizes sum
 *
 * Algorithm: 1. Try all 2^n assignments (which elements go to B for AND) 2. For each assignment: a. Elements in B:
 * compute AND(B) b. Elements not in B: go to set R (to be split between A and C) c. Compute totalXorRemaining = XOR(R)
 * d. Reduce elements in R by masking with ~totalXorRemaining e. Build XOR basis for reduced R f. Find maximum (XOR(A) +
 * XOR(C)) using basis g. Calculate score = maxXorSum + AND(B) and update answer
 *
 * Time Complexity: O(2^n * n * 32) for small n (n ≤ 15-20) Space Complexity: O(n)
 */
public class PartitionArrayForMaximumXorAndAnd {

    /**
     * Finds maximum score by dividing array into 3 subsequences A, B, C.
     *
     * @param nums Input array
     * @return Maximum score achievable
     */
    public long maximizeXorAndXor(int[] nums) {

        long ans = 0;
        int n = nums.length;
        int finalState = (1 << n) - 1;  // All 2^n possible assignments
        int max = Integer.MAX_VALUE;     // Initial value for AND operation

        // ================================================================
        // STEP 1: Compute total XOR of all elements
        // ================================================================
        var totalXor = 0;
        for (var x : nums) {
            totalXor ^= x;
        }

        // ================================================================
        // STEP 2: Try all possible assignments to partition B (AND)
        // ================================================================
        // i represents which elements go to partition B for AND operation
        // bit = 1 means element goes to B (AND), bit = 0 means goes to A or C (XOR)
        for (int i = 0; i <= finalState; i++) {
            int and = max;    // AND of elements in partition B
            var xor = 0;      // XOR of elements in partition B
            List<Integer> list = new ArrayList<>();  // Elements NOT in B (go to A or C)

            // ============================================================
            // Assign elements based on bitmask i
            // ============================================================
            for (int j = 0; j < n; j++) {
                int bit = (i >> j) & 1;  // Check if nums[j] goes to B or not
                if (bit == 1) {
                    // Element goes to partition B (for AND operation)
                    xor ^= nums[j];  // Track XOR of B elements
                    and &= nums[j];  // Compute AND of B elements
                } else {
                    // Element goes to remaining set (to be split between A and C)
                    list.add(nums[j]);
                }
            }

            // ============================================================
            // Compute XOR of remaining elements (not in B)
            // ============================================================
            // These elements will be split between A and C
            // XOR(remaining) = totalXor XOR XOR(B)
            // This is the constraint: XOR(A) XOR XOR(C) must equal this
            xor ^= totalXor;
            var invertedXor = ~xor;  // Bits that are 0 in xor (remaining XOR)
            // ============================================================
            // Reduce remaining elements by masking
            // ============================================================
            // For elements that will be split between A and C:
            // - Constraint: XOR(A) XOR XOR(C) = xor (computed above)
            // - We want to maximize XOR(A) + XOR(C)
            // - Only bits that are 0 in 'xor' can independently contribute
            // - Bits that are 1 in 'xor' are constrained by the XOR relationship
            // - Masking with ~xor keeps only the "free" bits that can vary
            List<Integer> reduced = new ArrayList<>();
            for (int x : list) {
                reduced.add(x & invertedXor);
            }

            // ============================================================
            // Handle edge case: empty partition B
            // ============================================================
            if (and == Integer.MAX_VALUE) {
                and = 0;  // AND of empty set is 0 (neutral element)
            }

            // ============================================================
            // Find maximum XOR(A) + XOR(C) achievable
            // ============================================================
            // Build XOR basis from reduced elements
            // The basis allows us to generate all possible XOR(A) values
            List<Integer> basis = basis(reduced);

            // Get maximum sum XOR(A) + XOR(C) using the basis
            // Since XOR(C) = xor XOR XOR(A), we find the split that maximizes the sum
            int maxXorSum = getMaxXor(basis);

            // ============================================================
            // Calculate score and update answer
            // ============================================================
            // Score = XOR(A) + AND(B) + XOR(C)
            // We found: maxXorSum = maximum of (XOR(A) + XOR(C))
            // Add AND(B) to get total score
            // Note: 'xor' represents the constraint XOR(A) XOR XOR(C)
            // The actual maxXorSum is computed considering this constraint
            ans = Math.max(ans, 1l * and + xor + 2 * maxXorSum);


        }

        return ans;
    }

    /**
     * Builds XOR basis (linear basis) from a list of numbers.
     *
     * XOR basis is a minimal set of numbers such that: - Any number that can be formed by XORing elements from the
     * original list can also be formed by XORing elements from the basis - The basis is minimal (no redundant
     * elements)
     *
     * Algorithm (Gaussian elimination for XOR): - For each number x in the list: * Try to reduce x using existing basis
     * elements * Reduction: x = min(x, x XOR y) for each y in basis * If x becomes 0, it's linearly dependent (skip) *
     * Otherwise, add x to basis
     *
     * Why min(x, x XOR y)? - We want to reduce x to its smallest form - XORing with y might flip some bits - Taking min
     * ensures we keep the smaller value - This helps maintain a canonical form
     *
     * Example: list = [6, 4, 2, 1] - Process 6 (110): basis = [6] - Process 4 (100): min(4, 4^6=2) = 2, basis = [6, 2]
     * - Process 2 (010): min(2, 2^6=4) = 2, min(2, 2^2=0) = 0, skip (dependent) - Process 1 (001): min(1, 1^6=7) = 1,
     * min(1, 1^2=3) = 1, basis = [6, 2, 1] - Basis can generate: 0,1,2,3,4,5,6,7 (all XOR combinations)
     *
     * @param list List of integers
     * @return XOR basis (minimal spanning set)
     */
    public List<Integer> basis(List<Integer> list) {

        List<Integer> result = new ArrayList<>();
        for (var x : list) {
            // Reduce x using existing basis elements
            for (var y : result) {
                x = Math.min(x, x ^ y);
            }

            // If x is non-zero, it's linearly independent, add to basis
            if (x > 0) {
                result.add(x);
            }
        }

        return result;
    }

    /**
     * Finds maximum XOR(A) + XOR(C) achievable using XOR basis.
     *
     * Context for this problem: - We need to split remaining elements into A and C - Constraint: XOR(A) XOR XOR(C) =
     * fixed value (from totalXor and B) - Goal: maximize XOR(A) + XOR(C)
     *
     * Strategy: Greedy approach - Start with ans = 0 - For each basis element x: * Try ans XOR x * Keep the larger of
     * ans and ans XOR x - This greedily builds the maximum XOR value
     *
     * Why this works for maximizing XOR(A) + XOR(C): - Let XOR(A) = a, then XOR(C) = constraint XOR a - We want to
     * maximize: a + (constraint XOR a) - The basis allows us to generate different values of 'a' - For each 'a' from
     * basis, we evaluate a + (constraint XOR a) - The greedy approach finds the 'a' that maximizes this sum -
     * Math.max(ans, ans ^ x) explores different XOR combinations
     *
     * How it relates to XOR(A) + XOR(C): - 'ans' represents potential values we can achieve - Each basis element allows
     * us to explore new combinations - The maximum value found corresponds to optimal split - If constraint = 0, this
     * gives us XOR(A) + XOR(A) which isn't quite right - But the formula in main method accounts for this: xor +
     * 2*maxXor
     *
     * Example: basis = [6, 2, 1] (binary: 110, 010, 001) - ans = 0 - Process 6: max(0, 0^6) = max(0, 6) = 6, ans = 6
     * (110) - Process 2: max(6, 6^2) = max(6, 4) = 6, ans = 6 (no change, 2 doesn't help) - Process 1: max(6, 6^1) =
     * max(6, 7) = 7, ans = 7 (111) - Maximum XOR = 7
     *
     * Note: This is a heuristic that works well for this problem. The actual relationship between this max XOR and
     * XOR(A) + XOR(C) is handled by the formula in the main method.
     *
     * @param list XOR basis elements
     * @return Maximum XOR value achievable (used in computing XOR(A) + XOR(C))
     */
    private int getMaxXor(List<Integer> list) {

        int ans = 0;
        for (var x : list) {
            // Greedily choose whether to XOR with x
            // This explores different XOR combinations
            ans = Math.max(ans, ans ^ x);
        }

        return ans;
    }
}