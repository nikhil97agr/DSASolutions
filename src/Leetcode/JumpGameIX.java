package Leetcode;

public class JumpGameIX {
    public int[] maxValue(int[] nums) {
        int n = nums.length;
        int ans[] = new int[n];

        int max = 0;
        for(int i=0;i<n;i++){
            max = Math.max(max, nums[i]);
            ans[i] = max;
        }

        int preMax[] = new int[n];
        int preMin[] = new int[n];
        preMin[n-1] = nums[n-1];
        for(int i=n-2;i>=0;i--){
            preMin[i] = Math.min(preMin[i+1], nums[i]);
        }

        preMax[0] =nums[0];

        for(int i=1;i<n;i++){
            preMax[i] = Math.max(preMax[i-1], nums[i]);
        }

        for(int i=n-1;i>=0;i--){
            int start = i;
            int end = n-1;
            int ind = -1;
            while(start <= end){
                int mid = (start + end)/2;
                if(preMin[mid] < ans[i]){
                    ind = mid;
                    start = mid + 1;
                }else{
                    end = mid - 1;
                }
            }
            if(ind != -1){
            ans[i] = Math.max(ans[i], ans[ind]);
                
            }
        }
        

        return ans;
    }
}