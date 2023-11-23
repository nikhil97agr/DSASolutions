package Leetcode;

// Problem Link : https://leetcode.com/problems/minimum-replacements-to-sort-the-array/

public class MinReplacementSortArray {
    public long minimumReplacement(int[] nums) {
        int n = nums.length;
        int prev = nums[n - 1];
        long total = 0;

        for (int i = n - 2; i >= 0; i--) {
            if (nums[i] > prev) {
                int steps = nums[i]/ prev;
                if (nums[i] % prev != 0) {
                    steps++;
                }

                total += steps - 1;
                prev = nums[i] / steps;
            } else {
                prev = nums[i];
            }
        }
        return total;
    }
}
