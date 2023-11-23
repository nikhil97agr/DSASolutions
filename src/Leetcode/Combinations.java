package Leetcode;

import java.util.ArrayList;
import java.util.List;

// Problem Link : https://leetcode.com/problems/combinations

public class Combinations {
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        solve(1, n, k, result, new ArrayList<>());
        return result;
    }

    private void solve(int i, int n, int k, List<List<Integer>> result, List<Integer> temp) {
        if (k == 0) {
            result.add(new ArrayList<>(temp));
            return;
        }

        if (i > n) {
            return;
        }

        temp.add(i);
        solve(i + 1, n, k - 1, result, temp);
        temp.remove(temp.size() - 1);
        solve(i + 1, n, k, result, temp);
    }
}