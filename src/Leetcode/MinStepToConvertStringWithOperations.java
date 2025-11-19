package Leetcode;
//Problem Link: https://leetcode.com/contest/weekly-contest-453/problems/minimum-steps-to-convert-string-with-operations/description/

public class MinStepToConvertStringWithOperations {

    public int minOperations(String word1, String word2) {
        int n = word1.length();
        Integer dp[] = new Integer[n];
        return solve(0, word1, word2, n, dp);
    }

    private int solve(int i, String s1, String s2, int n, Integer dp[]){
        if(i==n) return 0;
        if(dp[i] !=null) return dp[i];
        int ans = Integer.MAX_VALUE;
        for(int j=i;j<n;j++){
            int res = process(s1.substring(i, j+1), s2.substring(i, j+1));
            ans = Math.min(ans, res + solve(j+1, s1, s2, n, dp));
        }

        return  dp[i] = ans;
    }

    private int process( String s1, String s2){
        StringBuilder sb = new StringBuilder(s1).reverse();

        return Math.min(calc(s1, s2), 1+calc(sb.toString(), s2));

    }

    private int calc(String s1, String s2){
        if(s1.equals(s2)) return 0;
        int ans = 0;
        int cnt = 0;
        boolean vis[] = new boolean[s1.length()];
        int n = s1.length();

        for(int i=0;i<s1.length();i++){
            if(s1.charAt(i) != s2.charAt(i) && !vis[i]){
                char c = s1.charAt(i);
                boolean found= false;
                for(int j=i+1;j<n;j++){
                    if(c == s2.charAt(j) && s1.charAt(j) != s2.charAt(j) && !vis[j]){
                        if(s2.charAt(i) == s1.charAt(j)){
                            cnt++;
                            vis[i] = true;
                            vis[j] = true;
                            found = true;
                            break;
                        }
                    }
                }
                if(!found){
                    cnt++;
                }
            }

        }

        return cnt;

    }
}