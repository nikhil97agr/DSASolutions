package Codeforces;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

public final class CF1928B {
    private static long mod = (long)1e9+7;
    private static FastReader reader = new FastReader();
    public static void main(String[] args) {
        PrintWriter out = new PrintWriter(System.out);
        // int test = 1;
        int test = reader.nextInt();
        while (test-- > 0) {

            long n = read();
            long x = read();
            if(x==1){
                if(n%2==1){
                    out.println(1);
                }else{
                    out.println(0);
                }
                continue;
            }
            Set<Long> res = new HashSet<>();
            long posFront = x;
            int cnt = 0;
            long num = n-x;
            long req = 2*x - 2;
            for(long i = 1;i<=Math.sqrt(n-x);i++){
                if(num%i==0){
                    long first = i;
                    long second = num/i;
                    if(first >= req && first%2==0){
                        cnt++;
                        res.add(first);
                    }
                    if(second >= req && second%2==0 && second != first){
                        cnt++;
                        res.add(second);
                    }

//                    System.out.println(first+":"+second +":"+cnt);
                }
            }
            long num2 = n+x-2;
            for(long i = 1;i<=Math.sqrt(n+x-2);i++){
                if(num%i==0){
                    long first = i;
                    long second = num2/i;
                    if(first >= req && first%2==0){
                        res.add(first);
                        cnt++;
                    }
                    if(second >= req && second%2==0 && second != first){
                        res.add(second);
                        cnt++;
                    }

//                    System.out.println(first+":"+second +":"+cnt);
                }
            }

//            for(long i=x;i<=n;i+=2){
//                if(n%i==posFront){
//                    System.out.println(i);
//                }
//                if(n%i == posFront || n%i == i-2){
//                    cnt++;
//                }
//            }
//
//            System.out.println("-----------------------------------");
//
//            for(long i=Math.max(2, x);i<=n;i++){
//                long total = 2*i -2;
//                if(n%total ==0){
//                    if(x==2){
//                        System.out.println(i);
//                        cnt++;
//                    }
//                }else{
//                    long left = n % total;
//                    if(left <= i && x==left){
//                        System.out.println(i);
//                        cnt++;
//                    }
//                    else if(left > i){
//                        long remain = left - i;
//                        if(x == i - remain){
//                            System.out.println(i);
//                            cnt++;
//                        }
//
//                    }
//                }
//            }
            out.println(res.size());
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

    private static int gcd(int a, int b){
        if(a==0) return b;

        return gcd(b%a,a);
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