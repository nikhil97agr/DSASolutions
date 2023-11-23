package Leetcode;

//Problem Link : https://leetcode.com/problems/predict-the-winner

public class PredictWinner {
    public boolean predictTheWinner(int nums[]) {
        int n = nums.length;
        Integer dp[][][] = new Integer[n][n][2];

        return solve(nums, 0, n - 1, dp, 0) >= 0;   //score of player 1 is less than zero tham means player 2 has more score

    }

    private int solve(int nums[], int start, int end, Integer dp[][][], int player) {
        if (start > end) {  //if no elements left to traverse
            return 0;
        }

        if (dp[start][end][player] != null) //if this indexes for this player is already calculated
            return dp[start][end][player];

        if (player == 0) {  
            dp[start][end][player] = Math.max(nums[start] + solve(nums, start + 1, end, dp, 1),
                    nums[end] + solve(nums, start, end - 1, dp, 1));
        } else {
            dp[start][end][player] = Math.min(-nums[start] + solve(nums, start + 1, end, dp, 0),
                    -nums[end] + solve(nums, start, end - 1, dp, 0));
        }

        return dp[start][end][player];
    }

}
