package Leetcode;

import java.util.*;

public class LCBiweekly128D {
    public long numberOfSubarrays(int[] nums) {
        long ans = 0;
        TreeMap<Integer, List<Integer>> map = new TreeMap<>();
        Map<Integer, Integer> cnt = new HashMap<>();
        int nge[] = nge(nums, nums.length);
        int n = nums.length;
        for(int i=0;i<n;i++){
            map.computeIfAbsent(nums[i], (a -> new ArrayList<>())).add(i);
            cnt.put(nums[i], cnt.getOrDefault(nums[i], 0)+1);

        }


        for(int i=0;i<n;i++){
            int x = nums[i];
            int greater = nge[i];
            if(greater == -1){
                ans += 1L*(cnt.get(x));
                int count = cnt.get(x);
                if(count==1){
                    cnt.remove(x);
                    map.remove(x);
                }else{
                    cnt.put(x, count-1);
                }
            }else{
                int ele = binarySeach(map.get(x), cnt.get(x), greater);
                ans += 1L*ele;
                int count = cnt.get(x);
                if(count==1){
                    cnt.remove(x);
                    map.remove(x);
                }else{
                    cnt.put(x, count-1);
                }
            }
        }
        return ans;

    }

    private int binarySeach(List<Integer> list, int cnt, int ind){
        int n = list.size();
        int start = n - cnt;
        int st = n-cnt;
        int end = n-1;
        int ans = 0;
        while(start <= end){
            int mid = (start+end)/2;
            if(list.get(mid)  <= ind){
                start = mid+1;
                ans = list.get(mid) - st  + 1;
            }else{
                end = mid-1;
            }
        }
        return ans;
    }

    private int[] nge(int nums[], int n){
        int nge[] = new int[n];
        Arrays.fill(nge, -1);
        Stack<Integer> stack = new Stack<>();

        stack.push(0);

        for(int i=1;i<n;i++){
            while(!stack.isEmpty()){
                if(nums[i] <= nums[stack.peek()]){
                    break;
                }
                nge[stack.peek()] = i;
                stack.pop();
            }
            stack.push(i);
        }
        return nge;
    }
}