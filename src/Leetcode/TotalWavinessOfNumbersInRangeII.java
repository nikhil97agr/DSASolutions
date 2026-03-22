package Leetcode;

//Problem Link:https://leetcode.com/problems/total-waviness-of-numbers-in-range-ii

public class TotalWavinessOfNumbersInRangeII {

    public long totalWaviness(long num1, long num2) {

        char c1[] = Long.toString(num1 - 1).toCharArray();
        char c2[] = Long.toString(num2).toCharArray();

        return solve(c2, 0, 1, -1, -1, 0, c2.length, new Long[c2.length][2][11][11][20]) - solve(c1, 0, 1, -1, -1, 0,
                c1.length, new Long[c1.length][2][11][11][20]);
    }

    private long solve(char ch[], int i, int flag, int prev1, int prev2, int sum, int n, Long dp[][][][][]) {

        if (i == n) {
            return sum;
        }
        if (dp[i][flag][prev1 + 1][prev2 + 1][sum] != null) {
            return dp[i][flag][prev1 + 1][prev2 + 1][sum];
        }
        long ans = 0;
        if (flag == 0) {

            for (int dig = 0; dig <= 9; dig++) {
                if (dig == 0) {
                    if (prev1 == -1) {
                        ans += solve(ch, i + 1, 0, -1, -1, sum, n, dp);
                    } else if (prev2 == -1) {
                        ans += solve(ch, i + 1, 0, dig, prev1, sum, n, dp);
                    } else {
                        int x = 0;
                        if (prev1 > prev2) {
                            x++;
                        }
                        ans += solve(ch, i + 1, 0, dig, prev1, sum + x, n, dp);
                    }
                    continue;
                }

                if (prev2 == -1) {
                    ans += solve(ch, i + 1, 0, dig, prev1, sum, n, dp);
                } else {
                    int x = 0;
                    if (prev1 > prev2 && prev1 > dig) {
                        x++;
                    } else if (prev1 < prev2 && prev1 < dig) {
                        x++;
                    }
                    ans += solve(ch, i + 1, 0, dig, prev1, sum + x, n, dp);
                }
            }
        } else {
            int dig = ch[i] - '0';
            for (int d = 0; d < dig; d++) {
                if (d == 0) {
                    if (prev1 == -1) {
                        ans += solve(ch, i + 1, 0, -1, -1, sum, n, dp);
                    } else if (prev2 == -1) {
                        ans += solve(ch, i + 1, 0, d, prev1, sum, n, dp);
                    } else {
                        int x = 0;
                        if (prev1 > prev2) {
                            x++;
                        }
                        ans += solve(ch, i + 1, 0, d, prev1, sum + x, n, dp);
                    }
                    continue;
                }

                if (prev2 == -1) {
                    ans += solve(ch, i + 1, 0, d, prev1, sum, n, dp);
                } else {
                    int x = 0;
                    if (prev1 > prev2 && prev1 > d) {
                        x++;
                    } else if (prev1 < prev2 && prev1 < d) {
                        x++;
                    }
                    ans += solve(ch, i + 1, 0, d, prev1, sum + x, n, dp);
                }
            }

            if (prev2 == -1) {
                ans += solve(ch, i + 1, 1, dig, prev1, sum, n, dp);
            } else {
                int x = 0;
                if (prev1 > prev2 && prev1 > dig) {
                    x++;
                } else if (prev1 < prev2 && prev1 < dig) {
                    x++;
                }
                ans += solve(ch, i + 1, 1, dig, prev1, sum + x, n, dp);
            }
        }

        return dp[i][flag][prev1 + 1][prev2 + 1][sum] = ans;
    }
}