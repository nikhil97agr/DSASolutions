package Leetcode;

public class MagicDictionary {
    Trie trie;
    public MagicDictionary() {
        trie = new Trie();
    }
    
    public void buildDict(String[] dictionary) {
        for(String s : dictionary){
            trie.add(s, 0, s.length());
        }
    }
    
    public boolean search(String searchWord) {
        return trie.search(searchWord, 0, searchWord.length(), false);
    }

    class Trie{
        Trie trie[];
        boolean isLast;

        Trie(){
            this.trie = new Trie[26];
            isLast = false;
        }

        private void add(String s, int i, int n){
            if(i==n){
                isLast = true;
                return;
            }

            int ind = s.charAt(i) - 'a';

            if(trie[ind]==null){
                trie[ind] = new Trie();
            }

            trie[ind].add(s, i+1, n);


        }

        private boolean search(String s, int i, int n, boolean changed){
            if(i==n) return changed && isLast;


            int ind = s.charAt(i) - 'a';

            for(int j=0;j<26;j++){
                if(trie[j] !=null){
                    if(j!=ind && !changed){
                        if(trie[j].search(s, i+1, n, true)){
                            return true;
                        }
                    }
                    if(j==ind){
                        if(trie[j].search(s, i+1, n, changed)){
                            return true;
                        }
                    }
                }
            }

            return false;


        }
    }
}

/**
 * Your MagicDictionary object will be instantiated and called as such:
 * MagicDictionary obj = new MagicDictionary();
 * obj.buildDict(dictionary);
 * boolean param_2 = obj.search(searchWord);
 */