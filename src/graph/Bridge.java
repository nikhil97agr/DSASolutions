package graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Bridge {

    static int timer = 0;

    public List<List<Integer>> getAllBridges(int n, List<List<Integer>> edges) {
        //Stores the list of all bridge pairs
        List<List<Integer>> res = new ArrayList<>();

        //Create adjacency list for all the edges
        LinkedList<Integer> adjList[] = new LinkedList[n];

        for(int i=0;i<n;i++){
            adjList[i] = new LinkedList<>();

        }

        for(List<Integer> edge : edges){
            int u = edge.get(0);
            int v = edge.get(1);

            adjList[u].add(v);
            adjList[v].add(u);
        }

        //holds the total edges traversed


        //holds the intial time when an edge first visited
        int initialTime[] = new int[n];

        //holds the lowest possible time when an edge can be visited
        int lowestTime[] = new int[n];

        boolean visited[] = new boolean[n];

        dfs(0, adjList, visited, initialTime, lowestTime, -1, res);

        return res;
        
    }

    private static void dfs(int u, LinkedList<Integer> adjList[], boolean visited[], int initialTime[], int lowestTime[], int parent, List<List<Integer>> res){

        timer++;

        //sets the current time on first visit
        initialTime[u] = lowestTime[u] = timer;

        //mark curernt node as visited
        visited[u] = true;
        //iterate through all its connected node
        for(int v : adjList[u]){

            //skip the parent
            if(v == parent) continue;

            //if the node is not visited, do the dfs
            if(!visited[v]){
                dfs(v, adjList, visited, initialTime, lowestTime, u, res);
            }

            //assign the lowest time for the current node
            lowestTime[u] = min(lowestTime[u], lowestTime[v]);

            //if initial time of current node is less than the lowest time of next node then it is a bridge
            if(initialTime[u] < lowestTime[v]){
                List<Integer> temp = Arrays.asList(u, v);
                res.add(temp);
            }
        }

    }

    private static int min(int...arr){
        return Arrays.stream(arr).min().getAsInt();
    }
}