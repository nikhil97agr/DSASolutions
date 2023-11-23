package Leetcode;

//Problem Link : https://leetcode.com/problems/word-break

import java.util.HashMap;
import java.util.List;

public class WordBreak {

    class Trie {
        boolean isLast;
        HashMap<Character, Trie> trieMap;

        public Trie() {
            isLast = false;
            trieMap = new HashMap<Character, Trie>();
        }

        public void insert(String s, int i) {
            if (i == s.length()) {
                this.isLast = true;
                return;
            }

            Trie trie = trieMap.get(s.charAt(i));
            if (trie == null) {
                trie = new Trie();
                trieMap.put(s.charAt(i), trie);
            }
            trie.insert(s, i + 1);

        }

        public boolean check(String s, int i) {
            if (i == s.length()) {
                return isLast;
            }
            if (!trieMap.containsKey(s.charAt(i))) {
                return false;
            }
            return trieMap.get(s.charAt(i)).check(s, i + 1);
        }
    }

    public boolean wordBreak(String s, List<String> wordDict) {
        Trie trie = new Trie();
        for (String word : wordDict) {
            trie.insert(word, 0);
        }

        Boolean dp[] = new Boolean[s.length()];
        return solve(s, 0, 0, s.length(), dp, trie);

    }

    public boolean solve(String s, int start, int end, int n, Boolean dp[], Trie trie) {
        if (start == n && end == n) {
            return true;
        } else if (end == n) {
            return false;
        }

        if (dp[start] != null) {
            return dp[start];
        }
        dp[start] = (trie.check(s.substring(start, end + 1), 0) && solve(s, end + 1, end + 1, n, dp, trie))
                || solve(s, start, end + 1, n, dp, trie);

        return dp[start];
    }
}
