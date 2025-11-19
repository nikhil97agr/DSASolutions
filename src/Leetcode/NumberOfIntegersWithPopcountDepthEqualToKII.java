package Leetcode;

import java.util.ArrayList;
import java.util.List;

public class NumberOfIntegersWithPopcountDepthEqualToKII {
    public int[] popcountDepth(long[] nums, long[][] queries) {
        int n = nums.length;
        int popCount[] = new int[n];

        for(int i=0;i<n;i++){
            int cnt = getDepth(nums[i]);

            popCount[i] = cnt;


        }

        SegmentTree tree[] = new SegmentTree[6];

        for(int i=0;i<=5;i++){
            tree[i] = new SegmentTree(popCount, i);
        }
        int q = queries.length;

        List<Integer> ans = new ArrayList<>();
        for(int i=0;i<q;i++){
            long type = queries[i][0];

            if(type == 1){
                int l = (int)queries[i][1];
                int r = (int)queries[i][2];
                int k = (int)queries[i][3];
                ans.add(tree[k].query(0, n-1, l, r, 0));
            }else{
                int cnt = getDepth(queries[i][2]);

                for(int j=0;j<=5;j++){
                    tree[j].update(0, n-1, (int)queries[i][1], cnt, 0);
                }
            }
        }

        int res[] = new int[ans.size()];

        for(int i=0;i<ans.size();i++){
            res[i] = ans.get(i);
        }

        return res;
    }

    private int getDepth(long x){
        int ans = 0;

        while(x != 1l){
            ans++;
            x = Long.bitCount(x);
        }

        return ans;
    }



    static class SegmentTree{
        int tree[];
        int size;
        int arr[];
        int n;

        int k;



        public SegmentTree(int arr[], int k){
            this.n = arr.length;
            size = 4*n;
            this.arr = arr.clone();
            tree = new int[size];
            this.k = k;
            buildTree(0, n-1, 0);
        }

        public void buildTree(int start, int end, int ind){

            if(start == end){

                if(arr[start] !=k){
                    tree[ind] = 0;
                }else{
                    tree[ind] = 1;
                }
                return;
            }

            int mid = (start+end)>>1;

            buildTree(start, mid, 2*ind+1);
            buildTree(mid+1, end, 2*ind+2);


            tree[ind] = tree[2*ind+1] + tree[2*ind+2];

        }

        public int query(int s, int e, int qs, int qe, int ind){
            if(e < qs || s > qe){

                return 0;
            }

            if(qs <= s && e <= qe){
                return tree[ind];
            }

            int mid = (s+e)>>1;

            int left = query(s, mid, qs, qe, 2*ind+1);
            int right = query(mid+1, e, qs, qe, 2*ind+2);

            return left + right;
        }

        public void update(int start, int end, int index, int val, int treeIndex){
            if(index < start || index > end){
                return;
            }

            if(start == end){
                arr[index] = val;


                if(val == k){
                    tree[treeIndex] = 1;
                }else{
                    tree[treeIndex] = 0;
                }
                return;
            }

            int mid = (start + end)>>1;

            update(start, mid, index, val, 2*treeIndex+1);
            update(mid+1, end, index, val, 2*treeIndex+2);
            tree[treeIndex] = tree[treeIndex*2+1] + tree[treeIndex*2+2];
        }

    }

}