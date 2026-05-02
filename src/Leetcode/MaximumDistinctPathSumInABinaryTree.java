package Leetcode;

import java.util.Arrays;

//Problem Link: https://leetcode.com/problems/maximum-distinct-path-sum-in-a-binary-tree

public class MaximumDistinctPathSumInABinaryTree {

    int parent[] = new int[1001];
    int curr[] = new int[1001];
    int left[] = new int[1001];
    int right[] = new int[1001];
    int len = 0;
    boolean vis[] = new boolean[20001];

    public int maxSum(TreeNode root) {

        Arrays.fill(curr, -1001);
        graph(root, -1);
        int ans = Arrays.stream(curr).max().getAsInt();
        for (int i = 0;
                i < len; i++) {
            ans = Math.max(ans, dfs(i, -1));
        }

        return ans;

    }

    private int dfs(int u, int par) {

        if (u == -1 || vis[curr[u] + 1000]) {
            return 0;
        }

        vis[curr[u] + 1000] = true;

        int ans = curr[u] + Math.max(
                dfs(left[u], u),
                Math.max(dfs(right[u], u), dfs(parent[u], u))
        );

        vis[curr[u] + 1000] = false;
        return ans;

    }

    private int graph(TreeNode root, int parentIndex) {

        if (root == null) {
            return -1;
        }
        int currIndex = len;
        len++;

        curr[currIndex] = root.val;
        parent[currIndex] = parentIndex;
        left[currIndex] = graph(root.left, currIndex);
        right[currIndex] = graph(root.right, currIndex);

        return currIndex;
    }

    class TreeNode {

        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {

        }

        TreeNode(int val) {

            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {

            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

}