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

public final class CF1878B {
    private static FastReader reader = new FastReader();

    public static void main(String[] args) {
        PrintWriter out = new PrintWriter(System.out);
        // int test = 1;
        int test = reader.nextInt();
        while (test-- > 0) {
            int n = read();
            int arr[] = intArray(n);
            int bits[][] = new int[n][32];
            for (int i = 0; i < n; i++) {
                int x = arr[i];
                int j = 0;
                while (x > 0) {
                    int bit = x % 2;
                    bits[i][j++] = bit;
                    x /= 2;
                }
            }
            int result[][] = new int[n][32];

            result[n - 1] = bits[n - 1];
            for (int i = n - 2; i >= 0; i--) {
                for (int j = 0; j < 32; j++) {
                    if (bits[i][j] == 1) {
                        result[i][j] = 1 + result[i + 1][j];

                    }
                }
            }

            for (int i = 0; i < n; i++) {
                System.out.println(Arrays.toString(bits[i]) + ":" + Arrays.toString(result[i]));
            }

            int q = read();
            int res[] = new int[q];
            for (int i = 0; i < q; i++) {
                int l = read();
                int k = read();
                int j = 0;
                l--;
                int ans = l;

                if (arr[l] < k) {
                    res[i] = -1;
                    continue;
                }
                int temp = arr[l];
                for (j = 31; j >= 0; j--) {
                    if (temp >= k) {
                        
                        if (result[l][j] > 0) {
                            ans = Math.max(ans, l + result[l][j] - 1);
                            temp = temp ^ (1 << j);
                        }
                    } else {
                        break;
                    }
                    System.out.println(temp + ":" + j);
                }
                res[i] = ans + 1;
            }

            for (int x : res) {
                out.print(x + " ");
            }
            out.println();
        }

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