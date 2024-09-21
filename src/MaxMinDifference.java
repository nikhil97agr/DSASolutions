import java.util.Arrays;

public class MaxMinDifference {

    // Helper function to check if we can achieve a minimum difference of `x`
    boolean canAchieveMinDiff(int x, int start[], int d) {
        int currentPosition = start[0];
        int n = start.length;
        for (int i = 1; i < n; i++) {
            // Find the smallest number in the interval [start[i], start[i] + d] that is >= currentPosition + x
            if (start[i] > currentPosition + x) {
                currentPosition = start[i];
            } else if (start[i] + d >= currentPosition + x) {
                currentPosition += x;
            } else {
                return false;
            }
        }
        return true;
    }

    public int maximizeMinDifference(int[] start, int d) {
        int n = start.length;

        // Sort the start array to simplify the problem
        Arrays.sort(start);
        


        // Binary search on the answer
        int low = 0, high = d, result = 0;

        while (low <= high) {
            int mid = (low + high) / 2;
            if (canAchieveMinDiff(mid, start, d)) {
                result = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return result;
    }

}
