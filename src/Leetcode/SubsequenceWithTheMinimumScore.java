package Leetcode;

import java.util.Arrays;

//Problem Link: https://leetcode.com/problems/subsequence-with-the-minimum-score

public class SubsequenceWithTheMinimumScore {

    public int minimumScore(String s, String t) {

        if (subseq(s, t)) {
            return 0;
        }
        int n = s.length();
        int m = t.length();
        int left[] = new int[m];
        int right[] = new int[m];
        Arrays.fill(right, -1);
        Arrays.fill(left, -1);

        for (int i = 0, j = 0; i < n && j < m; i++) {
            if (s.charAt(i) == t.charAt(j)) {
                left[j] = i;
                j++;
            }
        }
        for (int i = n - 1, j = m - 1; i >= 0 && j >= 0; i--) {
            if (s.charAt(i) == t.charAt(j)) {
                right[j] = i;
                j--;
            }
        }

        int ans = m;
        for (int j = m - 1; j >= 0 && right[j] != -1; j--) {
            ans = Math.min(ans, j);
        }

        for (int i = 0; i < m; i++) {
            if (left[i] == -1) {
                break;
            }
            int cnt = m - 1;
            int start = i + 1;
            int end = m - 1;

            while (start <= end) {
                int mid = (start + end) / 2;
                if (right[mid] > left[i]) {
                    cnt = mid - 1;
                    end = mid - 1;
                } else {
                    start = mid + 1;
                }
            }

            ans = Math.min(ans, cnt - i);
        }

        return ans;
    }

    private boolean subseq(String s, String t) {

        int n = s.length();
        int m = t.length();
        int i = 0;
        int j = 0;
        while (i < n && j < m) {
            if (s.charAt(i) == t.charAt(j)) {
                j++;
            }
            i++;
        }

        return j == m;
    }
}