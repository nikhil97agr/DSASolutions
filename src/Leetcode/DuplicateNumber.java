package Leetcode;

public class DuplicateNumber {
    public int findDuplicate(int[] nums) {
        int n = nums.length;
        boolean arr[] = new boolean[n + 1];

        for (int x : nums) {
            if (arr[x])
                return x;

            arr[x] = true;
        }
        return -1;
    }
}
