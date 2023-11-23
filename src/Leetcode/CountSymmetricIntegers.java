package Leetcode;

//Problem Link : https://leetcode.com/problems/count-symmetric-integers/

public class CountSymmetricIntegers {
    public int countSymmetricIntegers(int low, int high) {
        int count = 0;

        while (low <= high) {
            if (check(low)) {
                count++;
            }
            low++;
        }
        return count;
    }

    private boolean check(int x) {
        char c[] = ("" + x).toCharArray();
        if (c.length % 2 == 1) {
            return false;
        }
        int left = 0;
        int right = 0;
        int low = 0;
        int high = c.length - 1;
        while (low < high) {
            left += (c[low] - '0');
            right += (c[high] - '0');
            low++;
            high--;
        }
        return left == right;
    }
}
