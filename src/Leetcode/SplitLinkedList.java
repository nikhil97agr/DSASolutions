package Leetcode;

//Problem Link : https://leetcode.com/problems/split-linked-list-in-parts/

public class SplitLinkedList {
    public ListNode[] splitListToParts(ListNode head, int k) {
        ListNode result[] = new ListNode[k];
        int len = 0;
        ListNode curr = head;
        while (curr != null) {
            len++;
            curr = curr.next;
        }
        if (len == 0)
            return result;

        int size = len / k;
        int left = len % k;
        curr = head;
        int i = 0;
        while (curr != null) {
            int temp = size;
            ListNode node;
            ListNode nodeTemp;
            node = curr;
            curr = curr.next;
            nodeTemp = node;
            if (left > 0)
                left--;
            else
                temp--;
            while (temp-- > 0) {
                curr = curr.next;
                nodeTemp = nodeTemp.next;
            }
            nodeTemp.next = null;
            result[i++] = node;
        }
        return result;
    }
}
