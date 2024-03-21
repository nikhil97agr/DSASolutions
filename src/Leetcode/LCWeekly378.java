package Leetcode;

import java.util.*;

public class LCWeekly378 {

    public int maximumLength(String s) {
        int ans = -1;
        int n = s.length();

        Map<Character, List<Integer>> map = new HashMap<>();
        int i=0;
        int cnt = 0;
        char ch = s.charAt(0);
        for(char c : s.toCharArray()){
            if(c == ch){
                cnt++;
            }else{
                map.computeIfAbsent(ch, character -> new ArrayList<>()).add(cnt);
                ch = c;
                cnt = 1;
            }
        }
        map.computeIfAbsent(ch, c -> new ArrayList<>()).add(cnt);
        for(char c : map.keySet()){
            List<Integer> value = map.get(c);
            TreeMap<Integer, Integer> treeMap = new TreeMap<>();
            for(int x : value){
                if(x >= 1){
                    treeMap.put(x, treeMap.getOrDefault(x, 0)+1);
                }
                if(x>1){
                    treeMap.put(x-1, treeMap.getOrDefault(x-1, 0)+1);
                }
                if(x > 2){
                    treeMap.put(x-2, treeMap.getOrDefault(x-2, 0)+1);
                }

            }

            for(Map.Entry<Integer, Integer> entry : treeMap.entrySet()){
                if(entry.getValue()>=3){
                    ans = Math.max(ans, entry.getKey());
                }
            }
        }
        return ans;
    }
}