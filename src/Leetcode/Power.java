package Leetcode;

// Problem Link : https://leetcode.com/problems/powx-n

public class Power {

    public double myPow(double x, int n) {
        if (n == 0)
            return 1d;

        if (n == 1)
            return x;

        if (n < 0) {
            return myPow(1 / x, -n);
        }

        return n % 2 == 0 ? myPow(x * x, n / 2) : myPow(x * x, n / 2) * x;
    }

}
