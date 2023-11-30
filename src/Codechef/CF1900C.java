package Codechef;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public final class CF1900C {

    static class Node{
        int val;
        char ch;
        Node left;
        Node right;

        public Node(int val, char ch){
            this.val = val;
            this.ch = ch;
        }
    }

    private static FastReader reader = new FastReader();
    public static void main(String[] args) {
        PrintWriter out = new PrintWriter(System.out);
        // int test = 1;
        int test = reader.nextInt();
        while (test-- > 0) {
            int n = read();
            char ch[] = reader.next().toCharArray();
            Map<Integer, Node> map = new HashMap<>();
            for(int i=0;i<n;i++){
                int l = read();
                int r = read();
                createNode(i+1,ch[i], map);

                if(l!=0){
                    createLeft(l, ch[l-1],  i+1, map);
                }

                if(r!=0){
                    createRight(r, ch[r-1], i+1, map);
                }
            }

            out.println(dfs(map.get(1)));

        }

        out.flush();
        out.close();
    }

    private static void traverse(Node root){
        if(root == null ) return;

        traverse(root.left);
        System.out.print(root.val+" ");
        traverse(root.right);
    }

    private static int dfs(Node node){
        if(node==null) return -1;

        if(node.left ==null && node.right == null) return 0;

        int left = dfs(node.left);
        int right = dfs(node.right);

        if(left==-1){
            if(node.ch != 'R'){
                return right+1;
            }
            return right;
        }
        if(right == -1){
            if(node.ch != 'L'){
                return left + 1;
            }
            return left;
        }

        if(node.ch != 'L') left++;
        if(node.ch !='R') right++;
        return Math.min(left, right);

    }

    private static void createRight(int val, char ch, int parent, Map<Integer, Node> map){
        Node par = createNode(parent,ch, map);
        Node child = createNode(val,ch, map);
        par.right =child;
    }

    private static void createLeft(int val, char ch, int parent, Map<Integer, Node> map){
        Node par = createNode(parent, ch, map);
        Node child = createNode(val, ch,map);
        par.left =child;
    }

    private static Node createNode(int val, char ch, Map<Integer, Node> map){
        if(map.containsKey(val)) return map.get(val);
        Node node = new Node(val, ch);
        map.put(val, node);
        return node;
    }


    private static String[] array(int n){
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
    private static int read(){
        return reader.nextInt();
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