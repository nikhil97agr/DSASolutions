package Leetcode;

import java.util.LinkedList;
import java.util.Queue;

public class DecodeString {
    public String decodeString(String s) {
        Queue<Character> que = new LinkedList<>();
        for (char c : s.toCharArray()) {
            que.add(c);
        }
        return solve(que);

    }

    private String solve(Queue<Character> que) {
        StringBuilder ans = new StringBuilder();
        int num = 0;
        while (!que.isEmpty()) {
            char c = que.poll();
            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            } else if (c == ']') {
                break;
            } else if (c == '[') {
                String recursiveString = solve(que);
                for (int i = 0; i < num; i++) {
                    ans.append(recursiveString);
                }
                num = 0;
            } else {
                ans.append(c);
            }
        }
        return ans.toString();
    }
}
