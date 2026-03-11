package Leetcode;

import java.util.HashSet;

//Problem Link: https://leetcode.com/problems/longest-duplicate-substring/description/

public class LongestDuplicateSubString {

    long mod1 = (long) 1e9 + 7;
    long mod2 = (long) 1e9 + 9;
    private String result = "";

    public String longestDupSubstring(String s) {

        int start = 1;
        int end = s.length();
        while (start <= end) {
            int mid = (start + end) / 2;
            if (possible(mid, s)) {
                start = mid + 1;
            } else {
                end = mid - 1;
            }
        }

        return result;
    }

    private boolean possible(int len, String s) {

        int hash1 = 0;
        int hash2 = 0;

        int p = 31;
        int power1 = pow(p, len - 1, mod1);
        int power2 = pow(p, len - 1, mod2);
        HashSet<String> seen = new HashSet<>();

        for (int i = 0; i < len; i++) {
            int ind = s.charAt(i) - 'a';
            hash1 = add(prod(hash1, p, mod1), ind + 1, mod1);
            hash2 = add(prod(hash2, p, mod2), ind + 1, mod2);
        }
        seen.add(hash1 + ":" + hash2);

        for (int i = len; i < s.length(); i++) {
            int ind = i - len;
            int prevInd = s.charAt(ind) - 'a';
            hash1 = updateHash(hash1, prevInd, power1, mod1);
            hash2 = updateHash(hash2, prevInd, power2, mod2);
            int currInd = s.charAt(i) - 'a';
            hash1 = add(prod(hash1, p, mod1), currInd + 1, mod1);
            hash2 = add(prod(hash2, p, mod2), currInd + 1, mod2);

            String key = hash1 + ":" + hash2;
            if (seen.contains(key)) {
                result = s.substring(ind + 1, i + 1);
                return true;
            }
            seen.add(key);
        }

        return false;
    }

    private int updateHash(int currHash, int ind, int power, long mod) {

        return add(currHash, -prod(ind + 1, power, mod), mod);
    }

    private int prod(long a, long b, long mod) {

        return (int) ((a * b) % mod);
    }

    private int add(long a, long b, long mod) {

        return (int) ((a + b + mod) % mod);
    }

    private int pow(long a, long b, long mod) {

        if (a == 0 || b == 1) {
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
}