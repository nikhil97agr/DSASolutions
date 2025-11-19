package Leetcode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//Problem Link: https://leetcode.com/problems/find-sum-of-array-product-of-magical-sequences/description/

public class FindSumOfArrayProductOfMagicalSequences {
    long mod = (long) 1e9 + 7;
    public int magicalSum(int m, int k, int[] nums) {
        int fact[] = new int[m+1];

        fact[0] = fact[1] =1;
        for(int i=2;i<=m;i++){
            fact[i] = prod(i, fact[i-1]);
        }
        Map<State, Integer> dp = new HashMap<>();
        return solve(0, nums, 0, m, k, fact, dp);
    }

    private int solve(int i, int nums[], int mask, int m, int k, int fact[], Map<State, Integer> dp){
        if(m==0 && Integer.bitCount(mask) == k) return 1;

        if(m==0 || i==nums.length) return 0;
        State state = new State(mask, i, k, m);
        if(dp.containsKey(state)) return dp.get(state);

        int ans = solve(i+1, nums, mask >> 1, m, k - (mask&1), fact, dp);

        for(int j=1;j<=m;j++){
            int newMask = mask + j;
            int res = solve(i+1, nums, newMask>>1, m - j, k - (newMask&1), fact, dp);
            int comb = comb(m, j, fact);
            int power = pow(nums[i], j);
            ans = add(ans, prod(res, prod(comb, power)));
        }

        dp.put(state, ans);

        return ans;


    }

    private int comb(int n, int r, int fact[]){
        int a= prod(fact[n], pow(fact[n-r], (int)(mod-2)));
        a = prod(a, pow(fact[r], (int)(mod-2)));

        return a;
    }

    private int add(int a, int b){
        long sum = mod +a +b;

        return (int)(sum%mod);
    }

    private int prod(int a, int b){
        long prod = (long) a * b;

        return (int)(prod%mod);
    }

    private int pow(int a, int b){
        if(a==0) return 0;
        if(a==1 || b==1) return a;
        if(b==0) return 1;

        int ans = pow(a, b/2);

        ans = prod(ans, ans);

        if((b&1)!=0){
            ans = prod(ans, a);
        }

        return ans;
    }

    class State{
        int mask;
        int i;
        int k;
        int m;

        public State(int mask, int i, int k, int m) {
            this.mask = mask;
            this.i = i;
            this.k = k;
            this.m = m;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return mask == state.mask && i == state.i && k == state.k && m == state.m;
        }

        @Override
        public int hashCode() {
            return Objects.hash(mask, i, k, m);
        }
    }


}