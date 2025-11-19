package Leetcode;

/**
 * Problem Link: https://leetcode.com/problems/number-of-strings-which-can-be-rearranged-to-contain-substring
 */

public class NumberOfStringContainSubstring {
    public int stringCount(int n) {
        int total = pow(26, n);
        int d1 = add(pow(25,n), add(pow(25,n), add(pow(25,n), prod(n, pow(25, n-1)))));
        int d2 = add(pow(24, n), add(pow(24,n), add(prod(n, pow(24, n-1)), add(pow(24, n), add(prod(n, pow(24,n-1)), 0)))));

        int d3 = add(
                pow(23, n),
                prod(n, pow(23, n-1))
        );


        int total2 = add(d1, add(-d2, d3));

        return add(total, -total2);
    }

    private int pow(long a, long b){
        long mod = (long)1e9 + 7;

        if(b == 1) return (int)a;
        if(b== 0) return 1;

        int p = pow(a, b/2);
        p = prod(p,p);
        if(b%2==1){
            p = prod(p, a);
        }

        return p;
    }

    private int prod(long a, long b){
        long mod = (long)1e9 + 7;
        return (int)((a*b)%mod);
    }

    private int add(long a, long b){
        long mod = (long)1e9 + 7;
        long total = a + b + mod;

        return (int)(total%mod);
    }
}