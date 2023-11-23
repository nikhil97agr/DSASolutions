package Leetcode;

//Problem Link : https://leetcode.com/problems/maximum-employees-to-be-invited-to-a-meeting/

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class MaximumEmployeesInvitedToMeeting {
    public int maximumInvitations(int[] fav) {
        int n = fav.length;
        LinkedList<Integer> adjList[] = new LinkedList[n];
        int indegree[] = new int[n];
        for (int i = 0; i < n; i++) {
            adjList[i] = new LinkedList<>();
        }

        for (int i = 0; i < n; i++) {
            adjList[i].add(fav[i]);
            indegree[fav[i]]++;
        }

        // Find the cycle length;
        boolean visited[] = new boolean[n];
        int pathLength[] = new int[n];
        int ans[] = new int[] { 0 };
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfs(i, visited, pathLength, adjList, 0, ans);
            }
        }

        // Using topological sort find the longest branch connected to it
        int dp[] = new int[n];
        Arrays.fill(dp, 1);
        Queue<Integer> que = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                que.add(i);
            }
        }

        while (!que.isEmpty()) {
            int node = que.poll();

            Iterator<Integer> it = adjList[node].iterator();
            while (it.hasNext()) {
                int child = it.next();
                indegree[child]--;
                if (indegree[child] == 0) {
                    que.add(child);
                }
                dp[child] = Math.max(dp[child], dp[node] + 1);
            }
        }

        int total = 0;
        for (int i = 0; i < n; i++) {
            // check the cycle of length 2

            if (fav[fav[i]] == i) {
                total += dp[i] + dp[fav[i]];
            }
        }

        total /= 2;

        return Math.max(total, ans[0]);
    }

    private void dfs(int node, boolean visited[], int pathLength[], LinkedList<Integer> adjList[], int currPathLength,
            int ans[]) {
        visited[node] = true;
        pathLength[node] = currPathLength + 1;

        Iterator<Integer> it = adjList[node].iterator();

        while (it.hasNext()) {
            int child = it.next();
            if (!visited[child]) {
                dfs(child, visited, pathLength, adjList, currPathLength + 1, ans);
            } else if (pathLength[child] != 0) {
                ans[0] = Math.max(ans[0], pathLength[node] - pathLength[child] + 1);
            }
        }
        pathLength[node] = 0;
    }
}