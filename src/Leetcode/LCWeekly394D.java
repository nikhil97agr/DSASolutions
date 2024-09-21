package Leetcode;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class LCWeekly394D {
    public boolean[] findAnswer(int n, int[][] edges) {
        LinkedList<Edge> adjList[] = new LinkedList[n];
        for(int i=0;i<n;i++){
            adjList[i] = new LinkedList<>();
        }

        for(int i=0;i<edges.length;i++){
            int e[] = edges[i];
            adjList[e[0]].add(new Edge(e[1], e[2], i));
            adjList[e[1]].add(new Edge(e[0], e[2], i));
        }

        int dist[] = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);

        dist[0] = 0;
        PriorityQueue<Edge> que = new PriorityQueue<>((e1, e2) -> e1.w - e2.w);

        que.offer(new Edge(0, 0, -1));

        while(!que.isEmpty()){
            Edge edge = que.poll();

            int u =edge.v;
            int d = edge.w;

            for(Edge e : adjList[u]){
                int v = e.v;
                int w = e.w;

                if(dist[v] > dist[u] + w){
                    dist[v] = dist[u] + w;
                    que.offer(new Edge(v, dist[v], -1));
                }
            }
        }

        que.offer(new Edge(n-1, dist[n-1], -1));
        boolean ans[] = new boolean[edges.length];

        while(!que.isEmpty()){
            Edge edge = que.poll();

            int u = edge.v;
            int d = edge.w;

            for(Edge e : adjList[u]){
                int  v = e.v;
                int w = e.w;

                if(dist[v] + w == dist[u]){
                    que.offer(new Edge(v, dist[v], -1));
                    ans[e.ind] = true;
                }
            }
        }




        return ans;
    }

    class Edge{
        int v;
        int w;
        int ind;

        public Edge(int v, int w, int ind){
            this.v = v;
            this.w = w;
            this.ind = ind;

        }
    }
}