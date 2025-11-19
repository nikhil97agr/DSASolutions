package Leetcode;

import java.util.*;

public class DigitOperationMakeElementsEquals {

    public static void main(String[] args) {
        System.out.println(new DigitOperationMakeElementsEquals().minOperations(10, 12));
    }
    public int minOperations(int n, int m) {
        boolean primes[] = new boolean[100000];
        Arrays.fill(primes, true);
        primes[0] = primes[1] = false;
        for(int i=2;i<=99999;i++){
            if(!primes[i] ) continue;

            int j=i+i;
            while(j<=99999){
                primes[j] = false;
                j+=i;
            }
        }
        int size = (""+n).length();
        Set<Integer> visited = new HashSet<>();
        PriorityQueue<int[]> que = new PriorityQueue<>(Comparator.comparingInt(i -> i[1]));
        que.offer(new int[]{n, n});
        visited.add(n);
        while(!que.isEmpty()){
            int next[] = que.poll();
             n = next[0];
            int score = next[1];
            if(n == m) return score;
            int dig[] = new int[size];
            int i=size-1;
            while(n>0){
                dig[i--] = n%10;
                n/=10;
            }

            for(i=0;i<size;i++){
                n=0;
                for(int j=0;j<size;j++){
                    if(i==j && dig[j] !=0){
                        dig[j]--;
                        n = n*10 + dig[j];
                        dig[j]++;
                    }else{
                        n= n*10 + dig[j];
                    }
                }

                if(!primes[n] && !visited.contains(n)){
                    que.offer(new int[]{n, score+n});
                    visited.add(n);
                }
                n=0;

                for(int j=0;j<size;j++){
                    if(i==j && dig[j]!=9){
                        dig[j]++;
                        n = n*10 + dig[j];
                        dig[j]--;
                    }else{
                         n = n*10 + dig[j];
                    }
                }

                if(!primes[n] && !visited.contains(n)){
                    que.offer(new int[]{n, score + n});
                    visited.add(n);
                }
            }

        }
        return -1;
    }

}