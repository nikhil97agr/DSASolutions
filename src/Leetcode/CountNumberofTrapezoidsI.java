package Leetcode;

import java.util.HashMap;
import java.util.Map;

public class CountNumberofTrapezoidsI {
    public int countTrapezoids(int[][] points) {
        Map<Integer, Integer> map = new HashMap<>();

        for(int p[]: points){
            map.merge(p[1], 1, Integer::sum);
        }

        Map<Integer, Integer> pairs= new HashMap<>();
        int sum = 0;
        int ans = 0;
        for(Map.Entry<Integer, Integer> entry : map.entrySet()){
            int key = entry.getKey();
            int val = entry.getValue();

            int prod = prod(val, val-1)/2;

            pairs.put(key, prod);
            sum  = sum(sum, prod);
        }


        for(Map.Entry<Integer, Integer> entry: pairs.entrySet()){
            int s = entry.getValue();
            sum = sum(sum, -s);

            ans = sum(ans, prod(sum, s));
        }

        return ans;


    }

    private int prod(int a, int b){
        long prod = 1l*a*b;
        long mod = (long)1e9 + 7;

        return (int)(prod%mod);
    }

    private int sum(int a, int b){

        long mod = (long)1e9+ 7;
        long sum = a+b+mod;

        return (int)(sum%mod);
    }
}