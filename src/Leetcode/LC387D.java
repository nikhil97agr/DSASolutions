package Leetcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LC387D {
    public int[] resultArray(int[] nums) {
        int n = nums.length;
        List<Integer> l1 =new ArrayList<>();
        List<Integer> l2 = new ArrayList<>();

        List<Integer> sl1 = new ArrayList<>();
        List<Integer> sl2 = new ArrayList<>();
        insert(sl1, l1, nums[0]);
        insert(sl2, l2, nums[1]);

        for(int i=2;i<n;i++){
            int c1 = getCount(sl1, nums[i]);
            int c2 = getCount(sl2 , nums[i]);
            if(c1 > c2){
                insert(sl1, l1, nums[i]);
            }else if(c2 > c1){
                insert(sl2, l2, nums[i]);
            }else{
                if(l1.size() <= l2.size()){
                    insert(sl1, l1, nums[i]);
                }else{
                    insert(sl2, l2, nums[i]);
                }
            }
        }
        l1.addAll(l2);

        int ans[] = new int[n];
        int i=0;
        for(int x : l1){
            ans[i++] = x;
        }
        return ans;
    }

    private void insert(List<Integer> sl, List<Integer> l , int n){
        int ind = Collections.binarySearch(sl, n);
        if(ind < 0){
            ind = -ind-1;
        }
        sl.add(ind, n);
        l.add(n);
    }

    private int getCount(List<Integer> sl , int n){
        int start = 0;
        int end = sl.size()-1;
        int lastIndex = -1;
        while(start <= end){
            int mid = (start+end)/2;

            if(sl.get(mid) > n){
                end = mid-1;
                lastIndex = mid;
            }else{
                start = mid+1;
            }
        }
        // System.out.println(lastIndex);
        if(lastIndex == -1){
            return 0;
        }
        return sl.size() -(lastIndex);
    }
}