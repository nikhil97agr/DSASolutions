package Leetcode;

import java.util.*;

public class LCWeekly449C{
    long val = 0;
    public long maxScore(int n, int[][] edges) {


        LinkedList<Integer> adjList[] = build(n, edges);

        int par[] = new int[n];
        for(int i=0;i<n;i++){
            par[i] = i;
        }

        for(int e[] : edges){
            int p1 = find(par, e[0]);
            int p2 = find(par, e[1]);

            if(p1 != p2){
                par[p1] = p2;
            }
        }

        Map<Integer, Integer> map = new HashMap<>();
        for(int i=0;i<n;i++){
            int p =find(par, i);
            add(p, map);
        }
        val = n;
        int cnt[][] = new int[map.size()][2];

        int ind = 0;

        for(Map.Entry<Integer, Integer> entry : map.entrySet()){
            cnt[ind] = new int[]{entry.getKey(), entry.getValue()};
            ind++;
        }

        Arrays.sort(cnt, (a, b)-> b[1] - a[1]);

        long ans = 0;
        boolean vis[] = new boolean[n];
        boolean vis2[] = new boolean[n];
        for (int[] c : cnt) {
            int u = c[0];
            int size = c[1];

            if (!vis2[u] && isCycle(u, -1, adjList, vis, vis2)) {

                if(size == 3){
                    ans += val*(val-1) + (val-1)*(val-2) + val*(val-2);
                    val-=3;
                    continue;
                }
                Queue<Long> que = new LinkedList<>();

                    ans += val*(val-1) + val*(val-2);
                    que.offer(val-1);
                    que.offer(val-2);
                    val-=3;

                    size--;
                    while(size-3 > 0){
                        long first = que.poll();
                        ans += first*val;
                        que.offer(val);
                        val--;
                        size--;
                    }

                    ans += que.poll()*val + que.poll()*val;
                    val--;



            }
        }

        for(int c[] : cnt){
            int u = c[0];
            int size = c[1];
            if(size == 1) continue;
            if(size ==2){
                ans += val*(val-1);
                val-=2;
                continue;
            }
            if(!vis[u]){
                int traverse = size-2;
                Queue<Long> que= new LinkedList<>();
                que.offer(val);
                boolean init = false;
                val--;
                while(traverse-- > 0){
                    long first = que.poll();

                    ans += first*val;
                    que.offer(val);
                    val--;
                    if(!init){
                        ans += first * val;
                        que.offer(val);
                        val--;
                    }
                    init = true;
                }
            }
        }


        return ans;
    }

    private void add(int val, Map<Integer, Integer> map){
        int cnt = map.getOrDefault(val, 0);
        map.put(val, cnt+1);
    }

    private int find(int par[], int i){
        if(par[i] == i) return i;
        return par[i] = find(par, par[i]);
    }

    private boolean isCycle(int u, int par, LinkedList<Integer>[] adjList, boolean[] cycleVis, boolean[] pathVis) {


        cycleVis[u] = pathVis[u] = true;

        for(int v : adjList[u]){
            if(v == par) continue;
            if(v!= par && pathVis[v]){
                return true;
            }

            if(isCycle(v, u, adjList, cycleVis, pathVis)){
                return true;
            }

        }
        cycleVis[u] = false;

        return false;
    }

    private LinkedList<Integer>[] build(int n, int edges[][]){
        LinkedList<Integer> [] adjList= new LinkedList[n];
        for(int i=0;i<n;i++) adjList[i] = new LinkedList<>();

        for(int e[] : edges){
            adjList[e[0]].add(e[1]);
            adjList[e[1]].add(e[0]);
        }
        return adjList;
    }
}