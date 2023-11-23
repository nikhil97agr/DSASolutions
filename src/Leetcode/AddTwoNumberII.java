package Leetcode;

import java.util.Stack;

// Problem Link : https://leetcode.com/problems/add-two-numbers-ii/

public class AddTwoNumberII {

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        Stack<ListNode> s1 = new Stack<>();     //stack to store the element of both the list node from end to start
        Stack<ListNode> s2 = new Stack<>();
        ListNode temp = l1;
        while (temp != null) {
            s1.add(temp);
            temp = temp.next;
        }

        temp = l2;
        while (temp != null) {
            s2.add(temp);
            temp = temp.next;
        }

        ListNode result = null;

        int carry = 0;

        while (!s1.isEmpty() && !s2.isEmpty()) {    // iterate over the two stack till one of them is empty
            l1 = s1.pop();
            l2 = s2.pop();
            int sum = l1.val + l2.val + carry;
            carry = sum / 10;   //calculate the carry for next numbers if sum of currents are added above 9
            sum %= 10;

            temp = new ListNode(sum);
            temp.next = result;
            result = temp;

        }

        while (!s1.isEmpty()) {     //now check for the rest of elements remaining in either of the two stacks
            ListNode l = s1.pop();
            int sum = l.val + carry;
            carry = sum / 10;
            sum %= 10;

            temp = new ListNode(sum);
            temp.next = result;
            result = temp;

        }

        while (!s2.isEmpty()) {
            ListNode l = s2.pop();
            int sum = l.val + carry;
            carry = sum / 10;
            sum %= 10;

            temp = new ListNode(sum);
            temp.next = result;
            result = temp;

        }

        if (carry != 0) {   //check if still any carry is left even after adding the last two numbers
            temp = new ListNode(carry);
            temp.next = result;
            result = temp;
        }
        return result;
    }

}
