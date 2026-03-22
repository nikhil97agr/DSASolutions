package Leetcode;//Problem Link: https://leetcode.com/problems/form-largest-integer-with-digits-that-add-up-to-target

public class FormLargestIntegerWithDigitsThatAddUpToTarget {

    public String largestNumber(int[] cost, int target) {

        return solve(cost, target, 9);
    }

    private String solve(int cost[], int target, int num) {

        if (target == 0) {
            return "";
        }
        if (num == 0) {
            return "0";
        }

        String res = "0";
        if (cost[num - 1] <= target) {
            String next = solve(cost, target - cost[num - 1], num);
            if (!next.equals("0")) {
                res = num + next;
            }
        }

        String next = solve(cost, target, num - 1);

        if (next.length() > res.length() || (next.length() == res.length() && next.compareTo(res) > 0)) {
            res = next;
        }

        return res;
    }
}