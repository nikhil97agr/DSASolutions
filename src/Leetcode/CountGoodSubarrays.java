package Leetcode;

import java.util.Arrays;
import java.util.Stack;

//Problem Link: https://leetcode.com/contest/weekly-contest-494/problems/count-good-subarrays/

/**
 * Solution for counting "good" subarrays where the bitwise OR of the subarray equals its maximum element.
 * Uses segment tree for range OR queries, monotonic stacks for finding greater elements,
 * and binary search to count valid subarrays.
 */
public class CountGoodSubarrays {

    SegmentTree tree;  // Segment tree for efficient range OR queries
    int n;             // Length of the input array
    int nums[];        // Input array

    public static void main(String[] args) {

        new CountGoodSubarrays().countGoodSubarrays(new int[]{7, 7, 4});
    }

    /**
     * Counts the total number of good subarrays.
     * A subarray is "good" if its bitwise OR equals its maximum element.
     *
     * @param nums Input array of integers
     * @return Total count of good subarrays
     */
    public long countGoodSubarrays(int[] nums) {

        long ans = 0;
        this.nums = nums;
        n = nums.length;

        // Build segment tree for range OR queries
        tree = new SegmentTree(nums.length);
        tree.build(0, n - 1, 1, nums);

        // Find next greater element for each index
        int nge[] = nge(nums);

        // Find previous greater element for each index
        int pge[] = pge(nums);

        // For each element as the maximum in a subarray
        for (int i = 0; i < n; i++) {
            // Find how far left we can extend while OR equals nums[i]
            long leftLen = searchLeft(pge[i], nums[i], i);

            // Find how far right we can extend while OR equals nums[i]
            long rightLen = searchRight(nge[i], nums[i], i);

            // Count all combinations: left extensions * right extensions + individual extensions + element itself
            ans += (leftLen * rightLen + leftLen + rightLen + 1);
        }

        return ans;
    }

    /**
     * Binary search to find how far right we can extend from start while OR equals val.
     * Searches in the range [start, next greater element).
     *
     * @param end   Index of next greater element (or -1 if none)
     * @param val   Target OR value (the maximum element)
     * @param start Starting index
     * @return Number of positions we can extend to the right
     */
    private int searchRight(int end, int val, int start) {

        int s = start;

        // Set the search boundary
        if (end == -1) {
            // No greater element to the right, search till end of array
            end = n - 1;
        } else {
            // Search up to (but not including) the next greater element
            end--;
        }

        int ans = -1;

        // Binary search for the rightmost position where OR equals val
        while (start <= end) {
            int mid = (start + end) / 2;

            // Query OR of range [s, mid]
            int or = tree.query(0, n - 1, 1, s, mid);
            if (or == val) {
                // OR still equals val, try extending further right
                ans = mid;
                start = mid + 1;
            } else {
                // OR exceeded val, search left half
                end = mid - 1;
            }
        }

        // Return the number of positions we can extend (0 if none)
        return ans == -1 ? 0 : ans - s;
    }

    /**
     * Binary search to find how far left we can extend from end while OR equals val.
     * Searches in the range (previous greater element, end].
     *
     * @param start Index of previous greater element (or -1 if none)
     * @param val   Target OR value (the maximum element)
     * @param end   Ending index
     * @return Number of positions we can extend to the left
     */
    private int searchLeft(int start, int val, int end) {

        int e = end;

        // Set the search boundary
        if (start == -1) {
            // No greater element to the left, search from beginning
            start = 0;
        } else if (nums[start] == val) {
            // If previous greater equals val, start after it
            start++;
        }

        int ans = -1;

        // Binary search for the leftmost position where OR equals val
        while (start <= end) {
            int mid = (start + end) / 2;

            // Query OR of range [mid, e]
            int or = tree.query(0, n - 1, 1, mid, e);
            if (or == val) {
                // OR still equals val, try extending further left
                ans = mid;
                end = mid - 1;
            } else {
                // OR exceeded val, search right half
                start = mid + 1;
            }
        }

        // Return the number of positions we can extend (0 if none)
        return ans == -1 ? 0 : e - ans;
    }

    /**
     * Finds the Next Greater Element (NGE) for each index.
     * Uses a monotonic stack to efficiently find the next element greater than nums[i].
     *
     * @param nums Input array
     * @return Array where nge[i] = index of next greater element, or -1 if none exists
     */
    private int[] nge(int nums[]) {

        var stack = new Stack<Integer>();
        var n = nums.length;
        var nge = new int[n];
        Arrays.fill(nge, -1);  // Default: no greater element found

        // Traverse left to right
        for (int i = 0; i < n; i++) {
            // Pop elements smaller than current element and set their NGE
            while (!stack.isEmpty() && nums[stack.peek()] < nums[i]) {
                nge[stack.pop()] = i;
            }
            stack.push(i);
        }
        return nge;
    }

    /**
     * Finds the Previous Greater Element (PGE) for each index.
     * Uses a monotonic stack to efficiently find the previous element greater than nums[i].
     *
     * @param nums Input array
     * @return Array where pge[i] = index of previous greater element, or -1 if none exists
     */
    private int[] pge(int nums[]) {

        var stack = new Stack<Integer>();
        var n = nums.length;
        var pge = new int[n];
        Arrays.fill(pge, -1);  // Default: no greater element found

        // Traverse right to left
        for (int i = n - 1; i >= 0; i--) {
            // Pop elements smaller than or equal to current element and set their PGE
            while (!stack.isEmpty() && nums[stack.peek()] <= nums[i]) {
                pge[stack.pop()] = i;
            }
            stack.push(i);
        }
        return pge;
    }

    /**
     * Segment Tree implementation for efficient range bitwise OR queries.
     * Supports O(log n) range queries after O(n) build time.
     */
    class SegmentTree {

        int tree[];  // Array representation of the segment tree

        /**
         * Constructor to initialize the segment tree.
         *
         * @param n Size of the input array
         */
        public SegmentTree(int n) {

            tree = new int[n * 4];  // Allocate 4*n space for segment tree
        }

        /**
         * Builds the segment tree recursively.
         * Each node stores the bitwise OR of its range.
         *
         * @param l       Left boundary of current segment
         * @param r       Right boundary of current segment
         * @param treeInd Current node index in the tree array
         * @param num     Input array
         */
        public void build(int l, int r, int treeInd, int num[]) {

            // Leaf node: store the array element
            if (l == r) {
                tree[treeInd] = num[l];
                return;
            }

            int mid = (l + r) / 2;

            // Recursively build left and right subtrees
            build(l, mid, 2 * treeInd, num);
            build(mid + 1, r, 2 * treeInd + 1, num);

            // Internal node: store OR of children
            tree[treeInd] = tree[2 * treeInd] | tree[2 * treeInd + 1];
        }

        /**
         * Queries the bitwise OR of a range [ql, qr].
         *
         * @param l       Left boundary of current segment
         * @param r       Right boundary of current segment
         * @param treeInd Current node index in the tree array
         * @param ql      Query left boundary
         * @param qr      Query right boundary
         * @return Bitwise OR of elements in range [ql, qr]
         */
        public int query(int l, int r, int treeInd, int ql, int qr) {

            // No overlap: return identity element for OR (0)
            if (ql > r || qr < l) {
                return 0;
            }

            // Complete overlap: return this node's value
            if (ql <= l && r <= qr) {
                return tree[treeInd];
            }

            int mid = (l + r) / 2;

            // Partial overlap: query both children and combine with OR
            return query(l, mid, 2 * treeInd, ql, qr) | query(mid + 1, r, 2 * treeInd + 1, ql, qr);
        }
    }
}