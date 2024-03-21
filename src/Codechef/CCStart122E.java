package Codechef;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

class CCStart122E {
    private static long mod = (long)1e9+7;
    private static FastReader reader = new FastReader();
    public static void main(String[] args) {
        PrintWriter out = new PrintWriter(System.out);
        // int test = 1;
        int test = reader.nextInt();
        while (test-- > 0) {

            int n = read();

            Map<Integer, Node> map = new HashMap<>();
            for(int i=0;i<n;i++){
                long score = read();
                map.put(i, new Node(score));
            }

            boolean visited[] = new boolean[n];
            Map<Integer, List<Integer>> m2 = new HashMap<>();
            for(int i=0;i<n-1;i++){
                int u = read();
                int v = read();
                m2.computeIfAbsent(u-1, key -> new ArrayList<>()).add(v-1);
                m2.computeIfAbsent(v-1, key -> new ArrayList<>()).add(u-1);
            }

            Queue<Integer> que = new LinkedList<>();

            que.offer(0);
            while(!que.isEmpty()){
                int x = que.poll();
                List<Integer> list = m2.get(x);
                visited[x] = true;
                for(int y : list){
                    if(!visited[y]){

                        que.offer(y);
                        map.get(x).child.add(map.get(y));
                    }
                }
            }

            Arrays.fill(visited, false);
            //First : same branch activated
            //Second : no branch activated
            List<Pair<Long>> score = new ArrayList<>();
            for(Node node : map.get(0).child){
                Pair<Long> p = dfs(node, 1);
                score.add(p);
            }
            Map<Integer, Long> res = new HashMap<>();

        }

        out.flush();
        out.close();
    }

    private static Pair<Long> dfs(Node node, int steps){
        if(node.child.isEmpty()){
            if(steps == 1){
                return new Pair<>(node.val - 1, node.val - 1);
            }
            return new Pair<>(node.val - (steps-1), node.val - steps);
        }

        List<Pair<Long>> list = new ArrayList<>();
        Pair<Long> res = null;
        if(steps == 1){
            res = new Pair<>(node.val-1 , node.val - 1);
        }else{
            res = new Pair<>(node.val - (steps-1), node.val - steps);
        }
        for(Node child : node.child){
            Pair<Long> pair = dfs(child, steps+1);
            list.add(pair);
        }

        long sum1 = list.stream().filter(p -> p.first >0).map(p -> p.first).reduce(0L, Long::sum);
        long sum2 = list.stream().filter(p -> p.second > 0).map(p -> p.second).reduce(0L, Long::sum);

        res.first += sum1;
        res.second += sum2;

        return res;



    }

    static class Node{
        long val;
        List<Node> child;
        public Node(long val){
            this.val = val;
            child = new ArrayList<>();
        }

        @Override
        public String toString() {
            return "Node{" +
                    "val=" + val +
                    ", child=" + child +
                    '}';
        }
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

    static class Trie{
        Trie trie[];
        boolean isLast;
        public Trie(){
            trie = new Trie[256];
        }

        public void build(String str, int i, int n){
            if(i == n){
                this.isLast = true;
                return;
            }

            char c = str.charAt(i);
            if(trie[c]==null){
                trie[c] = new Trie();
            }

            trie[c].build(str, i+1, n);
        }

        public boolean search(String str, int i, int n){
            if(i==n){
                return this.isLast;
            }

            char c = str.charAt(i);
            if(trie[c] ==null) return false;
            return trie[c].search(str, i+1, n);
        }
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
            return this.first.equals(pair.first) && this.second.equals(pair.second);
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