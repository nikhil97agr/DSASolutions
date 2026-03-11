package Leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Problem Link: https://leetcode.com/problems/parse-lisp-expression/description/

public class ParseLispExpression {

    /**
     * Evaluates a Lisp-style expression Supports: (add a b), (mult a b), (let var1 val1 var2 val2 ... expression)
     *
     * @param expression The Lisp expression to evaluate
     * @return The evaluated result
     */
    public int evaluate(String expression) {

        return solve(expression, new HashMap<>());
    }

    /**
     * Recursively solves the expression with the given variable scope
     *
     * @param exp The expression to solve
     * @param map The current variable scope (variable name -> value)
     * @return The evaluated result
     */
    private int solve(String exp, Map<String, Integer> map) {

        // Base case 1: If expression is a number, return it
        if (isNum(exp)) {
            return Integer.parseInt(exp);
        }

        // Base case 2: If expression is a variable, look it up in the map
        if (isVar(exp)) {
            return map.get(exp);
        }

        // Parse the expression into tokens
        List<String> parsed = parse(exp);

        // Handle "add" operation: (add a b) returns a + b
        if (parsed.get(0).equals("add")) {
            String a = parsed.get(1);
            String b = parsed.get(2);

            return solve(a, map) + solve(b, map);
        }

        // Handle "mult" operation: (mult a b) returns a * b
        if (parsed.getFirst().equals("mult")) {
            String a = parsed.get(1);
            String b = parsed.get(2);

            return solve(a, map) * solve(b, map);
        }

        // Handle "let" operation: (let var1 val1 var2 val2 ... expression)
        // Create a new scope with the current variables
        Map<String, Integer> newMap = new HashMap<>(map);

        // Process variable assignments in pairs (var, value)
        // Stop before the last element which is the expression to evaluate
        for (int i = 1; i < parsed.size() - 1; i += 2) {
            String var = parsed.get(i);
            String val = parsed.get(i + 1);

            // Evaluate the value in the current scope and assign to variable
            newMap.put(var, solve(val, newMap));
        }

        // Evaluate and return the final expression with the new scope
        return solve(parsed.getLast(), newMap);
    }

    /**
     * Parses a Lisp expression into tokens Example: "(add 1 2)" -> ["add", "1", "2"] Example: "(let x 2 (add x 1))" ->
     * ["let", "x", "2", "(add x 1)"]
     *
     * @param exp The expression to parse (must start with '(' and end with ')')
     * @return List of tokens
     */
    private List<String> parse(String exp) {

        List<String> res = new ArrayList<>();

        // Remove outer parentheses
        exp = exp.substring(1, exp.length() - 1);

        // Extract tokens separated by spaces
        int ind = 0;
        while (ind < exp.length()) {
            int endIndex = nextToken(exp, ind);
            res.add(exp.substring(ind, endIndex));
            ind = endIndex + 1; // Skip the space
        }

        return res;
    }

    /**
     * Finds the end index of the next token starting from 'start' Handles nested parentheses correctly
     *
     * @param exp The expression string
     * @param start The starting index
     * @return The end index of the token (exclusive)
     */
    private int nextToken(String exp, int start) {

        // If token doesn't start with '(', it's a simple token (variable or number)
        if (exp.charAt(start) != '(') {

            // Find the next space or end of string
            int ind = exp.indexOf(' ', start);
            if (ind == -1) {
                return exp.length();
            }
            return ind;
        }

        // Token starts with '(' - need to find matching ')'
        // Count parentheses to handle nested expressions
        int cnt = 1;
        start++;
        while (start < exp.length() && cnt > 0) {
            if (exp.charAt(start) == '(') {
                cnt++; // Opening parenthesis
            } else if (exp.charAt(start) == ')') {
                cnt--; // Closing parenthesis
            }

            start++;
        }
        return start;
    }

    /**
     * Checks if the expression is a variable Variables start with lowercase letters
     *
     * @param exp The expression to check
     * @return true if it's a variable, false otherwise
     */
    private boolean isVar(String exp) {

        char ch = exp.charAt(0);
        return ch >= 'a' && ch <= 'z';
    }

    /**
     * Checks if the expression is a number Numbers start with '-' (negative) or a digit
     *
     * @param exp The expression to check
     * @return true if it's a number, false otherwise
     */
    private boolean isNum(String exp) {

        char c = exp.charAt(0);

        return c == '-' || (c >= '0' && c <= '9');
    }
}