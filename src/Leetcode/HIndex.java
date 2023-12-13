package Leetcode;

import java.util.Arrays;

public class HIndex {
    public int hIndex(int[] arr) {
        Arrays.sort(arr);
        int cnt = 0;
        int n = arr.length;
        for(int i=n-1;i>=0;i--){
            cnt++;
            if(arr[i] < cnt){
                cnt--;
                break;
            }
        }
        return cnt;
    }
}
