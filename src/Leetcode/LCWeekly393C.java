package Leetcode;

import java.util.*;

public class LCWeekly393C {
    public long findKthSmallest(int[] coins, int k) {
        long maxPossible =1L*Arrays.stream(coins).min().getAsInt() * k;

        long start = 1;
        long end = maxPossible;
        long arr[] = Arrays.stream(coins).mapToLong(x -> 1L*x).toArray();
        Map<Integer, List<Long>> map = new HashMap<>();
        getLcm(arr, map, 0, arr.length, new ArrayList<>());
        while(start < end){
            long mid = start + (end - start)/2L;

            long cnt = getCount(mid, map, arr);
            if(cnt < 1L*k){
                start = mid + 1L;
            }else{
                end = mid;
            }
        }

        return start;


    }
    private void getLcm(long arr[], Map<Integer, List<Long>> map, int i, int n, List<Long> temp)
    {
        if(i==n){
            if(temp.size() <= 1) return;
            long lcm = temp.get(0);
            for(int ind=1;ind<temp.size();ind++){
                lcm = (lcm*temp.get(ind))/gcd(lcm, temp.get(ind));
            }
            map.computeIfAbsent(temp.size(), a-> new ArrayList<>()).add(lcm);
            return ;
        }

        getLcm(arr, map, i+1, n, temp);
        temp.add(arr[i]);
        getLcm(arr, map, i+1, n, temp);
        temp.remove(temp.size()-1);

    }

    private long getCount(long mid, Map<Integer, List<Long>> map , long arr[]){
        long cnt = 0;
        for(long x : arr){
            cnt += mid/x;
        }

        for(Map.Entry<Integer, List<Long>> entry : map.entrySet()){
            if(entry.getKey()%2==0){
                for(long x : entry.getValue()){
                    cnt -= (mid/x);
                }
            }else{
                for(long x : entry.getValue()){
                    cnt += (mid/x);
                }
            }
        }
        return cnt;
    }

    private long gcd(long a, long b){
        if(a==0) return b;
        return gcd(b%a, a);
    }
}
