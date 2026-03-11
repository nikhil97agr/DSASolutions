package Leetcode;

import java.util.Arrays;

//Problem Link: https://leetcode.com/problems/next-palindrome-using-same-digits/description/
public class NextPalindromeUsingSameDigits {

    public String nextPalindrome(String num) {

        int n = num.length();
        int j = -1;
        int k = -1;
        if (n == 1) {
            return "";
        }
        char ch[] = num.toCharArray();
        for (int i = 1; i < n / 2; i++) {
            if (ch[i] > ch[i - 1]) {
                j = i - 1;
            }

            if (j != -1 && ch[i] > ch[j]) {
                k = i;
            }

        }

        if (j == -1) {
            return "";
        }

        char temp = ch[j];
        ch[j] = ch[k];
        ch[k] = temp;

        char t[] = Arrays.copyOfRange(ch, j + 1, n / 2);
        Arrays.sort(t);

        StringBuilder first = new StringBuilder();
        for (int i = 0; i <= j; i++) {
            first.append(ch[i]);
        }
        first.append(new String(t));

        StringBuilder second = new StringBuilder(first.toString());
        if (n % 2 == 1) {
            first.append(num.charAt(n / 2));
        }

        return first + second.reverse().toString();
    }
}