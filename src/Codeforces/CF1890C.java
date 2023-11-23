package Codeforces;

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

public final class CF1890C {
    private static FastReader reader = new FastReader();

    public static void main(String[] args) {
        PrintWriter out = new PrintWriter(System.out);
        // int test = 1;
        int test = reader.nextInt();
        while (test-- > 0) {
            int n = read();
            String s = reader.next();
            List<Integer> indexes = new ArrayList<>();
            boolean result = true;
            if(n%2==1){
                out.println(-1);
                continue;
            }
            for (int i = 0; i < n - 1; i++) {
                if (s.charAt(i) == s.charAt(i + 1)) {
                    result = false;
                    break;
                }
            }
            if (result) {
                out.println(0);
                out.println();
                continue;
            }
            

            Deque<Character> que = new LinkedList<>();
            for (char c : s.toCharArray()) {
                que.addLast(c);
            }

            int cnt = 0;

            int i = 1;
            int lastIndex = n - i + 1;
            while (que.size() > 1 && cnt <=300) {
                if (que.getFirst() == que.getLast()) {
                    char c = que.getFirst();
                    if(c=='1'){
                        cnt++;
                        indexes.add(i-1);
                        que.addFirst('1');
                        que.removeLast();
                        lastIndex++;
                        i++;
                    }else{
                        cnt++;
                        indexes.add(lastIndex);
                        que.addLast('0');
                        que.removeFirst();
                        i++;
                        lastIndex++;
                    }

                }else{
                    que.removeLast();
                    que.removeFirst();
                    i++;
                    lastIndex--;
                }
            }

            if(cnt >300){
                out.println(-1);
            }else{
                out.println(cnt);
                for(int x : indexes){
                    out.print(x+" ");
                }
                out.println();
            }

        }

        out.flush();
        out.close();
    }

    private

    static String[] array(int n) {
        String s[] = new String[n];
        for (int i = 0; i < n; i++) {
            s[i] = reader.next();
        }
        return s;
    }

    private static int[] intArray(int n) {
        int arr[] = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = reader.nextInt();
        }
        return arr;
    }

    private static int read() {
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