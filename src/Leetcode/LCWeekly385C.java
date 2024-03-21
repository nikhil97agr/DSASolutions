package Leetcode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class LCWeekly385C {
    public int mostFrequentPrime(int[][] mat) {
        Map<Integer, Integer> map = new TreeMap<>(Collections.reverseOrder());

        int m = mat.length;
        int n = mat[0].length;
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                int di[] = new int[]{0, 0, 1, 1, 1, -1, -1, -1};
                int dj[] = new int[]{1, -1, 0, 1, -1, 0, 1, -1};
                for(int k=0;k<8;k++){
                    generate(map,mat, m, n, i, j, mat[i][j], di[k], dj[k]);
                }

            }

        }

        int cnt = 0;
        int ans= 0;
        for(Map.Entry<Integer, Integer> entry : map.entrySet()){
            if(cnt < entry.getValue() && isPrime(entry.getKey())){
                cnt = entry.getValue();
                ans = entry.getKey();
            }else if(cnt == entry.getValue() && ans < entry.getKey() && isPrime(entry.getKey()))
                ans = entry.getKey();
        }
        return ans ==0 ? -1: ans;
    }

    private static boolean isPrime(int n){
        if(n<=1) return false;
        if(n==2 || n==3) return true;

        if(n%2==0) return false;

        for(int i=3;i<=Math.sqrt(n);i+=2){
            if(n%i==0) return false;
        }
        return true;
    }

    private void generate(Map<Integer, Integer>map, int mat[][], int m, int n, int i, int j, int num, int di, int dj){
        if(di == -1){
            if(dj == -1){
                while(i + di >= 0 && j + dj >= 0){
                    i = i+di;
                    j = j + dj;
                    num = num * 10 + mat[i][j];
                    addToMap(map, num);
                }
            }else{
                while(i + di >= 0 && j + dj <n){
                    i = i+di;
                    j = j + dj;
                    num = num * 10 + mat[i][j];
                    addToMap(map, num);

                }
            }
        }else if(di == 1){
            if(dj == -1){
                while(i + di <m && j + dj >= 0){
                    i = i+di;
                    j = j + dj;
                    num = num * 10 + mat[i][j];
                    addToMap(map, num);

                }
            }else{
                while(i + di <m && j + dj <n){
                    i = i+di;
                    j = j + dj;
                    num = num * 10 + mat[i][j];
                    addToMap(map, num);

                }
            }
        }else{
            if(dj == -1){
                while(i + di <m && j + dj >= 0){
                    i = i+di;
                    j = j + dj;
                    num = num * 10 + mat[i][j];
                    addToMap(map, num);

                }
            }else{
                while(i + di <m && j + dj <n){
                    i = i+di;
                    j = j + dj;
                    num = num * 10 + mat[i][j];
                    addToMap(map, num);

                }
            }
        }
    }

    private void addToMap(Map<Integer, Integer> map, int n){
        if(n < 10) return;
        int cnt = map.getOrDefault(n, 0);
        map.put(n, cnt+1);
    }


}