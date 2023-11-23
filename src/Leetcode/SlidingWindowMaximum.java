package Leetcode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class SlidingWindowMaximum {
    public int[] maxSlidingWindow(int[] nums, int k) {
        PriorityQueue<Integer> que = new PriorityQueue<>(Collections.reverseOrder());

        int n = nums.length;
        int size = n - k + 1;

        int result[] = new int[size];
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < k; i++) {
            map.put(nums[i], map.getOrDefault(nums[i], 0) + 1);
            que.add(nums[i]);
        }

        int i = 0;
        int j = k;
        while (j < n) {
            while (!map.containsKey(que.peek())) {
                que.poll();
            }
            result[i] = que.peek();
            map.put(nums[i], map.get(nums[i]) - 1);
            if (map.get(nums[i]) == 0) {
                map.remove(nums[i]);
            }
            i++;
            map.put(nums[j], map.getOrDefault(nums[j], 0) + 1);
            que.add(nums[j]);
            j++;
        }

        while (!map.containsKey(que.peek())) {
            que.poll();
        }
        result[i] = que.peek();

        return result;

    }
}
