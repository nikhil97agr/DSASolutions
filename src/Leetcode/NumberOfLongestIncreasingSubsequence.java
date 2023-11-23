package Leetcode;

// Problem link : https://leetcode.com/problems/number-of-longest-increasing-subsequence

import java.util.Arrays;

class NumberOfLongestIncreasingSubsequence {
    public int findNumberOfLIS(int[] arr) {
        int n = arr.length;
        int count[] = new int[n]; // this will store number of LIS till a particular index
        int dp[] = new int[n]; // this will store the LIS length till a particular index

        Arrays.fill(dp, 1);
        Arrays.fill(count, 1);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (arr[i] > arr[j]) { // if this is forming a increasing order
                    int len = dp[j] + 1; // find the next maximum length if jth element is added with ith element in the
                                         // subsequence
                    if (dp[i] < len) { // if already calculated max length of ith index is less than the newly
                                       // calculated length with jth index
                        dp[i] = len;
                        count[i] = 0;
                        count[i] += count[j]; // update the count with number of subsequence that are formed with jth
                                              // element as ending element
                    } else if (dp[i] == len) { // if this length is also the previously calculated max length then just
                                               // update the count
                        count[i] += count[j];
                    }
                }
            }
        }
        int maxlen = 0;
        for (int i = 0; i < n; i++) {
            maxlen = Math.max(maxlen, dp[i]); // find the maximum length of the overall LIS
        }

        int ans = 0;
        for (int i = 0; i < n; i++) {
            ans += (dp[i] == maxlen) ? count[i] : 0; // count total LIS till each index
        }
        return ans;

    }
}