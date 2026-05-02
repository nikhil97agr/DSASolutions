package Leetcode;

import java.util.HashSet;
import java.util.Set;

//Problem Link: https://leetcode.com/problems/the-score-of-students-solving-math-expression

/**
 * Solution for scoring students' answers to a math expression. Students get 5 points for correct answers and 2 points
 * for answers that could be obtained by incorrectly applying operator precedence (evaluating left-to-right). Uses
 * dynamic programming with memoization to find all possible incorrect evaluations.
 */
public class TheScoreOfStudentsSolvingMathExpression {

    // DP table: nums[i][j] = set of all possible values from evaluating substring [i, j]
    Set<Integer> nums[][];

    /**
     * Calculates the total score for all student answers.
     *
     * @param s Math expression string with single-digit numbers and operators (+, *)
     * @param answers Array of student answers to score
     * @return Total score (5 points per correct answer, 2 points per plausible wrong answer)
     */
    public int scoreOfStudents(String s, int[] answers) {

        int result = 0;  // The correct answer following proper operator precedence
        int n = s.length();
        nums = new HashSet[n][n];

        // Generate all possible values from incorrect operator precedence
        solve(s.toCharArray(), 0, n - 1);

        // Calculate the correct answer (multiplication has higher precedence than addition)
        // Process the expression by handling multiplication first, then addition
        for (int i = 1, j = 0; i <= n; i += 2) {
            // When we hit a '+' or reach the end, multiply all numbers since last '+'
            if (i == n || s.charAt(i) == '+') {
                int mul = 1;
                // Multiply all consecutive numbers (separated by '*')
                while (j < i) {
                    mul *= (s.charAt(j) - '0');
                    j += 2;  // Skip operator to get next digit
                }
                result += mul;  // Add this product to the result
            }
        }

        int ans = 0;  // Total score

        // Score each student's answer
        for (int val : answers) {
            if (val == result) {
                // Correct answer: 5 points
                ans += 5;
            } else if (nums[0][n - 1].contains(val)) {
                // Plausible wrong answer (could be obtained with wrong precedence): 2 points
                ans += 2;
            }
            // Implausible wrong answer: 0 points (not added)
        }

        return ans;


    }

    /**
     * Recursively computes all possible values for a substring using dynamic programming. Tries all possible ways to
     * split the expression and combine results with operators. Only includes values <= 1000 to avoid overflow and
     * unrealistic answers.
     *
     * @param ch Character array of the expression
     * @param start Start index of the substring
     * @param end End index of the substring
     */
    private void solve(char ch[], int start, int end) {

        // Base case: single digit
        if (start == end) {
            nums[start][end] = new HashSet<>();
            nums[start][end].add(ch[start] - '0');
            return;
        }

        // Memoization: if already computed, return
        if (nums[start][end] != null) {
            return;
        }

        nums[start][end] = new HashSet<>();

        // Try splitting at each operator position
        for (int i = start + 1; i < end; i += 2) {
            // Recursively solve left and right subexpressions
            solve(ch, start, i - 1);
            solve(ch, i + 1, end);

            // Combine all possible values from left and right using the operator at position i
            for (int a : nums[start][i - 1]) {
                for (int b : nums[i + 1][end]) {
                    // Apply the operator and add result if it's <= 1000
                    if (ch[i] == '+' && a + b <= 1000) {
                        nums[start][end].add(a + b);
                    } else if (ch[i] == '*' && a * b <= 1000) {
                        nums[start][end].add(a * b);
                    }
                }
            }
        }
    }
}