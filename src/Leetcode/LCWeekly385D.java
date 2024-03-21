package Leetcode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LCWeekly385D {
    public long countPrefixSuffixPairs(String[] words) {
        Trie trie = new Trie();
        long ans = 0;

        for(int i=words.length-1;i>=0;i--){
            ans += trie.search(words[i], 0, words[i].length());
            trie.build(words[i], 0, words[i].length());

        }
        return ans;
    }

    class Trie{
        Map<Pair, Trie> map;
        int cnt = 0;

        public Trie(){
            map = new HashMap<>();
            cnt = 0;
        }

        public void build(String s, int i, int n){
            if(i==n){
                return;
            }


            Pair pair = new Pair(s.charAt(i), s.charAt(n-1-i));
            Trie node = new Trie();
            if(map.containsKey(pair)){
                node = map.get(pair);
            }

            map.put(pair, node);
            node.cnt++;
            node.build(s, i+1, n);
        }

        public int search(String s, int i, int n){
            if(i==n){
                return cnt;
            }

            Pair pair = new Pair(s.charAt(i), s.charAt(n-1-i));
            if(!map.containsKey(pair)){
                return 0;
            }
            Trie trie = map.get(pair);
            return trie.search(s, i+1, n);
        }

        @Override
        public String toString() {
            return "Trie{" +
                    "map=" + map +
                    ", cnt=" + cnt +
                    '}';
        }
    }

    class Pair{
        char a;
        char b;

        public Pair(char a, char b){
            this.a = a;
            this.b = b;
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

        @Override
        public String toString() {
            return "Pair{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }
    }
}