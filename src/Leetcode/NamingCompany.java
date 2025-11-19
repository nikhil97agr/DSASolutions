package Leetcode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NamingCompany {
    public long distinctNames(String[] ideas) {
        Map<Character, Set<String>> map = new HashMap<>();

        for(String s : ideas){
            char c  = s.charAt(0);
            map.computeIfAbsent(c, x -> new HashSet<>()).add(s.substring(1));
        }

        long ans = 0;

        for(char c = 'a';c<='z';c++){
            if(!map.containsKey(c)) continue;

            for(char c2 = (char)(c + 1);c2<='z';c2++){
                if(!map.containsKey(c2)) continue;
                Set<String> set1 = map.get(c);
                Set<String> set2 = map.get(c2);
                int cnt = 0;
                for(String s : set1){
                    if(set2.contains(s)) cnt++;
                }

                int l1 = set1.size() - cnt;
                int l2 = set2.size() - cnt;


                ans += 2l*l1*l2;
            }
        }

        return ans;
    }
}