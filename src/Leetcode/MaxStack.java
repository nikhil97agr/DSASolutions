package Leetcode;

import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

public class MaxStack {
    DLL head;
    DLL tail;
    TreeMap<Integer, Stack<DLL>> map;
    public MaxStack() {
        map = new TreeMap<>();
        head = new DLL(-1);
        tail = new DLL(-1);

        head.next = tail;
        tail.prev = head;
    }

    public void push(int x) {
        DLL node = new DLL(x);

        add(node);
        map.computeIfAbsent(x, y-> new Stack<>()).push(node);
    }

    public int pop() {
        DLL node = tail.prev;
        map.get(node.val).pop();
        delete(node);
        if(map.get(node.val).isEmpty()) map.remove(node.val);

        return node.val;
    }

    public int top() {
        return tail.prev.val;
    }

    public int peekMax() {
        return map.lastKey();
    }

    public int popMax() {

        int max= map.lastKey();

        DLL dll = map.get(max).pop();

        delete(dll);
        if(map.get(max).isEmpty()) map.remove(max);
        return dll.val;

    }

    private void add(DLL node){
        DLL prev = tail.prev;

        prev.next = node;
        node.prev = prev;
        tail.prev = node;
        node.next = tail;
    }

    private void delete(DLL node){
        DLL prev = node.prev;
        DLL next = node.next;
        prev.next = next;
        next.prev = prev;
    }



    class DLL{
        int val;
        DLL prev;
        DLL next;

        public DLL(int val){
            this.val = val;
        }
    }
}

/**
 * Your MaxStack object will be instantiated and called as such:
 * MaxStack obj = new MaxStack();
 * obj.push(x);
 * int param_2 = obj.pop();
 * int param_3 = obj.top();
 * int param_4 = obj.peekMax();
 * int param_5 = obj.popMax();
 */