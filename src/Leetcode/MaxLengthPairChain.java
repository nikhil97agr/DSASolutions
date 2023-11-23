package Leetcode;

//Problem Link : https://leetcode.com/problems/maximum-length-of-pair-chain

import java.util.Arrays;

public class MaxLengthPairChain {
    public int findLongestChain(int[][] pairs) {
        Arrays.sort(pairs, (a, b) -> {
            return a[1] - b[1];
        });
        int ans = 0;

        int curr = -1001;
        for (int p[] : pairs) {
            if (curr < p[0]) {
                curr = p[1];
                ans++;
            }
        }

        return ans;

    }

}
