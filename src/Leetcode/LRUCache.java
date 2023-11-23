package Leetcode;

import java.util.HashMap;
import java.util.Map;

// Problem link : https://leetcode.com/problems/lru-cache/

public class LRUCache {
    Node head;
    Node tail;
    Map<Integer, Node> keyNodeMap;
    int capacity;
    public LRUCache(int capacity) {
        
        this.capacity = capacity;
        this.keyNodeMap = new HashMap<>(capacity);
        head = new Node(0,0);
        tail = new Node(0,0);
        head.next = tail;
        tail.previous = head;

    }
    
    public int get(int key) {
        if(keyNodeMap.containsKey(key)){
            Node node = keyNodeMap.get(key);

            delete(node);
            bringToFront(node);

            return node.val;
        }
        return -1;
        
    }
    
    private void bringToFront(Node node) {

        Node temp = head.next;
        head.next = node;
        node.previous = head;
        node.next = temp;
        temp.previous = node;

    }

    private void delete(Node node) {
        Node temp = node.next;
        node.previous.next = temp;
        node.next.previous = node.previous;
    }

    public void put(int key, int value) {
        if(keyNodeMap.containsKey(key)){
            Node node=  keyNodeMap.get(key);
            node.val = value;

            delete(node);
            bringToFront(node);
            return;
        }

        Node node = new Node(key, value);
        keyNodeMap.put(key, node);
        bringToFront(node);

        if(keyNodeMap.size()>capacity){
            keyNodeMap.remove(tail.previous.key);
            delete(tail.previous);
        }
        
    }
}

class Node {
    int key;
    int val;
    Node previous;
    Node next;

    public Node(int key, int val){
        this.key = key;
        this.val = val;
    }
}
