package Leetcode;

import java.util.*;

public class LCWeekly404D {

    public int minimumDiameterAfterMerge(int[][] edges1, int[][] edges2) {
        Map<Integer, Integer> degreeA = new HashMap<>();
        Map<Integer, Integer> degreeB = new HashMap<>();

        Map<Integer, List<Integer>> adjListA = new HashMap<>();
        Map<Integer, List<Integer>> adjListB = new HashMap<>();

        generateTree(edges1, degreeA, adjListA);
        generateTree(edges2, degreeB, adjListB);
        int ans1[] = new int[]{0};
        int ans2[] = new int[]{0};

        getDiameter(adjListB, new HashSet<>(), ans2, 0);
        getDiameter(adjListA, new HashSet<>(), ans1, 0);
        int d1 = ans1[0] - 1;
        int d2 = ans2[0] - 1;


        return Math.max(d1, Math.max(d2, d1 - d1/2 + d2-d2/2 + 1));
    }


    private int getDiameter(Map<Integer, List<Integer>> adjList, Set<Integer> visited, int ans[], int u){

        visited.add(u);

        int max = 0;
        int second = 0;
        for(int v : adjList.get(u)){
            if(visited.contains(v)) continue;

            int dia = getDiameter(adjList, visited, ans, v);

            if(dia > max){
                second =max;
                max = dia;
            }else if(dia > second){
                second = dia;
            }
        }

        ans[0] = Math.max(ans[0], 1 + max + second);

        return max + 1;
    }

    private void generateTree(int edges[][], Map<Integer, Integer> degree, Map<Integer, List<Integer>> adjList){
        for(int edge[] : edges){
            int u = edge[0];
            int v = edge[1];

            adjList.computeIfAbsent(u, l -> new ArrayList<>()).add(v);
            adjList.computeIfAbsent(v, l -> new ArrayList<>()).add(u);

            degree.put(u, degree.getOrDefault(u, 0)+1);
            degree.put(v, degree.getOrDefault(v, 0)+1);
        }
    }
}