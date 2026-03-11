package Leetcode;

import java.util.Arrays;

//Problem Link: https://leetcode.com/problems/maximum-average-subarray-ii/description

public class MaximumAverageSubarrayII {

    public double findMaxAverage(int[] nums, int k) {

        if (k == 1) {
            return Arrays.stream(nums).max().getAsInt();
        }

        double min = Arrays.stream(nums).min().getAsInt();
        double max = Arrays.stream(nums).max().getAsInt();

        while (max - min > 1e-5) {
            double mid = (min + max) / 2;
            if (check(mid, nums, k)) {
                min = mid;
            } else {
                max = mid;
            }
        }

        return min;
    }

    private boolean check(double mid, int nums[], int k) {

        double transform[] = new double[nums.length];

        for (int i = 0; i < nums.length; i++) {
            transform[i] = nums[i] - mid;
        }

        double sum = 0;
        double min = 0;
        double outsideWindow = 0;
        for (int i = 0; i < k; i++) {
            sum += transform[i];
        }

        if (sum >= 0) {
            return true;
        }

        for (int i = k; i < nums.length; i++) {
            sum += transform[i];
            outsideWindow += transform[i - k];
            min = Math.min(min, outsideWindow);
            if (sum >= min) {
                return true;
            }
        }

        return false;
    }
}