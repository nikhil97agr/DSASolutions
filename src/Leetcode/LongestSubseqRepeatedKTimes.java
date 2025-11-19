package Leetcode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


//Problem Link: https://leetcode.com/problems/longest-subsequence-repeated-k-times

public class LongestSubseqRepeatedKTimes {
    public String longestSubsequenceRepeatedK(String s, int k) {
        Map<Character, Integer> map = new HashMap<>();
        for(char c : s.toCharArray()){
            map.merge(c, 1, Integer::sum);
        }

        for(char c = 'a';c<='z';c++){
            if(map.getOrDefault(c, 0) < k){
                map.remove(c);
            }else{
                map.put(c, map.get(c)/ k);
            }
        }
        Set<String> res =new HashSet<>();
         solve(map, k, new StringBuilder(),s ,res);

         String ans = "";

         for(String t : res){
             if(t.length() > ans.length()){
                 ans = t;
             }else if(t.length() == ans.length() && t.compareTo(ans) > 0){
                 ans = t;
             }
         }
         return ans;
    }

    private void solve(Map<Character, Integer> map ,int k, StringBuilder sb, String s, Set<String> set){

        for(char c ='z';c>='a';c--){
            int cnt = map.getOrDefault(c, 0);
            if(cnt > 0){
               map.merge(c, -1, Integer::sum);
                sb.append(c);


                solve(map, k, sb, s, set);


                map.merge(c, 1, Integer:: sum);
                sb.deleteCharAt(sb.length()-1);

            }
        }

        if(check(s, sb.toString(), k)){
            set.add(sb.toString());
        }

    }



    boolean check(String s, String t, int k){
        int i=0;
        int j=0;
        int n= s.length();
        int m = t.length();
        if(m==0) return false;
        while(k-- > 0){
            j=0;
            while(i < n && j < m ){
                if(s.charAt(i)  == t.charAt(j)){
                    j++;
                }
                i++;

            }

            if(j!=m) return false;
        }
        return true;
    }
}