package Leetcode;

import java.util.TreeMap;

public class CountIntervals {
    int ans;
    TreeMap<Integer, Integer> map;
    public CountIntervals() {
        ans = 0;
        map = new TreeMap<>();
    }

    public void add(int left, int right) {
        boolean added= false;
        while(map.ceilingKey(left)!=null){
            int key = map.ceilingKey(left);
            if(key > right){
                break;
            }

            int val = map.get(key);
            ans -= (val - key + 1);
            right = Math.max(right, val);
            map.remove(key);
        }

        while(map.floorKey(left)!=null){
            int key = map.floorKey(left);

            int val = map.get(key);

            if(val < left){
                break;
            }
            ans -= (val - key+ 1);
            left= Math.min(left, key);

            right = Math.max(right, val);
            map.remove(key);


        }

        ans += (right - left + 1);
        map.put(left, right);
    }



    public int count() {
        return ans;
    }
}

/**
 * Your CountIntervals object will be instantiated and called as such:
 * CountIntervals obj = new CountIntervals();
 * obj.add(left,right);
 * int param_2 = obj.count();
 */