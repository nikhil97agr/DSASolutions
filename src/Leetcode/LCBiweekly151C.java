package Leetcode;

import java.util.HashMap;
import java.util.Map;

public class LCBiweekly151C {
    public int minCost(int[] nums) {
        int n = nums.length;
        Map<Integer, Map<Integer, Integer>> dp = new HashMap<>();

        return solve(0, nums, nums[0],n, dp);
    }

    private int solve(int i, int nums[], int prev, int n, Map<Integer, Map<Integer, Integer>> dp){
        if(i== n-1) return nums[i];
        if(i==n-2) return Math.max(nums[i], nums[i+1]);

        int val = dp.getOrDefault(i, new HashMap<>()).getOrDefault(prev, -1);

        if(val !=-1) return val;

        int ans = Integer.MAX_VALUE;

        int first = nums[i];
        int second = nums[i+1];
        int third = nums[i+2];

        nums[i+2] = first;
        ans = Math.min(ans, Math.max(second, third) + solve(i+2, nums, first, n, dp));
        nums[i+2] = second;
        ans = Math.min(ans, Math.max(third, first) + solve(i+2, nums, second, n, dp));
        nums[i+2] = third;
        ans = Math.min(ans, Math.max(first, second) + solve(i+2, nums, third, n, dp));

        dp.computeIfAbsent(i, x -> new HashMap<>()).put(prev, ans);
        return ans;
    }
}