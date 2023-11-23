package Leetcode;

//Problem Link : https://leetcode.com/problems/extra-characters-in-a-string/description

public class ExtraCharacter {
    class Trie {
        Trie child[];
        boolean isLast;

        public Trie() {
            child = new Trie[26];
        }

        public void build(String s, int i, int n) {
            if (i == n) {
                this.isLast = true;
                return;
            }
            int ind = s.charAt(i) - 'a';
            if (child[ind] == null) {
                child[ind] = new Trie();
            }
            child[ind].build(s, i + 1, n);
        }

        public boolean find(String s, int i) {
            if (i == s.length())
                return this.isLast;

            int ind = s.charAt(i) - 'a';
            if (child[ind] == null)
                return false;

            return child[ind].find(s, i + 1);
        }
    }

    public int minExtraChar(String s, String[] dictionary) {
        Trie root = new Trie();
        for (String word : dictionary) {
            root.build(word, 0, word.length());
        }
        int n = s.length();
        Integer dp[] = new Integer[n];
        return solve(dp, s, 0, n, root);

    }

    private int solve(Integer dp[], String s, int i, int n, Trie root) {
        if (i == n)
            return 0;

        if (dp[i] != null)
            return dp[i];

        dp[i] = 1 + solve(dp, s, i + 1, n, root);
        for (int j = i + 1; j <= n; j++) {
            if (root.find(s.substring(i, j), 0)) {
                dp[i] = Math.min(dp[i], solve(dp, s, j, n, root));
            }
        }
        return dp[i];
    }
}
