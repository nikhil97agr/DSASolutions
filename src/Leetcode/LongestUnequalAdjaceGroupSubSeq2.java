package Leetcode;

//Problem Link : https://leetcode.com/contest/biweekly-contest-115/problems/longest-unequal-adjacent-groups-subsequence-ii/

import java.util.ArrayList;
import java.util.List;

public class LongestUnequalAdjaceGroupSubSeq2 {
    public List<String> getWordsInLongestSubsequence(int n, String[] w, int[] g) {
        List<String> dp[] = new ArrayList[n];
        for(int i=0;i<n;i++){
            dp[i] = new ArrayList<>();
            dp[i].add(w[i]);
        }
        for(int i=n-1;i>=0;i--){
            for(int j=i+1;j<n;j++){
                if(w[i].length() == w[j].length() && g[i] != g[j] && valid(w[i], w[j]) && dp[i].size() < dp[j].size()+1){
                    List<String> temp = new ArrayList<>();
                    temp.add(w[i]);
                    temp.addAll(dp[j]);
                    dp[i] = temp;
                }
            }
        }
        
        
        List<String> result = new ArrayList<>();
        for(int i=0;i<n;i++){
            if(result.size() < dp[i].size()){
                result = dp[i];
            }
        }
        return result;
    }

    private boolean valid(String s1, String s2){
        boolean found = false;
        for(int i=0;i<s1.length();i++){
            if(s1.charAt(i) != s2.charAt(i)){
                if(found) return false;
                found = true;
            }
        }
        return found;
    }
    
}
