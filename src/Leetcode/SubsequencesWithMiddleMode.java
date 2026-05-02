package Leetcode;

import java.util.HashMap;
import java.util.Map;

//Problem Link: https://leetcode.com/problems/subsequences-with-a-unique-middle-mode-ii/

/**
 * Solution for counting subsequences with a unique middle mode.
 *
 * A subsequence of length 5 has a "unique middle mode" if the element at the middle position (index 2) appears more
 * frequently in the subsequence than any other element.
 *
 * For a subsequence [a, b, c, d, e], element c is the middle mode if: - freq(c) > freq(x) for all x != c in the
 * subsequence
 *
 * Algorithm approach: 1. Fix each element as the middle element (index i in original array) 2. Count ways to form valid
 * 5-element subsequences with nums[i] as the mode 3. Use combinatorics and inclusion-exclusion principle 4. Track
 * frequencies in prefix and suffix using sliding window 5. Maintain cumulative statistics for efficient counting
 *
 * Key insight: For nums[i] to be the unique mode in a 5-element subsequence: - Select 2 elements before i and 2
 * elements after i - nums[i] must appear more than any other element in those 4 selected elements
 *
 * Time Complexity: O(n) Space Complexity: O(n) for factorials and frequency maps
 */
public class SubsequencesWithMiddleMode {

    int mod = 1_000_000_007;  // Modulo for all operations
    int fact[];               // Factorials for combinatorics: fact[i] = i!
    int inv[];                // Modular inverses of factorials

    /**
     * Counts subsequences where the middle element is the unique mode.
     *
     * @param nums Input array
     * @return Count of valid 5-element subsequences modulo 10^9 + 7
     */
    public int subsequencesWithMiddleMode(int[] nums) {

        int n = nums.length;

        // Precompute factorials and their modular inverses for combinations
        fact = new int[n + 1];
        inv = new int[n + 1];
        fact[0] = fact[1] = 1;
        inv[0] = inv[1] = 1;
        for (int i = 2; i <= n; i++) {
            fact[i] = prod(i, fact[i - 1]);           // fact[i] = i!
            inv[i] = pow(fact[i], mod - 2);            // inv[i] = (i!)^(-1) using Fermat's Little Theorem
        }

        int ans = 0;

        // Frequency maps for elements before and after current position
        var preCnt = new HashMap<Integer, Integer>();  // Count of each value in prefix
        var sufCnt = new HashMap<Integer, Integer>();  // Count of each value in suffix

        // Initialize suffix with all elements
        for (int i = 0; i < n; i++) {
            add(nums[i], sufCnt);
        }

        // Cumulative statistics for combinatorial calculations
        // These track various sum-of-products needed for inclusion-exclusion
        int suffSqSum = 0;      // Sum of cnt[x]^2 for all x in suffix
        int preSqSum = 0;       // Sum of cnt[x]^2 for all x in prefix
        int preSufSum = 0;      // Sum of preCnt[x] * sufCnt[x] for all x
        int preSqSuffSum = 0;   // Sum of preCnt[x]^2 * sufCnt[x] for all x
        int preSuffSqSum = 0;   // Sum of preCnt[x] * sufCnt[x]^2 for all x

        // Initialize suffix square sum
        for (var val : sufCnt.values()) {
            suffSqSum = add(suffSqSum, prod(val, val));
        }

        int right = n;  // Number of elements in suffix
        int left = 0;   // Number of elements in prefix

        // Fix each position as the middle element
        for (int i = 0; i < n; i++) {
            right--;
            remove(nums[i], sufCnt);  // Remove current element from suffix

            // Get counts of nums[i] in prefix and suffix
            var cPre = preCnt.getOrDefault(nums[i], 0);
            var cSuf = sufCnt.getOrDefault(nums[i], 0);

            // Update cumulative statistics (subtract old values before nums[i] is added to prefix)
            suffSqSum = add(suffSqSum, -prod(cSuf + 1, cSuf + 1));
            preSqSum = add(preSqSum, -prod(cPre, cPre));
            preSufSum = add(preSufSum, -prod(cPre, cSuf + 1));
            preSqSuffSum = add(preSqSuffSum, -prod(cPre, prod(cPre, cSuf + 1)));
            preSuffSqSum = add(preSuffSqSum, -prod(cPre, prod(cSuf + 1, cSuf + 1)));

            // ====================================================================
            // INCLUSION-EXCLUSION COUNTING WITH DETAILED EXPLANATIONS
            // ====================================================================
            // We want to count 5-element subsequences where nums[i] is the middle mode.
            // The subsequence has form: [prefix_1, prefix_2, nums[i], suffix_1, suffix_2]
            //
            // For nums[i] to be the unique mode:
            // - If nums[i] appears in the 4 selected elements (prefix_1, prefix_2, suffix_1, suffix_2),
            //   it appears 2 times total (1 + occurrence in the 4)
            // - Any other element can appear at most 1 time in the entire subsequence
            // - So we need: no element appears twice in the selected 4 elements
            // ====================================================================

            // COUNT 1: Total combinations (no restrictions)
            // --------------------------------------------------------------------
            // Choose any 2 elements from prefix and any 2 from suffix
            // Formula: C(left, 2) * C(right, 2)
            // This is our starting point - we'll subtract invalid cases
            // --------------------------------------------------------------------
            ans = add(ans, prod(comb(left, 2), comb(right, 2)));

            // COUNT 2: Subtract cases where nums[i] NEVER appears in the 4 selected elements
            // --------------------------------------------------------------------
            // If nums[i] doesn't appear in the 4 selected elements, it only appears once
            // (as the middle element), so it cannot be the unique mode if any other element
            // appears twice in the selected 4.
            //
            // Even if no element appears twice, nums[i] with frequency 1 is not strictly
            // greater than other elements with frequency 1, so it's not a unique mode.
            //
            // Formula: C(left - cPre, 2) * C(right - cSuf, 2)
            // - Choose 2 from prefix elements that are NOT nums[i]
            // - Choose 2 from suffix elements that are NOT nums[i]
            // --------------------------------------------------------------------
            ans = add(ans, -prod(comb(left - cPre, 2), comb(right - cSuf, 2)));

            // Remaining counts excluding nums[i]
            int preRemain = left - cPre;   // Elements in prefix different from nums[i]
            int sufRemain = right - cSuf;  // Elements in suffix different from nums[i]

            // COUNT 3: Subtract cases where some element X (not nums[i]) appears TWICE from SUFFIX
            // --------------------------------------------------------------------
            // Scenario: subsequence = [prefix_1, prefix_2, nums[i], X, X] where X appears twice
            //
            // In this case:
            // - nums[i] appears: 1 time (middle)
            // - Element X appears: 2 times (both from suffix)
            // - freq(X) = 2 > freq(nums[i]) = 1, so nums[i] is NOT the mode
            //
            // Calculation breakdown:
            // - For each element X in prefix, we need to count how many times it appears
            // - preSqSum = sum of cnt[x]^2 for all x in prefix
            //   * If element x appears c times, we can choose 2 of them in C(c, 2) = c*(c-1)/2 ways
            //   * But we need to count ordered pairs for the formula, so c^2 counts all pairs
            // - (preSqSum - preRemain) gives sum of cnt[x]^2 where cnt[x] >= 2 is weighted more
            //   * We subtract preRemain to adjust for single elements
            //
            // Formula components:
            // - (preSqSum - preRemain): Weighted count of prefix elements
            // - cSuf: Choose one X from suffix (first occurrence)
            // - sufRemain: Choose another element from suffix (could be X again or different)
            // - Divide by 2: Correct for double counting when both suffix elements are the same X
            //
            // Wait, let me recalculate the actual logic:
            // - We want cases where element X appears twice in the 4 selected elements
            // - preSqSum = sum of cnt[x]^2 for x in prefix
            // - For element X in prefix with count c, choosing it twice contributes c^2
            // - We select that X from suffix once, and one more from suffix
            // - Multiply by cSuf (if nums[i] appears in suffix, one selection could be nums[i])
            // - Multiply by sufRemain, divide by 2 for the two suffix positions
            // --------------------------------------------------------------------
            int a = add(preSqSum, -preRemain);
            a = prod(a, cSuf);
            a = prod(a, sufRemain);
            a = prod(a, pow(2, mod - 2));  // Multiply by 1/2 (modular inverse of 2)
            ans = add(ans, -a);

            // COUNT 4: Subtract cases where some element X appears TWICE from PREFIX
            // --------------------------------------------------------------------
            // Scenario: subsequence = [X, X, nums[i], suffix_1, suffix_2]
            //
            // Similar to Count 3, but element X appears twice in the prefix:
            // - nums[i] appears: 1 time (middle)
            // - Element X appears: 2 times (both from prefix)
            // - freq(X) = 2 > freq(nums[i]) = 1, so nums[i] is NOT the mode
            //
            // Formula components:
            // - (suffSqSum - sufRemain): Weighted count of suffix elements
            // - cPre: Choose one occurrence from prefix
            // - preRemain: Choose another element from prefix
            // - Divide by 2: Correct for double counting
            // --------------------------------------------------------------------
            int b = add(suffSqSum, -sufRemain);
            b = prod(b, cPre);
            b = prod(b, preRemain);
            b = prod(b, pow(2, mod - 2));  // Multiply by 1/2
            ans = add(ans, -b);

            // COUNT 5: Subtract cases where element X appears ONCE in prefix and ONCE in suffix
            // --------------------------------------------------------------------
            // Scenario: subsequence = [X, other, nums[i], X, other2] or similar
            //
            // In this case:
            // - nums[i] appears: 1 time (middle)
            // - Element X appears: 2 times (once in prefix, once in suffix)
            // - freq(X) = 2 > freq(nums[i]) = 1, so nums[i] is NOT the mode
            //
            // Calculation breakdown:
            // For each element X that appears in both prefix and suffix:
            // - We can select X from prefix and X from suffix
            // - We need to select one more element from prefix and one from suffix
            //
            // preSufSum = sum of preCnt[x] * sufCnt[x] for all x
            // This counts pairs where x appears in both prefix and suffix
            //
            // For the other selections:
            // - If we select X from prefix: we have (right) choices for suffix
            //   * But one of those choices is X itself (if x in suffix), so we subtract
            // - If we select X from suffix: we have (left) choices for prefix
            //   * But one of those choices is X itself (if x in prefix), so we subtract
            // - We're double counting when both prefix and suffix selections are X
            //
            // Formula:
            // c = cPre * right + cSuf * left - 2 * cPre * cSuf
            //   * cPre * right: ways to pick nums[i] from prefix and any element from suffix
            //   * cSuf * left: ways to pick nums[i] from suffix and any element from prefix
            //   * -2*cPre*cSuf: subtract double counting when both are nums[i]
            // e = preSufSum * c
            // --------------------------------------------------------------------
            int c = prod(cPre, right);
            c = add(c, prod(cSuf, left));
            int d = prod(2, prod(cPre, cSuf));
            c = add(c, -d);
            int e = prod(preSufSum, c);
            ans = add(ans, -e);

            // COUNT 6 & 7: Add back OVER-SUBTRACTED cases (Inclusion-Exclusion Correction)
            // --------------------------------------------------------------------
            // In Count 3, 4, and 5, we subtracted cases where some element X appears twice.
            // However, some cases were subtracted MULTIPLE times and need to be added back.
            //
            // COUNT 6: preSqSuffSum * cSuf
            // - preSqSuffSum = sum of preCnt[x]^2 * sufCnt[x]
            // - This handles cases where element X appears twice in prefix AND once in suffix
            // - Such cases were subtracted in both Count 3 and Count 5
            // - We add them back once to correct the over-subtraction
            //
            // COUNT 7: preSuffSqSum * cPre
            // - preSuffSqSum = sum of preCnt[x] * sufCnt[x]^2
            // - This handles cases where element X appears once in prefix AND twice in suffix
            // - Such cases were subtracted in both Count 4 and Count 5
            // - We add them back once to correct the over-subtraction
            //
            // This is the classic inclusion-exclusion principle:
            // |A ∪ B| = |A| + |B| - |A ∩ B|
            // We subtracted |A ∩ B| twice, so we add it back once
            // --------------------------------------------------------------------
            ans = add(ans, prod(preSqSuffSum, cSuf));
            ans = add(ans, prod(preSuffSqSum, cPre));

            // Move current element from middle to prefix
            left++;
            add(nums[i], preCnt);

            // Update cumulative statistics (add new values after nums[i] is added to prefix)
            suffSqSum = add(suffSqSum, prod(cSuf, cSuf));
            preSqSum = add(preSqSum, prod(cPre + 1, cPre + 1));
            preSufSum = add(preSufSum, prod(cPre + 1, cSuf));
            preSqSuffSum = add(preSqSuffSum, prod(cPre + 1, prod(cPre + 1, cSuf)));
            preSuffSqSum = add(preSuffSqSum, prod(cPre + 1, prod(cSuf, cSuf)));

        }

        return ans;
    }

    /**
     * Removes an element from the frequency map.
     *
     * @param val Element value to remove
     * @param map Frequency map
     */
    private void remove(int val, Map<Integer, Integer> map) {

        map.merge(val, -1, Integer::sum);  // Decrement count
        if (map.get(val) == 0) {
            map.remove(val);  // Remove key if count becomes 0
        }
    }

    /**
     * Adds an element to the frequency map.
     *
     * @param val Element value to add
     * @param map Frequency map
     */
    private void add(int val, Map<Integer, Integer> map) {

        map.merge(val, 1, Integer::sum);  // Increment count
    }

    /**
     * Computes binomial coefficient C(n, r) = n! / (r! * (n-r)!) Uses precomputed factorials and their modular
     * inverses.
     *
     * @param n Total number of items
     * @param r Number of items to choose
     * @return C(n, r) mod (10^9 + 7)
     */
    private int comb(int n, int r) {

        if (r > n) {
            return 0;  // Can't choose more than available
        }
        if (r == n) {
            return 1;  // Only one way to choose all
        }
        // C(n, r) = n! / (r! * (n-r)!)
        return prod(fact[n], prod(inv[r], inv[n - r]));
    }

    /**
     * Adds two numbers with modulo arithmetic, handling negative values.
     *
     * @param a First number
     * @param b Second number (can be negative)
     * @return (a + b) mod (10^9 + 7), guaranteed non-negative
     */
    private int add(int a, int b) {

        // Add mod to handle negative intermediate results
        long sum = 1l * a + b + mod;

        return (int) (sum % mod);
    }

    /**
     * Multiplies two numbers with modulo arithmetic.
     *
     * @param a First number
     * @param b Second number
     * @return (a * b) mod (10^9 + 7)
     */
    private int prod(int a, int b) {

        long p = 1l * a * b;
        return (int) (p % mod);
    }

    /**
     * Computes modular exponentiation: a^b mod (10^9 + 7). Uses iterative exponentiation by squaring.
     *
     * Commonly used to compute modular multiplicative inverse: - a^(p-2) ≡ a^(-1) (mod p) by Fermat's Little Theorem
     *
     * @param a Base
     * @param b Exponent
     * @return a^b mod (10^9 + 7)
     */
    private int pow(int a, int b) {

        int p = 1;  // Result
        while (b > 0) {
            if ((b & 1) == 1) {
                p = prod(p, a);  // If current bit is 1, multiply result by current a
            }

            a = prod(a, a);  // Square a for next bit
            b >>= 1;         // Shift to next bit
        }

        return p;
    }
}