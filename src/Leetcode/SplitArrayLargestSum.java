package Leetcode;

import java.util.Arrays;

//Problem Link : https://leetcode.com/problems/split-array-largest-sum/

public class SplitArrayLargestSum {

    public int splitArray(int[] nums, int k) {
        int total = Arrays.stream(nums).sum();
        int max = Arrays.stream(nums).max().getAsInt();
        int low = max;
        int high = total;
        int ans = 0;
        int n = nums.length;
        while (low <= high) {
            int mid = low + (high - low) / 2;

            int cnt = cntPartition(nums, n, mid);

            if (cnt > k) {
                low = mid + 1;
            } else {
                ans = mid;
                high = mid - 1;
            }
        }
        return ans;
    }

    private int cntPartition(int nums[], int n, int sum) {
        int cnt = 1;
        int total = 0;
        for (int i = 0; i < n; i++) {

            total += nums[i];
            if (total > sum) {
                cnt++;
                total = nums[i];
            }
        }
        return cnt;
    }

}
