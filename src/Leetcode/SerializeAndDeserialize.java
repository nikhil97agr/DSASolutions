package Leetcode;

import java.util.LinkedList;
import java.util.Queue;


public class SerializeAndDeserialize {

    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        if(root == null) return "()";

        String left = serialize(root.left);
        String right = serialize(root.right);
        StringBuilder sb = new StringBuilder().append("(").append(root.val).append(left).append(right).append(")");

        return sb.toString();
    }

    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        Queue<Character> que = new LinkedList<>();
        for(char c : data.toCharArray()){
            que.offer(c);
        }
        que.poll();
        return deserialize(que);
    }

    private TreeNode deserialize(Queue<Character> que){
        if(!que.isEmpty() && que.peek() == ')'){
            que.poll();
            return null;
        }
        int num = 0;
        while (Character.isDigit(que.peek())) {
            num = num*10 + (que.poll()-'0');

        }

        TreeNode root = new TreeNode(num);

        if(!que.isEmpty() && que.peek() == '('){
            que.poll();
            root.left = deserialize(que);
        }

        if(!que.isEmpty() && que.peek() == '('){
            que.poll();
            root.right = deserialize(que);
        }

        if(!que.isEmpty()){
            que.poll();
        }


        return root;
    }
}

// Your Codec object will be instantiated and called as such:
// Codec ser = new Codec();
// Codec deser = new Codec();
// String tree = ser.serialize(root);
// TreeNode ans = deser.deserialize(tree);
// return ans;