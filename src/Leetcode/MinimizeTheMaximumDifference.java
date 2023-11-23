package Leetcode;

import java.util.Arrays;

//Problem Link : https://leetcode.com/problems/minimize-the-maximum-difference-of-pairs

public class MinimizeTheMaximumDifference {
    public int minimizeMax(int[] nums, int p) {
        Arrays.sort(nums);
        int n = nums.length;
        int start = 0;
        int end = nums[n - 1] - nums[0]; // maximum difference possible among all the pairs

        while (start < end) {
            int mid = start + (end - start) / 2;

            if (isPossiblePairs(nums, mid, p, n)) { // check if P pairs are possible with mid as maximum difference
                end = mid;
            } else {
                start = mid + 1;
            }
        }
        return start;
    }

    private boolean isPossiblePairs(int nums[], int mid, int p, int n) {
        int total = 0;

        for (int i = 0; i < n - 1 && total < p; i++) {
            if (nums[i + 1] - nums[i] <= mid) {
                i++;
                total++;
            }
        }

        return total >= p;
    }
}
