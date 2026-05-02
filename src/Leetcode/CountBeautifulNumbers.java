package Leetcode;

import java.util.HashMap;
import java.util.Map;

//Problem Link: https://leetcode.com/problems/count-beautiful-numbers

public class CountBeautifulNumbers {

    Map<String, Integer> dp;

    public int beautifulNumbers(int l, int r) {

        dp = new HashMap<>();
        char c1[] = Integer.toString(l - 1).toCharArray();
        char c2[] = Integer.toString(r).toCharArray();

        int a = solve(c1, c1.length, 0, 0, 1, 1, 0, 0);
        dp = new HashMap<>();
        int b = solve(c2, c2.length, 0, 0, 1, 1, 0, 0);
        return b - a;
    }

    private int solve(char ch[], int n, int i, int sum, int prod, int flag, int started, int zero) {

        if (i == n) {
            if (started == 0) {
                return 0;
            }
            if (zero == 1 || prod % sum == 0) {
                return 1;
            }
            return 0;
        }

        String key = i + ":" + sum + ":" + prod + ":" + flag + ":" + started + ":" + zero;
        if (dp.containsKey(key)) {
            return dp.get(key);
        }
        int ans = 0;
        int limit = (flag == 1) ? ch[i] - '0' : 9;
        for (int d = 0; d <= limit; d++) {
            int nextFlag = (flag == 1 && d == limit) ? 1 : 0;

            if (started == 0) {
                if (d == 0) {
                    ans += solve(ch, n, i + 1, sum, prod, nextFlag, started, zero);
                } else {
                    ans += solve(ch, n, i + 1, sum + d, prod * d, nextFlag, 1, zero);
                }
            } else {
                ans += solve(ch, n, i + 1, sum + d, prod * d, nextFlag, started, (zero == 1 || d == 0) ? 1 : 0);
            }
        }
        dp.put(key, ans);
        return ans;
    }
}