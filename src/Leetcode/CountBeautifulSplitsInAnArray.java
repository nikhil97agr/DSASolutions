package Leetcode;
//Problem Link: https://leetcode.com/problems/count-beautiful-splits-in-an-array/

public class CountBeautifulSplitsInAnArray {

    int mod = 1_000_000_007;

    public int beautifulSplits(int[] nums) {

        int n = nums.length;
        if (n <= 2) {
            return 0;
        }
        int ans = 0;
        int p = 59;

        int hash[][] = new int[n][n];
        for (int i = 0; i < n; i++) {
            int h = 0;
            for (int j = i; j < n; j++) {
                h = add(prod(h, p), add(nums[j], 1));
                hash[i][j] = h;
            }
        }

        for (int i = 0; i < n - 2; i++) {
            int len = i + 1;
            int start = i + 1;
            int end = i + len;
            if (end < n - 1 && hash[0][start - 1] == hash[start][end]) {
                ans += (n - 1 - end);

                for (int j = i + 1; j < end; j++) {
                    len = j - i;
                    start = j + 1;
                    int e = j + len;
                    if (e < n && hash[i + 1][j] == hash[start][e]) {
                        ans++;
                    }
                }
                continue;
            }
            for (int j = i + 1; j < n - 1; j++) {
                len = j - i;
                start = j + 1;
                end = j + len;
                if (end < n && hash[i + 1][j] == hash[start][end]) {
                    ans++;
                }

            }
        }

        return ans;

    }

    private int add(long a, long b) {

        return (int) ((a + b) % mod);

    }

    private int prod(long a, long b) {

        return (int) ((a * b) % mod);
    }

}