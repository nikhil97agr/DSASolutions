package Leetcode;

import java.util.*;

public class RandomizedSet {

    Set<Integer> set;
    public RandomizedSet() {
        set = new HashSet<>();
    }

    public boolean insert(int val) {
        return set.add(val);
    }

    public boolean remove(int val) {
        return set.remove(val);
    }

    public int getRandom() {

        return set.stream().skip(new Random().nextInt(set.size())).findAny().get();

    }
}
