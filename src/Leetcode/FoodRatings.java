package Leetcode;

import java.util.*;

public class FoodRatings {
    public class PairComparator implements  Comparator<Pair>{

        @Override
        public int compare(Pair o1, Pair o2) {
            if(o1.rating == o2.rating){
                return o1.food.compareTo(o2.food);
            }
            return o2.rating - o1.rating;
        }
    }
    class Pair {
        String food;
        int rating;
        public Pair(String food, int rating){
            this.food = food;
            this.rating = rating;
        }

        public boolean equals(Object obj){
            Pair pair = (Pair) obj;
            return this.food.equals(pair.food);
        }

        public int hashCode(){
            return this.food.hashCode();
        }
    }

    Map<String, Pair> foodRatingMap;
    Map<String, TreeSet<Pair>> cuisineRatingMap;
    Map<String, String> foodCuisineMap;



    public FoodRatings(String[] foods, String[] cuisines, int[] ratings) {
        foodRatingMap = new HashMap<>();
        cuisineRatingMap = new HashMap<>();
        foodCuisineMap = new HashMap<>();
        int n = foods.length;
        for(int i=0;i<n;i++){
            String f = foods[i];
            String c = cuisines[i];
            int r = ratings[i];

            Pair pair = new Pair(f, r);
            foodCuisineMap.put(f,c);
            cuisineRatingMap.computeIfAbsent(c, c1 -> new TreeSet<>(new PairComparator())).add(pair);
            foodRatingMap.put(f, pair);
        }

    }
    
    public void changeRating(String food, int newRating) {
        Pair pair = foodRatingMap.get(food);
        String cuisine = foodCuisineMap.get(food);
        cuisineRatingMap.get(cuisine).remove(pair);
        pair.rating = newRating;
        cuisineRatingMap.get(cuisine).add(pair);
    }
    
    public String highestRated(String cuisine) {
        return cuisineRatingMap.get(cuisine).first().food;
    }
}