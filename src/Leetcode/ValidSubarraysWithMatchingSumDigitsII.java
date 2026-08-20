package Leetcode;

import java.util.ArrayList;
import java.util.List;

//Problem Link: https://leetcode.com/problems/valid-subarrays-with-matching-sum-digits-ii/

public class ValidSubarraysWithMatchingSumDigitsII {

    public int countValidSubarrays(int[] nums, int x) {

        // Maximum possible sum to prevent overflow
        long maxSum = 1_000_000_000_000_00L;

        // Store prefix sums grouped by their last digit (0-9)
        List<List<Long>> reminders = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            reminders.add(new ArrayList<>());
        }

        // Initialize with prefix sum 0 (before any elements)
        reminders.get(0).add(0l);
        long pre = 0;  // Current prefix sum
        int ans = 0;   // Count of valid subarrays

        for (int y : nums) {
            pre += y;  // Update prefix sum

            // Calculate what the last digit of previous prefix sum should be
            // If current prefix sum has last digit d, and we want subarray sum's last digit to be x,
            // then previous prefix sum's last digit should be (d - x) % 10
            int targetLastDigit = (int) ((pre - x + 10) % 10);
            List<Long> list = reminders.get(targetLastDigit);

            // Try all possible first digits (by checking different magnitudes)
            long power10 = 1;
            while (power10 <= pre) {
                // For first digit to be x, the subarray sum should be in range [x * power10, (x+1) * power10 - 1]
                // subarray sum = pre - previous_prefix_sum
                // So: x * power10 <= pre - prev <= (x+1) * power10 - 1
                // Rearranging: pre - (x+1) * power10 + 1 <= prev <= pre - x * power10
                long higher = pre - x * power10;        // Upper bound for previous prefix sum
                long lower = pre - ((x + 1) * power10 - 1);  // Lower bound for previous prefix sum

                // Count how many previous prefix sums fall in the valid range
                int lowerCount = bsLeft(list, lower);
                int higherCount = bsRight(list, higher);
                ans += (higherCount - lowerCount);

                if (power10 > maxSum) {
                    break;
                }
                power10 *= 10;  // Move to next magnitude (check next digit position)

            }

            // Add current prefix sum to the appropriate bucket based on its last digit
            int dig = (int) (pre % 10);
            reminders.get(dig).add(pre);

        }

        return ans;
    }

    // Binary search to find count of elements <= target (right boundary inclusive)
    private int bsRight(List<Long> list, long target) {

        int start = 0;
        int end = list.size() - 1;
        int cnt = 0;

        while (start <= end) {
            int mid = (start + end) / 2;
            if (list.get(mid) <= target) {
                cnt = mid + 1;  // Count all elements up to and including mid
                start = mid + 1;
            } else {
                end = mid - 1;
            }
        }

        return cnt;
    }

    // Binary search to find count of elements < target (left boundary exclusive)
    private int bsLeft(List<Long> list, long target) {

        int start = 0;
        int end = list.size() - 1;
        int cnt = 0;

        while (start <= end) {
            int mid = (start + end) / 2;

            if (list.get(mid) < target) {
                cnt = mid + 1;  // Count all elements strictly less than target
                start = mid + 1;
            } else {
                end = mid - 1;
            }
        }

        return cnt;
    }
}
