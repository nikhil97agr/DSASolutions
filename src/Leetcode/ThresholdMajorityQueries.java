package Leetcode;

import java.util.*;

public class ThresholdMajorityQueries {
    public int[] subarrayMajority(int[] nums, int[][] queries) {
        Map<Integer, Integer> map = new HashMap<>();
        TreeMap<Integer, TreeSet<Integer>>  freq = new TreeMap<>();
        int m =queries.length;
        int block =(int) Math.sqrt(nums.length);
        Query query[] =new Query[m];
        for(int i=0;i<m;i++){
            query[i] = new Query(queries[i][0], queries[i][1], i, queries[i][2], queries[i][0]/block);
        }
        int ans[] = new int[m];
        Arrays.sort(query, (q1, q2)->{

            if(q1.block == q2.block){
                return q1.r - q2.r;
            }

            return q1.block - q2.block;
        });
        int currl = 0;
        int curr = -1;
        for(Query q : query){

            while(currl > q.l){
                currl--;
                add(nums[currl], map, freq);
            }

            while(curr <q.r ){
                curr++;
                add(nums[curr], map, freq);
            }

            while(currl < q.l){
                remove( nums[currl], map, freq);
                currl++;
            }

            while(curr > q.r){
                remove(nums[curr], map, freq);
                curr--;
            }

            ans[q.i] = -1;

            if(!freq.isEmpty() && freq.lastKey() >= q.t){
                ans[q.i] = freq.lastEntry().getValue().first();
            }

        }

        return ans;

    }

    private void remove(int num, Map<Integer, Integer> map, TreeMap<Integer, TreeSet<Integer>> freq) {
        int cnt = map.get(num);

        TreeSet<Integer> set = freq.get(cnt);

        set.remove(num);

        if(set.isEmpty()){
            freq.remove(cnt);
        }
        if(cnt - 1 > 0)
        {
            map.put(num, cnt - 1);

            freq.computeIfAbsent(cnt-1, x-> new TreeSet<>()).add(num);
        }else{
            map.remove(num);
        }
    }

    private void add(int num, Map<Integer, Integer> map, TreeMap<Integer, TreeSet<Integer>> freq) {
        int cnt = map.getOrDefault(num, 0);
        if(cnt > 0){
            TreeSet<Integer> set = freq.get(cnt);
            set.remove(num);

            if(set.isEmpty()){
                freq.remove(cnt);
            }
        }


        map.put(num, cnt + 1);

        freq.computeIfAbsent(cnt + 1, x -> new TreeSet<>()).add(num);

    }

    class Query{
        int l;
        int r;
        int i;
        int t;
        int block;

        public Query(int l, int r, int i, int t, int block) {
            this.l = l;
            this.r = r;
            this.i = i;
            this.t = t;
            this.block = block;
        }
    }
}