package Leetcode;

//Problem Link :https://leetcode.com/problems/maximum-score-of-a-good-subarray

public class MaxScoreGoodSubarrya {
    public int maximumScore(int[] nums, int k) {
        int n = nums.length;
        int min[] = new int[n];
        min[k] = nums[k];
        for (int i = k - 1; i >= 0; i--) {
            min[i] = Math.min(min[i + 1], nums[i]);
        }
        for (int i = k + 1; i < n; i++) {
            min[i] = Math.min(min[i - 1], nums[i]);
        }

        int ans = nums[k];

        int i = 0, j = n - 1;
        while (i < k && j > k) {
            int small = Math.min(min[i], min[j]);
            ans = Math.max(ans, small * (j - i + 1));
            if (small == min[i]) {
                i++;
            } else {
                j--;
            }
        }
        while (i < k) {
            int small = Math.min(nums[j], min[i]);
            ans = Math.max(ans, small * (j - i + 1));
            i++;
        }

        while (j > k) {
            int small = Math.min(nums[i], min[j]);
            ans = Math.max(ans, small * (j - i + 1));
            j--;
        }
        return ans;
    }
}
