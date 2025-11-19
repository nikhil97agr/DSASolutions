package Leetcode;

import java.util.*;

public class MovieRentingSystem {
    Map<Integer, Map<Integer, Integer>> unrentShopMap;
    Map<Integer, TreeMap<Integer, TreeSet<Integer>>> unrentMovieMap;

    Map<Integer, Map<Integer, Integer>> rentedMovieMap;

    TreeMap<Integer, TreeMap<Integer, TreeSet<Integer>>> rentedPriceMap;

    public MovieRentingSystem(int n, int[][] entries) {
        unrentShopMap = new HashMap<>();
        unrentMovieMap = new HashMap<>();
        rentedMovieMap = new HashMap<>();
        rentedPriceMap = new TreeMap<>();
        for(int []e: entries){
            int shop = e[0];
            int movie= e[1];
            int price = e[2];

            unrentShopMap.computeIfAbsent(shop, s -> new HashMap<>()).put(movie, price);
            unrentMovieMap.computeIfAbsent(movie, m-> new TreeMap<>()).computeIfAbsent(price, p -> new TreeSet<>()).add(shop);
        }
    }

    public List<Integer> search(int movie) {
        List<Integer> result =new ArrayList<>();
        TreeMap<Integer, TreeSet<Integer>> map  = unrentMovieMap.get(movie);

        for(TreeSet<Integer> shops : map.values()){

            for(int shop : shops){
                result.add(shop);
                if(result.size() == 5) break;
            }

            if(result.size() == 5) break;

        }



        return result;
    }

    public void rent(int shop, int movie) {
        int price = unrentShopMap.get(shop).get(movie);
        unrentShopMap.get(shop).remove(movie);
        if(unrentShopMap.get(shop).isEmpty()){
            unrentShopMap.remove(shop);
        }

        unrentMovieMap.get(movie).get(price).remove(shop);
        if(unrentMovieMap.get(movie).get(price).isEmpty()){
            unrentMovieMap.get(movie).remove(price);
        }
        if(unrentMovieMap.get(movie).isEmpty()){
            unrentMovieMap.remove(movie);
        }
        rentedMovieMap.computeIfAbsent(shop, s -> new HashMap<>()).put(movie, price);
        rentedPriceMap.computeIfAbsent(price, p -> new TreeMap<>()).computeIfAbsent(shop, s -> new TreeSet<>()).add(movie);
    }

    public void drop(int shop, int movie) {
        int price = rentedMovieMap.get(shop).get(movie);

        unrentShopMap.computeIfAbsent(shop, s -> new HashMap<>()).put(movie, price);
        unrentMovieMap.computeIfAbsent(movie, m -> new TreeMap<>()).computeIfAbsent(price, p-> new TreeSet<>()).add(shop);

        rentedMovieMap.get(shop).remove(movie);

        rentedPriceMap.get(price).get(shop).remove(movie);
        if(rentedPriceMap.get(price).get(shop).isEmpty()){
            rentedPriceMap.get(price).remove(shop);
        }

        if(rentedPriceMap.get(price).isEmpty()){
            rentedPriceMap.remove(price);
        }
    }



    public List<List<Integer>> report() {
        List<List<Integer>> res = new ArrayList<>();

        for(TreeMap<Integer, TreeSet<Integer>> map: rentedPriceMap.values()){
            for(Map.Entry<Integer, TreeSet<Integer>> entry : map.entrySet()){
                int shop = entry.getKey();

                for(int movie : entry.getValue()){
                    res.add(List.of(shop, movie));
                    if(res.size()==5) break;
                }

                if(res.size() == 5) break;
            }
            if(res.size() == 5) break;
        }


        return res;
    }
}

/**
 * Your MovieRentingSystem object will be instantiated and called as such:
 * MovieRentingSystem obj = new MovieRentingSystem(n, entries);
 * List<Integer> param_1 = obj.search(movie);
 * obj.rent(shop,movie);
 * obj.drop(shop,movie);
 * List<List<Integer>> param_4 = obj.report();
 */