package Leetcode;

import java.util.*;

public class LC125C {
    public int[] countPairsOfConnectableServers(int[][] edges, int signalSpeed) {
        Map<Integer, List<Edge>> map = new HashMap<>();
        int n = edges.length;
        for(int edge[] : edges){
            int sr = edge[0];
            int dest = edge[1];
            int w = edge[2];

            map.computeIfAbsent(sr, s -> new ArrayList<>()).add(new Edge(dest ,w));
            map.computeIfAbsent(dest, s -> new ArrayList<>()).add(new Edge(sr, w));


        }
        int dist[][] = new int[n+1][n+1];
        for(int i=0;i<=n;i++){
            dfs(i,i,0, new boolean[n+1], map, dist);
        }

        int res[] = new int[n+1];
        for(int i=0;i<=n;i++){
            Map<Integer, Integer> m = new HashMap<>();
            Queue<int[]> que = new LinkedList<>();
            int cnt = 0;
            for(Edge e : map.get(i)){
                que.offer(new int[]{e.node, cnt++});
            }
            boolean vis[] = new boolean[n+1];
            vis[i] = true;
            while(!que.isEmpty()){
                int node[] = que.poll();
                vis[node[0]] = true;
                int lvl = node[1];
                if (dist[i][node[0]] % signalSpeed == 0){
                    m.put(lvl, m.getOrDefault(lvl,0)+1);
                }
                for(Edge e : map.get(node[0])){
                    if(!vis[e.node]){
                        que.offer(new int[]{e.node, lvl});
                    }
                }

            }
            List<Integer> list = new ArrayList<>(m.values());
            for(int k=0;k<list.size();k++){
                for(int j=k+1;j<list.size();j++){
                    res[i] += list.get(k)*list.get(j);
                }
            }
        }
        return res;
    }

    private void dfs(int src, int dest, int currDist, boolean[] vis, Map<Integer, List<Edge>> map, int[][] dist) {

        dist[src][dest] = currDist;

        vis[dest] = true;
        for(Edge edge : map.get(dest)){
            if(!vis[edge.node]){
                dfs(src, edge.node, currDist + edge.weight, vis, map, dist);
            }
        }
    }

    class Edge{
        int node;
        int weight;

        public Edge(int node, int weight){
            this.node = node;
            this.weight = weight;
        }
    }
}