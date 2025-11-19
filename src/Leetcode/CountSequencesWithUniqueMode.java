package Leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Count the number of subsequences of length 5 such that its mode is unique.
 * Where mode means the numbers which occurred maximum number of times in the sequence
 * and unique mode means the count of numbers occurred maximum number of times should be only 1
 */
public class CountSequencesWithUniqueMode {

    public static void main(String[] args) {
        System.out.println(new CountSequencesWithUniqueMode().subsequencesWithMiddleMode(new int[]{0,1,-1,1,1}));
    }
    long mod = (long)1e9+7;
    public int subsequencesWithMiddleMode(int[] nums) {


        long ans = 0;

        int n = nums.length;
        Map<Long, Long> fact = new HashMap<>();

        long prev = 1;
        for(long i=1;i<=n;i++){
            prev *=i;
            prev%=mod;

            fact.put(i, prev);
        }

        Map<Integer, Long> map = new HashMap<>();
        for(int x : nums){
            long cnt = map.getOrDefault(x, 0L);
            map.put(x, cnt + 1);
        }

        List<Integer> keys = new ArrayList<>(map.keySet());
fact.put(0l, 1l);
        for(int x : keys){
            long cnt = map.get(x);
            if( cnt < 3) continue;
            long left = n  - cnt;
            if(left < 2) continue;
            long cm1 = fact.get(cnt)/multiply(fact.get(3L), fact.get(cnt - 3));
            long cm2 = fact.get(left)/multiply(fact.get(2L), fact.get(left - 2));


            ans = add(ans, multiply(cm1, cm2));


        }
        for(int x : keys){
            long cnt = map.get(x);

            if(cnt < 4 ) continue;
            long left = n - cnt;
            if(left < 1) continue;
            long cm1 = fact.get(cnt)/multiply(fact.get(4L), fact.get(cnt - 4));
            long cm2 = fact.get(left)/multiply(1L, fact.get(left - 1));

            ans = add(ans, multiply(cm1, cm2));


        }

        for(int x : keys){
            long cnt = map.get(x);
            if(cnt < 5) continue;
            ans= add(ans, fact.get(cnt)/multiply(fact.get(5L), fact.get(cnt - 5)));
        }
        if(map.size() - 1 < 3){
            return (int)ans;
        }
        long s = map.size()-1;
        for(int x : keys){
            long cnt = map.get(x);
            if(cnt < 2) continue;


            long cm1 = fact.get(cnt)/multiply(fact.get(2L), fact.get(cnt - 2));
            long cm2 = fact.get(s)/multiply(fact.get(3L), fact.get(s - 3));

            for(int y : keys){
                if(y==x) continue;

                cm2 = multiply(cm2, map.get(y));
            }

            ans = add(ans, multiply(cm1, cm2));

        }



        return (int)ans;
    }


    private long multiply(long a, long b){
        return ((a%mod)*(b%mod))%mod;
    }

    private long add(long a, long b){
        return (a%mod + b%mod)%mod;
    }
}