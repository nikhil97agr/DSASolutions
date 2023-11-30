package Codechef;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

 class CcStart110D {
    private static FastReader reader = new FastReader();
    public static void main(String[] args) {
        PrintWriter out = new PrintWriter(System.out);
        // int test = 1;
        int test = reader.nextInt();
        long mod = (long)1e9+7;
        long dp[][] = new long[100000+1][2];
        dp[0][1] = 1;
        dp[0][0] = 1;

        for(int i=1;i<=100000;i++){
            for(int j=0;j<2;j++){
                for(int k=1;k<=4;k++){
                    int prev = i-k;
                    if(prev<0) prev = 0;
                    if(prev==0 && (k!=4 || j!=0)) continue;

                    if(k==1){
                        dp[i][j] += dp[prev][j^1];
                    }else{
                        dp[i][j] += dp[prev][j];
                    }
                    dp[i][j]%=mod;

                }
            }
        }
        while (test-- > 0) {
            int l = read();


            out.println(dp[l][0]);

        }

        out.flush();
        out.close();
    }



    private static long solve(int curr, int turn, int l, int prev, int prevTurn, Long dp[][][][], long mod){
        if(curr >= l){
            if(prev ==4 && prevTurn ==0) return 1;
            return 0;
        }

        if(dp[curr][prev][turn][prevTurn]!=null) return dp[curr][prev][turn][prevTurn];
        long ans = 0;
        for(int i=1;i<=4;i++){
            if(i==1){
                ans += solve(curr+i, (turn+1)%2, l, i, turn, dp, mod)%mod;
                ans %=mod;
            }else{
                ans += solve(curr+i, turn, l, i, turn, dp, mod)%mod;
                ans %=mod;
            }
        }
        return dp[curr][prev][turn][prevTurn] = ans;
    }


    private static String[] array(int n){
        String s[] = new String[n];
        for(int i=0;i<n;i++){
            s[i] = reader.next();
        }
        return s;
    }
    private static int[] intArray(int n){
        int arr[] = new int[n];
        for(int i=0;i<n;i++){
            arr[i] = reader.nextInt();
        }
        return arr;
    }
    private static int read(){
        return reader.nextInt();
    }

    static class FastReader {
        BufferedReader br;
        StringTokenizer st;

        public FastReader() {
            br = new BufferedReader(
                    new InputStreamReader(System.in));
        }

        String next() {
            while (st == null || !st.hasMoreElements()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }

        long nextLong() {
            return Long.parseLong(next());
        }

        int[] readIntArray(int arr[], int n) {
            arr = new int[n];
            for (int i = 0; i < n; i++) {
                arr[i] = nextInt();
            }
            return arr;
        }

        long[] readLongArray(long arr[], int n) {
            arr = new long[n];
            for (int i = 0; i < n; i++) {
                arr[i] = nextLong();
            }
            return arr;
        }

        double nextDouble() {
            return Double.parseDouble(next());
        }

        String nextLine() {
            String str = "";
            try {
                if (st.hasMoreTokens()) {
                    str = st.nextToken("\n");
                } else {
                    str = br.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return str;
        }
    }

}