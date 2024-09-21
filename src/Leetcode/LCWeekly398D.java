package Leetcode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LCWeekly398D{
    public int waysToReachStair(int k) {
        
        Map<Node, Long> dp = new HashMap<>();
        return (int)solve(1L, 1L*k, 0L, 0, dp);
    }
    
    private long solve(long i, long k, long jump, int prev, Map<Node, Long> dp){
        if(i > k+1) return 0;
        long ans = 0;
        Node key = new Node(i, jump, prev);

        if(dp.containsKey(key)) return dp.get(key);
        if(i==k) ans = 1;
        if(prev != 1){
            ans = ans + solve(i-1, k, jump, 1,dp);
        }
        ans = ans + solve(i + (1<<jump), k, jump+1, 2, dp);
        dp.put(key, ans);
        return ans;
        
    }
    
    class Node{
        long i;
        long jump;
        int prev;
        
        public Node(long i, long jump, int prev){
            this.i = i;
            this.jump = jump;
            this.prev = prev;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return i == node.i && jump == node.jump && prev == node.prev;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, jump, prev);
        }
    }
}