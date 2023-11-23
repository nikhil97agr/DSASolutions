package Leetcode;

//Problem link : https://leetcode.com/problems/asteroid-collision/

import java.util.Stack;

public class AsteroidCollision {
    public int[] asteroidCollision(int[] asteroids) {
        Stack<Integer> stack = new Stack<>();
        int n = asteroids.length;
        for (int i = 0; i < n; i++) { // push all the asteroids one by one in the stack
            if (stack.isEmpty()) { // if there is no asteroids present in the stack
                stack.push(asteroids[i]);
                continue;
            }
            if ((stack.peek() > 0 && asteroids[i] > 0) || (stack.peek() < 0 && asteroids[i] < 0)) { // check if the
                                                                                                    // asteroid at the
                                                                                                    // top & the current
                                                                                                    // asteroid are
                                                                                                    // moving in the
                                                                                                    // same direction
                stack.push(asteroids[i]);
                continue;
            }
            if (stack.peek() > 0) {

                if (stack.peek() < Math.abs(asteroids[i])) { // if the current asteroid has more power than the previous
                                                             // asteroid
                    i--;
                    stack.pop();
                } else if (stack.peek() == Math.abs(asteroids[i])) { // if both asteroid have same power then both will
                                                                     // destroyed
                    stack.pop();
                }
            } else {
                stack.push(asteroids[i]);
            }
        }
        int ans[] = new int[stack.size()];
        for (int i = stack.size() - 1; i >= 0; i--) {
            ans[i] = stack.pop();
        }
        return ans;
    }
}