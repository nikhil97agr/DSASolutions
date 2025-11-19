package Leetcode;

import java.util.*;

//Problem Link: https://leetcode.com/problems/apply-operations-to-maximize-score/

public class ApplyOperationToMaximizeScore {
    public int maximumScore(List<Integer> nums, int k) {
        int n = nums.size();
        int primeCnt[] = new int[n];

        for(int i=0;i<n;i++){
            primeCnt[i] = getCnt(nums.get(i));
        }
        long ans = 1;
        List<int[]> list = new ArrayList<>();
        for(int i=0;i<n;i++){
            list.add(new int[]{i, nums.get(i)});
        }

        list.sort((a, b) -> b[1] - a[1]);

        int rcnt[] = rightNge(primeCnt);
        int lcnt[] = leftNge(primeCnt);

        for(int i=0;i<n && k > 0;i++){
            int val = list.get(i)[1];
            int ind = list.get(i)[0];
            long total = 1l*lcnt[ind]*(rcnt[ind]+1);
            long min = Math.min(total, k);

            ans = productMod(ans,  getPow(val, min));
            k-= (int) min;
        }

        return (int)ans;
    }

    private long getPow(long val, long min){

        if(val == 1) return 1;
        if(min == 1) return val;

        long ans = getPow(val, min/2);
        ans = productMod(ans, ans);
        if(min%2==1){
            ans = productMod(ans, val);
        }

        return ans;

    }

    private int[] leftNge(int arr[]){
        int n = arr.length;
        int nge[] = new int[n];

        Stack<Integer> stack = new Stack<>();
        int i=n-1;
        while(i>=0){
            if(stack.isEmpty() || arr[i] < arr[stack.peek()]){
                stack.push(i);
                i--;
                continue;
            }

            while(!stack.isEmpty() && arr[stack.peek()] <= arr[i]){
                int ind = stack.pop();
                int cnt = ind - i;
                nge[ind] = cnt;
            }

            stack.push(i);
            i--;
        }

        while(!stack.isEmpty()){
            int ind = stack.pop();
            nge[ind] = ind+1;
        }

        return nge;
    }

    private int[] rightNge(int arr[]){
        int n =arr.length;
        int nge[]= new int[n];

        Stack<Integer> stack = new Stack<>();
        int i=0;
        while(i<n){
            if(stack.isEmpty() || arr[i] <= arr[stack.peek()]){
                stack.push(i);
                i++;
                continue;
            }

            while(!stack.isEmpty() && arr[stack.peek()] < arr[i]){
                int ind = stack.pop();
                int cnt = i - ind - 1;
                nge[ind] = cnt;
            }
            stack.push(i);
            i++;
        }

        while(!stack.isEmpty()){
            int ind = stack.pop();
            int cnt = i - ind - 1;
            nge[ind] = cnt;
        }
        return nge;
    }

    private long productMod(long a, long b){
        long mod = (long)1e9+7;

        a%=mod;
        b%=mod;

        return (a*b)%mod;
    }

    private int getCnt(int x){
        Set<Integer> set = new HashSet<>();

        while(x %2==0){
            set.add(2);
            x/=2;
        }

        for(int i=3;i<=Math.sqrt(x);i+=2){
            if(x%i==0){
                set.add(i);
                while(x%i==0){
                    x/=i;
                }
            }
        }

        if(x > 1){
            set.add(x);
        }

        return set.size();
    }
}