package Codechef;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.StringTokenizer;

public final class CCStart111D {
    private static long mod = (long)1e9+7;
    private static FastReader reader = new FastReader();
    public static void main(String[] args) {
        PrintWriter out = new PrintWriter(System.out);
        // int test = 1;
        int test = reader.nextInt();
        while (test-- > 0) {
            long n = read();
            if(n<3){
                out.println(-1);
                continue;
            }
            if(n%3==0){
                n/=3;
                out.println(n+" "+n+" "+n);
                continue;
            }
            if((n&1)==1){
                n--;
                out.println(1+" "+1+" "+(n/2));
                continue;
            }

            if((((n-2)/2)%2)==0){
                out.println(2+" "+2+" "+((n-2)/2));
                continue;
            }

            if((n&(n-1))==0){
                out.println(-1);
                continue;
            }
            boolean ans = false;
            long i=2;
            while(i<=n){
                long left = (n-i)/2;
                if (left % i == 0) {
                    out.println(i+" "+i+" "+left);
                    ans = true;
                    break;
                }
                i = i<<1;
            }
            if(!ans){
                out.println(-1);
            }

//
//            long i=1;
//            for(; i<=n;i++){
//                for(long j=i;j<=n;j++){
//                    for(long k=j;k<=n;k++){
//                        long sum  = (i*j)/gcd(i,j) + (j*k)/gcd(j,k) + (k*i)/gcd(k,i);
//                        if(sum==n){
//                            out.println(i+" "+j+" "+k);
//                        }
//                    }
//                }
//            }






        }

        out.flush();
        out.close();
    }

    private static long gcd(long a, long b){
        if(a==0) return b;
        return gcd(b%a, a);
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

    private long multiplyMod(long a, long b){
        return (a*b)%mod;
    }

    private long addMod(long a, long b){
        return (a+b)%mod;
    }

    static class Pair<T>{
        T first;
        T second;

        public Pair(T first, T second){
            this.first = first;
            this.second =second;
        }

        @Override
        public boolean equals(Object ob){
            Pair pair = (Pair)ob;
            return this.first == pair.first && this.second == pair.second;
        }

        @Override
        public int hashCode(){
            return (first.toString()+":"+second.toString()).hashCode();
        }


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