package Leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SumSubseqWidths {
    public int sumSubseqWidths(int[] nums) {
        long mod = (long) 1e9 + 7;
        long res = 0;
        int n = nums.length;
        Map<Integer, Long> powers = new HashMap<>();
        powers.put(0, 1l);
        powers.put(1, 2l);
        calcPower(mod, powers, n);
        Arrays.sort(nums);

        for (int i = n - 1; i >= 0; i--) {
            long pow = powers.get(i) == null ? calcPower(mod, powers, i) : powers.get(i);
            powers.put(i, pow);
            res += ((nums[i] % mod) * (powers.get(i) % mod)) % mod;
        }

        for (int i = 0; i < n; i++) {
            long pow = powers.get(n - 1 - i) == null ? calcPower(mod, powers, n - i - 1) : powers.get(n - i - 1);
            powers.put(n - i - 1, pow);
            res -= ((nums[i] % mod) * (powers.get(n - 1 - i) % mod)) % mod;
        }
        return (int)  ((res+mod) % mod);

    }

    private long calcPower(long mod, Map<Integer, Long> pow, int n) {
        if (pow.containsKey(n)) {
            return pow.get(n) % mod;
        }

        long ans = ((calcPower(mod, pow, n / 2) % mod) * (calcPower(mod, pow, n - n / 2) % mod)) % mod;
        pow.put(n, ans);
        return ans;
    }
}
