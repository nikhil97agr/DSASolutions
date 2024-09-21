package Leetcode;

import java.util.*;

public class WordLadder2 {
    public List<List<String>> findLadders(String begin, String end, List<String> wordList) {
        List<List<String>> res= new ArrayList<>();
        Set<String> dict = new HashSet<>(wordList);
        if(!dict.contains(end)) return res;
        Set<String> curr = new HashSet<>();
        Map<String, List<String>> map = new HashMap<>();

        curr.add(begin);
        boolean ended = false;

        while(!curr.isEmpty() && !ended){

            dict.removeAll(curr);

            Set<String> next = new HashSet<>();

            for(String s : curr){
                char ch[] = s.toCharArray();
                for(int i=0;i<s.length();i++){
                    char c = ch[i];
                    for(char a ='a';a<='z';a++){
                        ch[i] = a;

                        String temp = new String(ch);
                        if(!dict.contains(temp)) continue;
                        map.computeIfAbsent(s, st-> new ArrayList<>()).add(temp);
                        next.add(temp);
                        if(temp.equals(end)) ended = true;
                    }

                    ch[i] = c;
                }
            }

            curr = next;


        }

        dfs(res, new Stack<>(), map, begin, end);


        return res;
    }

    private void dfs(List<List<String>> res, Stack<String> list, Map<String, List<String>> map, String begin, String end){
        if(begin.equals(end)){
            list.add(begin);
            res.add(new ArrayList<>(list));
            list.remove(list.size()-1);
            return ;
        }
        if(!map.containsKey(begin)) return;
        list.push(begin);

        for(String s : map.get(begin)){
            dfs(res, list, map, s ,end);
        }


        list.pop();
    }
}