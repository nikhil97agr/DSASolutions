package Leetcode;

import java.util.HashMap;

//Problem Link:https://leetcode.com/problems/groups-of-strings

public class GroupsOfStrings {

    public int[] groupStrings(String[] words) {

        int n = words.length;
        int par[] = new int[n];
        for (int i = 0; i < n; i++) {
            par[i] = i;
        }
        var map = new HashMap<Integer, Integer>();
        for (int i = 0; i < n; i++) {
            String s = words[i];
            int mask = 0;

            //Find the mask for current string
            for (char c : s.toCharArray()) {
                mask |= (1 << (c - 'a'));
            }

            /**
             * for each bit do following things
             *
             * 1. If the bit is unset, set it and check if the new mask is present in the map. If yes, merge the two groups.
             * 2. If the bit is set, unset it and check if the new mask is present in the map. If yes, merge the two groups.
             * 3. If the bit is set, toggle another bit that is not set and check if the new mask is present in the map. If yes, merge the two groups.
             */
            for (int j = 0; j < 26; j++) {
                int bit = (mask >> j) & 1;
                if (bit == 0) {
                    int newMask = mask | (1 << j);
                    if (map.containsKey(newMask)) {
                        int p1 = find(map.get(newMask), par);
                        int p2 = find(i, par);
                        if (p1 != p2) {
                            par[p1] = p2;
                        }
                    }
                } else {
                    int newMask = mask ^ (1 << j);
                    if (map.containsKey(newMask)) {
                        int p1 = find(map.get(newMask), par);
                        int p2 = find(i, par);
                        if (p1 != p2) {
                            par[p1] = p2;
                        }
                    }
                    for (int k = 0; k < 26; k++) {

                        int b = (newMask >> k) & 1;
                        if (b == 0) {
                            int newMask2 = newMask | (1 << k);
                            if (map.containsKey(newMask2)) {
                                int p1 = find(map.get(newMask2), par);
                                int p2 = find(i, par);
                                if (p1 != p2) {
                                    par[p1] = p2;
                                }
                            }
                        }
                    }
                }
            }

            if (!map.containsKey(mask)) {
                map.put(mask, i);
            }
        }

        var grps = new HashMap<Integer, Integer>();
        for (int i = 0; i < n; i++) {
            int p = find(i, par);
            grps.merge(p, 1, Integer::sum);

        }

        int max = grps.values().stream().max(Integer::compareTo).get();

        return new int[]{grps.size(), max};
    }

    private int find(int p, int par[]) {

        if (par[p] == p) {
            return p;
        }
        return par[p] = find(par[p], par);
    }
}