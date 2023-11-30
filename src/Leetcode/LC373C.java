package Leetcode;

import java.util.*;

public class LC373C {
    public int[] lexicographicallySmallestArray(int[] nums, int limit) {
        int n = nums.length;
       int ans[] = new int[n];
       int pair[][] = new int[n][2];
       for(int i=0;i<n;i++){
           pair[i] = new int[]{nums[i], i};
       }

       Arrays.sort(pair, (a,b)->{
           return a[0] - b[0];
       });

       Map<Integer, PriorityQueue<Integer>> ele = new HashMap<>();
       Map<Integer, PriorityQueue<Integer>> ind = new HashMap<>();

       int grp = 0;
       int curr = pair[0][0];
       int index = pair[0][1];
       ele.computeIfAbsent(grp,  e -> new PriorityQueue<>()).add(curr);
       ind.computeIfAbsent(grp, e -> new PriorityQueue<>()).add(index);

       for(int i=1;i<n;i++){
           if(pair[i][0] - curr <= limit){
               addElement(ele, pair[i][0], grp);
               addElement(ind, pair[i][1], grp);
               curr = pair[i][0];
           }else{
               grp++;
               addElement(ele, pair[i][0], grp);
               addElement(ind, pair[i][1], grp);
               curr= pair[i][0];
           }

       }
       while(grp >= 0){
           PriorityQueue<Integer> num = ele.get(grp);
           PriorityQueue<Integer> i = ind.get(grp);
           while(!num.isEmpty()){
               ans[i.poll()] = num.poll();
           }
           grp--;
       }
       return ans;
    }

    private void addElement(Map<Integer, PriorityQueue<Integer>> map, int ele, int grp){
        map.computeIfAbsent(grp, e -> new PriorityQueue<>()).add(ele);
    }
}
