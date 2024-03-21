package Codeforces;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

public final class CF1937B {
    private static long mod = (long)1e9+7;
    private static FastReader reader = new FastReader();

    private static String YES = "YES";

    private static String NO = "NO";

    public static void main(String[] args) {
        PrintWriter out = new PrintWriter(System.out);
        // int test = 1;
        int test = reader.nextInt();
        while (test-- > 0) {
            int n = read();
            char ch[][] = new char[2][n];
            ch[0] = charArray();
            ch[1] = charArray();

            int cnt = 0;
           StringBuilder res = new StringBuilder();
           Integer dp[][] =new Integer[2][n];
           List<int[]> ind = new ArrayList<>(n+1);

           cnt = solve(0, 0, n, res, ch, dp, ind, 0);

           for(int x[] : ind){
//               System.out.println(x[0]+":"+x[1]);
               res.append(ch[x[0]][x[1]]);
           }
           out.println(res);
           out.println(cnt);

        }

        out.flush();
        out.close();
    }

    private static int solve(int i, int j, int n, StringBuilder res, char[][] ch, Integer[][] dp, List<int[]> ind, int idx) {
        if(i>1 || j >= n) return 0;
        if(i==1 && j==n-1){
            if(idx < ind.size())
            ind.set(idx, new int[]{i, j});
            else
                ind.add(new int[]{i,j});
            return 1;
        }
        if(dp[i][j] != null) return dp[i][j];

        if(idx < ind.size()){
            ind.set(idx, new int[]{i,j});
        }else{
            ind.add(new int[]{i,j});
        }
//        System.out.println("Loop: "+i+":"+j);
        for(Integer d[]: dp){
//            System.out.println(Arrays.toString(d));
        }
        if(i==0){
            if(j==n-1){
                dp[i][j] = solve(i+1, j, n, res, ch, dp, ind, idx+1);
            }else if(ch[i][j+1] == ch[i+1][j]){
                dp[i][j] = solve(i, j+1, n, res, ch, dp, ind, idx+1)+solve(i+1, j, n, res, ch, dp, ind, idx+1) ;
            }else if(ch[i][j+1] == '0'){
                dp[i][j] = solve(i, j+1, n, res, ch, dp, ind, idx+1);
            }else{
                dp[i][j] = solve(i+1, j, n, res, ch, dp, ind, idx+1);
            }
        }else{
            if(j==n-2){
                dp[i][j] = solve(i, j+1, n, res, ch, dp, ind, idx+1);
            }else if(dp[i-1][j+1]== null || dp[i-1][j+1] == 0){
                dp[i][j] = solve(i, j+1, n, res, ch, dp, ind, idx+1);
            }else if(ch[i][j] == ch[i-1][j+1]){
                if(ch[i][j+1] == ch[i-1][j+2]){
                    dp[i][j] = solve(i, j+1, n, res, ch, dp, ind, idx+1);
                }else if(ch[i][j+1]=='0'){
                    dp[i][j] = solve(i, j+1, n, res, ch, dp, ind, idx+1);
                }
            }else if(ch[i][j] == '0'){
                dp[i][j] = solve(i, j+1, n, res, ch, dp, ind, idx+1);
            }

        }

        if(dp[i][j]==null) dp[i][j] = 0;
        return dp[i][j];

    }

    private static String[] stringArray(int n){
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

    private static long[] longArray(int n){
        long arr[] = new long[n];
        for(int i=0;i<n;i++){
            arr[i] = reader.nextLong();
        }
        return arr;
    }

    private static char[] charArray(){
        return readStr().toCharArray();
    }

    private static String readStr(){
        return reader.next();
    }
    private static int read(){
        return reader.nextInt();
    }

    private static void addToMap(int val, Map<Integer, Integer> map){
        map.put(val, map.getOrDefault(val, 0)+1);
    }

    private static void removeFromMap(int val, Map<Integer, Integer> map){
        int count = map.get(val);
        if(count==1) map.remove(val);
        else map.put(val, count-1);
    }

    private static void addToMap(long val, Map<Long, Integer> map){
        map.put(val, map.getOrDefault(val, 0)+1);
    }

    private static void removeFromMap(long val, Map<Long, Integer> map){
        int count = map.get(val);
        if(count==1) map.remove(val);
        else map.put(val, count-1);
    }
    private static void addToMap(char val, Map<Character, Integer> map){
        map.put(val, map.getOrDefault(val, 0)+1);
    }

    private static void removeFromMap(char val, Map<Character, Integer> map){
        int count = map.get(val);
        if(count==1) map.remove(val);
        else map.put(val, count-1);
    }
    private static void addToMap(String val, Map<String, Integer> map){
        map.put(val, map.getOrDefault(val, 0)+1);
    }

    private static void removeFromMap(String val, Map<String, Integer> map){
        int count = map.get(val);
        if(count==1) map.remove(val);
        else map.put(val, count-1);
    }

    private static int max(int...arr){
        return Arrays.stream(arr).max().getAsInt();
    }

    private static int min(int...arr){
        return Arrays.stream(arr).min().getAsInt();
    }

    private static long min(long...arr){ return Arrays.stream(arr).min().getAsLong(); }

    private static long max(long...arr){ return Arrays.stream(arr).max().getAsLong(); }

    private static long gcd(long a, long b){
        if(a==0) return b;

        return gcd(b%a, a);
    }

    private static int gcd(int a, int b) {
        if (a == 0) return b;

        return gcd(b % a, a);
    }

    private long multiplyMod(long a, long b){
        return (a*b)%mod;
    }

    private long addMod(long a, long b){
        return (a+b)%mod;
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