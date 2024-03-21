package Leetcode;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class LCWeekly384C {
    public int maxPalindromesAfterOperations(String[] words) {
        int cnt = 0;
        int cn[] = new int[26];
        for(String w : words){
            for(char ch : w.toCharArray()){
                cn[ch-'a']++;
            }
        }
        Arrays.sort(words , Comparator.comparingInt(String::length));

        for(String w : words){
            Map<Integer, Integer> map = new HashMap<>();

            int len = w.length();
            int i = 0;
            while(len > 0 && i < 26){
                int dec = 0;
                if(cn[i]%2==1 && len%2==1){
                    dec++;
                    cn[i]--;
                    len--;
                }
                if(cn[i]>0 && len > 0){
                    if(cn[i] <= len){
                        int req = (cn[i]/2)*2;
                        dec += req;
                        cn[i] -= req;
                        len -= req;
                    }else{
                        int req = (len/2)*2;
                        dec += req;
                        cn[i] -= req;
                        len -= req;
                    }
                }

                map.put(i, dec);
                cn[i] += dec;
                i++;
            }

            if(len ==0){
                cnt++;
                for(Map.Entry<Integer, Integer> entry : map.entrySet()){
                    cn[entry.getKey()] -= entry.getValue();
                }
            }else if(len ==1){
                i=0;
                while(i<26 && len > 0){
                    if(cn[i] > 0){
                        cn[i]--;
                        len--;
                    }
                    i++;
                }
                if(len ==0){
                    cnt++;
                }
            }
        }
        return cnt;
    }
}