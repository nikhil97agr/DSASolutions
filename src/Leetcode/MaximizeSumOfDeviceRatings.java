package Leetcode;//Problem Link: https://leetcode.com/problems/maximize-sum-of-device-ratings/description/

public class MaximizeSumOfDeviceRatings {

    public long maxRatings(int[][] units) {

        var max = 1_000_000;
        var n = units.length;
        var m = units[0].length;

        var m1 = new long[n];
        var m2 = new long[n];

        var gain = new long[n];

        var sum = 0l;
        var extra = 0l;
        var ans = 0l;
        var min = 1l * max;
        var cnt = 0;

        for (var i = 0; i < n; i++) {
            var first = max;
            var second = max;

            for (var x : units[i]) {
                if (x < first) {
                    second = first;
                    first = x;

                } else if (x < second) {
                    second = x;
                }
            }

            m1[i] = first;
            m2[i] = (m == 1) ? 0 : second;

            gain[i] = Math.max(0, m2[i] - m1[i]);

            sum += m1[i];
            extra += gain[i];

            min = Math.min(m1[i], min);
        }

        ans = sum;

        for (int i = 0; i < n; i++) {
            if (m1[i] == min) {
                ans = Math.max(ans, sum + extra - gain[i]);

            } else {
                long newAns = sum + extra - gain[i] - m1[i] + min;
                ans = Math.max(ans, newAns);
            }
        }

        return ans;
    }
}
