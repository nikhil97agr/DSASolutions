package Leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Problem Link : https://leetcode.com/problems/smallest-sufficient-team/



class SmallestSufficientTeam {
    public int[] smallestSufficientTeam(String[] req_skills, List<List<String>> people) {
        
        Map<String, Integer> skillIndexMapping = new HashMap<>();
        int x = 0;
        int reqSkill = 0;
        for(String skill : req_skills){
            skillIndexMapping.put(skill, x++);
        }
        reqSkill = (1<<x)-1;    //total value that needs to be formed i.e. all the skill indexes should be 1

        int n = people.size();
        int skills [] = new int[n]; //values of each person with their skills index as 1
        for(int i=0;i<n;i++){
            for(String s : people.get(i)){
                int ind = skillIndexMapping.get(s);
                skills[i] = (skills[i] | (1<<ind));
            }
        }
        solve(0, 0, skills, reqSkill, new ArrayList<>());
        return convertToArray(result);

    }

    private int[] convertToArray(List<Integer> result){ //generate integer array out of list 
        int arr[] = new int[result.size()];
        for(int i=0;i<result.size();i++){
            arr[i] = result.get(i);
        }
        return arr;
    }
    List<Integer> result = new ArrayList<>();

    private void solve(int i, int currSkill, int mask[], int reqMask, List<Integer> temp){
        if(result.size()>0 && temp.size()>=result.size() || i == mask.length){  //base case : if either all the person are traversed or at some point 
                                                                                //number of people in the list more than the number of people already calculated in the resultant
            return;
        }

        temp.add(i);

        if((currSkill | mask[i])==reqMask){ //check if the adding the current index skill gives us the required skill or not then we found the answer
            result = new ArrayList<>(temp);

        }else if((currSkill | mask[i]) > currSkill){ // check if adding the current index skill will increase the current running skill 
            solve(i+1, currSkill | mask[i], mask, reqMask, temp);
        }

        temp.remove(temp.size()-1);
        solve(i+1, currSkill, mask, reqMask, temp); //excluding the current running skill and check with rest of the elements

    }
    
}
