package Leetcode;

//Problem Link : https://leetcode.com/problems/median-of-two-sorted-arrays/description/

public class MedianOfTwoSortedArray {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int m = nums1.length;
        int n = nums2.length;

        if (m > n) {
            return findMedianSortedArrays(nums2, nums1);
        }

        int low = 0;
        int high = m;
        int leftElements = (m + n + 1) / 2;
        int total = m + n;
        while (low <= high) {
            int mid1 = (low + high) >> 1;
            int mid2 = leftElements - mid1;

            int l1 = Integer.MIN_VALUE;
            int l2 = Integer.MIN_VALUE;
            int r1 = Integer.MAX_VALUE;
            int r2 = Integer.MAX_VALUE;

            if (mid1 < m) {
                r1 = nums1[mid1];
            }
            if (mid2 < n) {
                r2 = nums2[mid2];
            }
            if (mid1 > 0) {
                l1 = nums1[mid1 - 1];
            }
            if (mid2 > 0) {
                l2 = nums2[mid2 - 1];
            }

            if (l1 <= r2 && l2 <= r1) {
                return (total % 2 == 0) ? (Math.max(l1, l2) + Math.min(r1, r2)) / 2.0 : Math.max(l1, l2);
            } else if (l1 > r2) {
                high = mid1 - 1;
            } else {
                low = mid1 + 1;
            }
        }

        throw new IllegalArgumentException("Arrays are not sorted");
    }
}
