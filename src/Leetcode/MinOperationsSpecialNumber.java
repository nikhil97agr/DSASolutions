package Leetcode;

// Problem Link : https://leetcode.com/problems/minimum-operations-to-make-a-special-number/description/

public class MinOperationsSpecialNumber {
    public int minimumOperations(String num) {
        boolean zero = false;
        boolean five = false;

        int n = num.length();
        for (int i = n - 1; i >= 0; i--) {
            char ch = num.charAt(i);

            if (zero & (ch == '5' || ch == '0'))
                return n - i - 2;
            if (five && (ch == '2' || ch == '7'))
                return n - i - 2;

            if (ch == '0')
                zero = true;
            if (ch == '5')
                five = true;
        }

        if (zero)
            return n - 1;
        return n;
    }
}
