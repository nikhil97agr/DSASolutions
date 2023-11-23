package Leetcode;

// Problem Link : https://leetcode.com/problems/unique-binary-search-trees-ii   

import java.util.List;
import java.util.ArrayList;

public class UniqueBinarySearchTrees {

    public List<TreeNode> generateTrees(int n) {

        if (n == 0) {
            return new ArrayList<>();
        }

        List<TreeNode> dp[][] = new List[n + 1][n + 1];
        return solve(1, n, dp);
    }

    private List<TreeNode> solve(int start, int end, List<TreeNode> dp[][]) {
        if (start > end) {
            List<TreeNode> list = new ArrayList<>();
            list.add(null);
            return list;
        }

        if (dp[start][end] != null) {
            return dp[start][end];
        }

        List<TreeNode> list = new ArrayList<>();
        dp[start][end] = list;

        for (int i = start; i <= end; i++) {
            List<TreeNode> left = solve(start, i - 1, dp);
            List<TreeNode> right = solve(i + 1, end, dp);

            for (TreeNode leftNode : left) {
                for (TreeNode rightNode : right) {
                    TreeNode root = new TreeNode(i);
                    root.left = leftNode;
                    root.right = rightNode;
                    list.add(root);
                }
            }
        }

        return list;
    }

}
