package Leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LCBiweekly151D {
    public static void main(String[] args) {
        int ans [] = new LCBiweekly151D().permute(4, 6);
        System.out.println(Arrays.toString((ans)));
    }
    public int[] permute(int n, long k) {
        int cnt= n/2;
        long fact = 1;
        while(cnt > 0 && fact <k){
            fact*=cnt;
            cnt--;
        }

        cnt = (n+1)/2;
        while(cnt > 0 && fact < k){
            fact *=cnt;
            cnt--;
        }

        if(fact*2 < k) return new int[0];
        Map<Integer, Long> map = new HashMap<>();
        boolean vis[] = new boolean[n+1];
        int ans[] = new int[n];
        int even = n/2;
        int odd = (n+1)/2;
        if(n%2==0){
            for(int i=1;i<=n;i++){
                if(i%2==1)
                    fact = fact(odd-1, even, k);
                else{
                    fact = fact(odd, even-1, k);
                }
                if(fact < k){
                    k -= fact;
                }else{
                    ans[0] = i;
                    vis[i] = true;
                    boolean res = solve(ans, n, 1, i%2, i%2==0 ? even-1: even, i%2==1 ? odd-1:odd, k, vis);
                    if(res) return ans;
                    vis[i] = false;
                    ans[0] = 0;
                }
            }


        }else{
            for(int i=1;i<=n;i+=2){
                fact = fact(odd-1, even, k);
                if(fact < k) k-=fact;
                else{
                    ans[0] = i;
                    vis[i] =true;
                    boolean res = solve(ans, n, 1, 1, even, odd-1, k, vis);
                    if(res) return ans;

                    vis[i] = false;
                    ans[0] = 0;
                }
            }
        }
        return new int[0];
    }

    private boolean solve(int ans[], int n, int i, int prev, int even, int odd, long k, boolean vis[]){
        if(i==n) return true;
        long possible = k;
        if(prev ==0){
            for(int j=1;j<=n;j+=2){
                if(!vis[j]){
                    long fact = fact(odd-1, even, k);

                    if(fact < possible){
                        possible -= fact;
                    }else{
                        vis[j] = true;
                        ans[i] = j;
                        boolean is = solve(ans, n, i+1, 1, even, odd-1, possible, vis);
                        if(is) return true;
                        vis[j] =false;
                        ans[i] = 0;
                    }
                }
            }
        }else{
            for(int j=2;j<=n;j+=2){
                if(!vis[j]){
                    long fact = fact(odd, even-1, k);

                    if(fact < possible){
                        possible -= fact;
                    }else{
                        vis[j] = true;
                        ans[i] = j;
                        boolean is = solve(ans, n, i+1, 0, even-1, odd, possible, vis);
                        if(is) return true;
                        vis[j] =false;
                        ans[i] = 0;
                    }
                }
            }
        }

        return false;


    }

    private long fact(int odd, int even, long k){
        long fact = 1;
        while(odd > 0 && fact < k){
            fact*=odd;
            odd --;
        }

        while(even > 0 && fact < k){
            fact*=even;
            even--;
        }

        return fact;
    }
}