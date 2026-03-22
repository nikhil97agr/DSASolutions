package Leetcode;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Solution for finding the minimum number of operations to flip a boolean expression.
 * The expression contains '0', '1', '&' (AND), '|' (OR), and parentheses.
 * An operation is flipping a single digit (0 to 1 or 1 to 0).
 * Uses expression tree construction and dynamic programming to solve.
 */
public class MinOperationsToFlip {

    /**
     * Calculates the minimum operations needed to flip the expression's result.
     *
     * @param expression Boolean expression string with '0', '1', '&', '|', '(', ')'
     * @return Minimum number of digit flips needed to change the expression's result
     */
    public int minOperationsToFlip(String expression) {

        // Count the number of digits in the expression
        var digits = 0;
        for (char c : expression.toCharArray()) {
            if (c == '0' || c == '1') {
                digits++;
            }
        }

        // Special case: single digit expression requires 1 flip
        if (digits == 1) {
            return 1;
        }

        // Build an expression tree from the input string
        var stack = new Stack<TreeNode>();
        buildTree(stack, expression.toCharArray(), new AtomicInteger(0));

        // Get the root of the expression tree
        var root = stack.peek();
        if (root == null) {
            return 0;
        }

        // If root is a single digit, only 1 flip needed
        if (root.val == '0' || root.val == '1') {
            return 1;
        }

        // Solve using DP on the tree to get min flips for both outcomes
        Result result = solve(root);

        // Return the maximum of flips needed (to flip to opposite of current result)
        return Math.max(result.one, result.zero);
    }

    /**
     * Recursively calculates minimum flips needed to make subtree evaluate to 0 or 1.
     *
     * @param root Root of the current subtree
     * @return Result containing min flips to make expression 1 and min flips to make it 0
     */
    private Result solve(TreeNode root) {

        // Base case: null node
        if (root == null) {
            return new Result(0, 0);
        }

        // Leaf node with value '1': already 1 (0 flips), needs 1 flip to become 0
        if (root.val == '1') {
            return new Result(0, 1);
        }

        // Leaf node with value '0': needs 1 flip to become 1, already 0 (0 flips)
        if (root.val == '0') {
            return new Result(1, 0);
        }

        // Recursively solve for left and right subtrees
        Result left = solve(root.left);
        Result right = solve(root.right);

        // AND operation: result is 1 only if both operands are 1
        if (root.val == '&') {
            return new Result(
                    // To make AND = 1: both sides must be 1
                    left.one + right.one,
                    // To make AND = 0: at least one side must be 0 (choose minimum)
                    Math.min(left.zero + right.zero, Math.min(left.one + right.zero, left.zero + right.one))
            );
        }

        // OR operation: result is 0 only if both operands are 0
        return new Result(
                // To make OR = 1: at least one side must be 1 (choose minimum)
                Math.min(left.zero + right.one, Math.min(left.one + right.one, left.one + right.zero)),
                // To make OR = 0: both sides must be 0
                left.zero + right.zero
        );
    }


    /**
     * Recursively builds an expression tree from the character array.
     * Uses a stack-based approach to handle operators and parentheses.
     *
     * @param stack         Stack to build the tree structure
     * @param ch            Character array of the expression
     * @param atomicInteger Current index in the character array (mutable)
     */
    private void buildTree(Stack<TreeNode> stack, char ch[], AtomicInteger atomicInteger) {

        // Base case: reached end of expression
        if (atomicInteger.get() == ch.length) {
            return;
        }

        // Get current character and increment index
        char c = ch[atomicInteger.getAndIncrement()];

        if (c == '0' || c == '1') {
            // Create a leaf node for digit
            TreeNode node = new TreeNode(c);
            if (stack.isEmpty()) {
                // First node becomes root
                stack.push(node);

            } else {
                // Attach as right child of current operator
                stack.peek().right = node;
            }
        } else if (c == '(') {
            // Handle sub-expression within parentheses
            var temp = new Stack<TreeNode>();

            // Recursively build subtree for the parenthesized expression
            buildTree(temp, ch, atomicInteger);
            if (stack.isEmpty()) {
                // Sub-expression becomes root
                stack.push(temp.pop());
            } else {
                // Attach sub-expression as right child
                stack.peek().right = temp.pop();
            }
        } else if (c == ')') {
            // End of parenthesized expression, return to caller
            return;
        } else {
            // Operator ('&' or '|')
            var node = new TreeNode(c);
            // Left operand is the previous expression on stack
            node.left = stack.pop();
            stack.push(node);
        }

        // Continue building the tree
        buildTree(stack, ch, atomicInteger);

    }

    /**
     * Result class to store minimum flips needed for both possible outcomes.
     */
    class Result {

        int one;  // Minimum flips to make expression evaluate to 1
        int zero; // Minimum flips to make expression evaluate to 0

        /**
         * Constructor for Result.
         *
         * @param one  Minimum flips to make expression 1
         * @param zero Minimum flips to make expression 0
         */
        public Result(int one, int zero) {

            this.one = one;
            this.zero = zero;
        }
    }


    /**
     * TreeNode class representing a node in the expression tree.
     * Can be a digit ('0' or '1') or an operator ('&' or '|').
     */
    class TreeNode {

        char val;         // Value: '0', '1', '&', or '|'
        TreeNode left;    // Left child (left operand for operators)
        TreeNode right;   // Right child (right operand for operators)

        /**
         * Constructor for TreeNode.
         *
         * @param val Character value of the node
         */
        public TreeNode(char val) {

            this.val = val;
        }
    }
}