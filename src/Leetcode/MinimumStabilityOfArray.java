package Leetcode;

/**
 * Problem Link: https://leetcode.com/problems/minimum-stability-factor-of-array
 */
public class MinimumStabilityOfArray {

    public int minStable(int[] nums, int maxC) {
        SegmentTree tree = new SegmentTree(nums);
        int ans = 0;
        int n = nums.length;
        int start = 1;
        int end = n;

        while(start <= end){
            int mid = (start + end)/2;

            boolean possible = compute(mid, nums.length, tree, maxC);


            if(possible){
                end = mid-1;
            }else{
                start = mid+1;
                ans =  mid;
            }

        }

        return ans;

    }

    private boolean compute(int len, int n, SegmentTree tree, int max ){
        int i=0;
        int cnt = 0;
        while(i+len -1 < n){
            int gcd = tree.query(0, n-1,i, i + len-1, 0 );
            if(gcd >=2){
                i = i+len;
                cnt++;
            }else{
                i++;
            }
        }
        return cnt <= max;
    }


    private int gcd(int a, int b){
        if(a==0) return b;

        return gcd(b%a, a);
    }

     class SegmentTree{
        int tree[];
        int size;
        int arr[];
        int n;

        public SegmentTree(int arr[]){
            this.n = arr.length;
            size = 4*n;
            this.arr = arr.clone();
            tree = new int[size];

            buildTree(0, n-1, 0);
        }

        public void buildTree(int start, int end, int ind){

            if(start == end){
                //modify this logic accordingly
                tree[ind] = arr[start];
                return;
            }

            int mid = (start+end)>>1;

            buildTree(start, mid, 2*ind+1);
            buildTree(mid+1, end, 2*ind+2);

            //modify the logic accordingly
            tree[ind] = gcd(tree[2*ind+1] , tree[2*ind+2]);

        }

        public int query(int s, int e, int qs, int qe, int ind){
            if(e < qs || s > qe){
                //modify the logic accordingly
                return 0;
            }

            if(qs <= s && e <= qe){
                //modify the logic accordingly
                return tree[ind];
            }

            int mid = (s+e)>>1;

            //modify logic accordingly
            int left = query(s, mid, qs, qe, 2*ind+1);
            int right = query(mid+1, e, qs, qe, 2*ind+2);

            return gcd(left, right);
        }

        public void update(int start, int end, int index, int val, int treeIndex){
            if(index < start || index > end){
                return;
            }

            if(start == end){
                //update the logic accordingly
                arr[index] = val;
                tree[treeIndex] = val;
                return;
            }

            int mid = (start + end)>>1;

            //modify the logic accordingly
            update(start, mid, index, val, 2*treeIndex+1);
            update(mid+1, end, index, val, 2*treeIndex+2);
            tree[treeIndex] =gcd(tree[treeIndex*2+1], tree[index*2+1]);
        }

        private int gcd(int a, int b){
            if(a==0) return b;
            return gcd(b%a, a);
        }
    }
}