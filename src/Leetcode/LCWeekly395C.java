package Leetcode;

public class LCWeekly395C {
    public long minEnd(int n, int x) {
        long ans = x;
        if(n==1) return x;
        n--;
        while(n > 0){
            int pow = (int) (Math.log(n)/Math.log(2));
            int i=0;
             int cnt = -1;
            while(true){
                if((ans & (1L <<i))==0){
                    cnt++;
                }
                if(cnt == pow)break;
                i++;
            }

            ans = ans | (1L<<i);
            n = n - (1 << pow);
            }

        return ans;
    }
}