package Leetcode;

//Problem Link : https://leetcode.com/problems/frog-jump/

import java.util.Map;
import java.util.HashMap;

public class FrogJump {
    public boolean canCross(int[] stones) {
        int n = stones.length;
        Map<Integer, Integer> map = new HashMap<>();
        Map<String, Boolean> dp = new HashMap<>();
        for (int i = 0; i < n; i++) {
            map.put(stones[i], i);
        }
        return solve(n, map, dp, stones, 0, 1, true);
    }

    private boolean solve(int n, Map<Integer, Integer> map, Map<String, Boolean> dp, int[] stones, int i, int jump,
            boolean isStart) {
        if (i < 0 || i >= n)
            return false;

        if (i == n - 1)
            return true;

        if (jump == 0)
            return false;

        String key = "" + i + ":" + jump;
        if (dp.containsKey(key)) {
            return dp.get(key);
        }

        boolean ans = false;
        if (map.containsKey(stones[i] + jump)) {
            ans = solve(n, map, dp, stones, map.get(stones[i] + jump), jump, false);
        }

        if (!isStart && map.containsKey(stones[i] + jump + 1) && !ans) {
            ans = solve(n, map, dp, stones, map.get(stones[i] + jump + 1), jump + 1, false);
        }

        if (!isStart && map.containsKey(stones[i] + jump - 1) && !ans) {
            ans = solve(n, map, dp, stones, map.get(stones[i] + jump - 1), jump - 1, false);
        }

        dp.put(key, ans);
        return ans;
    }
}
