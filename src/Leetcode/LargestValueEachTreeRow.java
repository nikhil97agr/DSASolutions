package Leetcode;

//Problem Link : https://leetcode.com/problems/find-largest-value-in-each-tree-row

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LargestValueEachTreeRow {
    public List<Integer> largestValues(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null)
            return result;
        Queue<TreeNode> que = new LinkedList<>();
        que.add(root);
        while (!que.isEmpty()) {
            int n = que.size();
            int ans = Integer.MIN_VALUE;
            for (int i = 0; i < n; i++) {
                root = que.poll();
                ans = Math.max(ans, root.val);
                if (root.left != null)
                    que.add(root.left);
                if (root.right != null)
                    que.add(root.right);
            }

            if (ans != Integer.MIN_VALUE) {
                result.add(ans);
            }
        }
        return result;
    }
}
