package Leetcode;

import java.util.Stack;

//Problem Link: https://leetcode.com/problems/design-a-text-editor/

public class TextEditor {
    Stack<Character> left;
    Stack<Character> right;
    public TextEditor() {
        left = new Stack<>();
        right = new Stack<>();
    }

    public void addText(String text) {
        for(char c : text.toCharArray()){
            left.push(c);
        }
    }

    public int deleteText(int k) {
        int cnt = 0;
        while(k--> 0 && !left.isEmpty()){
            left.pop();
            cnt++;
        }
        return cnt;
    }

    public String cursorLeft(int k) {
        while(k-- > 0 && !left.isEmpty()){
            right.push(left.pop());
        }

        return generate();
    }

    public String cursorRight(int k) {
        while(k-- > 0 && !right.isEmpty()){
            left.push(right.pop());
        }

        return generate();
    }

    private String generate(){
        StringBuilder sb = new StringBuilder();
        Stack<Character> temp = (Stack)left.clone();
        int len = Math.min(10, temp.size());
        while(len-- > 0){
            sb.append(temp.pop());
        }
        return sb.reverse().toString();
    }
}
/**
 * Your Leetcode.TextEditor object will be instantiated and called as such:
 * Leetcode.TextEditor obj = new Leetcode.TextEditor();
 * obj.addText(text);
 * int param_2 = obj.deleteText(k);
 * String param_3 = obj.cursorLeft(k);
 * String param_4 = obj.cursorRight(k);
 */