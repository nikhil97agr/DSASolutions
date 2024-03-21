package Codeforces;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.StringTokenizer;

public final class CF1934C {
    private static long mod = (long)1e9+7;
    private static FastReader reader = new FastReader();

    private static String YES = "YES";

    private static String NO = "NO";

    public static void main(String[] args) {
        PrintWriter out = new PrintWriter(System.out);
        // int test = 1;
        int test = reader.nextInt();
        while (test-- > 0) {
            long n = read();
            long m = read();

            long topLeft = query(1,1);

            if(topLeft ==0){
                answer(1,1);
                continue;
            }

            long topRight = 0;
            long bottomRight = 0;
            long x1 = 0;
            long y1= 0;
            long x2 = 0;
            long y2= 0;
            if(topLeft +1 <= m){

              x1 = 1;
              y1 = topLeft +1;
              topRight = query(x1, y1);
              if(topRight==0){
                  answer(x1,y1);
                  continue;
              }
            }else{

                x1 =1+ topLeft - (m-1);
                y1 = m;
                topRight = query(x1, y1);
                if(topRight ==0){
                    answer(x1, y1);
                    continue;
                }
            }

            if(topLeft +1 <= n){
                x2 = topLeft +1;
                y2 = 1;
                bottomRight = query(x2, y2);
                if(bottomRight == 0){
                    answer(x2, y2);
                    continue;
                }
            }else{
                x2 = n;
                y2 = 1+topLeft - (n-1);
                bottomRight = query(x2, y2);
                if(bottomRight==0){
                    answer(x2, y2);
                    continue;
                }
            }

            if(topRight == bottomRight){
                long half = topRight/2;
                long ans = query(x1+half, y1-half);
                if(ans ==0){
                    answer(x1+half, y1-half);
                }else{
                    answer(x2-half, y2+half);
                }
                continue;

            }
            if(topRight > bottomRight){
                if(topRight%2==0){
                    long half = topRight/2;
                    long temp = query(x1 + half, y1 - half);
                    if(temp ==0)
                        answer(x1 + half, y1 - half);
                    else{
                        half = bottomRight /2;
                        answer(x2 - half, y2 + half);
                    }
                }else{
                    long half = bottomRight /2;
                    answer(x2 - half, y2 + half);

                }
            }else{
                if(bottomRight%2==0){
                    long half = bottomRight/2;
                    long temp = query(x2 - half, y2 + half);
                    if(temp == 0)
                    answer(x2 - half, y2+ half);
                    else{
                        half = topRight/2;
                        answer(x1 + half, y1 - half);
                    }
                }else{
                    long half = topRight/ 2;
                    answer(x1 + half, y1- half);
                }
            }







        }

        out.flush();
        out.close();
    }

    private static void answer(long x, long y){
        System.out.println("! "+x+" "+y);
        System.out.flush();

    }

    private static long query(long x, long y){
        System.out.println("? "+x+" "+y);
        System.out.flush();
        return read();
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