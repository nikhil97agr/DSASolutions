package Leetcode;

//Problem Link : https://leetcode.com/problems/shortest-palindrome/

public class ShortestPalindrome {
    public String shortestPalindrome(String s) {
        String reverse = s + ":" + new StringBuilder(s).reverse().toString();

        int n = reverse.length();

        int kmpTable[] = buildKmp(reverse, n);

        int len = kmpTable[n - 1];

        reverse = new StringBuilder(s.substring(len)).reverse().toString();
        return reverse + s;
    }

    private int[] buildKmp(String s, int n) {
        int table[] = new int[n];
        int i = 1;
        int j = 0;
        while (i < n) {
            if (s.charAt(i) == s.charAt(j)) {
                table[i] = ++j;
                i++;
            } else {
                if (j == 0) {
                    i++;
                } else {
                    j = table[j - 1];
                }
            }
        }

        return table;
    }
}
