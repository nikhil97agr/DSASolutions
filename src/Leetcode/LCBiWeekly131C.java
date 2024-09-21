package Leetcode;

import java.util.HashMap;
import java.util.Map;

public class LCBiWeekly131C{
    public int[] queryResults(int limit, int[][] queries) {
        Map<Integer, Integer> map = new HashMap<>();

        Map<Integer, Integer> color = new HashMap<>();
        int n = queries.length;
        int ans[] = new int[n];
        int total = limit+1;
        for(int i=0;i<n;i++){
            int q[] = queries[i];

            if(color.containsKey(q[0])){
                removeFromMap(color.get(q[0]), map);
                color.put(q[0], q[1]);
                addToMap(color.get(q[0]), map);
            }else{
                color.put(q[0], q[1]);
                addToMap(q[1], map);
            }

            ans[i] = map.size();

        }
        return ans;
    }

    private  void addToMap(int val, Map<Integer, Integer> map){
        map.put(val, map.getOrDefault(val, 0)+1);
    }

    private  void removeFromMap(int val, Map<Integer, Integer> map){
        int count = map.get(val);
        if(count==1) map.remove(val);
        else map.put(val, count-1);
    }
}