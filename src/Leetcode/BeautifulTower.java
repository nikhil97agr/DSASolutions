package Leetcode;

import java.util.List;

public class BeautifulTower {
    public long maximumSumOfHeights(List<Integer> maxHeights) {
        long ans = 0;
        int n = maxHeights.size();
        for (int i = 0; i < n; i++) {
            long sum = maxHeights.get(i);
            int prev = maxHeights.get(i);
            for (int j = i - 1; j >= 0; j--) {
                if (maxHeights.get(j) >= prev) {
                    sum += 1l * prev;
                } else {
                    sum += maxHeights.get(j);
                    prev = maxHeights.get(j);
                }
            }
            prev = maxHeights.get(i);
            for (int j = i + 1; j < n; j++) {
                if (maxHeights.get(j) >= prev) {
                    sum += 1l * prev;
                } else {
                    sum += maxHeights.get(j);
                    prev = maxHeights.get(j);
                }
            }
            ans = Math.max(ans, sum);
        }

        return ans;

    }
}
