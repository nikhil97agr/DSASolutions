package Leetcode;

//Problem Link : https://leetcode.com/problems/reverse-linked-list-ii

import java.util.Deque;
import java.util.LinkedList;

public class ReverseLinkedList {
    public ListNode reverseBetween(ListNode head, int left, int right) {
        Deque<ListNode> que = new LinkedList<>();
        ListNode curr = head;
        while (curr != null) {
            que.add(curr);
            curr = curr.next;
        }
        ListNode result = new ListNode(-1);
        ListNode temp = result;
        int cnt = 1;
        while (cnt < left) {
            temp.next = que.pollFirst();
            temp = temp.next;
            cnt++;
        }
        ListNode reverse = null;
        while (cnt <= right) {
            ListNode node = que.pollFirst();
            node.next = null;

            if (reverse == null) {
                reverse = node;
            } else {
                node.next = reverse;
                reverse = node;
            }
            cnt++;
        }
        temp.next = reverse;
        while (temp.next != null) {
            temp = temp.next;
        }
        while (!que.isEmpty()) {
            ListNode node = que.pollFirst();
            temp.next = node;
            temp = temp.next;
        }
        return result.next;
    }
}