package Leetcode;

import java.util.*;

public class LCBiweekly123C {
    public long maximumSubarraySum(int[] nums, int k) {
        long ans = Long.MIN_VALUE;
        int n = nums.length;
        long pref[] = new long[n+2];
        pref[0] = 0;
        for(int i=1;i<=n;i++){
            pref[i] = pref[i-1] + nums[i-1];
        }

        Map<Long, Long> map = new HashMap<>();
        for(int i=n-1;i>=0;i--){
            long x = nums[i];
            System.out.println(map);
            System.out.println(x+":"+(x+k)+":"+(x-k));
            if(map.containsKey(x+k)){
                ans = Math.max(ans, map.get(x+k) - pref[i+1] + x);
            }
            if(map.containsKey(x-k)){
                ans = Math.max(ans, map.get(x-k) - pref[i+1] + x);
            }

            map.put(x, Math.max(map.getOrDefault(x, Long.MIN_VALUE), pref[i+1]));
        }
        
        return ans==Long.MIN_VALUE ? 0 : ans;
    }
}