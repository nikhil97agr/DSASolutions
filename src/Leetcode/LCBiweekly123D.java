package Leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

public class LCBiweekly123D {
    public int numberOfPairs(int[][] p) {
        int ans = 0;

        Arrays.sort(p, (a,b)->{
            if(a[0] == b[0]) return b[1] - a[1];
            else return a[0] - b[0];
        });

        int n = p.length;
        for(int i=0;i<n;i++){
            TreeSet<Integer> set = new TreeSet<>();
            for(int j=i+1;j<n;j++){
                if(p[i][1] < p[j][1]){
                    continue;
                }
                Integer max = set.floor(p[i][1]);
                Integer min = set.ceiling(p[j][1]);

                if(max == null || min == null){
                    ans++;
                }


                set.add(p[j][1]);

            }
        }
        return ans;
    }

}