package Leetcode;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class SecondMinTimeToReachDestination {
    public int secondMinimum(int n, int[][] edges, int time, int change) {
        LinkedList<Integer>[] adjList = new LinkedList[n];
        Node node[] = new Node[n];
        for(int i=0;i<n;i++){
            adjList[i] = new LinkedList<>();
            node[i] = new Node(i);
        }
        for(int e[] : edges){
            int u = e[0] - 1;
            int v = e[1] - 1;

            adjList[u].add(v);
            adjList[v].add(u);
        }

        PriorityQueue<int[]> que = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));

        que.offer(new int[]{0, 0});
        node[0].min  = 0;

        while(!que.isEmpty()){
            int ele[] = que.poll();
            int ind = ele[0];
            int cnt = ele[1];

            if(ind == n-1){
                if(node[ind].max != Integer.MAX_VALUE) break;
            }
            int next = cnt+1;
            for(int x : adjList[ind]){
                if(node[x].min == Integer.MAX_VALUE){
                    node[x].min = next;
                }else if(node[x].min == next || node[x].max <= next){
                    continue;
                }else{
                    node[x].max = next;
                }
                que.offer(new int[]{x, next});


            }
        }

        int ans = 0;

        int nextChange = 0;
        int cnt= node[n-1].max;
        char curr= 'g';
        while(cnt > 0){
            if(curr == 'g'){
                nextChange += change;
                while(ans < nextChange && cnt > 0){
                    ans += time;
                    cnt--;
                }
                curr = 'r';
            }else{
                nextChange += change;

                while(nextChange < ans){
                    curr = curr == 'r'  ? 'g' : 'r';
                    nextChange += change;
                }

                if(curr == 'g'){
                    while(ans < nextChange && cnt > 0){
                        ans += time;
                        cnt--;
                    }
                    curr = 'r';
                }else{
                    ans = nextChange;
                    curr = 'g';
                }


            }
        }


        return ans;
    }


    class Node{
        int min;
        int max;
        int u;

        public Node(int u){
            this.u=u;
            this.min = this.max = Integer.MAX_VALUE;
        }
    }
}