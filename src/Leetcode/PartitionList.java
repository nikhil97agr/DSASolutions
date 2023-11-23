package Leetcode;

import java.util.LinkedList;
import java.util.Queue;

// Problem Link : https://leetcode.com/problems/partition-list/

public class PartitionList {
    public ListNode partition(ListNode head, int x) {
        Queue<Integer> small = new LinkedList<>();
        Queue<Integer> large = new LinkedList<>();
        while (head != null) {
            if (head.val < x) {
                small.add(head.val);
            } else {
                large.add(head.val);
            }
            head = head.next;
        }

        head = new ListNode(-1);
        ListNode temp = head;
        while (!small.isEmpty()) {
            temp.next = new ListNode(small.poll());
            temp = temp.next;
        }
        while (!large.isEmpty()) {
            temp.next = new ListNode(large.poll());
            temp = temp.next;
        }
        return head.next;
    }
}