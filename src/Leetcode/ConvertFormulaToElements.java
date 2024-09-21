package Leetcode;

import java.util.*;

class ConvertFormulaToElements {
    public static void main(String[] args) {
        System.out.println(new ConvertFormulaToElements().countOfAtoms("K4(ON(SO3)2)2"));
    }
    public String countOfAtoms(String formula) {
        Queue<Character> que = new LinkedList<>();

        for(char c : formula.toCharArray()) que.offer(c);

        Map<String, Integer> map = solve(que);

        List<String> list = new ArrayList<>(map.keySet());
        Collections.sort(list);
        StringBuilder res = new StringBuilder();

        for(String s : list){
            if(map.get(s)==1){
                res.append(s);
            }else{
                res.append(s);
                res.append(map.get(s));
            }
        }

        return res.toString();
    }

    private Map<String, Integer> solve(Queue<Character> que){
        Map<String, Integer> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        int dig = 0;
        while(!que.isEmpty()){
            char c = que.poll();
            if(c==')') {
                if(!sb.isEmpty()){
                    String s = sb.toString();
                    if(dig !=0){
                        map.put(s, map.getOrDefault(s,0)+dig);
                    }else{
                        map.put(s, map.getOrDefault(s, 0)+1);
                    }
                }
                sb = new StringBuilder();
                dig = 0;
                break;
            }
            if(c=='('){
                if(!sb.isEmpty()){
                    String s = sb.toString();
                    if(dig !=0){
                        map.put(s, map.getOrDefault(s, 0)+dig);
                    }else{
                        map.put(s, map.getOrDefault(s, 0)+1);
                    }
                }
                sb = new StringBuilder();
                dig = 0;
                Map<String, Integer> m = solve(que);
                for(Map.Entry<String, Integer> entry : m.entrySet()){
                    String s = entry.getKey();
                    int cnt = entry.getValue();
                    map.put(s, map.getOrDefault(s, 0)+cnt);
                }
                continue;
            }

            if(Character.isDigit(c)){
                dig = (dig*10 + (c-'0'));
            }else{
                if(Character.isUpperCase(c)){
                    if(!sb.isEmpty()){
                        String s= sb.toString();
                        if(dig !=0){
                            map.put(s, map.getOrDefault(s, 0)+dig);
                        }else{
                            map.put(s, map.getOrDefault(s, 0)+1);
                        }
                    }
                    sb = new StringBuilder();
                    sb.append(c);
                    dig = 0;
                }else{
                    sb.append(c);
                }
            }
        }
        if(!sb.isEmpty()){
            String s= sb.toString();
            if(dig ==0){
                map.put(s, map.getOrDefault(s, 0)+1);
            }else{
                map.put(s, map.getOrDefault(s, 0)+dig);
            }
        }
        int num = 0;
        while(!que.isEmpty() && Character.isDigit(que.peek())){
            num = num*10 + (que.poll()-'0');
        }
        if(num!=0){
            for(String keys : map.keySet()){
                map.put(keys, map.get(keys)*num);
            }
        }

        return map;
    }
}