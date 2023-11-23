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

public final class CF1890A {
    private static FastReader reader = new FastReader();

    public static void main(String[] args) {
        PrintWriter out = new PrintWriter(System.out);
        // int test = 1;
        int test = reader.nextInt();
        while (test-- > 0) {
            int n = read();
            int arr[] = intArray(n);
            if (n == 2) {
                out.println("YES");
                continue;
            }
            Arrays.sort(arr);
            int ans[] = new int[n];
            int j = 0;
            for (int i = 0; i < n; i += 2) {
                ans[i] = arr[j++];
            }
            j = n - 1;
            for (int i = 1; i < n; i += 2) {
                ans[i] = arr[j--];
            }
            boolean result = true;
            int sum = ans[0] + ans[1];
            for (int i = 0; i < n - 1; i++) {
                if (sum != ans[i] + ans[i + 1]) {
                    result = false;
                    break;
                }
            }
            if (!result) {
                result = true;
                ans = new int[n];
                j = n - 1;
                for (int i = 0; i < n; i += 2) {
                    ans[i] = arr[j--];
                }
                j = 0;
                for (int i = 1; i < n; i += 2) {
                    ans[i] = arr[j++];
                }

                sum = ans[0] + ans[1];
                for (int i = 0; i < n - 1; i++) {
                    if (ans[i] + ans[i + 1] != sum) {
                        result = false;
                        break;
                    }
                }
            }

            if (!result) {
                out.println("NO");
            } else {
                out.println("YES");
            }
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