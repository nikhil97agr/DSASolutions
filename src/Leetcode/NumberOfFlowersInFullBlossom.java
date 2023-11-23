package Leetcode;

//Problem Link : https://leetcode.com/problems/number-of-flowers-in-full-bloom

import java.util.PriorityQueue;

public class NumberOfFlowersInFullBlossom {
    public int[] fullBloomFlowers(int[][] flowers, int[] people) {
        int n = people.length;
        int res[] = new int[n];
        PriorityQueue<int[]> que = new PriorityQueue<>((a, b) -> {
            if (a[0] == b[0]) {
                if (b[2] == -1) {
                    return 0;
                } else if (a[2] == -1) {
                    return 1;
                }
                return 0;
            }
            return a[0] - b[0];
        });
        for (int f[] : flowers) {
            que.add(new int[] { f[0], f[1], 0 });
        }

        for (int i = 0; i < n; i++) {
            que.add(new int[] { people[i], i, -1 });
        }

        PriorityQueue<Integer> q1 = new PriorityQueue<>();
        while (!que.isEmpty()) {
            int ele[] = que.poll();
            if (ele[2] == -1) {
                while (!q1.isEmpty() && q1.peek() < ele[0]) {
                    q1.poll();
                }
                res[ele[1]] = q1.size();
            } else {
                q1.add(ele[1]);
            }
        }
        return res;
    }
}
