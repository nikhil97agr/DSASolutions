package Leetcode;

//Problem Link : https://leetcode.com/problems/number-of-music-playlists

public class NumberOfMusicPlaylists {
    public int numMusicPlaylists(int n, int goal, int k) {

        int mod = (int) 1e9 + 7;

        int dp[][] = new int[goal + 1][n + 1];

        dp[0][0] = 1;

        for (int i = 1; i <= goal; i++) {
            for (int j = 1; j <= Math.min(i, n); j++) {
                dp[i][j] = (int) ((1l * dp[i - 1][j - 1] * (n - j + 1)) % mod); // if a new song is added

                if (j > k) {
                    dp[i][j] = (int) ((1l * dp[i][j] + (1l * dp[i - 1][j] * (j - k) % mod)) % mod); // if reusing any
                                                                                                    // old song
                }
            }
        }
        return dp[goal][n];

    }
}
