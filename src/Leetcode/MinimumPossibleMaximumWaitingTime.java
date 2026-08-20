package Leetcode;
//Problem Link: https://leetcode.com/problems/minimum-possible-maximum-waiting-time/description/

public class MinimumPossibleMaximumWaitingTime {

    /**
     * Counts the number of unfinished tasks after each shift.
     *
     * Algorithm Overview: - Tasks must be processed sequentially from left to right - If tasks aren't completed during
     * a shift, they carry over to the next shift - If all tasks are completed, the next shift restarts from task 0 -
     * Uses prefix sum array for efficient range sum queries - Uses binary search to find how far we can progress in a
     * shift
     *
     * Time Complexity: O(n + m*log(n)) where n = tasks.length, m = shifts.length Space Complexity: O(n) for the prefix
     * sum array
     *
     * @param tasks Array where tasks[i] is the time required for the ith task
     * @param shifts Array where shifts[j] is the time available during the jth shift
     * @return Array where ans[j] is the number of unfinished tasks after the jth shift
     */
    public int[] countTasks(int[] tasks, int[] shifts) {

        int n = tasks.length;
        int m = shifts.length;
        int[] ans = new int[m];

        // Build prefix sum array for efficient range sum queries
        // pre[i] = sum of tasks[0...i]
        long[] prefixSum = new long[n];
        prefixSum[0] = tasks[0];
        for (int i = 1; i < n; i++) {
            prefixSum[i] = prefixSum[i - 1] + tasks[i];
        }

        // State tracking variables
        boolean isPartialTaskInProgress = false; // True if current task is partially completed
        long timeLeftOnCurrentTask = 0;          // Remaining time needed for current partial task
        int currentTaskIndex = 0;                // Index of current task being processed

        // Process each shift
        for (int shiftIdx = 0; shiftIdx < m; shiftIdx++) {
            long availableTime = shifts[shiftIdx];

            // Calculate total time needed to complete all remaining tasks
            long totalRemainingWork;
            if (isPartialTaskInProgress) {
                // Current partial task + all subsequent tasks
                totalRemainingWork = timeLeftOnCurrentTask + getRangeSum(prefixSum, currentTaskIndex + 1, n - 1);
            } else {
                // All tasks from current index onwards
                totalRemainingWork = getRangeSum(prefixSum, currentTaskIndex, n - 1);
            }

            // Case 1: We can complete all remaining tasks and have time left over
            // This means the shift ends early and we restart from task 0
            if (totalRemainingWork <= availableTime) {
                isPartialTaskInProgress = false;
                ans[shiftIdx] = 0;
                timeLeftOnCurrentTask = 0;
                currentTaskIndex = 0;
                continue;
            }

            // Case 2: We have a partial task in progress
            if (isPartialTaskInProgress) {
                // Sub-case 2a: Shift time exactly completes the partial task
                if (availableTime == timeLeftOnCurrentTask) {
                    ans[shiftIdx] = (n - 1) - currentTaskIndex; // Tasks after current one
                    isPartialTaskInProgress = false;
                    timeLeftOnCurrentTask = 0;
                    currentTaskIndex = (currentTaskIndex + 1) % n;
                    continue;
                }

                // Sub-case 2b: Shift time is insufficient to complete the partial task
                if (availableTime < timeLeftOnCurrentTask) {
                    ans[shiftIdx] = n - currentTaskIndex; // Current task + all after it
                    timeLeftOnCurrentTask -= availableTime;
                    // isPartialTaskInProgress remains true
                    continue;
                }

                // Sub-case 2c: Complete partial task and continue with more tasks
                availableTime -= timeLeftOnCurrentTask;
                currentTaskIndex++; // Move to next task after completing the partial one

                // Use binary search to find where we end up after using remaining time
                int endTaskIndex = binarySearch(prefixSum, availableTime, currentTaskIndex, n);
                long timeUsed = getRangeSum(prefixSum, currentTaskIndex, endTaskIndex);

                if (timeUsed == availableTime) {
                    // Exactly completed up to endTaskIndex
                    isPartialTaskInProgress = false;
                    currentTaskIndex = (endTaskIndex + 1) % n;
                    ans[shiftIdx] = (n - 1) - endTaskIndex;
                    timeLeftOnCurrentTask = 0;
                } else {
                    // Partially completed endTaskIndex
                    currentTaskIndex = endTaskIndex;
                    timeLeftOnCurrentTask = timeUsed - availableTime;
                    ans[shiftIdx] = n - endTaskIndex;
                }
            } else {
                // Case 3: Starting fresh (no partial task in progress)
                // Use binary search to find how far we can get with available time
                int endTaskIndex = binarySearch(prefixSum, availableTime, currentTaskIndex, n);
                long timeUsed = getRangeSum(prefixSum, currentTaskIndex, endTaskIndex);

                if (timeUsed == availableTime) {
                    // Exactly completed up to endTaskIndex
                    currentTaskIndex = (endTaskIndex + 1) % n;
                    ans[shiftIdx] = (n - 1) - endTaskIndex;
                } else {
                    // Partially completed endTaskIndex
                    isPartialTaskInProgress = true;
                    currentTaskIndex = endTaskIndex;
                    timeLeftOnCurrentTask = timeUsed - availableTime;
                    ans[shiftIdx] = n - endTaskIndex;
                }
            }
        }

        return ans;
    }

    /**
     * Performs binary search to find the furthest task index that can be reached with the given amount of time.
     *
     * Searches for the smallest index where the sum of tasks from 'start' to that index is greater than or equal to
     * 'value'.
     *
     * @param prefixSum Prefix sum array of task times
     * @param value Amount of time available
     * @param start Starting task index (inclusive)
     * @param n Total number of tasks
     * @return Index of the task where we end up (may be partially complete)
     */
    private int binarySearch(long[] prefixSum, long value, int start, int n) {

        int left = start;
        int right = n - 1;
        int originalStart = start;
        int resultIndex = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2; // Avoid potential overflow

            long sumInRange = getRangeSum(prefixSum, originalStart, mid);

            if (sumInRange >= value) {
                // We can reach at least 'mid', but check if we can go further left
                resultIndex = mid;
                right = mid - 1;
            } else {
                // Need to go further right
                left = mid + 1;
            }
        }

        return resultIndex;
    }

    /**
     * Calculates the sum of tasks in a given range using prefix sum array.
     *
     * This is a standard prefix sum range query: - sum[start...end] = prefixSum[end] - prefixSum[start-1] - Special
     * cases handled: empty range, range starting at index 0
     *
     * @param prefixSum Prefix sum array where prefixSum[i] = sum of tasks[0...i]
     * @param start Starting index (inclusive)
     * @param end Ending index (inclusive)
     * @return Sum of task times in the range [start, end], or 0 if range is invalid
     */
    private long getRangeSum(long[] prefixSum, int start, int end) {

        if (start > end) {
            return 0; // Invalid range
        }
        if (start == 0) {
            return prefixSum[end]; // Range from beginning
        }
        return prefixSum[end] - prefixSum[start - 1]; // Standard range query
    }
}