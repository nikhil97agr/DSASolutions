package Leetcode;//Problem Link: https://leetcode.com/problems/maximum-total-value/

/**
 * Solution - Maximum Total Value with Binary Search and Greedy Selection
 *
 * PROBLEM STATEMENT: Given arrays value[] and decay[], where each element i has an initial value[i] that decays by
 * decay[i] each time it's selected, find the maximum total value by selecting exactly m elements (elements can be
 * selected multiple times, and value decreases each time).
 *
 * APPROACH - BINARY SEARCH ON ANSWER + GREEDY SELECTION:
 *
 * KEY INSIGHT: We use binary search to find the optimal "stopping threshold": - If we decide to stop collecting when
 * values reach threshold T, we can calculate exactly how many selections we need and the total value obtained - Lower
 * threshold T → more selections needed (collect more from each element) - Higher threshold T → fewer selections needed
 * (stop earlier)
 *
 * ALGORITHM: 1. Binary search on the threshold value (from 1 to max(value)) 2. For each threshold, calculate how many
 * selections are needed 3. Find the minimum threshold where selections ≤ m 4. Greedily collect values above the
 * threshold 5. Use remaining tokens (if m not exhausted) to collect at threshold-1
 *
 * TIME COMPLEXITY: O(n log(max_value)) SPACE COMPLEXITY: O(1)
 */
public class MaximumTotalValue {

    // Modulo constant for the final answer
    int mod = 1_000_000_007;

    /**
     * Calculate maximum total value by selecting exactly m elements with decay.
     *
     * @param value initial value of each element
     * @param decay decay amount per selection for each element
     * @param m exact number of selections to make
     * @return maximum total value modulo 10^9 + 7
     */
    public int maxTotalValue(int[] value, int[] decay, int m) {

        var n = value.length;

        // Binary search bounds for the threshold
        // l = lower bound (minimum possible threshold)
        // h = upper bound (maximum value in the array)
        long l = 1;
        long h = 0;
        for (var v : value) {
            h = Math.max(h, v);  // Find maximum initial value
        }

        // ans will store the optimal threshold
        // Initialize to h+1 (in case no valid threshold exists)
        long ans = h + 1;

        // BINARY SEARCH: Find minimum threshold where selections needed ≤ m
        //
        // INVARIANT:
        // - If threshold is too high: we need fewer selections (< m)
        // - If threshold is too low: we need more selections (> m)
        //
        // We want the MINIMUM threshold where check(mid) ≤ m
        while (l <= h) {
            var mid = l + (h - l) / 2;

            // check(mid) returns: how many selections needed if we stop at threshold 'mid'
            if (check(value, decay, mid) <= m) {
                // We can afford to lower the threshold (collect more value)
                ans = mid;       // Update answer
                h = mid - 1;     // Try lower threshold
            } else {
                // Too many selections needed, threshold is too low
                l = mid + 1;     // Try higher threshold
            }
        }

        // GREEDY COLLECTION: Collect values above the threshold
        //
        // For each element, collect all values from value[i] down to ans (threshold)
        // The sequence is: value[i], value[i]-decay[i], value[i]-2*decay[i], ...
        // Stop when we reach or go below 'ans'

        long val = 0;      // Total value collected
        long token = 0;    // Total number of selections used

        for (int i = 0; i < n; i++) {
            long v = value[i];  // Initial value of element i
            long d = decay[i];  // Decay amount for element i

            // Only collect if initial value is >= threshold
            if (v >= ans) {
                // Calculate how many times we can select element i before reaching threshold
                // Formula: k = floor((v - ans) / d) + 1
                // Example: v=10, ans=3, d=2 → k = floor((10-3)/2) + 1 = 4
                //   Sequence: 10, 8, 6, 4 (stops before reaching 3)
                long k = (v - ans) / d + 1;
                token += k;  // Add to selection count

                // Calculate sum of arithmetic sequence
                // Sequence: v, v-d, v-2d, ..., v-(k-1)d
                // Formula: sum = k * (first + last) / 2
                long first = v;                  // First term
                long second = v - (k - 1) * d;   // Last term
                long sum = k * (first + second) / 2;
                val += sum;
            }
        }

        // Handle remaining tokens (if we haven't used all m selections)
        // We can use remaining tokens to collect at value (ans - 1)
        long remaining = m - token;
        if (remaining > 0 && ans > 1) {
            // Each remaining token gives us value of (ans - 1)
            val += remaining * (ans - 1);
        }

        return (int) (val % mod);
    }

    /**
     * Helper method to check how many selections are needed for a given threshold.
     *
     * This method calculates the total number of selections required if we collect all values from each element down to
     * the threshold 'mid'.
     *
     * LOGIC: For each element i with value[i] >= mid: - We can select it multiple times as it decays - Sequence:
     * value[i], value[i]-decay[i], value[i]-2*decay[i], ... - Stop when we reach or go below mid - Number of selections
     * = floor((value[i] - mid) / decay[i]) + 1
     *
     * EXAMPLE: value[i] = 10, decay[i] = 3, mid = 2 Sequence: 10, 7, 4 (stops here, next would be 1 which is < 2) Count
     * = (10 - 2) / 3 + 1 = 2 + 1 = 3 selections
     *
     * OPTIMIZATION: Early exit if element count exceeds 2 billion (prevents overflow and speeds up)
     *
     * @param value array of initial values
     * @param decay array of decay amounts
     * @param mid threshold value to check
     * @return total number of selections needed to collect down to threshold 'mid'
     */
    private long check(int value[], int decay[], long mid) {

        long element = 0;  // Total number of selections needed

        for (int i = 0; i < value.length; i++) {
            // Only count elements that have value >= threshold
            if (value[i] >= mid) {
                // Calculate how many times we can select element i
                // Formula: floor((value[i] - mid) / decay[i]) + 1
                // The +1 accounts for the final selection that reaches threshold
                element += (1l * value[i] - mid) / decay[i] + 1;
            }

            // Early exit optimization: if count exceeds 2 billion, return immediately
            // This prevents overflow and indicates threshold is definitely too low
            if (element > 2_000_000_000l) {
                return element;
            }
        }

        return element;
    }


}

/*
 * EXAMPLE WALKTHROUGH:
 *
 * Input:
 * value = [10, 15, 8]
 * decay = [2, 3, 1]
 * m = 8
 *
 * STEP 1: Initialize binary search bounds
 * l = 1, h = 15 (max value)
 *
 * STEP 2: Binary search for optimal threshold
 *
 * Iteration 1: mid = 8
 * - check(8) calculates:
 *   - Element 0: (10-8)/2 + 1 = 2 selections (values: 10, 8)
 *   - Element 1: (15-8)/3 + 1 = 3 selections (values: 15, 12, 9)
 *   - Element 2: (8-8)/1 + 1 = 1 selection (value: 8)
 *   - Total = 6 selections <= 8 ✓
 * - Update: ans = 8, h = 7
 *
 * Iteration 2: mid = 4
 * - check(4) calculates:
 *   - Element 0: (10-4)/2 + 1 = 4 selections (values: 10, 8, 6, 4)
 *   - Element 1: (15-4)/3 + 1 = 4 selections (values: 15, 12, 9, 6)
 *   - Element 2: (8-4)/1 + 1 = 5 selections (values: 8, 7, 6, 5, 4)
 *   - Total = 13 selections > 8 ✗
 * - Update: l = 5
 *
 * Continue until finding optimal threshold...
 *
 * STEP 3: Calculate final value with optimal threshold
 * - For each element, sum arithmetic sequence above threshold
 * - Use remaining tokens (if any) to collect at (threshold - 1)
 * - Return total value modulo 10^9 + 7
 *
 * KEY INSIGHT:
 * Binary search finds the minimum threshold where we can stay within m selections.
 * This maximizes our total value because lower thresholds mean collecting more
 * high-value items from the decay sequences.
 */