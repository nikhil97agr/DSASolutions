package Leetcode;

public class SumKMirrorNumbers {
    int cnt = 0;
    public long kMirror(int k, int n) {
        long ans = 0;

        cnt =0 ;

        int len = 1;
        while(cnt < n){
            int arr[] = new int[len];

            ans += generate(arr, k, n, 0, len-1);
            len++;
        }

        return ans;
    }

    private long generate(int arr[], int k, int n, int start, int end){

        long ans = 0;
        for(int i = (start == 0 ? 1 : 0) ; i<=9 && cnt < n ;i++){
            arr[start] = arr[end] = i;

            if(end - start <= 1){
                long value = getVal(arr);
                if (isMirror(value, k)) {
                    ans +=  value;
                    cnt++;
                }
            }else{
                ans += generate(arr, k, n, start+1, end-1);
            }
        }

        return ans;
    }

    private long getVal(int arr[]){
        long ans = 0;

        for(int x : arr){
            ans  = ans*10 + x;
        }

        return ans;
    }


    private boolean isMirror(long n, int k){
        StringBuilder sb = new StringBuilder();

        while(n > 0){
            long dig = n%k;
            sb.append(dig);
            n/=k;
        }

        String s = sb.toString();

        return s.contentEquals(sb.reverse());
    }
}