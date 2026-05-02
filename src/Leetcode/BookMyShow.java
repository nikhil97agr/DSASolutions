package Leetcode;

//ProblemLink: https://leetcode.com/problems/booking-concert-tickets-in-groups

public class BookMyShow {

    int n;
    SegmentTree tree;
    int m;
    int start[];

    public BookMyShow(int n, int m) {

        this.n = n;
        this.m = m;
        start = new int[n];
        for (int i = 0; i < n; i++) {
            tree.add(1, i, m, 0, n - 1);
        }
    }

    public int[] gather(int k, int maxRow) {

        long total = tree.search(1, 0, maxRow, 0, n - 1);
        if (total < k) {
            return new int[0];
        }

        for (int i = 0; i <= maxRow; i++) {
            int left = m - start[i];
            if (left >= k) {
                int ans[] = new int[]{i, start[i]};
                start[i] += k;
                tree.add(1, i, m - start[i], 0, n - 1);
                return ans;
            }
        }

        return new int[0];
    }

    public boolean scatter(int k, int maxRow) {

        long total = tree.search(1, 0, maxRow, 0, n - 1);
        if (total < k) {
            return false;
        }
        int i = 0;
        while (k > 0) {
            if (m - start[i] <= k) {
                k -= (m - start[i]);
                start[i] = m;
                tree.add(1, i, 0, 0, n - 1);
                i++;
            } else {
                start[i] += k;
                tree.add(1, i, m - start[i], 0, n - 1);
                break;
            }
        }

        return true;

    }

    class SegmentTree {

        long tree[];

        public SegmentTree(int n) {

            tree = new long[n * 4];

        }

        public long search(int ind, int ql, int qr, int l, int r) {

            if (qr < l || ql > r) {
                return 0;
            }

            if (ql <= l && r <= qr) {
                return tree[ind];
            }

            int mid = (l + r) / 2;

            return search(2 * ind, ql, qr, l, mid) + search(2 * ind, ql, qr, mid + 1, r);
        }

        public void add(int ind, int i, int val, int l, int r) {

            if (i < l || i > r) {
                return;
            }

            if (l == r) {
                tree[ind] = val;

                return;
            }

            int mid = (l + r) / 2;
            add(2 * ind, i, val, l, mid);
            add(2 * ind + 1, i, val, mid + 1, r);

            tree[ind] = tree[2 * ind] + tree[2 * ind + 1];

        }
    }
}

/**
 * Your BookMyShow object will be instantiated and called as such: BookMyShow obj = new BookMyShow(n, m); int[] param_1
 * = obj.gather(k,maxRow); boolean param_2 = obj.scatter(k,maxRow);
 */