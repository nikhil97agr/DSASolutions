package Leetcode;

import java.util.*;

public class LC374D {
    public static void main(String[] args) {
        System.out.println(countCompleteSubstrings("igigee", 2));
    }
    public static int countCompleteSubstrings(String word, int k) {
        int count[] = new int[26];
        int n = word.length();
        int ans = 0;

        int i=0;
        int j=1;
        int l=0;
        count[word.charAt(0)-'a']++;
        while(j<n){
            count[word.charAt(j)-'a']++;
            while(i<j && Math.abs(word.charAt(j-1) - word.charAt(j))>2){
                count[word.charAt(i)-'a']--;
                i++;
            }
            boolean temp = true;
            while(count[word.charAt(i)-'a']>k){
                count[word.charAt(i)-'a']--;
                i++;
            }
            for(int c : count){
                if(c!=0 && c!=k){
                    temp = false;
                    break;
                }
            }

            if(temp){
                ans++;
            }
            j++;
        }
        
        while(i<n){
            count[word.charAt(i)-'a']--;
            boolean temp = true;
            for(int c : count){
                if(c!=0 && c!=k){
                    temp =false;
                    break;
                }
            }
            if(temp) ans++;
            i++;
        }
        return ans;
    }
}
