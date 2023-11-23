package Leetcode;

//Problem Link : https://leetcode.com/problems/reorganize-string/

import java.util.PriorityQueue;

public class ReorganizeString {
    class Pair {
        char c;
        int cnt;

        public Pair(char c, int cnt) {
            this.c = c;
            this.cnt = cnt;
        }
    }

    public String reorganizeString(String s) {
        char ch[] = s.toCharArray();
        int cnt[] = new int[26];
        for (char c : ch) {
            cnt[c - 'a']++;
        }

        PriorityQueue<Pair> que = new PriorityQueue<>((a, b) -> b.cnt - a.cnt);

        for (char c = 'a'; c <= 'z'; c++) {
            if (cnt[c - 'a'] != 0) {
                que.add(new Pair(c, cnt[c - 'a']));
            }
        }

        StringBuilder result = new StringBuilder();

        while (!que.isEmpty()) {
            if (que.size() == 1) {
                Pair p = que.poll();
                if (p.cnt != 1) {
                    return "";
                }
                result.append(p.c);
            } else {
                Pair p1 = que.poll();
                Pair p2 = que.poll();
                result.append(p1.c);
                result.append(p2.c);
                p1.cnt--;
                p2.cnt--;
                if (p1.cnt != 0) {
                    que.add(p1);
                }
                if (p2.cnt != 0) {
                    que.add(p2);
                }
            }
        }

        return result.toString();
        
    }
}
