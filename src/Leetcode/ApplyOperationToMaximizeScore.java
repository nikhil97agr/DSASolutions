package Leetcode;

import java.util.*;

// Problem Link: https://leetcode.com/problems/apply-operations-to-maximize-score/

/**
 * Solution for Apply Operations to Maximize Score
 *
 * Problem: Given an array nums and integer k, perform k operations where each operation:
 * - Choose a subarray
 * - Multiply score by max element in subarray
 * - Remove that element
 * Goal: Maximize the final score
 *
 * Approach: Greedy with Prime Score
 * - Prime score of a number = count of distinct prime factors
 * - For each element, find the range where it's the maximum prime score
 * - Process elements in descending order of value
 * - For each element, use it as many times as possible within its valid range
 */
public class ApplyOperationToMaximizeScore {

    /**
     * Calculates maximum score after k operations
     *
     * @param nums The input array
     * @param k Number of operations allowed
     * @return Maximum score modulo 10^9+7
     */
    public int maximumScore(List<Integer> nums, int k) {
        int n = nums.size();

        // Calculate prime count (number of distinct prime factors) for each element
        int primeCnt[] = new int[n];
        for(int i=0;i<n;i++){
            primeCnt[i] = getCnt(nums.get(i));
        }

        long ans = 1;

        // Create list of [index, value] pairs
        List<int[]> list = new ArrayList<>();
        for(int i=0;i<n;i++){
            list.add(new int[]{i, nums.get(i)});
        }

        // Sort by value in descending order (greedy: use larger values first)
        list.sort((a, b) -> b[1] - a[1]);

        // For each index, find how many elements to the right have prime count >= current
        int rcnt[] = rightNge(primeCnt);
        // For each index, find how many elements to the left have prime count > current
        int lcnt[] = leftNge(primeCnt);

        // Process elements in descending order of value
        for(int i=0;i<n && k > 0;i++){
            int val = list.get(i)[1];      // Current value
            int ind = list.get(i)[0];      // Original index

            // Total subarrays where this element has max prime score
            long total = 1l*lcnt[ind]*(rcnt[ind]+1);

            // Use this element min(total, k) times
            long min = Math.min(total, k);

            // Multiply answer by val^min
            ans = productMod(ans,  getPow(val, min));
            k-= (int) min;
        }

        return (int)ans;
    }

    /**
     * Calculates (val ^ min) % MOD using binary exponentiation
     * Time complexity: O(log min)
     */
    private long getPow(long val, long min){

        if(val == 1) return 1;
        if(min == 1) return val;

        // Divide and conquer: val^min = (val^(min/2))^2
        long ans = getPow(val, min/2);
        ans = productMod(ans, ans);

        // If min is odd, multiply by val once more
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