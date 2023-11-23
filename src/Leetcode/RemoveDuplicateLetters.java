package Leetcode;

//Problem Link : https://leetcode.com/problems/remove-duplicate-letters

import java.util.Stack;

public class RemoveDuplicateLetters {
    public String removeDuplicateLetters(String s) {
        Stack<Character> stack = new Stack<>();
        boolean visited[] = new boolean[26];
        int n = s.length();
        int lastPos[] = new int[26];
        for (int i = 0; i < n; i++) {
            lastPos[s.charAt(i) - 'a'] = i;
        }
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if (visited[c - 'a'])
                continue;
            while (!stack.isEmpty() && stack.peek() > c && i < lastPos[stack.peek() - 'a']) {
                visited[stack.pop() - 'a'] = false;
            }

            stack.push(c);
            visited[c - 'a'] = true;
        }

        char res[] = new char[stack.size()];
        int i = res.length;
        while (!stack.isEmpty()) {
            res[--i] = stack.pop();
        }

        return String.valueOf(res);
    }
}
