package Leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LCWeekly402D {

    class SegmentTree{
        int tree[];
        int size;
        int arr[];
        int n ;

        public SegmentTree(int arr[], int n){
            this.size  = 4*n;
            tree = new int[size];
            this.arr = arr;
            this.n = n;
            buildTree(0, n-1, 0);
        }

        private int buildTree(int start, int end, int curr){
            if(start == end){
                int cnt = (start !=0 && start!= n-1 && (arr[start] > arr[start-1] && arr[start] > arr[start+1])) ? 1 : 0;

                return tree[curr] = cnt;
            }

            int mid = (start + end)>>1;

            tree[curr] = buildTree(start, mid, 2*curr+1) + buildTree(mid+1, end, 2*curr+2);

            return tree[curr];
        }

        public int query(int s, int e, int qs, int qe, int i){

            if(qs <=s && e <= qe){

                return tree[i];
            }

            if(e < qs || s > qe) return 0;

            int mid = (s+e)>>1;

            return query(s, mid, qs, qe, 2*i+1) + query(mid+1, e, qs, qe, 2*i+2);
        }

        public void update(int start, int end, int i, int treeIndex, int val){
            if(i < start || i > end) return ;
            if(start == end){
                arr[i] = val;
                int cnt = (start !=0 && start != n-1 && (arr[start] > arr[start-1] && arr[start] > arr[start+1])) ? 1 : 0;
                tree[treeIndex] = cnt;
                return;
            }

            int mid = (start+end)>>1;

            update(start, mid, i, 2*treeIndex+1, val);
            update(mid+1, end, i, 2*treeIndex+2, val);
            tree[treeIndex] = tree[2*treeIndex+1] + tree[2*treeIndex+2];


        }


    }
    public List<Integer> countOfPeaks(int[] nums, int[][] queries) {
        SegmentTree tree = new SegmentTree(nums, nums.length);
        List<Integer> ans = new ArrayList<>();
        int n = nums.length;
        for(int[] q : queries){
            if(q[0] == 1){
                int cnt = tree.query(0, nums.length-1, q[1], q[2], 0);
                int start = q[1];
                int end = q[2];
                if(start == end){
                    if(start !=0 && start != n-1 && (nums[start] > nums[start-1] && nums[start] > nums[start+1])){
                        cnt--;
                    }

                }else{
                    if(start !=0 && start != n-1 && (nums[start] > nums[start-1] && nums[start] > nums[start+1])){
                        cnt--;
                    }

                    if(end !=0 && end != n-1 && (nums[end] > nums[end-1] && nums[end] > nums[end+1])){
                        cnt--;
                    }

                }
                ans.add(cnt);
            }else{
                int ind = q[1];
                int val = q[2];
                nums[ind] = val;
                tree.update(0, nums.length-1, ind, 0, val);
                if(ind-1 >=0){
                    tree.update(0, nums.length-1, ind-1, 0, nums[ind-1]);
                }
                if(ind+1  < n){
                    tree.update(0, n-1, ind+1, 0, nums[ind+1]);
                }
            }
        }
        return ans;
    }
}