package Leetcode;

import java.util.Arrays;
import java.util.LinkedList;

public class MinimumScoreAfterRemovalsonaTree {
    int res= Integer.MAX_VALUE;
    public int minimumScore(int[] nums, int[][] edges) {
        int n = nums.length;

        LinkedList<Integer> adjList[] = adjList(edges,n );

        int total = 0;
        for(int x : nums) total ^= x;

        dfs(0, -1, total, adjList, n, nums);

        return res;

    }

    private int dfs(int i, int par, int total, LinkedList<Integer> adjList[], int n, int[] nums) {

        int xor = nums[i];

        for(int child : adjList[i]){
            if(child != par){
                xor ^= dfs(child, i, total, adjList, n, nums);
            }
        }


        for(int child :adjList[i]){
            if(child == par){
                dfs2(child, i, total, adjList, nums, xor, i);
            }
        }

        return xor;
    }

    private int dfs2(int i, int par, int total, LinkedList<Integer>[] adjList, int[] nums, int ans1, int ancestor) {


        int xor = nums[i];

        for(int child : adjList[i]){
            if(child != par){
                xor ^= dfs2(child, i, total, adjList, nums, ans1, ancestor);
            }
        }

        if(par == ancestor) return xor;

        res = min(res, max(ans1, xor, total^ans1^xor) - min(ans1, xor, total^ans1^xor));

        return xor;
    }

    private int min(int...arr){
        return Arrays.stream(arr).min().getAsInt();
    }

    private int max(int...arr){
        return Arrays.stream(arr).max().getAsInt();
    }

    private LinkedList<Integer>[] adjList(int edges[][], int n){
        LinkedList<Integer> adjList[] = new LinkedList[n];

        for(int i=0;i<n;i++) adjList[i] = new LinkedList<>();

        for(int e[] : edges){
            int u =e[0], v=e[1];
            adjList[u].add(v);
            adjList[v].add(u);
        }


        return adjList;
    }
}