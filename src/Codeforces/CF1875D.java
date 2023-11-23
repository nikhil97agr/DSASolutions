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

public final class CF1875D {
    private static FastReader reader = new FastReader();

    public static void main(String[] args) {
        PrintWriter out = new PrintWriter(System.out);
        // int test = 1;
        int test = reader.nextInt();
        while (test-- > 0) {
            int n = read();
            long arr[] = new long[n];
            TreeMap<Long, Integer> map = new TreeMap<>();

            for (int i = 0; i < n; i++) {
                arr[i] = read();
                map.put(arr[i], map.getOrDefault(arr[i], 0) + 1);
            }
            int next = 0;
            for (long x : map.keySet()) {
                if (x == next)
                    next++;
                else
                    break;
            }
            if (next == 0) {
                out.println(0);
                continue;
            }
            long dp[] = new long[next];
            Arrays.fill(dp, Long.MAX_VALUE);
            for (int i = 0; i < next; i++) {
                dp[i] = Math.min(dp[i], 1l * next * (map.get(1l * i) - 1) + i);

                for (int j = i - 1; j >= 0; j--) {
                    dp[j] = Math.min(dp[j], dp[i] + 1l * i * (map.get(1l * j) - 1) + j);
                }
            }
            out.println(dp[0]);

        }

        out.flush();
        out.close();
    }

    private static String[] array(int n) {
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