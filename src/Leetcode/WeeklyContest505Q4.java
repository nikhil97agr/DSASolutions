package Leetcode;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * Leetcode.WeeklyContest505Q4 - Maximum Sum with At Most m Subarrays (Aliens Trick / WQS Binary Search)
 *
 * PROBLEM STATEMENT: Given an array of integers, find the maximum sum by selecting at most m non-overlapping subarrays,
 * where each subarray has length between minLen and maxLen (inclusive).
 *
 * APPROACH - "ALIENS TRICK" (WQS Binary Search / Lagrange Multipliers):
 *
 * The challenge is optimizing with constraint "at most m subarrays" efficiently. Direct DP tracking count would be O(m
 * * n^2).
 *
 * KEY INSIGHT: Instead of enforcing "at most m", we use binary search on a "penalty" value λ: - For each subarray
 * selected, subtract penalty λ from the total sum - Higher penalty → fewer subarrays selected in optimal solution -
 * Lower penalty → more subarrays selected in optimal solution - Binary search finds minimum penalty where optimal uses
 * ≤ m subarrays - Reconstruct actual answer from penalized DP result
 *
 * MATHEMATICAL INTUITION: Define f(λ) = max(sum - λ * count) over all valid selections The function f(λ) is
 * piecewise-linear and convex in the number of subarrays used. Binary search on λ finds the transition point where
 * count changes from >m to ≤m.
 *
 * ALGORITHM STEPS: 1. Build prefix sum array for O(1) subarray sum queries 2. Binary search on penalty λ in range [0,
 * sum of absolute values] 3. For each penalty, solve DP: maximize (sum - λ * count) using monotonic deque 4. Find
 * minimum penalty where count ≤ m 5. Reconstruct answer: penalized_value + penalty * m
 *
 * TIME COMPLEXITY: O(n log(totalSum)) where totalSum is sum of absolute values SPACE COMPLEXITY: O(n) for DP arrays and
 * monotonic deque
 */
class WeeklyContest505Q4 {

    /**
     * Represents negative infinity for DP initialization. Matches Python's NEG = -10**30 Used to mark
     * impossible/uncomputed states.
     */
    private static final long NEGATIVE_INFINITY = -1000000000000000000L;

    /**
     * Finds maximum sum using at most maxSubarrays non-overlapping subarrays.
     *
     * @param nums input array of integers
     * @param maxSubarrays maximum number of subarrays allowed (at most m)
     * @param minLen minimum length of each subarray (inclusive)
     * @param maxLen maximum length of each subarray (inclusive)
     * @return maximum possible sum
     */
    public long maximumSum(int[] nums, int maxSubarrays, int minLen, int maxLen) {

        int n = nums.length;

        // STEP 1: Build prefix sum array for O(1) subarray sum calculation
        // prefixSum[i] = sum of nums[0..i-1]
        // Subarray sum from startPos to endPos = prefixSum[endPos] - prefixSum[startPos]
        long[] prefixSum = new long[n + 1];
        long penaltyUpperBound = 0;

        for (int i = 0; i < n; i++) {
            prefixSum[i + 1] = prefixSum[i] + nums[i];
            // Calculate upper bound for binary search on penalty
            // Maximum useful penalty = sum of all absolute values + 1
            penaltyUpperBound += Math.abs((long) nums[i]);
        }
        penaltyUpperBound += 1;

        // STEP 2: Binary search on penalty value λ
        //
        // Invariant:
        // - At penalty 'high': optimal solution uses ≤ maxSubarrays (high penalty discourages selection)
        // - At penalty 'low': optimal solution uses > maxSubarrays (low penalty encourages selection)
        //
        // Goal: Find minimum penalty where count ≤ maxSubarrays
        long low = 0, high = penaltyUpperBound;

        while (low < high) {
            long midPenalty = low + (high - low) / 2;
            DPResult result = solvePenalizedDP(prefixSum, n, minLen, maxLen, midPenalty);

            if (result.count <= maxSubarrays) {
                // Count is acceptable, try lower penalty for potentially better answer
                high = midPenalty;
            } else {
                // Too many subarrays selected, need higher penalty to reduce count
                low = midPenalty + 1;
            }
        }

        // STEP 3: Reconstruct the actual answer
        //
        // CRITICAL: The reconstruction formula uses maxSubarrays (NOT actual count)!
        // This is the standard Aliens Trick formula:
        //   actual_answer = penalized_value + penalty * m
        //
        // Why multiply by m and not actual count?
        // Because we're finding the penalty threshold where the transition happens.
        // The formula essentially "undoes" the penalty for exactly m subarrays.
        //
        // We check both 'low' and 'low-1' because:
        // - At 'low': might use < maxSubarrays (underutilized)
        // - At 'low-1': might use > maxSubarrays (exceeded limit)
        // - One of these gives the optimal configuration for exactly/at-most m
        DPResult resultAtLowPenalty = solvePenalizedDP(prefixSum, n, minLen, maxLen, low);
        long answer = resultAtLowPenalty.value + low * maxSubarrays;

        // Also check penalty = low - 1 (might give better answer)
        if (low > 0) {
            DPResult resultAtPrevPenalty = solvePenalizedDP(prefixSum, n, minLen, maxLen, low - 1);
            long candidateAnswer = resultAtPrevPenalty.value + (low - 1) * maxSubarrays;
            // Take minimum because we're essentially solving a dual problem
            answer = Math.min(answer, candidateAnswer);
        }

        return answer;
    }


    /**
     * Solves the penalized DP problem for a given penalty value.
     *
     * DP STATE DEFINITION: dpValue[i] = maximum (sum - penalty * count) using first i elements dpCount[i] = number of
     * subarrays used to achieve dpValue[i]
     *
     * DP TRANSITION: For each position i, we have two choices:
     *
     * 1. DON'T end a subarray at position i: dpValue[i] = dpValue[i-1] dpCount[i] = dpCount[i-1]
     *
     * 2. END a subarray at position i, starting from some position startPos ∈ [i-maxLen, i-minLen]: dpValue[i] =
     * dpValue[startPos] + (prefixSum[i] - prefixSum[startPos]) - penalty dpCount[i] = dpCount[startPos] + 1
     *
     * OPTIMIZATION - MONOTONIC DEQUE: For choice 2, we need to maximize: dpValue[startPos] + (prefixSum[i] -
     * prefixSum[startPos]) - penalty = (dpValue[startPos] - prefixSum[startPos]) + prefixSum[i] - penalty
     *
     * Define transformedValue[startPos] = dpValue[startPos] - prefixSum[startPos] Then we need: max
     * transformedValue[startPos] for startPos ∈ [i-maxLen, i-minLen]
     *
     * The monotonic deque maintains candidates in decreasing order of (transformedValue, count), allowing O(1)
     * retrieval of the best starting position.
     *
     * CRITICAL PYTHON MATCHING: - Python keeps dpv[0] = NEG (not 0!) - Base case handled by: basev, basec =
     * max((dpv[j], dpc[j]), (0, 0)) - This means: if dpv[j] < 0, use (0, 0) instead (allows starting first subarray)
     *
     * @param prefixSum prefix sum array
     * @param n array length
     * @param minLen minimum subarray length
     * @param maxLen maximum subarray length
     * @param penalty penalty value λ for each subarray selected
     * @return DPResult containing (penalized value, count of subarrays)
     */
    private DPResult solvePenalizedDP(long[] prefixSum, int n, int minLen, int maxLen, long penalty) {

        // DP arrays
        // CRITICAL: Initialize dpValue[0] as NEGATIVE_INFINITY (matching Python's dpv[0] = NEG)
        // This is NOT 0! The base case is handled through the max() logic below.
        long[] dpValue = new long[n + 1];  // dpValue[i] = max penalized value up to position i
        int[] dpCount = new int[n + 1];    // dpCount[i] = count of subarrays for dpValue[i]

        Arrays.fill(dpValue, NEGATIVE_INFINITY);
        // Note: dpCount is already initialized to 0 by default
        // Note: dpValue[0] remains as NEGATIVE_INFINITY, NOT set to 0!

        // Monotonic deque for range maximum query optimization
        // Each element is a triple: {transformedValue, subarrayCount, startIndex}
        // - transformedValue = dpValue[j] - prefixSum[j]
        // - subarrayCount = dpCount[j]
        // - startIndex = j (the actual position)
        //
        // Invariant: Deque maintains elements in decreasing order of (transformedValue, count)
        Deque<long[]> deque = new ArrayDeque<>();

        // Process each position from 1 to n
        for (int currentPos = 1; currentPos <= n; currentPos++) {

            // ==============================================================================
            // STEP 1: ADD NEW CANDIDATE to deque
            // ==============================================================================
            // startPos = currentPos - minLen is the rightmost valid starting position
            // for a subarray ending at currentPos (creates subarray of exactly minLen length)
            int startPos = currentPos - minLen;

            if (startPos >= 0) {
                // Extract DP state at position startPos
                long baseValue = dpValue[startPos];
                long baseCount = dpCount[startPos];

                // CRITICAL: Match Python's max((dpv[j], dpc[j]), (0, 0)) logic
                // This uses lexicographic tuple comparison:
                // - If (baseValue, baseCount) < (0, 0), use (0, 0) instead
                // - Since dpCount is always >= 0, this simplifies to: if baseValue < 0, use (0, 0)
                //
                // This allows starting the first subarray from a "clean slate" (0 value, 0 count)
                if (baseValue < 0 || (baseValue == 0 && baseCount < 0)) {
                    baseValue = 0;
                    baseCount = 0;
                }

                // Compute transformed value for this candidate
                // transformedValue = dpValue[startPos] - prefixSum[startPos]
                // This is the key quantity we need to maximize for the DP transition
                long transformedValue = baseValue - prefixSum[startPos];
                long candidateCount = baseCount;

                // MAINTAIN MONOTONIC DEQUE (decreasing order)
                // Remove candidates from back that are worse than or equal to current candidate
                //
                // Lexicographic comparison: (v1, c1) <= (v2, c2) means:
                // - v1 < v2, OR
                // - v1 == v2 AND c1 <= c2
                //
                // We remove elements where (last.value, last.count) <= (transformedValue, candidateCount)
                while (!deque.isEmpty()) {
                    long[] lastCandidate = deque.peekLast();
                    if (lastCandidate[0] < transformedValue ||
                            (lastCandidate[0] == transformedValue && lastCandidate[1] <= candidateCount)) {
                        deque.pollLast();  // Remove worse candidate
                    } else {
                        break;  // Found a better candidate, stop removing
                    }
                }

                // Add current candidate to deque
                deque.addLast(new long[]{transformedValue, candidateCount, startPos});
            }

            // ==============================================================================
            // STEP 2: REMOVE EXPIRED CANDIDATES from front
            // ==============================================================================
            // Valid starting positions for ending at currentPos: [currentPos-maxLen, currentPos-minLen]
            // If a candidate's index < currentPos-maxLen, the subarray would be too long (> maxLen)
            int minValidStartPos = currentPos - maxLen;
            while (!deque.isEmpty() && deque.peekFirst()[2] < minValidStartPos) {
                deque.pollFirst();  // Remove out-of-range candidate
            }

            // ==============================================================================
            // STEP 3: COMPUTE DP[currentPos] - Choose best between two options
            // ==============================================================================

            // OPTION 1: Don't end a subarray at currentPos (inherit from previous position)
            long bestValue = dpValue[currentPos - 1];
            int bestCount = dpCount[currentPos - 1];

            // OPTION 2: End a subarray at currentPos using best starting position from deque
            if (!deque.isEmpty()) {
                long[] bestCandidate = deque.peekFirst();  // Best candidate (maximum transformedValue)

                // Calculate penalized value if we end a subarray at currentPos
                // candidateValue = dpValue[startPos] + (prefixSum[currentPos] - prefixSum[startPos]) - penalty
                //                = (dpValue[startPos] - prefixSum[startPos]) + prefixSum[currentPos] - penalty
                //                = transformedValue + prefixSum[currentPos] - penalty
                long candidateValue = bestCandidate[0] + prefixSum[currentPos] - penalty;
                long candidateCount = bestCandidate[1] + 1;  // Increment subarray count

                // LEXICOGRAPHIC COMPARISON: Choose candidate if (candidateValue, candidateCount) > (bestValue, bestCount)
                // This means: candidateValue > bestValue, OR (candidateValue == bestValue AND candidateCount > bestCount)
                if (candidateValue > bestValue || (candidateValue == bestValue && candidateCount > bestCount)) {
                    bestValue = candidateValue;
                    bestCount = (int) candidateCount;
                }
            }

            // Store computed DP values for current position
            dpValue[currentPos] = bestValue;
            dpCount[currentPos] = bestCount;
        }

        // Return final answer: penalized value and count at position n
        return new DPResult(dpValue[n], dpCount[n]);
    }

    /**
     * Helper class to encapsulate DP result.
     *
     * Contains both the penalized value (sum - penalty * count) and the count of subarrays. This allows the solve
     * method to return both pieces of information.
     *
     * Equivalent to Python's tuple return: (value, count)
     */
    private static class DPResult {

        long value;  // Penalized DP value (sum - penalty * count)
        int count;   // Number of subarrays used to achieve this value

        /**
         * Constructor for DPResult.
         *
         * @param value the penalized DP value
         * @param count the number of subarrays selected
         */
        DPResult(long value, int count) {

            this.value = value;
            this.count = count;
        }
    }
}
