package Leetcode;

public class ShortestSupersequence {
    public static void main(String[] args) {
        System.out.println(new ShortestSupersequence().shortestCommonSupersequence("abac", "cab"));
    }
    public String shortestCommonSupersequence(String str1, String str2) {
        int n = str1.length();
        int m = str2.length();
        String lcs = lcs(str1, str2, n, m);
        StringBuilder res = new StringBuilder();

        int i=0;
        int j=0;

        for(char c : lcs.toCharArray()){
            while(i < n && str1.charAt(i) != c){
                res.append(str1.charAt(i));
                i++;
            }
            i++;
            while(j< m && str2.charAt(j) != c){
                res.append(str2.charAt(j));
                j++;
            }
            j++;
            res.append(c);
        }
        while(i< n ){
            res.append(str1.charAt(i));
            i++;
        }
        while(j < m){
            res.append(str2.charAt(j));
            j++;
        }

        return res.toString();
    }


    private String lcs(String s1, String s2, int n, int m){
        Integer dp[][] = new Integer[n+1][m+1];
        for(int i=0;i<=n;i++) dp[i][m] = 0;
        for(int j=0;j<=m;j++) dp[n][j] = 0;
        int len = solve(s1, s2, 0, 0, n, m, dp);
        StringBuilder sb  = new StringBuilder();

        int i=0;
        int j=0;

        while(i< n && j < m){
            if(s1.charAt(i) == s2.charAt(j)){

                sb.append(s1.charAt(i));
                i++;
                j++;
            }else if(dp[i+1][j]> dp[i][j+1]){

                i++;
            }else{

                j++;
            }
        }


        return sb.toString();
    }

    private int solve(String s1, String s2, int i, int j, int n, int m, Integer dp[][]){
        if(i >=n || j>=m) return 0;

        if(dp[i][j]!=null) return dp[i][j];
        int ans =0;
        if(s1.charAt(i) == s2.charAt(j)){
            ans = 1 + solve(s1, s2, i+1, j+1, n, m, dp);
        }
        ans = Math.max(ans, Math.max(solve(s1, s2, i+1, j, n,m , dp), solve(s1, s2, i, j+1, n, m, dp)));

        return dp[i][j] = ans;
    }
}