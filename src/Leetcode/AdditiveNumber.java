package Leetcode;

import java.math.BigInteger;

public class AdditiveNumber {
    public boolean isAdditiveNumber(String num) {

        int n = num.length();

        for(int i=1;i<n;i++){
            if(num.startsWith("0") && i > 1) return false;
            BigInteger b1 = new BigInteger(num.substring(0, i));
            for(int j=i+1;j<n;j++){
                String sub = num.substring(i, j);

                if(sub.startsWith("0") && sub.length() > 1) break;
                BigInteger b2 = new BigInteger(num.substring(i, j));
                if(check(b1, b2, num, j, n)){
                    return true;
                }
            }
        }

        return false;
    }

    private boolean check(BigInteger b1, BigInteger b2, String num, int i, int n) {
        if(i==n) return true;

        for(int j=i+1;j <=n;j++){
            String sub = num.substring(i, j);
            if(sub.startsWith("0") && sub.length() > 1) break;
            BigInteger b3 = new BigInteger(sub);

            if(check(b1, b2, b3) && check(b2, b3, num, j, n)){
                return true;
            }

        }

        return false;
    }

    private boolean check(BigInteger b1, BigInteger b2, BigInteger b3){
        BigInteger res = b1.add(b2);

        return res.equals(b3);
    }
}