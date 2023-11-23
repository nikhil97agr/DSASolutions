package Leetcode;

import java.util.ArrayList;
import java.util.List;

// Problem Link : https://leetcode.com/problems/all-possible-full-binary-trees/


public class AllFullBinaryTrees {

    public List<TreeNode> allPossibleFBT(int n) {
        if (n % 2 == 0)
            return new ArrayList<>();

        List<List<TreeNode>> dp = new ArrayList<>();
        for (int i = 0; i <= n; i++)
            dp.add(new ArrayList<>());
        return solve(n, dp);

    }

    private List<TreeNode> solve(int n, List<List<TreeNode>> dp) {

        if (n % 2 == 0)
            return new ArrayList<>();

        if (!dp.get(n).isEmpty())
            return dp.get(n);

        List<TreeNode> list = new ArrayList<>();
        if (n == 1) {
            TreeNode node = new TreeNode(0);
            list.add(node);
            return list;
        }

        for (int i = 1; i < n; i += 2) {
            List<TreeNode> left = solve(i, dp);
            List<TreeNode> right = solve(n - i - 1, dp);

            for (TreeNode leftNode : left) {
                for (TreeNode rightNode : right) {
                    TreeNode root = new TreeNode(0);
                    root.left = leftNode;
                    root.right = rightNode;
                    list.add(root);
                }
            }
        }

        dp.set(n, list);
        return list;

    }

}
