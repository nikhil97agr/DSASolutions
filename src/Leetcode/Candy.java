package Leetcode;

//Problem Link : https://leetcode.com/problems/candy

import java.util.Arrays;
import java.util.Collections;
import java.util.PriorityQueue;

public class Candy {
    public int candy(int[] ratings) {
        int n = ratings.length;
        int ans = 0;
        int arr[] = new int[n];
        int temp[][] = new int[n][2];

        for (int i = 0; i < n; i++) {
            temp[i] = new int[] { ratings[i], i };
        }
        Arrays.sort(temp, (a, b) -> a[0] - b[0]);
        Arrays.fill(arr, 1);

        for (int x[] : temp) {
            int ind = x[1];
            int val = x[0];

            if (ind > 0 && ratings[ind - 1] > val) {
                arr[ind - 1] = Math.max(arr[ind - 1], arr[ind] + 1);
            }
            if (ind < n - 1 && ratings[ind + 1] > val) {
                arr[ind + 1] = Math.max(arr[ind + 1], arr[ind] + 1);
            }
        }

        ans = Arrays.stream(arr).sum();
        return ans;
    }
}
