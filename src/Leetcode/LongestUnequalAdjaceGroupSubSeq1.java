package Leetcode;

//Problem Link : https://leetcode.com/contest/biweekly-contest-115/problems/longest-unequal-adjacent-groups-subsequence-i/

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class LongestUnequalAdjaceGroupSubSeq1 {
    public List<String> getWordsInLongestSubsequence(int n, String[] words, int[] groups) {
        List<String> result = new ArrayList<>();
        for(int i=0;i<n;i++){
            Stack<Integer> stack = new Stack<>();
            List<String> t = new ArrayList<>();
            for(int j=i;j<n;j++){
                if(stack.isEmpty()){
                    stack.push(groups[j]);
                    t.add(words[j]);
                }
                else{
                    if(stack.peek() != groups[j]){
                        stack.push(groups[j]);
                        t.add(words[j]);
                    }
                }
            }
            if(result.size() < t.size()){
                result = t;
            }
            
        }
        return result;
    }
}
