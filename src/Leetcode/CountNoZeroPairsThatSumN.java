package Leetcode;
//Problem Link: https://leetcode.com/contest/weekly-contest-470/problems/count-no-zero-pairs-that-sum-to-n/description/

public class CountNoZeroPairsThatSumN {
    public long countNoZeroPairs(long n) {
        long ans = 0;
        String s  = Long.toString(n);
        int len = s.length();
        char digits[] = new char[len];

        for(int i=0;i<len;i++){
            digits[i] = s.charAt(len -i - 1);
        }

        for(int i=1;i<=len;i++){
            for(int j=1;j<=len;j++){
                Long dp[][] = new Long[len + 1][10];
                ans += solve(0, digits, 0, dp, i, j);
            }
        }


        return ans;
    }

    private long solve(int i, char digits[], int carry, Long dp[][], int l1, int l2){


        if(i == digits.length){
            return carry == 0 ? 1 : 0;
        }

        if(dp[i][carry] !=null)  return dp[i][carry];

        long res = 0;
        int dig = digits[i] - '0';
        int arr[] = new int[]{1,2,3,4,5,6,7,8,9};
        int d1[] = i >= l1 ? new int[]{0} : arr;
        int d2[] = i >= l2 ? new int[]{0} : arr;

        for(int x : d1){
            for(int y : d2){
                int sum = x + y + carry;

                if(sum % 10 == dig){
                    res += solve(i+1, digits, sum / 10, dp, l1, l2);
                }
            }
        }

        return dp[i][carry] = res;
    }
}