package Leetcode;

//Problem Link : https://leetcode.com/problems/permutations/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Permutations {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> list = new ArrayList<>();
        int n = nums.length;
        Integer temp[] = new Integer[nums.length];
        solve(list, n, temp, nums, 0);
        return list;
    }

    private void solve(List<List<Integer>> result, int n, Integer temp[], int nums[], int i) {
        if (i == n) {
            result.add(Arrays.asList(temp.clone()));
            return;
        }

        for (int j = 0; j < n; j++) {
            if (nums[j] != -1000) {
                temp[i] = nums[j];
                nums[j] = -1000;
                solve(result, n, temp, nums, i + 1);
                nums[j] = temp[i];
            }
        }

        temp[i] = null;

    }
}