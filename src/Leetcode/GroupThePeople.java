package Leetcode;

//Problem Link: https://leetcode.com/problems/group-the-people-given-the-group-size-they-belong-to

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class GroupThePeople {
    public List<List<Integer>> groupThePeople(int[] groupSizes) {
        List<List<Integer>> result = new ArrayList<>();
        Map<Integer, List<Integer>> map = new TreeMap<>();
        int n = groupSizes.length;
        for (int i = 0; i < n; i++) {
            map.computeIfAbsent(groupSizes[i], a -> new ArrayList<>());
            map.get(groupSizes[i]).add(i);
        }
        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            int i = 0;
            int size = entry.getKey();
            while (i < entry.getValue().size()) {
                List<Integer> temp = new ArrayList<>();

                int j = i;

                for (; j < i + size; j++) {
                    temp.add(entry.getValue().get(j));
                }
                i += size;
                result.add(temp);

            }

        }

        return result;
    }
}
