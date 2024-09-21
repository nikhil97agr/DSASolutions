package array;

import java.util.Arrays;

public class CountInversions {
    public static void main(String[] args) {
        System.out.println(inversionCount(new long[]{2,4,1,3,5}, 5));
    }
    static long inversionCount(long arr[], int n) {
        return solve(arr, 0, n-1);
    }

    private static long solve(long arr[], int start, int end){

        if(start == end) return 0;

        int mid = (start+end)>>1;


        long left[] = Arrays.copyOfRange(arr, start, mid+1);
        long right[] = Arrays.copyOfRange(arr, mid+1, end+1);

        long cnt = 0;

        cnt+= solve(left, 0, left.length-1);
        cnt+= solve(right, 0, right.length-1);
        int n = left.length;
        int m = right.length;
        int i=0;
        int j=0;
        int k=0;
        while(j < left.length && k < right.length){
            if(right[k] >= left[j]){
                arr[i++] = right[k++];
            }else{
                arr[i++] = left[j++];
                cnt +=(m-k);
            }


        }

        while(j< n){
            arr[i++] = left[j++];
        }

        while(k<m){
            arr[i++] = right[k++];
        }


        return cnt;



    }
}