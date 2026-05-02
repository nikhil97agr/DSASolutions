package Leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

//Problem Link: https://leetcode.com/problems/maximum-total-reward-using-operations-ii/

/**
 * Solution for maximizing total reward using operations with constraints.
 *
 * Problem: Given an array of reward values, you can collect rewards with constraints: - Start with total reward = 0 -
 * For each reward value x you want to collect: * You can only collect x if x > current total reward * After collecting
 * x, your new total = old total + x - Each reward can be used at most once - Maximize the final total reward
 *
 * Key insight: Greedy + DP with backward construction
 *
 * Greedy observation: - The largest reward value should ALWAYS be included in the optimal solution - Why? If we can
 * collect any reward at some point, we can always save the largest for last - After sorting, rewardValues[n-1] is
 * guaranteed to be in the optimal solution
 *
 * DP state: - solve(nextValue) = maximum reward we can collect starting with total = 0, such that after all operations,
 * total + nextValue can be collected next - In other words: we want total < nextValue
 *
 * Why this formulation? - We know the last reward collected is rewardValues[n-1] - Before collecting it, we need total
 * < rewardValues[n-1] - So we find the maximum total we can achieve that is < rewardValues[n-1] - The answer is:
 * max_achievable_total + rewardValues[n-1]
 *
 * Constraint propagation: - If we collect reward r when total = t, new total = t + r - For the next reward x: we need x
 * > t + r, so x >= t + r + 1 - But we also want x < nextValue (to stay valid) - Combined: the next value we can use
 * must be < min(nextValue - r, r)
 *
 * Why min(nextValue - r, r)? - nextValue - r: ensures total + r + next_reward < nextValue - r: ensures next_reward < r
 * (otherwise we couldn't have collected r when total was less than r) - Actually: next value < r because after
 * collecting r, total >= r, so future rewards must be > total
 *
 * Example: rewardValues = [1, 6, 4, 3, 2] - Sort: [1, 2, 3, 4, 6] - Largest = 6 (always included) - Find max total < 6:
 * * Can collect 1 (0 < 1), total = 1 * Can collect 2 (1 < 2), total = 3 * Can collect 4 (3 < 4), total = 7... wait,
 * this exceeds 6! * So we need total = 5 max * Collect 1: total = 1 * Collect 4: total = 5 - Final: 5 + 6 = 11
 *
 * Time Complexity: O(n² * max_value) with memoization, typically much faster Space Complexity: O(max_value) for
 * memoization map
 */
public class MaximumTotalRewardUsingOperationsII {

    /**
     * Finds the maximum total reward achievable.
     *
     * @param rewardValues Array of available reward values
     * @return Maximum total reward
     */
    public int maxTotalReward(int[] rewardValues) {

        // ================================================================
        // STEP 1: Sort rewards to enable greedy strategy
        // ================================================================
        Arrays.sort(rewardValues);
        var dp = new HashMap<Integer, Integer>();

        var n = rewardValues.length;

        // ================================================================
        // STEP 2: Greedy insight - always include the largest reward
        // ================================================================
        // The largest reward can always be collected last
        // We need to find the maximum total we can achieve before collecting it

        // Convert to set for O(1) lookup
        Set<Integer> set = Arrays.stream(rewardValues).boxed().collect(Collectors.toSet());

        // Find max total we can achieve that is < rewardValues[n-1]
        // Then add rewardValues[n-1] to get final answer
        return rewardValues[n - 1] + solve(rewardValues[n - 1] - 1, rewardValues, set, dp);
    }

    /**
     * Recursively finds the maximum total reward achievable with constraint.
     *
     * State: nextValue = the next reward value we want to be able to collect Goal: Find maximum total reward we can
     * achieve such that total < nextValue
     *
     * This ensures that after achieving this total, we can still collect nextValue.
     *
     * Base cases: 1. nextValue == 0: Can't collect anything (need reward > 0), return 0 2. nextValue exists in set: We
     * can directly collect it as first reward, return nextValue
     *
     * Recursive case: - Try collecting each available reward nums[i] where nums[i] < nextValue - After collecting
     * nums[i], total becomes nums[i] + (previous total) - For the next reward to be valid, it must be > new total -
     * Also, the next reward must be < nums[i] (constraint from problem logic) - Combined constraint: next value <
     * min(nextValue - nums[i], nums[i])
     *
     * Why min(nextValue - nums[i], nums[i] - 1)? - nextValue - nums[i]: Ensures new_total + next_reward < nextValue -
     * nums[i] - 1: After collecting nums[i], we can only collect rewards > total Since total >= nums[i], future rewards
     * must be > nums[i] But wait, that doesn't make sense...
     *
     * Actually, the logic is: - We collect nums[i] first, so total = nums[i] - Next reward must be > nums[i] (to
     * satisfy constraint) - But we're building backwards, so we want the maximum we can achieve BEFORE collecting
     * nums[i] - If we achieve value x before collecting nums[i], then total after = x + nums[i] - We need x + nums[i] <
     * nextValue, so x < nextValue - nums[i] - Also, to have collected value before nums[i], each prior reward must have
     * been < nums[i] - So the maximum we can achieve before nums[i] is bounded by nums[i] - 1
     *
     * @param nextValue The value we want to be able to collect next
     * @param nums Sorted array of reward values
     * @param set Set of reward values for O(1) lookup
     * @param dp Memoization map
     * @return Maximum total achievable such that total < nextValue
     */
    private int solve(int nextValue, int nums[], Set<Integer> set, Map<Integer, Integer> dp) {

        // ================================================================
        // Memoization check
        // ================================================================
        if (dp.containsKey(nextValue)) {
            return dp.get(nextValue);
        }

        // ================================================================
        // BASE CASES
        // ================================================================

        // Base case 1: nextValue is 0, can't collect any reward
        // (All rewards must be positive and greater than current total)
        if (nextValue == 0) {
            return 0;
        }

        // Base case 2: nextValue is directly available as a reward
        // We can collect it as the first reward (when total = 0)
        if (set.contains(nextValue)) {
            return nextValue;
        }

        // ================================================================
        // RECURSIVE CASE: Try all valid rewards
        // ================================================================

        // Find all rewards < nextValue using binary search
        int ind = binarySearch(nums, nextValue);

        int ans = 0;

        // Try each reward nums[i] where nums[i] < nextValue
        for (int i = 0; i <= ind; i++) {
            // Option: Collect nums[i] at some point
            // After collecting nums[i], we need to have achieved some total x
            // such that x + nums[i] is what we have before collecting nextValue
            // Constraint: x + nums[i] < nextValue, so x < nextValue - nums[i]
            // Also: x < nums[i] (to be able to collect nums[i] when total was x)
            // Therefore: x < min(nextValue - nums[i], nums[i])

            // Recursively find max total achievable with new constraint
            int nextMax = Math.min(nextValue - nums[i], nums[i] - 1);
            ans = Math.max(ans, nums[i] + solve(nextMax, nums, set, dp));
        }

        // ================================================================
        // Memoize and return
        // ================================================================
        dp.put(nextValue, ans);

        return ans;
    }

    /**
     * Binary search to find the rightmost index where nums[index] < val.
     *
     * Finds the largest index i such that nums[i] < val. Returns -1 if no such index exists (all elements >= val).
     *
     * This is used to find all rewards that are less than a given value.
     *
     * Example: nums = [1, 2, 3, 4, 6], val = 5 - Returns 3 (nums[3] = 4 < 5, but nums[4] = 6 >= 5)
     *
     * Example: nums = [1, 2, 3, 4, 6], val = 1 - Returns -1 (no element < 1)
     *
     * @param nums Sorted array of integers
     * @param val Value to compare against
     * @return Rightmost index where nums[index] < val, or -1 if none exists
     */
    private int binarySearch(int nums[], int val) {

        int ind = -1;  // Result: rightmost index with nums[ind] < val
        int start = 0;
        int end = nums.length - 1;

        while (start <= end) {
            int mid = (start + end) / 2;

            if (nums[mid] < val) {
                // Found a valid index, record it
                ind = mid;
                // Search right for potentially larger valid index
                start = mid + 1;
            } else {
                // nums[mid] >= val, search left
                end = mid - 1;
            }
        }

        return ind;
    }
}