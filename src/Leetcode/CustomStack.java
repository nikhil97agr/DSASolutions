package Leetcode;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Stack;

class CustomStack {
    int max;
    Deque<Integer> que;
    public CustomStack(int maxSize) {
            que = new LinkedList<>();
            max = maxSize;
    }
    
    public void push(int x) {
        if(que.size() == max) return ;

        que.offer(x);
    }
    
    public int pop() {
        if(que.isEmpty()) return -1;
        return que.pollLast();
    }
    
    public void increment(int k, int val) {
        Stack<Integer> stack = new Stack<>();

        int size = Math.min(k, que.size());
        while(size > 0){
            stack.push(que.poll());
            size--;
        }
        while(!stack.isEmpty()){
            que.offerFirst(stack.pop() + val);
        }
    }
}

/**
 * Your Leetcode.CustomStack object will be instantiated and called as such:
 * Leetcode.CustomStack obj = new Leetcode.CustomStack(maxSize);
 * obj.push(x);
 * int param_2 = obj.pop();
 * obj.increment(k,val);
 */