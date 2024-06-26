package Codechef;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

class CCStart114D {
    private static long mod = (long)1e9+7;
    private static FastReader reader = new FastReader();
    public static void main(String[] args) {
        PrintWriter out = new PrintWriter(System.out);
        // int test = 1;
        int test = reader.nextInt();
        while (test-- > 0) {
            int n =read();
            Node node[] = new Node[n+1];
            for(int i=1;i<=n;i++){
                node[i] = new Node(i);
            }

            for(int i=0;i<n-1;i++){
                int u = read();
                int v = read();
                node[u].add(node[v]);
                node[v].add(node[u]);
                node[u].degree++;
                node[v].degree++;
            }

            calculateLeaves(node[1],new boolean[n+1]);
            calcSeconds(node[1], new boolean[n+1]);

            Queue<Node> que = new LinkedList<>();
            que.offer(node[1]);
            int currSec = 0;
            int ans = 0;
            boolean vis[] = new boolean[n+1];
            while(!que.isEmpty()){
                int size = que.size();
                while(size-- >0){
                    Node root = que.poll();
                    vis[root.val ]= true;
                    if(root.sec <= currSec){
                        ans = Math.max(root.leafs, ans);
                    }
                    for(Node child : root.child){
                        if(!vis[child.val]){
                            que.offer(child);
                        }
                    }
                }
                currSec++;

            }
            out.println(ans);

        }

        out.flush();
        out.close();
    }

    private static int calcSeconds(Node root, boolean vis[]){
        if(root==null) return 0;
        if(root.val !=1 && root.degree==1){
            root.sec = 0;
            return 0;
        }
        root.sec = Integer.MAX_VALUE;
        vis[root.val] = true;
        for(Node child : root.child){
            if(!vis[child.val]){
              root.sec = min(calcSeconds(child, vis), root.sec);
            }
        }
        root.sec++;
        return root.sec;
    }

    private static int calculateLeaves(Node root, boolean vis[]){
        if(root==null) return 0;

        if(root.degree==1 && root.val !=1) {
            return 1;
        }

        vis[root.val] = true;

        for(Node child : root.child){
            if(!vis[child.val]){
                root.leafs += calculateLeaves(child, vis);
            }
        }

        return root.leafs;
    }

    static class Node{
        int val;
        int degree;
        int leafs;
        int sec;

        List<Node> child;
        public Node(int val){
            this.val = val;
            degree= 0;
            sec = 0;
            leafs = 0;
            this.child = new ArrayList<>();
        }

        @Override
        public String toString() {
            return "Node{" +
                    "val=" + val +
                    ", degree=" + degree +
                    ", leafs=" + leafs +
                    ", sec=" + sec +
                    '}';
        }

        public void add(Node child){
            this.child.add(child);
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