package Leetcode;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class ApplyDiscountEveryNOrders {
    int cnt = 0;
    int n;
    int disc;
    Map<Integer, Integer> map;
    public ApplyDiscountEveryNOrders(int n, int discount, int[] products, int[] prices) {
        this.n = n;
        this.disc = discount;
        map = new HashMap<>();
        for(int i=0;i<products.length;i++){
            map.put(products[i], prices[i]);
        }
    }

    public double getBill(int[] product, int[] amount) {
        double result = IntStream.range(0, product.length).boxed().map(x  -> {
            int price = map.get(product[x]);
            int amt = amount[x];

            return 1d*amt*price;
        }).reduce(0d, Double::sum);
        cnt++;

        if(cnt %n ==0){
            result = (result * (100-disc))/100;
        }
        return result;
    }
}

/**
 * Your Cashier object will be instantiated and called as such:
 * Cashier obj = new Cashier(n, discount, products, prices);
 * double param_1 = obj.getBill(product,amount);
 */