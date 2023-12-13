package Codeforces;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public final class CF1903C {
    private static FastReader reader = new FastReader();
    public static void main(String[] args) {
        PrintWriter out = new PrintWriter(System.out);
        // int test = 1;
        int test = reader.nextInt();
        while (test-- > 0) {
            int n = read();
            int k = read();
            int arr[] = intArray(n);

            if(k==1){
                boolean possible = true;
                for(int i=0;i<n-1;i++){
                    if(arr[i] > arr[i+1]){
                        possible = false;
                        break;
                    }
                }
                if(!possible) out.println("NO");
                else out.println("YES");
                continue;
            }

            out.println("YES");

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