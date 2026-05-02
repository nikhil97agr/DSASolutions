package Leetcode;

//Problem Link: https://leetcode.com/problems/lexicographically-smallest-beautiful-string/

public class SmallestBeautifulString {

    public String smallestBeautifulString(String s, int k) {

        int n = s.length();
        char res[] = s.toCharArray();
        int i = n - 1;
        for (; i >= 0; i--) {
            int ind = (res[i] - 'a') + 1;
            if (ind >= k || res[i] == 'z') {
                continue;
            }
            boolean found = false;
            for (char c = (char) (ind + 'a'); c < ('a' + k); c++) {
                if ((i - 1 >= 0 && res[i - 1] == c) || (i - 2 >= 0 && res[i - 2] == c)) {
                    continue;
                }
                res[i] = c;
                found = true;
                break;
            }

            if (!found) {
                continue;
            }
            for (int j = i + 1; j < n; j++) {
                res[j] = 'a';
            }
            break;
        }

        if (i < 0) {
            return "";
        }

        char seq[] = new char[]{'a', 'b', 'c'};

        int ind = 0;
        int j = i + 1;
        if (i - 1 >= 0 && res[i - 1] == 'a') {
            if (j < n) {
                res[j] = 'b';
            }
            j++;
        }
        for (; j < n; j++) {
            if (seq[ind] - 'a' >= k) {
                return "";
            }
            if (j - 2 >= 0 && seq[ind] == res[j - 2]) {
                j--;
                ind++;
                ind %= 3;
                continue;
            }
            res[j] = seq[ind];
            ind++;
            ind %= 3;
        }

        return new String(res);
    }
}