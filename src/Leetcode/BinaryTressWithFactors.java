package Leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//Problem Link : https://leetcode.com/problems/binary-trees-with-factors

public class BinaryTressWithFactors {
    class Solution {
        public int numFactoredBinaryTrees(int[] arr) {
            long mod = (long) 1e9 + 7;
            Set<Integer> set = new HashSet<>();

            Map<Integer, Long> dp = new HashMap<>();
            int n = arr.length;
            Arrays.sort(arr);
            for (int x : arr) {
                dp.put(x, 1l);
                set.add(x);
            }
            for (int i = 0; i < n; i++) {
                solve(arr[i], i, dp, mod, arr, set);
            }
            long ans = 0;
            for (int i = 0; i < n; i++) {
                ans += dp.get(arr[i]);
                ans %= mod;
            }
            return (int) ans;
        }

        private void solve(int num, int ind, Map<Integer, Long> dp, long mod, int arr[], Set<Integer> set) {
            for (int j = ind - 1; j >= 0; j--) {
                if (num % arr[j] == 0) {
                    if (num / arr[j] == arr[j]) {
                        dp.put(num, (dp.get(num) + (dp.get(arr[j]) * dp.get(arr[j])) % mod) % mod);
                    } else if (set.contains(num / arr[j]) && arr[j] > (num / arr[j])) {
                        dp.put(num, (dp.get(num) + (dp.get(arr[j]) * dp.get(num / arr[j]) * 2) % mod) % mod);
                    }
                }
            }
        }
    }
}
