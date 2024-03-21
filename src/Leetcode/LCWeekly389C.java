package Leetcode;

import java.util.*;

public class LCWeekly389C {
    public int minimumDeletions(String word, int k) {
        Map<Character, Integer> map = new HashMap<>();
        for(char c : word.toCharArray()){
            int cnt = map.getOrDefault(c, 0);
            map.put(c, cnt+1);
        }

        List<Map.Entry<Character, Integer>> entry = new ArrayList<>(map.entrySet());

        Collections.sort(entry, (e1, e2)-> e2.getValue() - e1.getValue());
        int ans = 0;
        int start = 0;
        int end = entry.size()-1;
        Integer dp[][] = new Integer[entry.size()][entry.size()];

        return solve(start, end, dp, entry, entry.size(), k);
    }

    private int solve(int start, int end, Integer[][] dp, List<Map.Entry<Character, Integer>> list, int n, int k) {
        if(start >= end) return 0;
        if(dp[start][end] != null) return dp[start][end];
        if(list.get(start).getValue() - list.get(end).getValue() > k){
            return dp[start][end] = Math.min(
              solve(start+1, end, dp, list, n, k) + list.get(start).getValue() - (list.get(end).getValue() +k),
              solve(start, end-1, dp, list, n, k ) + list.get(end).getValue()
            );
        }

        return dp[start][end] = 0;
    }
}