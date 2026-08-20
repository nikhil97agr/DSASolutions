package algorithms;

import java.util.Arrays;

public class ManacherAlgorithm {

    public int p[];
    String t;

    public ManacherAlgorithm(String s) {

        StringBuilder sb = new StringBuilder();
        sb.append("#");
        for (char c : s.toCharArray()) {
            sb.append(c).append("#");
        }

        t = sb.toString();
        p = new int[t.length()];
        Arrays.fill(p, 1);

        build();
    }

    public void build() {

        int n = t.length();
        int l = 1;
        int r = 1;
        for (int i = 1; i < n; i++) {
            if (i < r) {
                p[i] = Math.max(0, Math.min(r - i, p[l + r - i]));
            } else {
                p[i] = Math.max(0, r - i);
            }
            while (i + p[i] < n && i - p[i] >= 0 && t.charAt(i + p[i]) == t.charAt(i - p[i])) {
                p[i]++;
            }
            if (i + p[i] > r) {
                l = i - p[i];
                r = i + p[i];
            }
        }
    }


}