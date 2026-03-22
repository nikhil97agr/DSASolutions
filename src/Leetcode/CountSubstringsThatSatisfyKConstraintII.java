package Leetcode;

//Problem Link: https://leetcode.com/problems/count-substrings-that-satisfy-k-constraint-ii/

public class CountSubstringsThatSatisfyKConstraintII {

    public long[] countKConstraintSubstrings(String s, int k, int[][] queries) {

        int n = s.length();
        long ans[] = new long[queries.length];
        int subString[] = new int[n];
        int one = 0;
        int zero = 0;
        int left = 0;
        int right = 0;
        while (right < n) {
            if (s.charAt(right) == '0') {
                zero++;
            } else {
                one++;
            }
            while (left <= right && Math.min(one, zero) > k) {
                if (s.charAt(left) == '0') {
                    zero--;
                } else {
                    one--;
                }
                left++;
            }

            subString[right] = left;
            right++;
        }

        long pre[] = new long[n];
        pre[0] = 1;

        for (int i = 1; i < n; i++) {
            pre[i] = pre[i - 1] + (i - subString[i] + 1);
        }

        for (int i = 0; i < queries.length; i++) {
            int l = queries[i][0];
            int r = queries[i][1];
            int ind = search(subString, l, r, l);
            long len = ind - l + 1;
            long total = (len * (len + 1)) / 2 + (pre[r] - pre[ind]);
            ans[i] = total;
        }

        return ans;
    }

    private int search(int substring[], int l, int r, int val) {

        int ind = -1;
        while (l <= r) {
            int mid = (l + r) / 2;
            if (substring[mid] > val) {

                r = mid - 1;
            } else {
                ind = mid;
                l = mid + 1;
            }
        }

        return ind;
    }
}