package Leetcode;

import java.util.*;

public class LCBiweekly120D {
    public long[] placedCoins(int[][] edges, int[] cost) {
        int n = cost.length;
        Node node[] = new Node[n];
        for(int i=0;i<n;i++){
            node[i] = new Node(i,cost[i]);
        }
        for(int edge[] : edges){
            node[edge[0]].child.add(node[edge[1]]);
            node[edge[1]].child.add(node[edge[0]]);

        }
        long ans[] = new long[n];
        solve(new boolean[n], node[0], ans);
        return ans;
    }

    private Response solve( boolean visited[],  Node root, long ans[]){
        if(root==null) return new Response();

        visited[root.index] = true;

        Response response = new Response();
        response.set.add(root.cost);
        response.maxSet.add(root.cost);
        for(Node child : root.child) {
            if (!visited[child.index]) {
                Response childResponse = solve(visited, child, ans);
                response.count += childResponse.count;
                while(!childResponse.set.isEmpty()){
                    long val = childResponse.set.poll();
                    if(response.set.size() <3){
                        response.set.add(val);
                    }else if(response.set.peek() < val){
                        response.set.poll();
                        response.set.add(val);
                    }

                }
                while(!childResponse.maxSet.isEmpty()){
                    long val = childResponse.maxSet.poll();
                    if(response.maxSet.size() < 3){
                        response.maxSet.add(val);
                    }else if(response.maxSet.peek() > val){
                        response.maxSet.poll();
                        response.maxSet.add(val);
                    }
                }
            }
        }
        if(response.count < 3){
            ans[root.index] = 1;

        }
        else{
            long firstMax = response.set.poll();
            long secondMax = response.set.poll();
            long thirdMax = response.set.poll();

            long first = response.maxSet.poll();
            long second = response.maxSet.poll();
            long third = response.maxSet.poll();
            long prod= firstMax * secondMax * thirdMax;
            long prod2 = thirdMax * third * second;
            response.set.offer(firstMax);
            response.set.offer(secondMax);
            response.set.offer(thirdMax);

            response.maxSet.offer(first);
            response.maxSet.offer(second);
            response.maxSet.offer(third);
            ans[root.index] = Math.max(0, Math.max(prod, prod2));
        }

        return response;
    }

    class Response{
        PriorityQueue<Long> set;
        PriorityQueue<Long> maxSet;
        int count;
        public Response(){
            count = 1;
            set = new PriorityQueue<>();
            maxSet = new PriorityQueue<>(Collections.reverseOrder());
        }

        public String toString(){
            return this.set.toString()+":"+count;
        }

    }
    class Node{
        long cost;
        List<Node> child;
        int index;

        public Node(int index, int cost){
            this.cost = cost;
            this.index = index;
            child = new ArrayList<>();
        }
    }
    
    
}