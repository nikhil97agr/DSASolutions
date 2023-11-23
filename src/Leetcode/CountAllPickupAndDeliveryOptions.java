package Leetcode;

//Problem Link : https://leetcode.com/problems/count-all-valid-pickup-and-delivery-options/

public class CountAllPickupAndDeliveryOptions {
    public int countOrders(int n) {
        long mod = (int) 1e9 + 7;
        long ans = 1;
        for (long i = 2 * n; i > 0; i -= 2) {
            ans = (ans * ((i * (i - 1)) / 2) % mod) % mod;
        }

        return (int) ans;
    }
}