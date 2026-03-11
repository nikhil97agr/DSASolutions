package Leetcode;

import java.util.ArrayList;
import java.util.List;

//Problem Link: https://leetcode.com/problems/stamping-the-sequence/description/

public class StampingTheSequence {

    public int[] movesToStamp(String stamp, String target) {

        int n = stamp.length();
        int m = target.length();
        char s[] = stamp.toCharArray();
        char t[] = target.toCharArray();

        int cnt = 0;
        List<Integer> result = new ArrayList<>();
        boolean vis[] = new boolean[m];

        while (cnt != m) {
            boolean flag = false;
            for (int i = 0; i <= m - n; i++) {
                if (!vis[i] && possible(t, i, s)) {
                    cnt = update(t, i, n, cnt);
                    result.add(i);
                    vis[i] = true;
                    flag = true;

                    if (cnt == m) {
                        break;
                    }

                }
            }

            if (!flag) {
                return new int[0];
            }

        }

        int ans[] = new int[result.size()];
        for (int i = result.size() - 1; i >= 0; i--) {
            ans[result.size() - 1 - i] = result.get(i);
        }

        return ans;
    }

    private int update(char t[], int pos, int len, int cnt) {

        for (int i = 0; i < len; i++) {
            if (t[i + pos] != '?') {
                t[i + pos] = '?';
                cnt++;
            }
        }

        return cnt;
    }

    private boolean possible(char t[], int pos, char s[]) {

        for (int i = 0; i < s.length; i++) {
            if (t[i + pos] != '?' && t[i + pos] != s[i]) {
                return false;
            }
        }

        return true;
    }
}