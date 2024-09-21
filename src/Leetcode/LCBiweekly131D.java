package Leetcode;

import java.util.*;

public class LCBiweekly131D {
    public List<Boolean> getResults(int[][] queries) {
        List<Boolean> result = new ArrayList<>();
        TreeSet<Integer> set = new TreeSet<>();
        Map<Integer, Integer> pointToDistMap = new HashMap<>();
        set.add(0);

        TreeMap<Integer, TreeSet<Integer>> map = new TreeMap<>();
        for(int q[] : queries){

            if(q[0] == 1){
                Integer lower = set.floor(q[1]);
                Integer higher = set.ceiling(q[1]);

                pointToDistMap.put(q[1], q[1] - lower);

                map.computeIfAbsent(q[1]-lower, dist -> new TreeSet<>()).add(q[1]);

                if(higher != null){
                    int dist = higher - q[1];
                    int prev =pointToDistMap.get(higher);
                    map.get(prev).remove(higher);
                    if(map.get(prev).isEmpty()) map.remove(prev);
                    map.computeIfAbsent(dist, d -> new TreeSet<>()).add(higher);
                    pointToDistMap.put(higher, dist);
                }
                set.add(q[1]);
                continue;
            }

            int x = q[1];
            int sz = q[2];

            Integer key = map.ceilingKey(sz);
            if(key == null){
                if( x >= set.last() + sz){
                    result.add(true);
                }else{
                    result.add(false);
                }
                continue;
            }

            result.add(search(x, sz, map.tailMap(sz), set));

        }
        return result;
    }

    private boolean search(int x, int sz, SortedMap<Integer, TreeSet<Integer>> map, TreeSet<Integer> set){
        for(TreeSet<Integer> s  : map.values()){
            if(s.first() <= x){
                return true;
            }
        }

        Integer lower = set.floor(x);

        return x - lower >= sz;

    }
}