package Leetcode;

//Problem Link : https://leetcode.com/problems/132-pattern/

import java.util.Stack;

public class Pattern132 {
    public boolean find132pattern(int[] nums) {
        Stack<Integer> stack = new Stack<>();
        int n = nums.length;
        
        //Stores the min number from left i.e. min[i] = min(nums[0....i])
        int min[] = new int[n];
        min[0] = nums[0];
        for(int i=1;i<n;i++){
            min[i] = Math.min(nums[i], min[i-1]);
        }

        for(int i=n-1;i>=0;i--){
            if(nums[i] > min[i]){
                while(!stack.isEmpty() && stack.peek()<=min[i]){
                    stack.pop();
                }
                if(!stack.isEmpty() && stack.peek() < nums[i]) return true;
            }
            stack.push(nums[i]);
        }
        return false;
    }
}
