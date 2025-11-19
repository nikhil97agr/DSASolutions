package Leetcode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MaximizeTheNumberOfPartitionsAfterOperations {
    Map<State, Integer> dp;
    public int maxPartitionsAfterOperations(String s, int k) {
        dp = new HashMap<>();
        return solve(s.toCharArray(), k, 0, 0, 1, s.length());
    }

    private int solve(char ch[], int k, int mask, int i, int flag, int n) {
        if (i == n) {
            if (mask == 0)
                return 0;

            return 1;
        }

        State state = new State(i, flag, mask);

        if(dp.containsKey(state)) return dp.get(state);
        int ans = 0;
        int ind = ch[i] - 'a';
        int bitCount = Integer.bitCount(mask);
        if (flag == 1) {
            for (char c = 'a'; c <= 'z'; c++) {
                int i1 = c - 'a';
                if ((mask & (1 << i1)) != 0) {
                    ans = Math.max(ans, solve(ch, k, mask, i + 1, 0, n));
                } else {
                    if (bitCount == k) {
                        ans = Math.max(ans, 1 + solve(ch, k, 1 << i1, i + 1, 0, n));
                    } else {
                        ans = Math.max(ans, solve(ch, k, mask | (1 << i1), i + 1, 0, n));
                    }

                }
            }
        }

        if((mask & (1<<ind))!=0){
            ans= Math.max(ans, solve(ch, k, mask, i+1, flag, n));
        }else{
            if(bitCount == k){
                ans = Math.max(ans, 1 + solve(ch, k, 1<<ind, i+1, flag, n));
            }else{
                ans = Math.max(ans, solve(ch, k, mask | (1<<ind), i+1, flag, n));
            }
        }

        dp.put(state, ans);
        return ans;

    }
    class State{
        int i;
        int flag;
        int mask;

        public State(int i, int flag, int mask) {
            this.i = i;
            this.flag = flag;
            this.mask = mask;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return i == state.i && flag == state.flag && mask == state.mask;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, flag, mask);
        }
    }
}