package Leetcode;

//Problem Link : https://leetcode.com/problems/coin-change-ii/

class CoinChangeII {
    public int change(int amount, int[] coins) {
        int n = coins.length;
        Integer dp[][] = new Integer[n][amount + 1];

        return solve(n, coins, 0, amount, dp);
    }

    private int solve(int n, int coins[], int i, int amount, Integer dp[][]) {
        if (amount == 0) {
            return 1;
        } else if (i == n || amount < 0) {
            return 0;
        }

        if (dp[i][amount] != null) {
            return dp[i][amount];
        }

        dp[i][amount] = solve(n, coins, i, amount - coins[i], dp) + solve(n, coins, i + 1, amount, dp);
        return dp[i][amount];
    }
}