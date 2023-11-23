package Leetcode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PointThatIntersectWithCar {
    public int numberOfPoints(List<List<Integer>> nums) {
        Set<Integer> ans = new HashSet<>();
        for(List<Integer> num : nums){
            for(int i=num.get(0);i<=num.get(1);i++){
                ans.add(i);
            }
        }
        return ans.size();
    }
}
