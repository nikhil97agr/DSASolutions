package Leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

//Problem Link: https://leetcode.com/problems/longest-common-subpath

public class LongestCommonSubpath {

    long mod2 = (long) 1e9 + 7;
    long mod1 = (long) 1e9 + 9;

    public int longestCommonSubpath(int n, int[][] paths) {

        int start = 1;
        int end = Arrays.stream(paths).mapToInt(x -> x.length).min().getAsInt();
        int ans = 0;
        while (start <= end) {
            int mid = (start + end) / 2;

            if (possible(mid, paths, n)) {
                ans = mid;
                start = mid + 1;
            } else {
                end = mid - 1;
            }
        }

        return ans;
    }

    private boolean possible(int len, int paths[][], int n) {

        var map = new HashMap<String, Integer>();
        int p = n + 1;
        int pow1 = pow(p, len - 1, mod1);
        int pow2 = pow(p, len - 1, mod2);

        for (int path[] : paths) {
            int hash1 = 0;
            int hash2 = 0;
            var set = new HashSet<String>();
            for (int i = 0; i < len; i++) {
                int dig = path[i] + 1;
                hash1 = add(prod(hash1, p, mod1), dig, mod1);
                hash2 = add(prod(hash2, p, mod2), dig, mod2);
            }

            set.add(hash1 + ":" + hash2);
            for (int i = len; i < path.length; i++) {
                int prev = path[i - len] + 1;
                int curr = path[i] + 1;
                hash1 = add(hash1, -prod(pow1, prev, mod1), mod1);
                hash2 = add(hash2, -prod(pow2, prev, mod2), mod2);
                hash1 = prod(hash1, p, mod1);
                hash1 = add(hash1, curr, mod1);
                hash2 = prod(hash2, p, mod2);
                hash2 = add(hash2, curr, mod2);

                set.add(hash1 + ":" + hash2);
            }

            for (var h : set) {
                map.merge(h, 1, Integer::sum);
            }
        }

        for (int val : map.values()) {
            if (val == paths.length) {
                return true;
            }
        }

        return false;

    }

    private int pow(long a, long b, long mod) {

        if (b == 1 || a <= 1) {
            return prod(a, 1, mod);
        }
        if (b == 0) {
            return 1;
        }

        int p = pow(a, b / 2, mod);
        p = prod(p, p, mod);

        if (b % 2 == 1) {
            p = prod(p, a, mod);
        }

        return p;

    }


    private int add(long a, long b, long mod) {

        long sum = a + b + mod;

        return (int) (sum % mod);
    }

    private int prod(long a, long b, long mod) {

        return (int) ((a * b) % mod);
    }
}