package Leetcode;
//Problem Link: https://leetcode.com/problems/minimum-cost-to-divide-array-into-subarrays/

/**
 *
 * Let n(l,r) is sum of nums from l to r
 * <p>
 * Let c(l,r) is sum of cost from l to r
 * <p>
 * => cost(i) = (n(0,r) + k * i)*(c(l,r))
 * <p>
 * => cost(3) = (n(0, r1) + k * 1) * c(0,r1) + (n(0, r2) + 2 * k) *c(r1 + 1, r2) + (n(0, n-1) + 3 *k) *c(r2 +1, n-1)
 * <p>
 * => let eq1 = (n(0, r1)*c(0,r1) + n(0,r2)*c(r1 + 1, r2) + n(0, n-1)*c(r2 + 1, n-1))
 * <p>
 * => let eq2 =  k * (c(0, r1) + 2c(r1 + 1, r2) + 3c(r2 + 1, n-1))
 * <p>
 * => By solving eq2, <br/> k * ((c(0,r1)+c(r1+1, r2) + c(r2+1, n-1)) + k*(c(r1 + 1, r2) + c(r2 + 1, n-1)) + k * (c(r2 +
 * 1, n-1))
 * <p>
 * => eq3 = k * c(0, n-1) + k *c(r1 +1, n-1) + k * c(r2+1, n-1)
 * <p>
 * => By comparing eq1 and eq3, <br/> (n(0,r1)*c(0,r1) + k*c(0,n-1)) + n(0,r2)*c(r1+1, r2) + k*c(r1+1, n-1) + n(0,
 * n-1)*c(r2+1, n-1) + k*c(r2+1, n-1)
 * <p>
 * => this tells that for any pair of (l,r), cost will be <br/>
 * <b>=> n(0, r)*c(l,r) + k * (c(l, n-1)</b>
 *
 *
 *
 */
public class MinimumCostToDivideArrayIntoSubarrays {

    Long dp[];

    public long minimumCost(int[] nums, int[] cost, int k) {

        var n = nums.length;
        dp = new Long[n];
        var preNum = new long[n];
        var preCost = new long[n + 1];
        for (var i = 0; i < n; i++) {
            if (i > 0) {
                preNum[i] = preNum[i - 1];
            }
            preNum[i] += nums[i];
            preCost[i + 1] = preCost[i] + cost[i];
        }

        return solve(0, n, preNum, preCost, k);
    }

    private long solve(int start, int n, long preNum[], long preCost[], int k) {

        if (start == n) {
            return 0;
        }

        if (dp[start] != null) {
            return dp[start];
        }

        long ans = Long.MAX_VALUE;
        for (int end = start; end < n; end++) {
            long a = preNum[end] * (preCost[end + 1] - preCost[start]);
            long b = k * (preCost[n] - preCost[start]);
            ans = Math.min(ans, a + b + solve(end + 1, n, preNum, preCost, k));
        }

        return dp[start] = ans;
    }
}

