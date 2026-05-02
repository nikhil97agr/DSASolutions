package Leetcode;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

//Problem Link: https://leetcode.com/problems/maximize-active-section-with-trade-ii

/**
 * Solution for finding maximum active sections after trading segments.
 *
 * Problem: Given a binary string s and queries [l, r], for each query: - Can swap a segment [l, r] with any other
 * segment of equal length - Goal: Maximize the number of '1's in the longest consecutive sequence
 *
 * Key insight: Trading segments optimally - We want to merge '1' segments by removing '0' gaps between them - A trade
 * can replace a '1' segment with '0's from elsewhere - This allows us to "bridge" gaps and create longer consecutive
 * '1' sequences
 *
 * Strategy: 1. Precompute all '1' and '0' segments (runs of consecutive characters) 2. For each query [l, r], determine
 * which '1' segments overlap with the range 3. Try different trade scenarios: a. Replace overlapping '1's with '0's to
 * bridge gaps b. Extend '1' segments by trading '0' segments 4. Use segment tree to efficiently query maximum gain from
 * merging segments
 *
 * Time Complexity: O(n + q * log n) where n = string length, q = queries Space Complexity: O(n) for storing segments
 * and segment tree
 */
public class MaximizeActiveSectionWithTradeII {

    /**
     * Finds maximum active (consecutive '1's) sections for each query.
     *
     * @param s Binary string
     * @param queries Array of [left, right] ranges for potential trades
     * @return List of maximum consecutive '1' counts for each query
     */
    public List<Integer> maxActiveSectionsAfterTrade(String s, int[][] queries) {

        int totalOnes = 0;  // Total count of '1's in the string
        int n = s.length();
        char[] ch = s.toCharArray();

        // oneIndex: Maps start position → length of each '1' segment
        TreeMap<Integer, Integer> oneIndex = new TreeMap<>();

        // prefix[i]: Count of '1's in range [0, i)
        int[] prefix = new int[n + 1];

        // Track current segment being processed
        char prevChar = ch[0];
        int segmentLength = 0;
        int segmentStart = 0;

        // zeroIndex: List of [start, length] for each '0' segment
        List<int[]> zeroIndex = new ArrayList<>();

        // ================================================================
        // STEP 1: Parse string into segments and build prefix sum
        // ================================================================
        for (int i = 0; i < n; i++) {
            if (ch[i] == prevChar) {
                // Continue current segment
                segmentLength++;
            } else {
                // End current segment, start new one
                if (prevChar == '1') {
                    oneIndex.put(segmentStart, segmentLength);
                    totalOnes += segmentLength;
                } else {
                    zeroIndex.add(new int[]{segmentStart, segmentLength});
                }
                segmentStart = i;
                prevChar = ch[i];
                segmentLength = 1;
            }

            // Build prefix sum for quick range '1' count queries
            prefix[i + 1] = prefix[i] + (ch[i] - '0');
        }

        // Handle last segment
        if (prevChar == '0') {
            zeroIndex.add(new int[]{segmentStart, segmentLength});
        } else {
            oneIndex.put(segmentStart, segmentLength);
            totalOnes += segmentLength;
        }

        // ================================================================
        // STEP 2: Handle edge cases where no trading is beneficial
        // ================================================================
        List<Integer> result = new ArrayList<>();

        // If all '1's or only one '0' segment, can't improve by trading
        if (zeroIndex.isEmpty() || zeroIndex.size() == 1) {
            for (int i = 0; i < queries.length; i++) {
                result.add(totalOnes);
            }
            return result;
        }

        // ================================================================
        // STEP 3: Build segment tree for efficient range queries
        // ================================================================
        // Store at each position: potential gain from merging adjacent '0' segments
        SegmentTree tree = new SegmentTree(n);

        // For each pair of consecutive '0' segments, calculate merge potential
        for (int i = 0; i < zeroIndex.size() - 1; i++) {
            int[] z1 = zeroIndex.get(i);
            int[] z2 = zeroIndex.get(i + 1);
            // Store combined length of consecutive '0' segments
            // This represents how many '1's we could add by bridging this gap
            tree.insert(1, z1[0], z1[1] + z2[1], 0, n - 1);
        }

        // ================================================================
        // STEP 4: Process each query
        // ================================================================
        for (int[] q : queries) {
            int l = q[0];
            int r = q[1];

            // Case 1: Query range contains only '1's
            // No benefit from trading (can't improve)
            if (prefix[r + 1] - prefix[l] == (r - l + 1)) {
                result.add(totalOnes);
                continue;
            }

            // Find '1' segments that overlap with [l, r]
            // ll: First '1' segment starting at or after l
            // rr: Last '1' segment starting at or before r
            Integer ll = oneIndex.ceilingKey(l);
            Integer rr = oneIndex.floorKey(r);

            // Case 2: No '1' segments overlap with query range
            // Trading won't help (no '1's to trade away)
            if (ll == null || rr == null || rr < l || ll > r) {
                result.add(totalOnes);
                continue;
            }

            int maxActive = totalOnes;  // Start with baseline (no trade)
            // ============================================================
            // SCENARIO A: Only one '1' segment overlaps with [l, r]
            // ============================================================
            if (ll.equals(rr)) {
                // Subcase A1: Both endpoints are '0's
                // Trade the '1' segment for '0's, gain the whole range as '1's
                if (ch[l] == '0' && ch[r] == '0') {
                    int gain = (r - l + 1) - oneIndex.get(ll);
                    maxActive = Math.max(maxActive, totalOnes + gain);
                    result.add(maxActive);
                    continue;
                }

                // Subcase A2: Right endpoint is '1'
                // Can't improve (would trade away '1's we want)
                if (ch[r] == '1') {
                    result.add(maxActive);
                    continue;
                }

                // Subcase A3: Left endpoint is start of '1' segment
                // Can't benefit from trading
                if (ll == l) {
                    result.add(maxActive);
                    continue;
                }

                // Subcase A4: Left is '0', right is '0', can extend '1' segment
                // Find the '0' segment containing position l using binary search
                int zeroSegIdx = findZeroSegmentIndex(zeroIndex, l);

                // Calculate gain: '0' segment length + range length - '1' segment length
                int len = zeroIndex.get(zeroSegIdx)[1] + (r - ll + 1);
                len -= oneIndex.get(ll);
                maxActive = Math.max(maxActive, totalOnes + len);
                result.add(maxActive);
                continue;
            }

            // ============================================================
            // SCENARIO B: Multiple '1' segments overlap with [l, r]
            // ============================================================

            // Strategy B1: Extend from right boundary
            // If right endpoint is '0', try extending the rightmost '1' segment
            if (ch[r] == '0') {
                Integer r1 = oneIndex.lowerKey(rr);  // Previous '1' segment before rr
                int len = r - r1 + 1;  // Total range to consider
                len -= (oneIndex.get(rr) + oneIndex.get(r1));  // Subtract '1's we trade away
                maxActive = Math.max(maxActive, totalOnes + len);
            }

            // Strategy B2: Extend from left boundary
            if (ch[l] == '0') {
                // Left is '0', extend the leftmost '1' segment
                Integer l1 = oneIndex.higherKey(ll);  // Next '1' segment after ll
                int len = l1 - l;  // Range to fill with '1's
                len -= oneIndex.get(ll);  // Subtract '1's we trade away
                maxActive = Math.max(maxActive, totalOnes + len);
            } else if (ll != l) {
                // Left is '1' but not at the start of '1' segment
                // Find '0' segment before ll using binary search
                int zeroSegIdx = findZeroSegmentIndex(zeroIndex, l);

                Integer l1 = oneIndex.higherKey(ll);  // Next '1' segment
                int len = (l1 - ll) + zeroIndex.get(zeroSegIdx)[1];
                len -= oneIndex.get(ll);
                maxActive = Math.max(maxActive, totalOnes + len);
            }

            // Strategy B3: Merge middle segments
            // Use segment tree to find best gain from merging '1' segments
            // by trading away '1's in the middle
            Integer r1 = oneIndex.lowerKey(rr);
            maxActive = Math.max(maxActive, totalOnes + tree.query(1, ll, r1, 0, n - 1));

            result.add(maxActive);
        }

        return result;
    }

    /**
     * Binary search to find the index of '0' segment containing position pos.
     *
     * @param zeroIndex List of [start, length] for '0' segments
     * @param pos Position to search for
     * @return Index of '0' segment containing pos
     */
    private int findZeroSegmentIndex(List<int[]> zeroIndex, int pos) {

        int start = 0;
        int end = zeroIndex.size() - 1;

        while (start < end) {
            int mid = (start + end) >> 1;
            if (zeroIndex.get(mid)[0] >= pos) {
                end = mid;
            } else {
                start = mid + 1;
            }
        }

        return end;
    }
}

/**
 * Segment Tree for efficient range maximum queries.
 *
 * Stores the maximum value in any range [l, r]. Used to find the best gain from merging '1' segments.
 */
class SegmentTree {

    int[] tree;  // Tree array storing maximum values

    /**
     * Constructor to initialize segment tree.
     *
     * @param n Size of the array
     */
    public SegmentTree(int n) {

        tree = new int[4 * n];  // Standard segment tree size
    }

    /**
     * Inserts/updates a value at position i.
     *
     * @param ind Current node index in segment tree
     * @param i Position to update
     * @param val Value to insert
     * @param l Left bound of current segment
     * @param r Right bound of current segment
     */
    public void insert(int ind, int i, int val, int l, int r) {

        // Position i is outside current segment
        if (i < l || r < i) {
            return;
        }
        // Reached leaf node
        if (l == r) {
            tree[ind] = val;
            return;
        }

        int mid = (l + r) >> 1;

        // Recursively update left and right children
        insert(2 * ind, i, val, l, mid);
        insert(2 * ind + 1, i, val, mid + 1, r);

        // Update current node as max of children
        tree[ind] = Math.max(tree[2 * ind], tree[2 * ind + 1]);
    }

    /**
     * Queries the maximum value in range [ql, qr].
     *
     * @param ind Current node index in segment tree
     * @param ql Query left bound
     * @param qr Query right bound
     * @param l Left bound of current segment
     * @param r Right bound of current segment
     * @return Maximum value in range [ql, qr]
     */
    public int query(int ind, int ql, int qr, int l, int r) {

        // Current segment is completely outside query range
        if (qr < l || r < ql) {
            return 0;
        }

        // Current segment is completely inside query range
        if (ql <= l && r <= qr) {
            return tree[ind];
        }

        int mid = (l + r) >> 1;

        // Query both children and return maximum
        return Math.max(query(2 * ind, ql, qr, l, mid),
                query(2 * ind + 1, ql, qr, mid + 1, r));
    }
}