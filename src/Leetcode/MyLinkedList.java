package Leetcode;

public class MyLinkedList {

    MyNode head;
    MyNode tail;
    public MyLinkedList() {
        head = new MyNode(-1);
        tail = new MyNode(-1);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int index) {
        MyNode curr= head;
        while(curr!=tail && index>=0){
            curr = curr.next;
            index--;
        }

        if(curr==tail) return -1;

        return curr.val;
    }

    public void addAtHead(int val) {
        MyNode node = new MyNode(val);
        addNode(head, node);
    }

    public void addAtTail(int val) {
        MyNode node = new MyNode(val);
        addNode(tail.prev, node);
    }

    private void addNode(MyNode head, MyNode curr){
        MyNode next = head.next;
        head.next = curr;
        curr.prev = head;
        curr.next = next;
        next.prev = curr;
    }

    private void deleteNode(MyNode head, MyNode node){
        MyNode next = node.next;
        head.next =next;
        next.prev = head;
    }

    public void addAtIndex(int index, int val) {
        MyNode curr = head;
        MyNode node = new MyNode(val);
        while(curr!=tail && index>=0){
            curr = curr.next;
            index--;
        }

        if(curr==tail){
            if(index<0){
                addNode(tail.prev, node);
            }
            return;
        }

        addNode(curr.prev, node);


    }

    public void deleteAtIndex(int index) {
        MyNode curr = head;
        while(curr!=tail && index>=0){
            curr = curr.next;
            index--;
        }

        if(curr!=tail) deleteNode(curr.prev, curr);
    }
}

class MyNode{
    int val;
    MyNode prev;
    MyNode next;
    public MyNode(int val){
        this.val = val;
    }
}

/**
 * Your MyLinkedList object will be instantiated and called as such:
 * MyLinkedList obj = new MyLinkedList();
 * int param_1 = obj.get(index);
 * obj.addAtHead(val);
 * obj.addAtTail(val);
 * obj.addAtIndex(index,val);
 * obj.deleteAtIndex(index);
 */
