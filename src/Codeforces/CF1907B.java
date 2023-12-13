package Codeforces;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

public final class CF1907B {
    private static long mod = (long)1e9+7;
    private static FastReader reader = new FastReader();
    public static void main(String[] args) {
        PrintWriter out = new PrintWriter(System.out);
        // int test = 1;
        int test = reader.nextInt();
        while (test-- > 0) {
            String s = reader.next();
            Deque<Pair<Integer>> lower =new LinkedList<>();
            Deque<Pair<Integer>> upper = new LinkedList<>();
            int n = s.length();
            for(int i=0;i<n;i++){
                char c = s.charAt(i);
                if (Character.isUpperCase(c)) {
                    if(c=='B'){
                        if(!upper.isEmpty()){
                            upper.removeLast();
                        }
                    }else{
                        upper.add(new Pair<>(c, i));
                    }
                }else{
                    if(c=='b'){
                        if(!lower.isEmpty()){
                            lower.removeLast();
                        }
                    }else{
                        lower.add(new Pair<>(c, i));
                    }
                }
            }
            StringBuilder result = new StringBuilder();

            while(!lower.isEmpty() && !upper.isEmpty()){
                if(lower.peek().second < upper.peek().second){
                    result.append(lower.poll().first);
                }else{
                    result.append(upper.poll().first);
                }
            }

            while(!lower.isEmpty()){
                result.append(lower.poll().first);
            }
            while(!upper.isEmpty()){
                result.append(upper.poll().first);
            }
            out.println(result.toString());
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

    private long multiplyMod(long a, long b){
        return (a*b)%mod;
    }

    private long addMod(long a, long b){
        return (a+b)%mod;
    }

    static class Pair<T>{
        char first;
        int second;

        public Pair(char first, int second){
            this.first = first;
            this.second =second;
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