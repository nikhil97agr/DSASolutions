package Leetcode;

//Problem Link : https://leetcode.com/problems/optimize-water-distribution-in-a-village

import java.util.PriorityQueue;

public class OptimizeWaterDistributionInVillage {

    class Path{
        int u;
        int v;
        int cost;
        public Path(int u, int v, int cost){
            this.u = u;
            this.v = v;
            this.cost = cost;
        }
    }
    public int minCostToSupplyWater(int n, int[] wells, int[][] pipes) {
        int m = pipes.length;
        //Here includes a virtual node that act as a source to each node which cost transfer of water equal to well cost
        PriorityQueue<Path> que = new PriorityQueue<>((a,b)-> a.cost - b.cost);
        
        for(int i=0;i<n;i++){
            que.offer(new Path(0, i+1, wells[i]));
        }

        for(int j=0;j<m;j++){
            que.offer(new Path(pipes[j][0], pipes[j][1], pipes[j][2]));
        }
        int parent[] = new int[n+1];
        for(int i=0;i<=n;i++){
            parent[i] = i;
        }  
        int ans = 0;
        while(!que.isEmpty()){
            Path path =que.poll();

            int p1 = findParent(parent, path.u);
            int p2 = findParent(parent, path.v);
            if(p1!=p2){
                parent[p1] = p2;
                ans += path.cost;
            }
        }

        int p = findParent(parent, 1);
        
        for(int i=0;i<=n;i++){
            if(p!= findParent(parent, i)) return -1;
        }



        return ans;
    }

    private int findParent(int parent[], int i){
        if(parent[i] ==i) return i;

        return parent[i] = findParent(parent, parent[i]);
    }
}
