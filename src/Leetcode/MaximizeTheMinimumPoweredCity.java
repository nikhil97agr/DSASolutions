package Leetcode;

public class MaximizeTheMinimumPoweredCity {
    public long maxPower(int[] stations, int r, int k) {
        int n = stations.length;

        long power[] = calcPower(stations, r, n );

        long start = 0, end = 0;
        for(long x : power){
            end = Math.max(end, x  + k);
        }

        long ans = 0;

        while(start <= end){
            long mid = (start + end)/2;

            if(possible(power, r, k, mid)){
                ans = mid;
                start = mid + 1;
            }else{
                end = mid - 1;
            }
        }


        return ans;
    }

    private boolean possible(long power[], int r, long k, long mid){
        power = power.clone();
        int n =power.length;
        long sum = 0;

        for(int i=0;i<n;i++){
            sum += power[i];

            if(sum < mid){
                long req = mid - sum;
                k-= req;
                if(k < 0) return false;

                int end = Math.min(n, i + 2*r + 1);
                power[end] -=req;
                sum += req;
            }
        }


        return true;
    }
    private long [] calcPower(int stations[], int r, int n){
        long power[] = new long[n+1];
        for(int i=0;i<n;i++){
            int start = Math.max(i-r, 0);
            int end = Math.min(n, i +r + 1);
            power[start] += stations[i];
            power[end] -= stations[i];
        }

        return power;
    }
}