package Leetcode;

import java.util.HashMap;
import java.util.Map;

public class LCBiweekly152C {
    public static void main(String[] args) {
        System.out.println(new LCBiweekly152C().longestCommonPrefix(new String[]{"cdb","c","aee","afdd","dad","bdebb","cdecf","a","efdb","cffe","bed","ba"}, 2));
    }
    public int[] longestCommonPrefix(String[] words, int k) {
        int n = words.length;
        int ans[] = new int[n];
        if(n-1 < k){
            return ans;
        }

        Trie trie = new Trie();
        for(String s : words) trie.add(s, 0 ,s.length());

        for(int i=0;i<n;i++){
            ans[i] = trie.search(words[i], 0, words[i].length(), k);
        }


        return ans;
    }


    class Trie{
        Map<Character, Trie> map;
        int cnt;
        int maxLength;

        public Trie(){
            map =new HashMap<>();
            cnt = 0;

        }

        private void add(String s, int i, int n){
            if(i==n){
                return;
            }

            char c = s.charAt(i);
            if(!map.containsKey(c)) map.put(c, new Trie());
            Trie trie = map.get(c);
            trie.cnt++;
            trie.add(s, i+1, n);
        }

        @Override
        public String toString() {
            return "Trie{" +
                    "trie=" + map +
                    ", cnt=" + cnt +
                    '}';
        }

        private int search(String s, int i, int n, int k){

            int ans = 0;
            for(Map.Entry<Character, Trie> entry : map.entrySet()){
                Trie trie = entry.getValue();
                char c = entry.getKey();
                if(i == -1){
                    if(trie.cnt >=k){
                        ans = Math.max(ans, 1 + trie.search(s, i, n, k));
                    }
                }else{
                        if(i <n && c == s.charAt(i)){
                            if(trie.cnt-1 >=k ){
                                ans = Math.max(ans, 1 + trie.search(s, i+1, n, k));
                            }
                        }else{
                            if(trie.cnt >=k){
                                ans = Math.max(ans, 1 + trie.search(s, -1, n, k));
                            }
                        }

                }
            }
            return ans;
        }
    }
}