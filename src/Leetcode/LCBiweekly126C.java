package Leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LCBiweekly126C {
    public String minimizeStringValue(String s) {
        char ch[] = s.toCharArray();
        int n = s.length();
        int pref[][] = new int[n][26];
        int suff[][] = new int[n][26];
        for(int i=1;i<n;i++){

            pref[i] = pref[i-1].clone();
            if(ch[i-1]=='?') continue;
            pref[i][ch[i-1]-'a']++;
        }

        for(int i=n-2;i>=0;i--){
            suff[i] = suff[i+1].clone();
            if(ch[i+1] == '?') continue;
            suff[i][ch[i+1]-'a']++;
        }
        List<Integer> ind = new ArrayList<>();
        List<Character> chars = new ArrayList<>();
        for(int i=0;i<n;i++){

            int min = Integer.MAX_VALUE;
            char c = 'a';
            if(ch[i] != '?'){
                if(i!=n-1){
                    pref[i+1] = pref[i].clone();
                    pref[i+1][ch[i]-'a']++;
                }
                continue;
            }
            for(char c1 = 'a';c1<='z';c1++){
                if(pref[i][c1-'a'] + suff[i][c1-'a'] < min){
                    min = pref[i][c1-'a'] + suff[i][c1-'a'];
                    c = c1;
                }
            }

            ch[i] = c;
            ind.add(i);
            chars.add(c);
            if(i!=n-1){
                pref[i+1] = pref[i].clone();
                pref[i+1][c-'a']++;

            }
        }
        Collections.sort(chars);
        for(int i=0;i<chars.size();i++){
            ch[ind.get(i)] = chars.get(i);
        }
        return new String(ch);

    }
}