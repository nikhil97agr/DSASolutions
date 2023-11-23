package Leetcode;

//Problem Link  :https://leetcode.com/problems/build-array-where-you-can-find-the-maximum-exactly-k-comparisons

public class BuildArrayFindMaxInKComparison {
    public int numOfArrays(int n, int m, int k) {
        Integer dp[][][] = new Integer[n][k + 1][m + 1];
        return solve(n, k, m, 0, 0, 0, (int) 1e9 + 7, dp);
    }

    // Solve using LIS
    private int solve(int n, int k, int m, int ind, int currSize, int largest, int mod, Integer dp[][][]){
        if(ind==n){
            if(currSize ==k) return 1;
            return 0;
        }
        if(dp[ind][currSize][largest]!=null) return dp[ind][currSize][largest];
        int total = 0;
        for(int i=1;i<=m;i++){
            if(i>largest){
                total = total + solve(n, k, m, ind+1, currSize+1, i, mod, dp)%mod;
            }else{
                total = total + solve(n, k, m, ind+1, currSize, largest, mod, dp)%mod;
            }
            total%=mod;
        }

        return dp[ind][currSize][largest] =  total;
    }
}
