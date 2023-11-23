package Leetcode;

import java.util.Arrays;

// Problem Link : https://leetcode.com/problems/maximum-running-time-of-n-computers/

public class MaximumRunningTimeOfComputer {

    public long maxRunTime(int n, int[] batteries) {

        long low = 0;
        long high = (Arrays.stream(batteries).mapToLong(a -> (long) a).sum()) / n;
        // find maximum time can be divided into all computers if all batteries are used
        // at their maximum capacity

        while (low < high) {
            long mid = (high + low + 1) / 2;

            long totalTimeByBattery = Arrays.stream(batteries).mapToLong(a -> Math.min(a, mid)).sum();
            // find the total time for which batteries will support if we target the mid
            // time

            if (totalTimeByBattery >= mid * n) {
                low = mid;
            } else {
                high = mid - 1;
            }
        }

        return low;
    }

}
