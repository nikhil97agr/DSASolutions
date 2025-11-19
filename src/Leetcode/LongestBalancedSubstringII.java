package Leetcode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//Problem Link: https://leetcode.com/contest/weekly-contest-471/problems/longest-balanced-substring-ii/description/

public class  LongestBalancedSubstringII {

    public int longestBalanced(String s) {

        return Math.max(
                single(s, 'a'),
                Math.max(single(s,'b'),
                        Math.max(single(s, 'c'),
                                Math.max(doub(s, 'a', 'b'),
                                        Math.max(doub(s, 'b','c'),
                                                Math.max(doub(s, 'c', 'a'), triple(s, 'a','b','c'))))))
        );


    }

    private int triple(String s, char a, char b, char c) {
        int ans= 0;
        Map<Pair, Integer> map = new HashMap<>();
        map.put(new Pair(0,0), -1);
        int x = 0, y=0,z=0;
        int n = s.length();
        for(int i=0;i<n;i++){
            if(s.charAt(i) == a)x++;
            if(s.charAt(i) ==b) y++;
            if(s.charAt(i) == c)z++;

            int d1 = x - y;
            int d2 = y - z;
            Pair pair = new Pair(d1, d2);

            if (map.containsKey(pair)) {
                ans = Math.max(ans,  i - map.get(pair));

            }else{
                map.put(pair, i);
            }
        }

        return ans;

    }

    private int doub(String s, char a, char b) {

        Map<Integer, Integer> map = new HashMap<>();

        map.put(0, -1);
        int x = 0;
        int y = 0;
        int n = s.length();
        int ans = 0;
        for(int i=0;i<n;i++){
            if(s.charAt(i) == a)
                x++;
            else if(s.charAt(i) == b) y++;
            else{
                map = new HashMap<>();
                map.put(0, i);
                x=0;
                y=0;
                continue;
            }

            int diff = x-y;

            if(map.containsKey(diff)){
                ans = Math.max(ans, i - map.get(diff));
            }else{
                map.put(diff, i);
            }
        }

        return ans;
    }

    private int single(String s, char a) {

        int cnt = 0;

        int ans =0;
        int n = s.length();
        for(int i=0;i<n;i++){
            if(s.charAt(i) == a){
                cnt++;
            }else{
                cnt = 0;
            }
            ans = Math.max(ans, cnt);
        }

        return ans;
    }

    class Pair{
        int a;
        int b;
        public Pair(int a, int b){
            this.a = a;
            this.b= b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair pair = (Pair) o;
            return a == pair.a && b == pair.b;
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }

}