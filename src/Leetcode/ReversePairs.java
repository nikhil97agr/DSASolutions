package Leetcode;

//Problem Link : https://leetcode.com/problems/reverse-pairs

public class ReversePairs {
    int ans = 0;

    public int reversePairs(int[] nums) {
        int n = nums.length;
        mergeSort(0, n - 1, nums);
        return ans;
    }

    private int[] mergeSort(int start, int end, int nums[]){
        if(start==end){
            return new int[]{nums[start]};
        }

        int mid = (start+end)/2;

        int left[] = mergeSort(start, mid, nums);
        int right[] = mergeSort(mid+1, end, nums);

        int m = left.length;
        int n = right.length;
        
        int i=0;
        int j=0;
        while(i<m && j<n){
            if( 1l*left[i] > 1l*2*right[j]){
                ans += m-i;
                j++;
            }else{
                i++;
            }
        }
        int merge[] = new int[m+n];
        i=0;
        j=0;
        int k = 0;
        while(i<m && j<n){
            if(left[i] <= right[j]){
                merge[k++] = left[i++];
            }else{
                merge[k++] = right[j++];
            }
        }

        while(i < m ){
            merge[k++] = left[i++];
        }
        while(j<n){
            merge[k++] = right[j++];
        }
        return merge;
    }
}
