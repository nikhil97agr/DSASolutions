package Leetcode;

// Problem Link : https://leetcode.com/problems/minimum-speed-to-arrive-on-time

public class MinimumSpeedToArrive {

    public int minSpeedOnTime(int dist[], double hour) {
        int start = 1;
        int end = 10000000;

        int ans = -1;

        while (start <= end) {
            int mid = start + (end - start) / 2;

            if (possible(dist, mid, hour)) {
                ans = mid;
                end = mid - 1;
            } else {
                start = mid + 1;
            }
        }
        return ans;
    }

    private boolean possible(int dist[], int speed, double hour) {
        double total = 0d;
        int n = dist.length;
        for (int i = 0; i < n; i++) {
            double time = (1.0d * dist[i]) / (1.0d * speed);

            if (i == n - 1) {
                total += time;
            } else {
                total += Math.ceil(time);
            }

            if (total > hour)
                return false;

        }
        return total <= hour;
    }

}
