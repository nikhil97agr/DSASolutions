package Leetcode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class BraceExpansionII {
    public List<String> braceExpansionII(String expression) {
        Set<String> result = new HashSet<>();

        Stack<Set<String>> stack = new Stack<>();
        char op = ',';
        for(int i=0;i<expression.length();i++){
            char ch = expression.charAt(i);

            if(ch == '{'){
                int cnt = 1;
                int j =i;
                while(expression.charAt(j) != '}' || cnt != 0){
                    j++;

                    if(expression.charAt(j) == '}') cnt++;
                    else if(expression.charAt(j) == '{') cnt--;

                }

                List<String> list = braceExpansionII(expression.substring(i+1, j));

                if(op == '*'){
                    stack.push(merge(stack.pop(), list));

                }else{
                    stack.push(new HashSet<>(list));
                }

                i = j;
                op = '*';
                continue;
            }

            if(Character.isLetter(ch)){

                List<String> list = List.of(""+ch);

                if(op == '*'){
                    stack.push(merge(stack.pop(), list));
                }else{
                    stack.push(new HashSet<>(list));
                }
                op = '*';
                continue;
            }

            op = ',';


        }

        while(!stack.isEmpty()){
            Set<String> set = stack.pop();

            result.addAll(set);
        }



        return result.stream().sorted().toList();
    }

    private Set<String> merge(Set<String> set, List<String> list){
        Set<String> result = new HashSet<>();
        for(String s : set){
            for(String s1 : list){
                result.add(s + s1);
            }
        }
        return result;
    }
}