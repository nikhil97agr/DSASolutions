package Codechef;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.LinkedHashSet;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.Deque;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public final class CCStart109C {
    private static FastReader reader = new FastReader();
    public static void main(String[] args) {
        PrintWriter out = new PrintWriter(System.out);
        // int test = 1;
        int test = reader.nextInt();
        while (test-- > 0) {
            int n = read();
            int arr[] = intArray(n);

            List<Integer> even = Arrays.stream(arr).filter(a-> a%2==0).sorted().boxed().collect(Collectors.toList());
            List<Integer> odd = Arrays.stream(arr).filter(a-> a%2==1).sorted().boxed().collect(Collectors.toList());
            for(int x : arr){
                if(x%2==0) even.add(x);
                else odd.add(x);
            }

            if(even.size()%2!=0 || odd.size()%2!=0){
                out.println(-1);
                continue;
            }
            int ans[] = new int[n];

            int ind=0;

            for(int i=0;i<even.size()/2;i++){
                int a = even.get(i);
                int b = even.get(even.size()-1-i);
                ans[ind] = (a+b)/2;
                ans[ind+n/2] = (b-a)/2;
                ind++;
            }

            for(int i=0;i<odd.size()/2;i++){
                int a = odd.get(i);
                int b = odd.get(odd.size()-1-i);
                ans[ind] = (a+b)/2;
                ans[ind+n/2] = (b-a)/2;
                ind++;
            }
            for(int x : ans){
                out.print(x+" ");
            }
            out.println();

        }

        out.flush();
        out.close();
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