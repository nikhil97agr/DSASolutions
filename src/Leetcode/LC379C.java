package Leetcode;

import java.util.HashMap;
import java.util.Map;

class LC379C {
    public int maximumSetSize(int[] nums1, int[] nums2) {
        Map<Integer, Integer> m1 = new HashMap<>();
        Map<Integer, Integer> m2 = new HashMap<>();
        for(int x : nums1) m1.put(x, m1.getOrDefault(x, 0)+1);
        for(int x : nums2) m2.put(x, m2.getOrDefault(x, 0)+1);
        int n = nums1.length;


        int ans = 0;
        int cnt1 = 0;
        int cnt2 = 0;

        int common = 0;
        for(Map.Entry<Integer, Integer> entry : m1.entrySet()){
            if(m2.containsKey(entry.getKey())){
                common++;
            }else{
                cnt1++;
            }
        }

        for(Map.Entry<Integer, Integer> entry : m2.entrySet()){
            if(!m1.containsKey(entry.getKey())){
                cnt2++;
            }
        }
        int max1 = Math.min(cnt1, n/2);
        int max2 = Math.min(cnt2, n/2);

        if(max1 + max2 ==n) return max1+max2;

        return max1  + max2 + Math.min(common, n - (max1 + max2));






    }
}
