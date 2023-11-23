package Leetcode;

public class MaxOddBinaryNumber {
    public String maximumOddBinaryNumber(String s) {
        int cnt = 0;
        int zero = 0;
        for (char c : s.toCharArray()) {
            if (c == '1') {
                cnt++;
            } else {
                zero++;
            }
        }

        StringBuilder result = new StringBuilder();
        for (int i = 1; i < cnt; i++) {
            result.append("1");
        }

        for (int i = 1; i <= zero; i++) {
            result.append("0");
        }
        result.append("1");
        return result.toString();
    }

}
