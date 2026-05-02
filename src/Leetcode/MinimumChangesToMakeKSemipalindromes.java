package Leetcode;//Problem Link: https://leetcode.com/problems/minimum-changes-to-make-k-semi-palindromes/

/**
 * Solution for partitioning a string into k semi-palindromes with minimum character changes.
 *
 * A string is a "semi-palindrome" if there exists a positive integer d (divisor of the length) such that when we group
 * characters at positions i, i+d, i+2d, ... (for each starting offset), these groups form palindromes.
 *
 * Example: "abcdabcd" with d=4 is a semi-palindrome because: - Positions 0,4 form "aa" (palindrome) - Positions 1,5
 * form "bb" (palindrome) - Positions 2,6 form "cc" (palindrome) - Positions 3,7 form "dd" (palindrome)
 *
 * Algorithm: 1. Precompute minCost[i][j]: minimum changes to make substring s[i..j] a semi-palindrome - Try all valid
 * divisors d of the substring length - For each divisor, check palindrome property for each offset 2. Use DP to
 * partition the string into k parts, minimizing total cost - dp[i][cnt] = minimum cost to partition s[i..n-1] into
 * (k-cnt+1) semi-palindromes
 *
 * Time Complexity: O(n³ * sqrt(n)) for preprocessing + O(n² * k) for DP Space Complexity: O(n² + n*k)
 */
public class MinimumChangesToMakeKSemipalindromes {

    Integer dp[][];      // dp[i][cnt] = min cost to partition s[i..n-1] into (k-cnt+1) parts
    int[][] minCost;     // minCost[i][j] = min changes to make s[i..j] a semi-palindrome
    int n;               // Length of the string

    /**
     * Finds minimum character changes to partition string into k semi-palindromes.
     *
     * @param s String to partition
     * @param k Number of partitions
     * @return Minimum number of character changes needed
     */
    public int minimumChanges(String s, int k) {

        n = s.length();
        minCost = new int[n][n];  // Precompute costs for all substrings
        dp = new Integer[n][k + 1];  // Memoization for DP

        // ========================================================================
        // PHASE 1: Precompute minimum cost to make each substring a semi-palindrome
        // ========================================================================
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int len = j - i + 1;  // Length of substring s[i..j]
                int bestForThisSubstring = Integer.MAX_VALUE;

                // Try all possible divisors d of the length
                // A divisor d means we check positions with step size d
                for (int d = 1; d <= len / 2; d++) {
                    if (len % d == 0) {  // d must be a divisor of length
                        int currentCost = 0;

                        // For each offset (0 to d-1), check if positions form a palindrome
                        // Offset 0: positions i, i+d, i+2d, ..., j
                        // Offset 1: positions i+1, i+1+d, i+1+2d, ..., j-d+1
                        // etc.
                        for (int offset = 0; offset < d; offset++) {
                            // Two pointers for palindrome check with step d
                            int l = i + offset;  // Start from left
                            int r = i + offset + ((len / d - 1) * d);  // Start from right

                            // Check palindrome property for this offset's group
                            while (l < r) {
                                if (s.charAt(l) != s.charAt(r)) {
                                    currentCost++;  // Need to change one character
                                }
                                l += d;  // Move right by step d
                                r -= d;  // Move left by step d
                            }
                        }

                        // Track minimum cost across all valid divisors
                        bestForThisSubstring = Math.min(bestForThisSubstring, currentCost);
                    }
                }

                minCost[i][j] = bestForThisSubstring;
            }
        }

        // ========================================================================
        // PHASE 2: DP to partition string into k semi-palindromes
        // ========================================================================
        // Start at index 0, this is the 1st partition, need k total partitions
        return solve(0, 1, k);
    }

    private int solve(int i, int cnt, int k) {

        if (cnt == k) {

            if (n - i < 2) {
                return 1_000_000;
            }
            return minCost[i][n - 1];
        }

        if (dp[i][cnt] != null) {
            return dp[i][cnt];
        }

        int ans = 1_000_000;

        for (int j = i + 1; j <= n - 1 - (k - cnt) * 2; j++) {
            int res = solve(j + 1, cnt + 1, k);
            if (res < 1_000_000) {
                ans = Math.min(ans, minCost[i][j] + res);
            }
        }

        return dp[i][cnt] = ans;
    }
}