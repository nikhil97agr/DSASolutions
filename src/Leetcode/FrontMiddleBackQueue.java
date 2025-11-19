package Leetcode;

import java.util.ArrayDeque;
import java.util.Deque;

public class FrontMiddleBackQueue {
    Deque<Integer> left;
    Deque<Integer> right;

    public FrontMiddleBackQueue() {
        left = new ArrayDeque<>();
        right = new ArrayDeque<>();
    }
    
    public void pushFront(int val) {
        left.offerFirst(val);
        balance();
    }
    
    public void pushMiddle(int val) {
        if(left.size() == right.size()){
            left.offerLast(val);
        }else{
            int v = left.pollLast();
            left.offerLast(val);
            left.offerLast(v);
        }
        balance();
    }
    
    public void pushBack(int val) {
        right.offerLast(val);
        balance();
    }

    private void balance(){
        int lsize = left.size();
        int rsize = right.size();

        if(lsize -rsize > 1){
            right.offerFirst(left.pollLast());
        }else if(rsize - lsize >= 1){
            left.offerLast(right.pollFirst());
        }
    }

    
    public int popFront() {
        if(left.isEmpty()) return -1;

        int val = left.pollFirst();

        balance();
        return val;
    }
    
    public int popMiddle() {
        if(left.isEmpty()){
            return -1;
        }

        int val = left.pollLast();

        balance();
        return val;
    }
    
    public int popBack() {
        if(left.isEmpty()) return -1;

        if(right.isEmpty()) return left.pollLast();

        int val = right.pollLast();
        balance();
        return val;
    }
}

/**
 * Your FrontMiddleBackQueue object will be instantiated and called as such:
 * FrontMiddleBackQueue obj = new FrontMiddleBackQueue();
 * obj.pushFront(val);
 * obj.pushMiddle(val);
 * obj.pushBack(val);
 * int param_4 = obj.popFront();
 * int param_5 = obj.popMiddle();
 * int param_6 = obj.popBack();
 */