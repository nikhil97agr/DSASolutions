package Leetcode;

import java.util.HashSet;
import java.util.Set;

//Problem Link: https://leetcode.com/problems/distinct-echo-substrings/description/

class DistinctEchoSubstrings {


    public int distinctEchoSubstrings(String s) {

        Set<String> set = new HashSet<>();
        int ans = 0;
        int n = s.length();
        for (int i = 0; i < n; i++) {
            StringBuilder sb = new StringBuilder();
            String sub = s.substring(i);
            int zarray[] = zarray(sub, sub.length());
            sb.append(sub.charAt(0));
            for (int j = i + 1, len = 2, ind = 1; j < n; j++, len++, ind++) {
                sb.append(sub.charAt(ind));

                String st = sb.toString();
                if (len % 2 == 1) {
                    continue;
                }
                if (set.contains(st)) {
                    continue;
                }
                int half = len / 2;
                if (zarray[half] >= half) {
                    ans++;
                    set.add(st);
                }


            }
        }

        return ans;
    }

    public int[] zarray(String s, int n) {

        int zarray[] = new int[n];
        int left = 0;
        int right = 0;
        for (int i = 1; i < n; i++) {
            if (i > right) {
                left = right = i;
                while (right < n && s.charAt(right) == s.charAt(right - left)) {
                    right++;
                }
                zarray[i] = right - left;
                right--;
            } else {
                int k = i - left;
                if (zarray[k] < right - i + 1) {
                    zarray[i] = zarray[k];
                } else {
                    left = i;
                    while (right < n && s.charAt(right) == s.charAt(right - left)) {
                        right++;
                    }
                    zarray[i] = right - left;
                    right--;
                }
            }
        }

        return zarray;
    }
}