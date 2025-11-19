package Leetcode;

import java.util.*;

public class LCBiweekly149C {
    public int maxFreeTime(int eventTime, int[] startTime, int[] endTime) {
        List<Integer> list = new ArrayList<>();
        int n = endTime.length;
        list.add(startTime[0]);

        for(int i=0;i<n-1;i++){
            list.add(startTime[i+1] - endTime[i]);

        }
        list.add(eventTime - endTime[n-1]);
        int left[] = new int[list.size()];
        int right[] = new int[list.size()];
        int max = list.get(0);
        for(int i=1;i<list.size();i++){
            left[i] = Math.max(left[i-1] , max);
            max = Math.max(max, list.get(i));
        }

        max = list.get(list.size()-1);
        for(int i=list.size()-2;i>=0;i--){
            right[i] = Math.max(right[i+1], max);
            max = Math.max(max, list.get(i));
        }

        int ans = 0;

        for(int i=0;i<n;i++){
            ans = Math.max(ans, list.get(i) + list.get(i+1));
            int size = endTime[i] - startTime[i];

            if(bs(left, 0, i-1, size) || bs2(right, i+2, list.size(), size)){

                ans = Math.max(ans, list.get(i) + list.get(i+1) + size);
            }
        }

        return ans;
    }

    private boolean bs(int arr[], int start, int end, int ele){
        while(start <= end){
            int mid = (start+end)/2;
            if (arr[mid] >= ele) {
                return true;
            }
            start = mid+1;
        }
        return false;
    }

    private boolean bs2(int arr[], int start, int end, int ele){
        while(start <= end){
            int mid = (start+end)/2;
            if(arr[mid] >= ele){
                return true;
            }
            end = mid-1;
        }
        return false;
    }

}