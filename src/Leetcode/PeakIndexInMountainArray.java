package Leetcode;

// Problem Link : https://leetcode.com/problems/peak-index-in-a-mountain-array

public class PeakIndexInMountainArray {
    public int peakIndexInMountainArray(int[] arr) {
        int n = arr.length;

        int start = 0;
        int end = n - 1;
        while (start < end) {
            int mid = start + (end - start) / 2;
            if (arr[mid] > arr[mid + 1]) {
                end = mid;
            } else {
                start = mid + 1;
            }
        }
        return start;
    }
}