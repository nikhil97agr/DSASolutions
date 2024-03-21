package Leetcode;

public class LCWeekly384D {
    public int countMatchingSubarrays(int[] nums, int[] pattern) {
        int n = nums.length;
        for(int i=0;i<n-1;i++){
            if(nums[i+1] > nums[i]){
                nums[i] = 1;
            }else if(nums[i+1] < nums[i]){
                nums[i] = -1;
            }else{
                nums[i] = 0;
            }
        }
        int lps[] = buildLps(pattern, pattern.length);
        n--;
        int i = 0;
        int j=0;
        int m = pattern.length;
        int cnt = 0;

        while(i<n){

            if(nums[i] == pattern[j]){
                i++;
                j++;
            }

            if(j==m){
                cnt++;
                j = lps[j-1];
            }else if(i < n && nums[i] != pattern[j]){
                if(j!=0){
                    j = lps[j-1];
                }else{
                    i = i+1;
                }
            }
        }
        return cnt;
    }

    private int[] buildLps(int[] pattern, int n) {
        int lps[] = new int[n];
        int i=1;
        int len = 0;
        lps[0] = 0;

        while(i < n){
            if(pattern[i] == pattern[len]){
                len++;
                lps[i] = len;
                i++;
            }else{
                if(len!=0){
                    len = lps[len-1];
                }else{
                    lps[i] = len;
                    i++;
                }
            }
        }

        return lps;

    }
}