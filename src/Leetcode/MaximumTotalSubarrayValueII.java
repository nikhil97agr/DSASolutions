package Leetcode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;

//Problem Link: https://leetcode.com/problems/maximum-total-subarray-value-ii/

public class MaximumTotalSubarrayValueII {

    public long maxTotalValue(int[] nums, int k) {

        var tree = new SegmentTree(nums);
        int n = nums.length;
        long ans = 0;

        var que = new PriorityQueue<Node>((a, b) -> b.val - a.val);
        que.offer(new Node(0, n - 1, 0));
        var vis = new HashSet<String>();
        vis.add("0:" + (n - 1));
        while (k > 0 && !que.isEmpty()) {
            Node node = que.poll();
            int start = node.start;
            var end = node.end;

            int ind[] = getMinMax(start, end, tree, n);
            long sum = Math.abs(nums[ind[0]] - nums[ind[1]]);
            ans += sum;
            k--;

            if (start + 1 <= end) {
                String key = (start + 1) + ":" + end;
                if (!vis.contains(key)) {
                    int ii[] = getMinMax(start + 1, end, tree, n);
                    int diff = Math.abs(nums[ii[0]] - nums[ii[1]]);
                    que.offer(new Node(start + 1, end, diff));
                    vis.add(key);
                }
            }

            if (start <= end - 1) {
                String key = start + ":" + (end - 1);
                if (!vis.contains(key)) {
                    int ii[] = getMinMax(start, end - 1, tree, n);
                    int diff = Math.abs(nums[ii[0]] - nums[ii[1]]);
                    que.offer(new Node(start, end - 1, diff));
                    vis.add(key);
                }
            }
        }

        return ans;
    }

    private int[] getMinMax(int start, int end, SegmentTree tree, int n) {

        int min = tree.getMin(1, start, end, 0, n - 1);
        int max = tree.getMax(1, start, end, 0, n - 1);

        return new int[]{Math.min(min, max), Math.max(min, max)};
    }

    class Node {

        int start;
        int end;
        int val;

        public Node(int start, int end, int val) {

            this.start = start;
            this.end = end;
            this.val = val;
        }
    }

    class SegmentTree {

        int nums[];
        int min[];
        int max[];

        public SegmentTree(int nums[]) {

            this.nums = nums;
            min = new int[4 * nums.length];
            max = new int[4 * nums.length];

            Arrays.fill(min, -1);
            Arrays.fill(max, -1);

            for (int i = 0; i < nums.length; i++) {
                insert(1, i, 0, nums.length - 1);
            }
        }

        private void insert(int treeInd, int i, int start, int end) {

            if (i < start || i > end) {
                return;
            }

            if (start == end) {
                min[treeInd] = max[treeInd] = i;
                return;
            }

            int mid = (start + end) / 2;

            insert(2 * treeInd, i, start, mid);
            insert(2 * treeInd + 1, i, mid + 1, end);

            updateMin(treeInd);
            updateMax(treeInd);

        }

        private void updateMax(int treeInd) {

            int left = max[2 * treeInd];
            int right = max[2 * treeInd + 1];
            if (left == right && left == -1) {
                max[treeInd] = -1;
            } else if (left == -1) {
                max[treeInd] = right;
            } else if (right == -1) {
                max[treeInd] = left;
            } else {
                if (nums[left] > nums[right]) {
                    max[treeInd] = left;
                } else {
                    max[treeInd] = right;
                }
            }
        }

        private void updateMin(int treeInd) {

            int left = min[2 * treeInd];
            int right = min[2 * treeInd + 1];
            if (left == right && left == -1) {
                min[treeInd] = -1;
            } else if (left == -1) {
                min[treeInd] = right;
            } else if (right == -1) {
                min[treeInd] = left;
            } else {
                if (nums[left] > nums[right]) {
                    min[treeInd] = right;
                } else {
                    min[treeInd] = left;
                }
            }
        }

        public int getMin(int treeInd, int ql, int qr, int l, int r) {

            if (ql > qr || l > r || ql > r || qr < l) {
                return -1;
            }

            if (ql <= l && r <= qr) {
                return min[treeInd];
            }

            int mid = (l + r) / 2;

            int left = getMin(2 * treeInd, ql, qr, l, mid);
            int right = getMin(2 * treeInd + 1, ql, qr, mid + 1, r);

            if (left == -1 && right == -1) {
                return -1;
            }
            if (left == -1) {
                return right;
            }

            if (right == -1) {
                return left;
            }

            if (nums[left] < nums[right]) {
                return left;
            }

            return right;
        }

        public int getMax(int treeInd, int ql, int qr, int l, int r) {

            if (ql > qr || l > r || ql > r || qr < l) {
                return -1;
            }

            if (ql <= l && r <= qr) {
                return max[treeInd];
            }

            int mid = (l + r) / 2;

            int left = getMax(2 * treeInd, ql, qr, l, mid);
            int right = getMax(2 * treeInd + 1, ql, qr, mid + 1, r);

            if (left == -1 && right == -1) {
                return -1;
            }
            if (left == -1) {
                return right;
            }

            if (right == -1) {
                return left;
            }

            if (nums[left] > nums[right]) {
                return left;
            }

            return right;
        }
    }
}