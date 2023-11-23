package Leetcode;

//Problem Link: https://leetcode.com/problems/reconstruct-itinerary/

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ReconstructItinerary {
    public List<String> findItinerary(List<List<String>> tickets) {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        for (List<String> list : tickets) {
            map.putIfAbsent(list.get(0), new ArrayList<String>());
            map.putIfAbsent(list.get(1), new ArrayList<String>());
            map.get(list.get(0)).add(list.get(1));
        }
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            Collections.sort(entry.getValue());
        }
        int total = map.size() * 3;
        List<String> result = new ArrayList<String>();
        Stack<String> stack = new Stack<String>();
        stack.push("JFK");
        while (!stack.isEmpty()) {
            if (!map.containsKey(stack.peek())) {
                result.add(stack.pop());
                continue;
            }

            String temp = stack.peek();
            if (map.get(temp).size() == 0) {
                result.add(temp);
                stack.pop();
                map.remove(temp);
                continue;
            }
            stack.push(map.get(temp).get(0));
            map.get(temp).remove(0);
            if (map.get(temp).size() == 0) {
                map.remove(temp);
            }
        }

        Collections.reverse(result);
        return result;
    }
}
