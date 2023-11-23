package Leetcode;

// Problem Link : https://leetcode.com/problems/kth-largest-element-in-an-array

import java.util.PriorityQueue;

public class KthLargestElement {
    public int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> que = new PriorityQueue<>();
        int i = 0;
        int n = nums.length;
        while (que.size() < k) {
            que.add(nums[i++]);
        }

        while (i < n) {
            if (nums[i] > que.peek()) {
                que.poll();
                que.add(nums[i]);
            }
            i++;
        }

        return que.peek();
    }

}
