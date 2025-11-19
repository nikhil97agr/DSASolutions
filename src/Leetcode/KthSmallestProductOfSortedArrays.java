package Leetcode;
// Problem Link: https://leetcode.com/problems/kth-smallest-product-of-two-sorted-arrays/

public class KthSmallestProductOfSortedArrays {

    /**
     *
     * @param a first array
     * @param b second array
     * @param k kth smallest product to be found out
     * @return kth smallest product among each pair from a & b;
     */
    public long kthSmallestProduct(int[] a, int[] b, long k) {
        long ans = 0;
        int n = a.length;
        int m = b.length;

        //calculate the product of corner values to find the maximum and minimum possible product
        long p1 = (long) a[0] * b[0], p2 = (long) a[0] *b[m-1], p3 = (long) a[n - 1] *b[0], p4= (long) a[n - 1] *b[m-1];

        long min = Math.min(p1, Math.min(p2, Math.min(p3,p4)));
        long max = Math.max(p1, Math.max(p2, Math.max(p3,p4)));

        //Using binary search, iterate between minimum and maximum product values
        while(min <= max){

            long mid = (min + max)/2;

            //find how many pairs will make mid as product
            long cnt = getCnt(mid, a, b);

            //if total pairs are less than k, that means kth smallest is larger that mid-value
            if(cnt < k){
                min = mid + 1;
            }else{
                max = mid-1;
                ans = mid;
            }
        }

        return ans;


    }

    /**
     *
     * @param mid product for which pairs needs to count
     * @param a first array
     * @param b second array
     * @return number of pairs that will have product less than equal to mid
     */
    private long getCnt(long mid, int a[], int b[]){
        long total = 0;

        for(int i=0;i<a.length;i++){
            /**
             * for negative value of a[i], count will be from highest to lowest
             * for positive value of a[i], count will be from lowest to highest
             */
            total += a[i] < 0 ? bs1(mid, a[i], b) : bs2(mid, a[i], b);
        }

        return total;
    }

    /**
     *
     * @param val possible product to be counted
     * @param num value of first number
     * @param arr array from which pairs to be counted
     * @return number of elements that will have value less than val after product with num
     */
    private long bs2(long val, long num, int arr[]){
        int start = 0;
        int end = arr.length - 1;

        long total =0;
        int n =arr.length;

        while(start <= end){
            int mid = (start + end)/2;
            long v = arr[mid];

            if(num*v <= val){
                total = mid+1;
                start = mid+1;
            }else{
                end = mid-1;
            }
        }

        return total;
    }

    /**
     *
     * @param val possible product to be counted
     * @param num value of first number
     * @param arr array from which pairs to be counted
     * @return number of elements that will have value less than val after product with num
     */
    private long bs1(long val, long num, int arr[]){
        int start = 0;
        int end= arr.length -1;
        long total = 0;
        int n = arr.length;
        while(start <= end){
            int mid = (start + end)/2;

            long v = arr[mid];

            if(num*v <= val){
                total = n - mid;
                end = mid-1;
            }else{
                start = mid+1;
            }
        }

        return total;
    }

}