package Leetcode;

// Problem Link : https://leetcode.com/problems/implement-stack-using-queues/

import java.util.LinkedList;
import java.util.Queue;

public class StackUsingQueue {

    class MyStack {
        Queue<Integer> stack;

        public MyStack() {
            stack = new LinkedList<>();
        }

        public void push(int x) {
            stack.add(x);
        }

        public int pop() {
            Queue<Integer> temp = new LinkedList<>();
            while (stack.size() > 1) {
                temp.add(stack.poll());
            }
            int ans = stack.poll();
            stack = temp;
            return ans;
        }

        public int top() {
            Queue<Integer> temp = new LinkedList<>();
            while (stack.size() > 1) {
                temp.add(stack.poll());
            }
            int ans = stack.poll();
            temp.add(ans);
            stack = temp;
            return ans;
        }

        public boolean empty() {
            return stack.isEmpty();
        }
    }

}
