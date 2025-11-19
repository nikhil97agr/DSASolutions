package Leetcode;

import java.util.*;

public class FindXSumOfAllKLongSubarrays {
    public long[] findXSum(int[] nums, int k, int x) {
        int n = nums.length;
        int m = n-k+1;
        long ans[] = new long[m];
        Map<Integer, Integer> map = new HashMap<>();
        Comparator<Node> comparator= (a,b)  ->{
            if(a.cnt == b.cnt) return b.val - a.val;

            return b.cnt - a.cnt;
        };
        TreeSet<Node> included = new TreeSet<>(comparator);
        TreeSet<Node> excluded = new TreeSet<>(comparator);
        int ind = 0;
        int start = 0;
        int end = k-1;
        long sum = 0;

        for(int i=0;i<k;i++){
            map.merge(nums[i], 1, Integer::sum);
        }

        for(int key : map.keySet()){
            included.add(new Node(key, map.get(key)));
            sum += 1l*key*map.get(key);

            if(included.size() > x){
                Node node = included.pollLast();
                sum -= 1l*node.val*node.cnt;
                excluded.add(node);
            }
        }
        while( end+1 < n){
            ans[ind++] = sum;

            sum = remove(nums[start], included, excluded, map, sum, x);
            end++;
            start++;
            sum = add(nums[end], included, excluded, map, sum, x);


        }

        ans[ind] = sum;

        return ans;
    }

    private long remove(int val, TreeSet<Node> included, TreeSet<Node> excluded, Map<Integer, Integer> map, long sum, int x){
        Node node = new Node(val,map.get(val));

        map.merge(node.val, -1, Integer::sum);
        if(map.get(val) ==0) map.remove(val);


        if(included.contains(node)){
            included.remove(node);
            sum += update(node, -1);
            node.cnt--;
            if(map.containsKey(val)){
                excluded.add(node);
            }
        }else{
            excluded.remove(node);
            node.cnt--;
            if(map.containsKey(val)){
                excluded.add(node);
            }
        }

        while(!excluded.isEmpty() && included.size() < x){
            Node n = excluded.pollFirst();
            sum += update(n, 1);
            included.add(n);
        }

        return sum;

    }

    private long add(int val, TreeSet<Node> included, TreeSet<Node> excluded, Map<Integer, Integer> map, long sum, int x){
        map.merge(val, 1, Integer::sum);
        Node node= new Node(val, map.get(val) - 1);

        if(included.contains(node)){
            included.remove(node);
            sum += node.val;
            node.cnt++;
            included.add(node);
        }else{
            excluded.remove(node);
            node.cnt++;
            excluded.add(node);
            if(!included.isEmpty()){
                Node n = included.pollLast();
                sum += update(n, -1);
                excluded.add(n);
            }

        }

        while(!excluded.isEmpty() && included.size() < x){
            Node n =excluded.pollFirst();
            sum += update(n, 1);
            included.add(n);
        }

        return sum;
    }

    private long update(Node node,long val){
        return val*node.val*node.cnt;
    }







    class Node{
        int val;
        int cnt;

        public Node(int val, int cnt){
            this.val =val;
            this.cnt= cnt;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return val == node.val;
        }

        @Override
        public int hashCode() {
            return Objects.hash(val);
        }

        @Override
        public String toString() {
            return "Node{" +
                    "val=" + val +
                    ", cnt=" + cnt +
                    '}';
        }
    }
}