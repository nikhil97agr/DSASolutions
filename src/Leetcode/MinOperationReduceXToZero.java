package Leetcode;

//Problem Link : https://leetcode.com/problems/minimum-operations-to-reduce-x-to-zero/

import java.util.Arrays;

public class MinOperationReduceXToZero {
    public int minOperations(int[] nums, int x) {
        int total = Arrays.stream(nums).sum();
        int n = nums.length;
        // if whole array sums to x then we have to remove every element to make it zero
        if (total == x)
            return n;

        /*
         * if the whole array to not even able to sum to x then its not possible to make
         * x zero even after removing all the elemnts
         */
        if (total < x)
            return -1;
        // if we have some corner elements sum to x then rest of middle elements will
        // result (total-x)
        total = total - x;

        int left = 0;
        int right = 0;
        int curr = 0;
        int max = 0;

        // Finding the longest subarray which will sum to (sum(nums) - x)
        while (right < n) {
            curr += nums[right];

            while (left <= right && curr > total) {
                curr -= nums[left++];
            }

            if (curr == total) {
                max = Math.max(max, (right - left + 1));
            }
            right++;
        }

        return max == 0 ? -1 : n - max;
    }
}