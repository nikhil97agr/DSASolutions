package Leetcode;

import java.util.Collections;
import java.util.TreeSet;

class SuperUglyNumber {
    public static void main(String[] args) {
        int n = 15;
        int primes[] = new int[]{3,5,7,11,19,23,29,41,43,47};
        System.out.println(nthSuperUglyNumber(n, primes));
    }
    public static int nthSuperUglyNumber(int n, int[] primes) {
        if(n==1) return 1;
        TreeSet<Long> q1 = new TreeSet<>();
        q1.add(1L);
        TreeSet<Long> q2 = new TreeSet<>(Collections.reverseOrder());
        q2.add(1L);
        while(!q1.isEmpty()){

            long y = q1.pollFirst();
            for(long x : primes){
                long z = x*y;
                if(q2.size() < n){
                    q2.add(z);
                    q1.add(z);
                }else{

                    if(z < q2.first() && !q2.contains(z)){
                        q2.pollFirst();
                        q1.add(z);
                        q2.add(z);
                    }
                }
            }

        }

        return Math.toIntExact(q2.first());

    }
}