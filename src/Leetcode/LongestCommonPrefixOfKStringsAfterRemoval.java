package Leetcode;

import java.util.TreeMap;

//Problem Link: https://leetcode.com/problems/longest-common-prefix-of-k-strings-after-removal

/**
 * Solution for finding the longest common prefix of k strings after removing one string.
 *
 * Problem: Given an array of words and integer k, for each word w, find the length of the
 * longest common prefix that is shared by at least k words when word w is removed from
 * the array.
 *
 * Key insight:
 * - A prefix of length L is "valid" if it appears in at least k different words
 * - When we remove word[i], some prefixes might drop below the k threshold
 * - Use a Trie to track prefix frequencies and a TreeMap to track valid prefix lengths
 *
 * Algorithm:
 * 1. Build a trie with all words, tracking how many words share each prefix
 * 2. Maintain a TreeMap of valid prefix lengths (those appearing in >= k words)
 * 3. For each word i:
 *    a. Remove word[i] from the trie (decrement counts)
 *    b. Check the maximum valid prefix length (largest key in TreeMap)
 *    c. Add word[i] back to the trie
 *
 * Example: words = ["abc", "ab", "abd"], k = 2
 * - Initially: "a" appears 3 times, "ab" appears 3 times
 * - Remove "abc": "a" appears 2 times (valid), "ab" appears 2 times (valid) → answer = 2
 * - Remove "ab": "a" appears 2 times (valid), "ab" appears 1 time (invalid) → answer = 1
 * - Remove "abd": "a" appears 2 times (valid), "ab" appears 2 times (valid) → answer = 2
 *
 * Time Complexity: O(n * L) where n = number of words, L = max word length
 * Space Complexity: O(n * L) for the trie
 */
public class LongestCommonPrefixOfKStringsAfterRemoval {

    TreeMap<Integer, Integer> map;  // Maps prefix_length -> count_of_such_prefixes

    /**
     * Finds the longest common prefix length for each word when removed.
     *
     * @param words Array of strings
     * @param k     Minimum number of words that must share a prefix
     * @return Array where ans[i] = longest valid prefix length when words[i] is removed
     */
    public int[] longestCommonPrefix(String[] words, int k) {

        Trie trie = new Trie();
        map = new TreeMap<>();  // Tracks valid prefix lengths

        int n = words.length;
        int ans[] = new int[n];

        // Phase 1: Build the trie with all words
        // This populates the trie with prefix counts and the map with valid prefix lengths
        for (int i = 0; i < n; i++) {
            trie.add(words[i].toCharArray(), words[i].length(), k);
        }

        // Phase 2: For each word, simulate its removal
        for (int i = 0; i < n; i++) {
            String s = words[i];

            // Remove word[i] from trie (decrements counts along its path)
            trie.remove(s.toCharArray(), s.length(), k);

            // After removal, check the longest valid prefix length
            // map.lastKey() gives the maximum prefix length that appears in >= k words
            if (!map.isEmpty()) {
                ans[i] = map.lastKey();
            }
            // If map is empty, ans[i] remains 0 (no valid prefix exists)

            // Add word[i] back to restore the trie for next iteration
            trie.add(s.toCharArray(), s.length(), k);
        }

        return ans;


    }

    /**
     * Adds a prefix length to the map.
     * Increments the count of how many prefixes have this length.
     *
     * This is called when a prefix of length 'a' becomes valid (reaches k occurrences).
     *
     * @param a Prefix length to add
     */
    public void add(int a) {

        map.merge(a, 1, Integer::sum);  // Increment count for this prefix length
    }

    /**
     * Removes a prefix length from the map.
     * Decrements the count of how many prefixes have this length.
     *
     * This is called when a prefix of length 'a' becomes invalid (drops below k occurrences).
     * If count reaches 0, remove the entry entirely.
     *
     * @param a Prefix length to remove
     */
    public void remove(int a) {

        map.merge(a, -1, Integer::sum);  // Decrement count for this prefix length
        if (map.get(a) == 0) {
            map.remove(a);  // Remove key if no prefixes of this length are valid
        }
    }

    /**
     * Trie data structure for storing words and tracking prefix frequencies.
     *
     * Each node represents a character position in words, and maintains:
     * - cnt: Number of words that pass through this node
     * - trie[]: 26 children for lowercase letters 'a' to 'z'
     *
     * A prefix is "valid" if its node has cnt >= k.
     */
    class Trie {

        int cnt;       // Count of words passing through this node
        Trie trie[];   // Children nodes for each letter 'a' to 'z'

        /**
         * Constructor for Trie node.
         */
        public Trie() {

            trie = new Trie[26];  // 26 lowercase letters
            cnt = 0;              // Initially no words pass through
        }

        /**
         * Adds a word to the trie.
         *
         * As we traverse the word, we increment the count at each prefix node.
         * When a prefix count reaches k, we add its length to the valid prefix map.
         *
         * @param ch Array of characters representing the word
         * @param n  Length of the word
         * @param k  Minimum threshold for a prefix to be considered valid
         */
        public void add(char ch[], int n, int k) {

            Trie root = this;

            // Traverse each character of the word
            for (int i = 0; i < n; i++) {
                // Convert character to index (0-25 for 'a'-'z')
                int ind = ch[i] - 'a';

                // Create child node if it doesn't exist
                if (root.trie[ind] == null) {
                    root.trie[ind] = new Trie();
                }

                // Move to child node (next character in prefix)
                root = root.trie[ind];

                // Increment count: one more word has this prefix
                root.cnt++;

                // Check if this prefix just became valid (reached k occurrences)
                // If so, add prefix length (i+1) to the outer class's map
                if (root.cnt >= k) {
                    LongestCommonPrefixOfKStringsAfterRemoval.this.add(i + 1);
                }
            }
        }

        /**
         * Removes a word from the trie.
         *
         * As we traverse the word, we decrement the count at each prefix node.
         * When a prefix count drops from k to k-1, it becomes invalid, so we remove
         * its length from the valid prefix map.
         *
         * Note: We decrement first, then check if count is now k-1 (was k before).
         *
         * @param ch Array of characters representing the word
         * @param n  Length of the word
         * @param k  Minimum threshold for a prefix to be considered valid
         */
        public void remove(char ch[], int n, int k) {

            Trie root = this;

            // Traverse each character of the word
            for (int i = 0; i < n; i++) {
                // Convert character to index (0-25 for 'a'-'z')
                int ind = ch[i] - 'a';

                // Move to child node (next character in prefix)
                root = root.trie[ind];

                // Decrement count: one fewer word has this prefix
                root.cnt--;

                // Check if this prefix just became invalid
                // If count is now k-1, it was k before (valid), but now it's invalid
                if (root.cnt == k - 1) {
                    // Remove prefix length (i+1) from the outer class's map
                    LongestCommonPrefixOfKStringsAfterRemoval.this.remove(i + 1);
                }
            }
        }
    }
}