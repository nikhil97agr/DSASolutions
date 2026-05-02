package Leetcode;

import java.util.Arrays;
import java.util.PriorityQueue;

//Problem Link: https://leetcode.com/problems/earliest-second-to-mark-indices-ii

/**
 * Solution for finding the earliest second to mark all indices.
 *
 * Problem: Given an array nums and a sequence changeIndices, at each second i (1-indexed): 1. You can perform ONE of
 * these actions: a. Decrement nums[j] by 1 for any j (if nums[j] > 0) b. Mark index changeIndices[i] (only if
 * nums[changeIndices[i]] == 0) 2. Goal: Find the earliest second by which all indices can be marked
 *
 * Constraints: - An index can only be marked when its value is 0 - At second i, you can mark index changeIndices[i] (if
 * its value is 0) - You must mark all n indices
 *
 * Key insights: 1. Binary search on the answer (earliest second) 2. For a given time limit, greedily determine if it's
 * feasible 3. Strategy: For each index, use the FIRST opportunity to mark it (earliest occurrence in changeIndices) 4.
 * Allocate remaining seconds to decrement operations 5. Greedy choice: Skip marking indices with small values and
 * decrement them manually instead
 *
 * Algorithm: 1. Binary search on seconds [0, m] 2. For each candidate time, check feasibility: - Process seconds in
 * REVERSE (from mid-1 to 0) - Track "free" seconds that can be used for decrements - At first occurrence of each index,
 * decide: mark it (costs 1 extra decrement) or skip - Use min-heap to greedily skip indices with smallest values 3.
 * Check if remaining decrements are sufficient
 *
 * Example: nums = [2,2,0], changeIndices = [1,2,1,2,1,2,1,2] - Need to reduce nums[0] from 2→0, nums[1] from 2→0 - Can
 * mark at positions where changeIndices points to the index - Total operations: 2 + 2 + 3 marks = 7 seconds needed
 *
 * Time Complexity: O(m log m log n) - binary search × check with priority queue Space Complexity: O(n) for auxiliary
 * arrays and priority queue
 */
public class EarliestSecondToMarkIndicesII {

    /**
     * Finds the earliest second by which all indices can be marked.
     *
     * @param nums Array where nums[i] needs to be reduced to 0 before marking
     * @param changeIndices Array where changeIndices[i] is the index that can be marked at second i+1
     * @return Earliest second to mark all indices, or -1 if impossible
     */
    public int earliestSecondToMarkIndices(int[] nums, int[] changeIndices) {

        int n = nums.length;  // Number of indices to mark
        int m = changeIndices.length;  // Number of seconds available
        int ans = -1;  // Result: earliest second (-1 if impossible)

        // Calculate total decrements needed if we manually decrement all indices
        long sum = 0;
        for (int x : nums) {
            sum += x;
        }

        // Find the first occurrence of each index in changeIndices
        // firstChange[i] = earliest second where we can mark index i
        int firstChange[] = new int[n];
        Arrays.fill(firstChange, -1);

        for (int i = 0; i < m; i++) {
            int ind = changeIndices[i] - 1;  // Convert to 0-indexed

            // Only record first occurrence for indices that need marking (nums[ind] > 0)
            // Indices with nums[ind] = 0 don't need marking opportunities
            if (nums[ind] > 0 && firstChange[ind] == -1) {
                firstChange[ind] = i;
            }
        }

        // Binary search on the answer
        int l = 0;
        int r = m;

        while (l <= r) {
            var mid = (l + r) / 2;

            // Check if we can mark all indices within 'mid' seconds
            if (check(nums, changeIndices, mid, firstChange, sum)) {
                ans = mid;
                r = mid - 1;  // Try to find earlier solution
            } else {
                l = mid + 1;  // Need more time
            }
        }

        return ans;
    }

    /**
     * Checks if all indices can be marked within 'mid' seconds.
     *
     * Strategy (greedy approach - process in REVERSE): 1. Process seconds from mid-1 down to 0 2. Track "mark" = number
     * of free seconds available for decrements 3. At each first occurrence of an index: - Option A: Use this second to
     * mark the index (requires nums[ind] prior decrements) - Option B: Skip marking here, decrement manually later
     * (costs nums[ind] seconds) 4. Use a min-heap to greedily skip indices with smallest values
     *
     * Why process in reverse? - We want to identify the first (earliest) marking opportunity for each index -
     * Processing backwards lets us see all marking opportunities before deciding - We can greedily choose which indices
     * to mark at their first opportunity
     *
     * Greedy choice: - Keep large-value indices in the heap (mark them to save operations) - Remove small-value indices
     * from heap (decrement them manually) - This minimizes total operations needed
     *
     * @param nums Original array values
     * @param indices changeIndices array
     * @param mid Time limit (number of seconds)
     * @param firstChange First occurrence of each index
     * @param sum Total sum of all nums values
     * @return true if feasible within mid seconds, false otherwise
     */
    private boolean check(int nums[], int indices[], int mid, int firstChange[], long sum) {

        // Min-heap: stores nums[i] for indices we plan to mark
        // Smallest values are at the top (candidates for removal)
        var que = new PriorityQueue<Integer>();

        // mark = number of "free" seconds available for decrement operations
        int mark = 0;

        // Process seconds in reverse order (from mid-1 down to 0)
        for (int start = mid - 1; start >= 0; start--) {
            int ind = indices[start] - 1;  // Index that can be marked at this second

            // Check if this is the FIRST (earliest) opportunity to mark index 'ind'
            if (firstChange[ind] == start) {
                // This is the first chance to mark this index

                // Add to heap: consider marking this index
                que.offer(nums[ind]);

                // Decision: Should we mark this index or skip it?
                if (mark > 0) {
                    // We have a free second available
                    // Use it for marking this index (consumes 1 second)
                    mark--;
                } else {
                    // No free seconds available
                    // Skip the smallest value index (remove from heap)
                    // This means we'll decrement it manually instead of marking
                    mark++;  // Gain 1 second by not marking the smallest
                    que.poll();  // Remove smallest value from heap
                }
            } else {
                // Not a first occurrence, this second is "free"
                // Can be used for decrement operations
                mark++;
            }
        }

        // Calculate final feasibility check:
        //
        // sum = total decrements needed if we manually decrement everything
        // s2 = sum of values for indices we'll mark (saved decrements)
        // que.size() = number of indices we'll mark
        //
        // Operations needed:
        // - Decrements: (sum - s2) = total decrements minus saved ones
        // - Marks: (nums.length - que.size()) = indices not in heap need manual decrement+mark
        //   Actually, this should be interpreted as:
        //   - We have n indices total
        //   - que.size() indices will be marked (these need nums[i] decrements each, already in s2)
        //   - (n - que.size()) indices won't be marked, need to be decremented to 0
        //
        // Total operations: (sum - s2) + (n - que.size())
        // Available seconds: mark
        long s2 = que.stream().mapToLong(x -> x).sum();

        // Check: Can we do all required operations within available seconds?
        // (sum - s2) = decrements for indices we're NOT marking
        // (nums.length - que.size()) = number of marks still needed
        return sum - s2 + nums.length - que.size() <= mark;
    }
}