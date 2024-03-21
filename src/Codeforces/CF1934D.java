package Codeforces;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

public final class CF1934D {
    private static long mod = (long)1e9+7;
    private static FastReader reader = new FastReader();

    private static String YES = "YES";

    private static String NO = "NO";

    public static void main(String[] args) {
        PrintWriter out = new PrintWriter(System.out);
        // int test = 1;
        int test = reader.nextInt();
        while (test-- > 0) {
            long x = reader.nextLong();
            long m = reader.nextLong();

            List<Long> res = new ArrayList<>(64);
            res.add(x);
            boolean possible = true;
            out.println();
            long bitX[] = new long[61];
            long bitM[] = new long[61];
            int ind = 0;
            long temp = x;
            while(x >0)
            {
                long mod = x%2;
                bitX[ind++] = mod;
                x/=2;
            }
            x = temp;
            ind = 0;
            while(m >0){
                long mod = m%2;
                bitM[ind++] = mod;
                m/=2;
            }
            System.out.println(Arrays.toString(bitX));
            System.out.println(Arrays.toString(bitM));
            while(!Arrays.equals(bitX, bitM) && res.size() <=64){
                ind = bitX.length - 1;
                
            }

            if(res.size() >64 || !possible){
                out.println(-1);
            }else{
                out.println(res.size()-1);
                for(long y : res){
                    out.print(y+" ");
                }
                out.println();
            }

        }

        out.flush();
        out.close();
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