package Leetcode;

//Problem Link : https://leetcode.com/problems/letter-combinations-of-a-phone-number

import java.util.ArrayList;
import java.util.List;

public class LetterCombinationsOfPhoneNumber {
    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        if (digits.length() == 0) {
            return new ArrayList<>();
        }
        solve(result, digits, digits.length(), 0, new char[digits.length()]);
        return result;
    }

    private void solve(List<String> result, String digits, int n, int i, char[] temp) {
        if (i == n) {
            result.add(String.valueOf(temp));
            return;
        }

        String chars = "";
        switch (digits.charAt(i)) {
            case '2':
                chars = "abc";
                break;
            case '3':
                chars = "def";
                break;
            case '4':
                chars = "ghi";
                break;
            case '5':
                chars = "jkl";
                break;
            case '6':
                chars = "mno";
                break;
            case '7':
                chars = "pqrs";
                break;
            case '8':
                chars = "tuv";
                break;
            case '9':
                chars = "wxyz";
                break;
        }

        for (char c : chars.toCharArray()) {
            temp[i] = c;
            solve(result, digits, n, i + 1, temp);
        }

    }

}
