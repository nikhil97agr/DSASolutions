package Codeforces;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.StringTokenizer;

public final class CF1955C {
    private final static long mod = (long)1e9+7;
    private final static FastReader reader = new FastReader();
    private final static String YES = "YES";
    private final static String NO = "NO";

    public static void main(String[] args) {
        PrintWriter out = new PrintWriter(System.out);
        // int test = 1;
        int test = reader.nextInt();
        while (test-- > 0) {

            solve(out);
        }

        out.flush();
        out.close();
    }

    private static void solve(PrintWriter out){
        int n = read();
        long k = readLong();
        long arr[] = longArray(n, false);
        int i=0;
        int j=n-1;
        long ans =0;
        int turn = 0;
        while(i<j){
            if(arr[i] > arr[j]){
                if(turn ==0){
                    if(arr[j]*2 <=k){
                        k-= arr[j]*2;
                        arr[i] -= arr[j];
                        j--;
                        ans++;
                    }else{
                        break;
                    }
                }else{
                    if(arr[j]*2 -1L <= k){
                        k-= (arr[j]*2 - 1L);
                        arr[i] -= (arr[j] - 1);
                        j--;
                        ans++;
                        turn = 0;
                    }else{
                        break;
                    }
                }
            }else if(arr[i] < arr[j]){
                if(turn == 0){
                    if(arr[i]*2-1L <= k){
                        k-= (arr[i]*2 - 1L);
                        arr[j] -= (arr[i] - 1);
                        ans++;
                        i++;
                        turn = 1;
                    }else{
                        break;
                    }
                }else{
                    if(k>= arr[i]*2){
                        k-= arr[i]*2;
                        arr[j] -= arr[i];
                        i++;
                        ans++;
                    }else{
                        break;
                    }

                }
            }else{
                if(turn == 0){
                    if(k>= arr[i]*2-1L){
                        k-= (arr[i]*2-1L);
                        i++;
                        arr[j] = 1L;
                        ans++;
                        turn = 1;
                    }else{
                        break;
                    }
                }else{
                    if(k >= arr[j]*2 -1L){
                        k-= (arr[j]*2 - 1L);
                        j--;
                        arr[i] = 1L;
                        ans++;
                        turn = 0;
                    }else{
                        break;
                    }
                }
            }
        }

        if(i==j){
            if(k>= arr[i]){
                ans++;
            }

        }
        out.println(ans);
    }


    private static String[] stringArray(int n, boolean oneIndexed){
        int i=0;
        String s[] = new String[n];
        if(oneIndexed){
            i=1;
            s = new String[n+1];
            n++;
        }

        for(;i<n;i++){
            s[i] = reader.next();
        }
        return s;
    }

    private static long readLong(){
        return reader.nextLong();
    }



    private static int[] intArray(int n, boolean oneIndexed){
        int i=0;
        int arr[] = new int[n];
        if(oneIndexed){
            i = 1;
            arr = new int[n+1];
            n++;
        }
        for(;i<n;i++){
            arr[i] = reader.nextInt();
        }
        return arr;
    }

    private static long[] longArray(int n, boolean oneIndexed){
        long arr[] = new long[n];
        int i =0;
        if(oneIndexed){
            i=1;
            arr = new long[n+1];
            n++;
        }
        for(;i<n;i++){
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