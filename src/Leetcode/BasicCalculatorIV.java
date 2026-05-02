package Leetcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Problem Link: https://leetcode.com/problems/basic-calculator-iv

/**
 * Solution for evaluating and simplifying polynomial expressions with variables.
 *
 * Problem: Given an expression string with variables, numbers, +, -, *, and parentheses:
 * 1. Parse and evaluate the expression as a polynomial
 * 2. Substitute given variable values
 * 3. Return the simplified polynomial in a specific format
 *
 * Expression examples:
 * - "a + b * (c + d)" → polynomial with terms like "a", "b*c", "b*d"
 * - "((a + b) * (c + d))" → expands to "a*c + a*d + b*c + b*d"
 *
 * Output format:
 * - Terms sorted by degree (descending), then lexicographically
 * - Each term: "coefficient*var1*var2*..." (variables sorted alphabetically)
 * - Example: ["2*a*b", "3*a", "5"]
 *
 * Algorithm approach:
 * 1. Parse the expression into a Leetcode.Polynomial object using recursive descent parsing
 * 2. Represent polynomials as Map<List<String>, Integer>
 *    - Key: sorted list of variables (e.g., ["a", "b"] for term a*b)
 *    - Value: coefficient
 * 3. Support operations: add, subtract, multiply polynomials
 * 4. Evaluate by substituting variable values
 * 5. Format output according to requirements
 *
 * Parsing strategy:
 * - Handle parentheses recursively
 * - Parse operators: *, +, -
 * - Apply operator precedence (multiplication before addition/subtraction)
 *
 * Time Complexity: O(n * m²) where n = expression length, m = number of terms
 * Space Complexity: O(m) for storing polynomial terms
 */
public class BasicCalculatorIV {

    /**
     * Evaluates a polynomial expression with variable substitution.
     *
     * @param expression String expression with variables, numbers, and operators
     * @param evalvars   Variables to substitute
     * @param evalints   Values for the variables
     * @return List of polynomial terms in required format
     */
    public List<String> basicCalculatorIV(String expression, String[] evalvars, int[] evalints) {

        // Build variable-to-value mapping for evaluation
        var map = new HashMap<String, Integer>();
        for (var i = 0; i < evalvars.length; i++) {
            map.put(evalvars[i], evalints[i]);
        }

        // Parse expression → evaluate with substitutions → convert to output format
        return parse(expression).evaluate(map).toList();
    }

    /**
     * Parses an expression string into a Leetcode.Polynomial object.
     *
     * Uses recursive descent parsing with operator precedence:
     * 1. Handle parentheses recursively (highest precedence)
     * 2. Parse operands (numbers and variables)
     * 3. Collect operators (+, -, *)
     * 4. Apply multiplication first (higher precedence)
     * 5. Apply addition/subtraction left-to-right
     *
     * @param exp Expression string to parse
     * @return Leetcode.Polynomial representation of the expression
     */
    public Polynomial parse(String exp) {

        var list = new ArrayList<Polynomial>();      // Operands
        var symbols = new ArrayList<Character>();     // Operators (+, -, *)
        int n = exp.length();
        var i = 0;

        // PHASE 1: Tokenize and parse operands
        while (i < n) {
            // Case 1: Handle parenthesized sub-expressions
            if (exp.charAt(i) == '(') {
                var j = i;
                var balance = 0;

                // Find matching closing parenthesis
                for (; j < n; j++) {
                    if (exp.charAt(j) == '(') {
                        balance++;
                    } else if (exp.charAt(j) == ')') {
                        balance--;
                    }
                    if (balance == 0) {
                        break;
                    }
                }

                // Recursively parse content inside parentheses
                list.add(parse(exp.substring(i + 1, j)));
                i = j;
                i++;
                continue;
            }

            // Case 2: Handle numbers and variables
            if (Character.isLetterOrDigit(exp.charAt(i))) {
                var j = i;
                boolean flag = false;

                // Find end of token
                for (; j < n; j++) {
                    if (exp.charAt(j) == ' ') {
                        list.add(make(exp.substring(i, j)));
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    list.add(make(exp.substring(i)));
                }
                i = j;
                i++;
                continue;
            }

            // Case 3: Handle operators
            if (exp.charAt(i) != ' ') {
                symbols.add(exp.charAt(i));
            }

            i++;
        }

        // PHASE 2: Apply multiplication (higher precedence)
        for (var ind = symbols.size() - 1; ind >= 0; ind--) {
            if (symbols.get(ind) == '*') {
                var p1 = list.get(ind);
                var p2 = list.remove(ind + 1);
                list.set(ind, combine(p1, p2, '*'));
                symbols.remove(ind);
            }
        }

        // PHASE 3: Apply addition/subtraction (left to right)
        if (list.isEmpty()) {
            return new Polynomial();
        }
        var result = list.get(0);
        for (var ind = 0; ind < symbols.size(); ind++) {
            result = combine(result, list.get(ind + 1), symbols.get(ind));
        }

        return result;

    }

    /**
     * Creates a polynomial from a single token (number or variable).
     *
     * @param s Token string (e.g., "5" or "x")
     * @return Leetcode.Polynomial representing the token
     */
    private Polynomial make(String s) {

        Polynomial result = new Polynomial();
        List<String> list = new ArrayList<>();

        if (Character.isDigit(s.charAt(0))) {
            // Constant term: empty variable list with coefficient
            result.update(list, Integer.parseInt(s));
        } else {
            // Variable term: list with one variable, coefficient 1
            list.add(s);
            result.update(list, 1);
        }

        return result;
    }

    /**
     * Combines two polynomials using the given operator.
     *
     * @param p1     First polynomial
     * @param p2     Second polynomial
     * @param symbol Operator: '+', '-', or '*'
     * @return Result of the operation
     */
    private Polynomial combine(Polynomial p1, Polynomial p2, char symbol) {

        if (symbol == '*') {
            return p1.prod(p2);
        }
        if (symbol == '-') {
            return p1.sub(p2);
        }

        return p1.add(p2);
    }
}

/**
 * Leetcode.Polynomial class representing a polynomial expression.
 *
 * Internal representation:
 * - Map<List<String>, Integer>: variables → coefficient
 * - Key: Sorted list of variables (e.g., ["a", "b"] represents a*b)
 * - Value: Coefficient for this term
 *
 * Example polynomial: 2*a*b + 3*a + 5
 * - {["a","b"] → 2, ["a"] → 3, [] → 5}
 */
class Polynomial {

    Map<List<String>, Integer> map;  // Variable list → coefficient mapping

    /**
     * Constructor for empty polynomial.
     */
    public Polynomial() {

        map = new HashMap<>();

    }

    /**
     * Updates (adds to) the coefficient of a term.
     *
     * @param key List of variables in the term (sorted)
     * @param val Coefficient to add
     */
    public void update(List<String> key, int val) {

        map.merge(key, val, Integer::sum);
    }

    /**
     * Adds another polynomial to this one.
     *
     * @param other Leetcode.Polynomial to add
     * @return New polynomial representing the sum
     */
    public Polynomial add(Polynomial other) {

        var result = new Polynomial();

        // Copy all terms from this polynomial
        for (var entry : map.entrySet()) {
            result.update(entry.getKey(), entry.getValue());
        }

        // Add all terms from other polynomial
        for (var entry : other.map.entrySet()) {
            result.update(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * Subtracts another polynomial from this one.
     *
     * @param other Leetcode.Polynomial to subtract
     * @return New polynomial representing the difference
     */
    public Polynomial sub(Polynomial other) {

        var result = new Polynomial();

        // Copy all terms from this polynomial
        for (var entry : map.entrySet()) {
            result.update(entry.getKey(), entry.getValue());
        }

        // Subtract all terms from other polynomial (negate coefficient)
        for (var entry : other.map.entrySet()) {
            result.update(entry.getKey(), -entry.getValue());
        }

        return result;
    }

    /**
     * Multiplies this polynomial by another polynomial.
     *
     * Uses distributive property: (a + b) * (c + d) = ac + ad + bc + bd
     * For each pair of terms, multiply coefficients and merge variable lists.
     *
     * @param other Leetcode.Polynomial to multiply by
     * @return New polynomial representing the product
     */
    public Polynomial prod(Polynomial other) {

        var result = new Polynomial();

        // Multiply each term in this polynomial by each term in other
        for (var e1 : map.entrySet()) {
            for (var e2 : other.map.entrySet()) {
                // Merge variable lists (e.g., [a] + [b] = [a, b])
                var mergeKeys = new ArrayList<String>(e1.getKey());
                mergeKeys.addAll(e2.getKey());
                Collections.sort(mergeKeys);  // Keep sorted alphabetically

                // Multiply coefficients
                result.update(mergeKeys, e1.getValue() * e2.getValue());
            }
        }

        return result;
    }

    /**
     * Evaluates the polynomial by substituting variable values.
     *
     * For each term, substitutes known variables and keeps unknown ones.
     * Example: 2*a*b with {a → 3} becomes 6*b
     *
     * @param variableMap Map of variable names to values
     * @return New polynomial with variables substituted
     */
    public Polynomial evaluate(Map<String, Integer> variableMap) {

        var result = new Polynomial();

        for (var entry : map.entrySet()) {
            var variables = new ArrayList<String>();
            var val = entry.getValue();  // Start with coefficient

            // Process each variable in this term
            for (var key : entry.getKey()) {
                if (variableMap.containsKey(key)) {
                    // Variable has a value: multiply into coefficient
                    val *= variableMap.get(key);
                } else {
                    // Variable unknown: keep it
                    variables.add(key);
                }
            }
            result.update(variables, val);
        }
        return result;

    }

    /**
     * Compares two variable lists lexicographically.
     *
     * @param l1 First variable list
     * @param l2 Second variable list
     * @return Negative if l1 < l2, positive if l1 > l2, 0 if equal
     */
    public int compare(List<String> l1, List<String> l2) {

        for (int i = 0; i < l1.size(); i++) {
            if (!l1.get(i).equals(l2.get(i))) {
                return l1.get(i).compareTo(l2.get(i));
            }
        }
        return 0;
    }

    /**
     * Converts the polynomial to the required output format.
     *
     * Output format requirements:
     * 1. Sort terms by degree (descending), then lexicographically
     * 2. Each term: "coefficient*var1*var2*..." (variables sorted)
     * 3. Omit terms with coefficient 0
     *
     * @return List of string representations of terms
     */
    public List<String> toList() {

        var result = new ArrayList<String>();
        var keys = new ArrayList<List<String>>(map.keySet());

        // Sort by: 1) degree (descending), 2) lexicographically
        keys.sort((k1, k2) -> {
            if (k1.size() != k2.size()) {
                return k2.size() - k1.size();  // Higher degree first
            }
            return compare(k1, k2);  // Lexicographic comparison
        });

        // Format each term
        for (var key : keys) {
            var val = map.get(key);

            // Skip terms with zero coefficient
            if (val == 0) {
                continue;
            }

            // Build term string: "coefficient*var1*var2*..."
            StringBuilder res = new StringBuilder();

            res.append(val);

            for (var k : key) {
                res.append("*");
                res.append(k);
            }

            result.add(res.toString());
        }

        return result;
    }


}