package Leetcode;

import java.util.LinkedList;
import java.util.Queue;

//Problem Link: https://leetcode.com/problems/basic-calculator-iii/description/

public class BasicCalculatorIII {

    public int calculate(String s) {

        Queue<String> que = new LinkedList<>();

        int n = s.length();
        int i = 0;
        while (i < n) {
            char c = s.charAt(i);

            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                while (i < n && Character.isDigit(c)) {
                    sb.append(c);
                    i++;
                    if (i < n) {
                        c = s.charAt(i);
                    }
                }
                que.offer(sb.toString());
            } else if (c != ' ') {
                que.offer(c + "");
                i++;
            } else {
                i++;
            }
        }

        return solve(que);
    }

    private int solve(Queue<String> que) {

        Queue<String> q1 = new LinkedList<>();
        while (!que.isEmpty()) {
            String next = que.poll();
            if (next.equals(")")) {
                break;
            }

            if (next.equals("(")) {
                int val = solve(que);
                q1.offer(val + "");
                continue;
            }

            q1.offer(next);
        }

        // Step 1: Handle multiplication first
        Queue<String> afterMul = new LinkedList<>();
        while (!q1.isEmpty()) {
            String current = q1.poll();

            if (!q1.isEmpty() && q1.peek().equals("*")) {
                int result = Integer.parseInt(current);
                while (!q1.isEmpty() && q1.peek().equals("*")) {
                    q1.poll();
                    String next = q1.poll();
                    result = result * Integer.parseInt(next);
                }
                afterMul.offer(result + "");

            } else {
                afterMul.offer(current);
            }
        }

        // Step 2: Handle division
        Queue<String> afterDiv = new LinkedList<>();
        while (!afterMul.isEmpty()) {
            String current = afterMul.poll();

            if (!afterMul.isEmpty() && afterMul.peek().equals("/")) {
                int result = Integer.parseInt(current);
                while (!afterMul.isEmpty() && afterMul.peek().equals("/")) {
                    afterMul.poll();
                    String next = afterMul.poll();
                    result = result / Integer.parseInt(next);
                }

                afterDiv.offer(result + "");
            } else {
                afterDiv.offer(current);
            }
        }

        // Step 3: Handle addition and subtraction
        int result = 0;
        if (!afterDiv.isEmpty()) {
            result = Integer.parseInt(afterDiv.poll());
        }
        while (!afterDiv.isEmpty() && (afterDiv.peek().equals("-") || afterDiv.peek().equals("+"))) {
            String op = afterDiv.poll();
            int num = Integer.parseInt(afterDiv.poll());
            if (op.equals("+")) {

                result += num;
            } else {
                result -= num;
            }
        }

        return result;
    }
}