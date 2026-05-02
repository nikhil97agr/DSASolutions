package Leetcode;

import java.util.Arrays;

//Problem Link: https://leetcode.com/problems/make-array-empty

public class MakeArrayEmpty {

    public long countOperationsToEmptyArray(int[] nums) {

        long ans = 0;
        int n = nums.length;
        SegmentTree tree = new SegmentTree(n);
        int pair[][] = new int[n][2];
        for (int i = 0; i < n; i++) {
            pair[i] = new int[]{nums[i], i};
        }

        Arrays.sort(pair, (a, b) -> a[0] - b[0]);
        int prev = -1;

        for (var p : pair) {
            int ind = p[1];
            if (ind > prev) {
                int cnt = tree.search(1, 0, n - 1, 0, ind) - tree.search(1, 0, n - 1, 0, prev);
                int left = (ind - prev) - cnt;
                ans += left;

            } else {
                int c1 = tree.search(1, 0, n - 1, 0, ind);
                int c2 = tree.search(1, 0, n - 1, 0, n - 1);
                int c3 = tree.search(1, 0, n - 1, 0, prev);
                int cc1 = (n - 1 - prev) - (c2 - c3);
                int cc2 = (ind + 1) - c1;

                ans += cc1 + cc2;

            }

            prev = ind;

            tree.add(1, ind, 0, n - 1);

        }

        return ans;
    }

    class SegmentTree {

        int tree[];

        public SegmentTree(int n) {

            tree = new int[n * 4];

        }

        public void add(int treeInd, int ind, int start, int end) {

            if (ind < start || ind > end) {
                return;
            }
            if (start == end) {
                tree[treeInd] = 1;
                return;
            }

            int mid = (start + end) / 2;

            add(2 * treeInd, ind, start, mid);
            add(2 * treeInd + 1, ind, mid + 1, end);
            tree[treeInd] = tree[2 * treeInd] + tree[2 * treeInd + 1];
        }

        public int search(int treeInd, int s, int e, int qs, int qe) {

            if (qe < s || qs > e) {
                return 0;
            }

            if (qs <= s && e <= qe) {
                return tree[treeInd];
            }
            int mid = (s + e) / 2;

            return search(2 * treeInd, s, mid, qs, qe) + search(2 * treeInd + 1, mid + 1, e, qs, qe);
        }
    }
}