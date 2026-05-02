package Leetcode;

import java.math.BigInteger;

//Problem Link: https://leetcode.com/problems/count-numbers-with-non-decreasing-digits

public class CountNumbersWithNonDecreasingDigits {

    Integer dp[][][];

    public int countNumbers(String l, String r, int base) {

        BigInteger integer = new BigInteger(l);
        integer = integer.subtract(BigInteger.ONE);
        char c1[] = baseB(integer.toString(), base);

        char c2[] = baseB(r, base);
        dp = new Integer[c1.length][10][2];
        int a = solve(c1, c1.length, base, 0, 0, 1);
        dp = new Integer[c2.length][10][2];

        int b = solve(c2, c2.length, base, 0, 0, 1);

        return add(b, -a);

    }

    private int solve(char ch[], int n, int base, int i, int prev, int flag) {

        if (i == n) {
            return 1;
        }

        if (dp[i][prev][flag] != null) {
            return dp[i][prev][flag];
        }

        int limit = (flag == 1) ? ch[i] - '0' : base - 1;
        int ans = 0;

        for (int d = prev; d <= limit; d++) {
            int newFlag = (flag == 1 && d == limit) ? 1 : 0;
            ans = add(ans, solve(ch, n, base, i + 1, d, newFlag));
        }

        return dp[i][prev][flag] = ans;

    }

    private int add(int a, int b) {

        int mod = 1_000_000_007;
        return (int) ((1l * a + b + mod) % mod);
    }

    public char[] baseB(String num, int base) {

        BigInteger a = new BigInteger(num);
        BigInteger b = new BigInteger("" + base);

        StringBuilder res = new StringBuilder();
        while (a.compareTo(BigInteger.ZERO) > 0) {
            BigInteger rem = a.mod(b);
            res.append(rem);
            a = a.divide(b);
        }

        return res.reverse().toString().toCharArray();
    }


}