package Leetcode;

import java.util.ArrayList;

//Problem Link: https://leetcode.com/problems/minimum-deletions-to-make-alternating-substring/

/**
 * Solution for finding minimum deletions to make a substring alternating.
 *
 * An alternating string has characters that alternate between 'A' and 'B' (e.g., "ABABAB").
 *
 * The problem supports two types of queries: 1. Type 1 [1, index]: Flip character at index (A -> B or B -> A) 2. Type 2
 * [2, l, r]: Return minimum deletions to make substring [l, r] alternating
 *
 * Key insight: The minimum number of deletions equals the number of positions where a character is the same as its
 * previous character (consecutive duplicates).
 *
 * Uses a segment tree to efficiently: - Track positions with consecutive duplicate characters - Update when characters
 * are flipped - Query the count of duplicates in any range
 */
public class MinimumDeletionsToMakeAlternatingSubstring {

    /**
     * Processes queries on a string to find minimum deletions for alternating substrings.
     *
     * @param s Input string consisting of 'A' and 'B' characters
     * @param queries 2D array where each query is either: [1, index] - flip character at index [2, l, r] - count
     * minimum deletions for substring [l, r]
     * @return Array of results for type 2 queries
     */
    public int[] minDeletions(String s, int[][] queries) {

        char ch[] = s.toCharArray();

        int n = ch.length;

        // Segment tree to track positions where ch[i] == ch[i-1]
        var tree = new SegmentTree(n);

        // Initialize: mark all positions where current char equals previous char
        // tree[i] = 1 if ch[i] == ch[i-1], else 0
        for (int i = 1; i < n; i++) {
            if (ch[i] == ch[i - 1]) {
                tree.add(1, i, 1, 0, n - 1);
            }
        }

        var result = new ArrayList<Integer>();

        // Process each query
        for (var q : queries) {
            // Type 2 query: find minimum deletions in range [l, r]
            if (q[0] == 2) {
                int l = q[1];
                int r = q[2];

                // Single character substring is already alternating
                if (l == r) {
                    result.add(0);
                    continue;
                }

                // Count positions in [l, r] where ch[i] == ch[i-1]
                int cnt = tree.query(1, l, r, 0, n - 1);

                // Adjust for boundary: if ch[l] == ch[l-1], we counted it but it's
                // outside our substring concern (ch[l-1] is not in [l, r])
                if (l - 1 >= 0 && ch[l] == ch[l - 1]) {
                    cnt--;
                }

                result.add(cnt);
                continue;
            }

            // Type 1 query: flip character at index
            var ind = q[1];
            char prev = ch[ind];
            char next = prev == 'A' ? 'B' : 'A';  // Flip the character

            // Update segment tree for position ind
            // Check if flipping creates/removes a duplicate with previous character
            if (ind - 1 >= 0) {
                if (ch[ind - 1] == next) {
                    // After flip, ch[ind] will equal ch[ind-1], mark as duplicate
                    tree.add(1, ind, 1, 0, n - 1);
                } else {
                    // After flip, ch[ind] will differ from ch[ind-1], not a duplicate
                    tree.add(1, ind, 0, 0, n - 1);
                }
            }

            // Update segment tree for position ind+1
            // Check if flipping affects duplicate status at next position
            if (ind + 1 < n) {
                if (ch[ind + 1] == next) {
                    // After flip, ch[ind+1] will equal ch[ind], mark as duplicate
                    tree.add(1, ind + 1, 1, 0, n - 1);
                } else {
                    // After flip, ch[ind+1] will differ from ch[ind], not a duplicate
                    tree.add(1, ind + 1, 0, 0, n - 1);
                }
            }

            // Actually flip the character in our array
            ch[ind] = next;

        }

        // Convert result list to array
        var ans = new int[result.size()];
        for (int i = 0; i < ans.length; i++) {
            ans[i] = result.get(i);
        }

        return ans;
    }

    /**
     * Segment Tree implementation for range sum queries and point updates. Stores count of duplicate positions (where
     * ch[i] == ch[i-1]) in each range.
     */
    class SegmentTree {

        int tree[];  // Array representation of segment tree

        /**
         * Constructor to initialize the segment tree.
         *
         * @param n Size of the input array
         */
        public SegmentTree(int n) {

            tree = new int[n * 4];  // Segment tree needs 4*n space
        }

        /**
         * Updates a specific position in the segment tree.
         *
         * @param treeInd Current node index in the segment tree
         * @param ind Array index to update
         * @param val New value (0 or 1) to set at position ind
         * @param start Start of the range represented by current node
         * @param end End of the range represented by current node
         */
        public void add(int treeInd, int ind, int val, int start, int end) {

            // Index is outside current range
            if (ind < start || ind > end) {
                return;
            }

            // Reached the leaf node corresponding to index ind
            if (start == end) {
                tree[treeInd] = val;  // Set value at this position
                return;
            }

            // Recursively update the appropriate child
            int mid = (start + end) / 2;

            add(2 * treeInd, ind, val, start, mid);
            add(2 * treeInd + 1, ind, val, mid + 1, end);

            // Update current node with sum of children
            tree[treeInd] = tree[2 * treeInd] + tree[2 * treeInd + 1];
        }

        /**
         * Queries the segment tree for the sum in a range.
         *
         * @param treeInd Current node index in the segment tree
         * @param qs Query range start
         * @param qe Query range end
         * @param start Start of the range represented by current node
         * @param end End of the range represented by current node
         * @return Sum of values in the query range [qs, qe]
         */
        public int query(int treeInd, int qs, int qe, int start, int end) {

            // Current range is completely outside query range
            if (qe < start || qs > end) {
                return 0;
            }

            // Current range is completely within query range
            if (qs <= start && end <= qe) {
                return tree[treeInd];
            }

            // Partial overlap - query both children and sum results
            int mid = (start + end) / 2;

            return query(2 * treeInd, qs, qe, start, mid) + query(2 * treeInd + 1, qs, qe, mid + 1, end);
        }
    }
}