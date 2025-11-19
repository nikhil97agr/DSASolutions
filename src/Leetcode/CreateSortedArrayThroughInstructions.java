package Leetcode;

import java.util.Arrays;

public class CreateSortedArrayThroughInstructions {

    public int createSortedArray(int[] arr) {
        int max = Arrays.stream(arr).max().getAsInt();

        int ans = 0;
        SegmentTree tree =new SegmentTree(max+1);
        for(int x : arr){
            ans = add(ans, Math.min(tree.query(0, max, 0, x-1, 0), tree.query(0, max, x+1, max, 0)));

            tree.update(0, max, x, 0);
        }

        return ans;
    }

    private int add(int a, int b){
        long mod = (long)1e9 + 7;

        long sum = a+b;

        return (int)(sum%mod);
    }

    class SegmentTree{
        int tree[];

        public SegmentTree(int n){
            tree = new int[n*4];

        }

        public int query(int start, int end, int queryStart, int queryEnd, int treeInd){
            if(queryStart > queryEnd) return 0;
            if(queryEnd < start || queryStart > end){
                return 0;
            }

            if(queryStart <= start && end <= queryEnd){
                return tree[treeInd];
            }


            int mid = (start + end)/2;

            return query(start, mid, queryStart, queryEnd, treeInd*2+1) + query(mid+1, end, queryStart, queryEnd, treeInd*2 + 2);
        }

        public int update(int start, int end, int ind, int treeInd){
            if(ind < start || end < ind) return tree[treeInd];


            if(start == end){
                tree[treeInd]++;
                return tree[treeInd];
            }

            int mid = (start + end)/2;

            return tree[treeInd] =  update(start, mid, ind, treeInd*2+1) + update(mid+1, end, ind, 2*treeInd + 2);
        }
    }
}