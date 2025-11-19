package Codechef;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

class CCStart166C {
    private final static long mod = (long)1e9+7;
    private final static FastReader reader = new FastReader();
    private final static String YES = "YES";
    private final static String NO = "NO";

    private final static String DRAW = "DRAW";

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
        int a[] = intArray(n, true);

        Set<Integer> set = new HashSet<>();
        PriorityQueue<Long> que = new PriorityQueue<>();
        for(int i=1;i<=n;i++){
            if(set.contains(i)) continue;

            que.offer(getCount(set,i, a, 0));
        }

        long ans = 0;
        while(que.size() > 1){
            long x = que.poll();
            long y = que.poll();

            ans += (x+y);
            que.offer(x+y);

        }

        out.println(ans);
    }

    private static long getCount(Set<Integer> set, int i, int a[], long curr){
        if(set.contains(i)){
            return curr;
        }

        set.add(i);

        return getCount(set, a[i], a, curr+1);
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

    private static int sum(int...arr){
        return Arrays.stream(arr).sum();
    }

    private static long sum(long...arr){
        return Arrays.stream(arr).sum();
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

    private static int abs(int a){
        return Math.abs(a);
    }


    private static long abs(long a){
        return Math.abs(a);
    }

    private static long gcd(long a, long b){
        if(a==0) return b;

        return gcd(b%a, a);
    }

    private static int gcd(int a, int b) {
        if (a == 0) return b;

        return gcd(b % a, a);
    }

    private static long modInverse(long x, long y){
        if(y==0) return 1;
        if(y==1) return x;

        long ans = modInverse(x, y/2);

        ans = multiplyMod(ans%mod, ans%mod);
        if(x%2==1){
            ans = multiplyMod(ans, x);
        }
        return ans;
    }

    private static long multiplyMod(long a, long b){
        return (a*b)%mod;
    }

    private static long addMod(long a, long b){
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


    static class SegmentTree{
        int tree[];
        int size;
        int arr[];
        int n;

        public SegmentTree(int arr[]){
            this.n = arr.length;
            size = 4*n;
            this.arr = arr.clone();
            tree = new int[size];

            buildTree(0, n-1, 0);
        }

        public void buildTree(int start, int end, int ind){

            if(start == end){
                //modify this logic accordingly
                tree[ind] = arr[start];
                return;
            }

            int mid = (start+end)>>1;

            buildTree(start, mid, 2*ind+1);
            buildTree(mid+1, end, 2*ind+2);

            //modify the logic accordingly
            tree[ind] = tree[2*ind+1] + tree[2*ind+2];

        }

        public int query(int s, int e, int qs, int qe, int ind){
            if(e < qs || s > qe){
                //modify the logic accordingly
                return 0;
            }

            if(qs <= s && e <= qe){
                //modify the logic accordingly
                return tree[ind];
            }

            int mid = (s+e)>>1;

            //modify logic accordingly
            int left = query(s, mid, qs, qe, 2*ind+1);
            int right = query(mid+1, e, qs, qe, 2*ind+2);

            return left + right;
        }

        public void update(int start, int end, int index, int val, int treeIndex){
            if(index < start || index > end){
                return;
            }

            if(start == end){
                //update the logic accordingly
                arr[index] = val;
                tree[treeIndex] = val;
                return;
            }

            int mid = (start + end)>>1;

            //modify the logic accordingly
            update(start, mid, index, val, 2*treeIndex+1);
            update(mid+1, end, index, val, 2*treeIndex+2);
            tree[treeIndex] = tree[treeIndex*2+1] + tree[treeIndex*2+2];
        }
    }

}