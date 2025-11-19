package Leetcode;

import java.util.LinkedList;

public class LongestPalindromicPathInGraph {
    Integer dp[][][];
    public int maxLen(int n, int[][] edges, String label) {
        LinkedList<Integer> adjList[] = new LinkedList[n];

        for(int i=0;i<n;i++){
            adjList[i] = new LinkedList<>();
        }
        dp = new Integer[1<<n][n][n];
        for(int e[] : edges){
            int u =e[0];
            int v =e[1];

            adjList[u].add(v);
            adjList[v].add(u);


        }
        int par[] = new int[n];
        char ch[] = label.toCharArray();
        int cnt = 0;
        boolean vis[] = new boolean[n];
        for(int i=0;i<n;i++){
            if(!vis[i]){
                dfs(vis, cnt, par, i,adjList);
                cnt++;
            }
        }

        int ans = 1;
        for(int i=0;i<n;i++){
            for(int j=i+1;j<n;j++){
                if(par[i] == par[j] && ch[i] ==ch[j]){

                    ans = Math.max(ans, dfs(i, j, ch, (1<<i) | (1<<j), adjList));

                }
            }
        }


        return ans;
    }
    private int dfs(int u, int v, char ch[], int mask, LinkedList<Integer> adjList[]){
        if(u==v) return 1;

        if(dp[mask][u][v] != null) return dp[mask][u][v];

        int ans = 0;

        for(int u1 : adjList[u]){
            for(int v1 : adjList[v]){

                if((mask&(1<<u1)) == 0 && (mask&(1<<v1)) ==0 && ch[u1] == ch[v1]){
                    ans = Math.max(ans, dfs(u1, v1, ch, mask | (1<<u1) | (1<<v1), adjList));
                }

            }
        }
        if(ans ==0 && !adjList[u].contains(v)) return dp[mask][u][v] = 0;

        return dp[mask][u][v] = ans + 2;
    }
    private void dfs(boolean vis[], int cnt, int par[], int u, LinkedList<Integer> adjList[]){
        vis[u] = true;
        par[u] = cnt;

        for(int v : adjList[u]){
            if(!vis[v]) dfs(vis, cnt, par, v, adjList);
        }
    }
}