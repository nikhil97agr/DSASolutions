package Leetcode;

// Problem Link : https://leetcode.com/problems/employee-importance/

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EmployeeImportance {
    public int getImportance(List<Employee> employees, int id) {
        Map<Integer, Employee> idMap = new HashMap<>();

        for (Employee e : employees) {
            idMap.put(e.id, e);
        }

        Set<Integer> visited = new HashSet<>();

        return solve(idMap, id, visited);
    }

    private int solve(Map<Integer, Employee> idMap, int id, Set<Integer> visited) {

        visited.add(id);
        Employee e = idMap.get(id);
        int imp = e.importance;

        for (int i : e.subordinates) {
            if (!visited.contains(i)) {
                imp += solve(idMap, i, visited);
            }
        }
        return imp;
    }
}

class Employee {
    public int id;
    public int importance;
    public List<Integer> subordinates;
}
