package Leetcode;

//Problem Link: https://leetcode.com/problems/minimum-operations-to-make-array-modulo-alternating-ii/

/**
 * Solution to find minimum operations to make array modulo alternating. The goal is to make adjacent elements have
 * different values modulo k.
 */
public class MinimumOperationsToMakeArrayModuloAlternatingII {

    /**
     * Main method to calculate minimum operations required. Strategy: Compute optimal target values for even and odd
     * indices separately, then combine them ensuring alternating pattern.
     *
     * @param nums the input array
     * @param k the modulo value
     * @return minimum number of operations needed
     */
    public long minOperations(int[] nums, int k) {

        // Compute optimal target values and costs for even indices
        var even = compute(nums, k, 0);
        // Compute optimal target values and costs for odd indices
        var odd = compute(nums, k, 1);

        // If the best target values are different, we can use both optimal values
        if (even.x1 != odd.x1) {
            return even.c1 + odd.c1;
        }

        // If best targets are the same, we need to use suboptimal for one position
        // Try: optimal for even + second best for odd
        long a1 = even.c1 + odd.c2;
        // Try: second best for even + optimal for odd
        long a2 = even.c2 + odd.c1;

        return Math.min(a1, a2);
    }

    /**
     * Computes the optimal target value for elements at positions start, start+2, start+4, etc. Uses prefix sums to
     * efficiently calculate costs for different target values.
     *
     * @param arr the input array
     * @param k the modulo value
     * @param start starting index (0 for even positions, 1 for odd positions)
     * @return Result containing best and second-best target values with their costs
     */
    private Result compute(int arr[], int k, int start) {

        int len = 2 * k;

        // Count frequency of each modulo value in a circular manner
        int cnt[] = new int[len];
        int preCnt[] = new int[len + 1]; // Prefix count array
        long preSum[] = new long[len + 1]; // Prefix sum array

        // Build frequency array for elements at positions start, start+2, start+4, ...
        for (int i = start; i < arr.length; i += 2) {
            int mod = arr[i] % k;
            cnt[mod]++;        // Count in first half
            cnt[mod + k]++;    // Duplicate in second half for circular handling
        }

        // Build prefix arrays for efficient range queries
        for (int i = 0; i < len; i++) {
            preCnt[i + 1] = preCnt[i] + cnt[i];
            preSum[i + 1] = preSum[i] + 1l * i * cnt[i];
        }

        // Variables to track best and second-best target values
        int x1 = 0;    // Best target value
        int x2 = 0;    // Second-best target value
        long c1 = Long.MAX_VALUE;  // Cost for best target
        long c2 = Long.MAX_VALUE;  // Cost for second-best target

        // Try each possible window of size k
        for (int s = 0, e = k - 1; s < k; s++, e++) {
            int mid = (s + e) / 2;

            // Get count and sum for elements in range [s, mid]
            var left = compute(preSum, preCnt, s, mid + 1);
            // Get count and sum for elements in range [mid+1, e]
            var right = compute(preSum, preCnt, mid + 1, e + 1);

            // Calculate cost: moving all elements to 'mid'
            // For left side: bring values up to mid
            // For right side: bring values down to mid
            long req = 1l * left.cnt * mid - left.sum + right.sum - 1l * right.cnt * mid;

            // Update best and second-best values
            if (req < c1) {
                c2 = c1;
                x2 = x1;
                c1 = req;
                x1 = mid;
            } else if (req < c2) {
                c2 = req;
                x2 = mid;
            }
        }

        return new Result(x1, x2, c1, c2);


    }

    /**
     * Helper method to compute count and sum for a range using prefix arrays.
     *
     * @param preSum prefix sum array
     * @param preCnt prefix count array
     * @param start start index (inclusive)
     * @param end end index (exclusive)
     * @return Pair containing count and sum for the range
     */
    public Pair compute(long preSum[], int preCnt[], int start, int end) {

        // Use prefix array difference to get range values
        int cnt = preCnt[end] - preCnt[start];
        long sum = preSum[end] - preSum[start];

        return new Pair(cnt, sum);
    }

    /**
     * Helper class to store count and sum for a range of elements.
     */
    class Pair {

        int cnt;   // Count of elements
        long sum;  // Sum of elements

        public Pair(int cnt, long sum) {

            this.cnt = cnt;
            this.sum = sum;
        }
    }

    /**
     * Helper class to store the best and second-best target values with their costs.
     */
    class Result {

        int x1;    // Best target value
        int x2;    // Second-best target value
        long c1;   // Cost for best target
        long c2;   // Cost for second-best target

        public Result(int x1, int x2, long c1, long c2) {

            this.x1 = x1;
            this.x2 = x2;
            this.c1 = c1;
            this.c2 = c2;
        }
    }

}