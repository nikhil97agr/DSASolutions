package Codechef;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class CCStart109D {
    private static FastReader reader = new FastReader();
    public static void main(String[] args) {
        PrintWriter out = new PrintWriter(System.out);
        // int test = 1;
        int test = reader.nextInt();
        while (test-- > 0) {
            int n = read();
            int a[] = intArray(n);
            int colorA[] = intArray(n);
            int b[] = intArray(n);
            int colorB[] =intArray(n);

            Map<Integer, TreeMap<Integer, Integer>> map = new HashMap<>();

            for(int i=0;i<n;i++){


                TreeMap<Integer, Integer> tree = map.computeIfAbsent(colorB[i], color -> new TreeMap<>());
                tree.put(b[i], tree.getOrDefault(b[i], 0)+1);
            }

            for(int i=0;i<n;i++){
                if(!map.containsKey(colorA[i])){
                    continue;
                }
                TreeMap<Integer, Integer> tree = map.computeIfAbsent(colorA[i], color -> new TreeMap<>());
                tree.put(a[i], tree.getOrDefault(a[i], 0)+1);
            }
            boolean result = true;
            int ans[] = new int[n];
            for(int i=n-1;i>=0;i--){
                int color = colorA[i];
                if(!map.containsKey(color)){
                    ans[i] = a[i];
                    continue;
                }
                if(i==n-1){
                    Map.Entry<Integer, Integer> entry = map.get(color).lastEntry();
                    ans[i] = entry.getKey();
                    if(entry.getValue()==1){
                        map.get(color).remove(entry.getKey());
                    }
                    else{
                        map.get(color).put(entry.getKey(), entry.getValue()-1);
                    }
                    continue;
                }

                int prevValue = ans[i+1];
                Map.Entry<Integer, Integer> entry = map.get(color).floorEntry(prevValue);
                if(entry == null){
                    result = false;
                    break;
                }

                ans[i] = entry.getKey();
                if(entry.getValue()==1){
                    map.get(color).remove(entry.getKey());
                }else{
                    map.get(color).put(entry.getKey(), entry.getValue()-1);
                }
            }

            if(!result){
                out.println("No");
                continue;
            }

            for(int i=0;i<n-1;i++){
                if(ans[i] > ans[i+1]){
                    result = false;
                    break;
                }
            }
            String str = result ? "Yes" : "No";

            out.println(str);


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