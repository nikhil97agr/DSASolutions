package Leetcode;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VeganFriendly {
    public List<Integer> filterRestaurants(int[][] restaurants, int veganFriendly, int maxPrice, int maxDistance) {
        return Arrays.stream(restaurants)
                .filter(res ->{
                    if(veganFriendly==0) return true;
                    return veganFriendly == res[2];
                })
                .filter(res -> res[3]<=maxPrice && res[4]<=maxDistance)
                .sorted((a,b)-> {
                    if(b[1] == a[1]) return b[0] - a[0];
                    return b[1] - a[1];
                })
                .map(res -> res[0])
                .collect(Collectors.toList());
    }
}
