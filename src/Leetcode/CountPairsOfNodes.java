package Leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//Problem Link: https://leetcode.com/problems/count-pairs-of-nodes/

public class CountPairsOfNodes {

    public int[] countPairs(int n, int[][] edges, int[] queries) {

        int cnt[] = new int[n];
        int sorted[] = new int[n];
        Map<Integer, Integer> map[] = new HashMap[n];

        for (int i = 0; i < n; i++) {
            map[i] = new HashMap<>();
        }

        for (int e[] : edges) {
            int u = e[0] - 1;
            int v = e[1] - 1;
            cnt[u]++;
            cnt[v]++;
            sorted[u]++;
            sorted[v]++;
            if (u < v) {
                map[u].merge(v, 1, Integer::sum);
            } else {
                map[v].merge(u, 1, Integer::sum);
            }
        }

        int q = queries.length;
        int ans[] = new int[q];
        Arrays.sort(sorted);

        for (int i = 0; i < q; i++) {
            int req = queries[i];
            int start = 0;
            int end = n - 1;
            int total = 0;
            while (start < end) {
                if (sorted[start] + sorted[end] > req) {
                    total += end - start;
                    end--;
                } else {
                    start++;
                }
            }
            for (int j = 0; j < n; j++) {
                for (var entry : map[j].entrySet()) {
                    int key = entry.getKey();
                    int val = entry.getValue();
                    if (cnt[j] + cnt[key] > req && cnt[j] + cnt[key] - val <= req) {
                        total--;
                    }
                }
            }
            ans[i] = total;
        }

        return ans;
    }
}