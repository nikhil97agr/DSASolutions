package Leetcode;

public class LCWeekly385B {
    public int longestCommonPrefix(int[] arr1, int[] arr2) {
        int ans = 0;
        Trie trie = new Trie();

        for(int x : arr2){
            String s = String.valueOf(x);
             trie.build(s, 0, s.length());
        }
        for(int x : arr1){
            String s = String.valueOf(x);
            ans = Math.max(ans, trie.search(s, 0, s.length()));
        }

        return ans;
    }

     class Trie{
        Trie trie[];
        boolean isLast;
        public Trie(){
            trie = new Trie[256];
        }

        public void build(String str, int i, int n){
            if(i == n){
                this.isLast = true;
                return;
            }

            char c = str.charAt(i);
            if(trie[c]==null){
                trie[c] = new Trie();
            }

            trie[c].build(str, i+1, n);
        }

        public int search(String s, int i, int n){
            if(i==n) return n;

            char c = s.charAt(i);
            if(trie[c] == null) return i;

            return trie[c].search(s, i+1, n);
        }
    }
}