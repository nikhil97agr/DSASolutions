package Leetcode;

import java.util.*;

class LCWeekly441B {
    public List<Integer> solveQueries(int[] nums, int[] queries) {
        List<Integer> res = new ArrayList<>();

        Map<Integer, TreeSet<Integer>> map = new HashMap<>();
        int n = nums.length;
        for(int i=0;i<n;i++){
            int x = nums[i];

            map.computeIfAbsent(x, y -> new TreeSet<>()).add(i);
        }


        for(int i : queries){
            int val = nums[i];

            if(map.get(val).size() == 1){
                res.add(-1);
                continue;
            }
            int ans = Integer.MAX_VALUE;
            TreeSet<Integer> set = map.get(val);
            if(set.lower(i) != null){
                ans = min(ans, i - set.lower(i));
            }else{
                ans = min(ans, min(set.last() - i, i + (n - set.last())));
            }

            if(set.higher(i) != null){
                ans = min(ans, set.higher(i) - i);
            }else{
                ans = min(ans, min(i - set.first(), n - i + set.first()));
            }


            res.add(ans);
        }

        return res;
    }

    private int min(int a, int b){
        return Math.min(a,b);
    }
}