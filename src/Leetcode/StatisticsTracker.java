package Leetcode;

import java.util.*;

public class StatisticsTracker {
    long sum;
    int total;
    TreeMap<Integer, Integer> minMap;
    TreeMap<Integer, Integer> maxMap;
    int minTotal;
    int maxTotal;
    Map<Integer, Integer> numberCount;
    TreeMap<Integer, TreeSet<Integer>> countMap;
    Queue<Integer> que;

    public StatisticsTracker() {
        sum = 0;
        total = 0;
        minMap = new TreeMap<>();
        maxMap = new TreeMap<>();
        minTotal = maxTotal = 0;
        numberCount = new HashMap<>();
        countMap = new TreeMap<>();
        que = new LinkedList<>();
    }

    public void addNumber(int number) {
        sum += number;
        total++;
        que.offer(number);

        int cnt = numberCount.getOrDefault(number, 0);


        if(cnt !=0){
            countMap.get(cnt).remove(number);
            if(countMap.get(cnt).isEmpty()){
                countMap.remove(cnt);
            }

        }
        cnt++;
        numberCount.put(number, cnt);
        countMap.computeIfAbsent(cnt, x -> new TreeSet<>()).add(number);

        maxMap.merge(number, 1, Integer::sum);
        maxTotal++;
        balance();
    }

    public void removeFirstAddedNumber() {
        int num = que.poll();
        sum -= num;
        total--;
        int cnt = numberCount.get(num);
        countMap.get(cnt).remove(num);
        if(countMap.get(cnt).isEmpty()) countMap.remove(cnt);
        if(cnt == 1){
            numberCount.remove(num);

        }else{
            cnt--;
            numberCount.put(num, cnt);
            countMap.computeIfAbsent(cnt, x-> new TreeSet<>()).add(num);
        }

        if(maxMap.containsKey(num)){
            maxMap.merge(num, -1, Integer::sum);
            if(maxMap.get(num) == 0) maxMap.remove(num);
            maxTotal--;
        }else{
            minMap.merge(num, -1, Integer::sum);
            if(minMap.get(num) ==0) minMap.remove(num);
            minTotal--;
        }

        balance();
    }

    private void balance(){
        if(minTotal ==0 && maxTotal ==0) return;

        if (minTotal > maxTotal) {
            int high = minMap.lastKey();
            minMap.merge(high, -1, Integer::sum);
            if(minMap.get(high)==0) minMap.remove(high);
            maxMap.merge(high, 1, Integer::sum);
            minTotal--;
            maxTotal++;

        }else if(maxTotal - minTotal > 1){
            int low = maxMap.firstKey();
            maxMap.merge(low, -1, Integer::sum);
            if(maxMap.get(low)==0) maxMap.remove(low);
            minMap.merge(low, 1, Integer::sum);
            minTotal++;
            maxTotal--;
        }

        int low = maxMap.firstKey();
        maxMap.merge(low, -1, Integer::sum);
        if(maxMap.get(low)==0) maxMap.remove(low);
        minMap.merge(low, 1, Integer::sum);

        int high = minMap.lastKey();
        minMap.merge(high, -1, Integer::sum);
        if(minMap.get(high) ==0) minMap.remove(high);
        maxMap.merge(high, 1, Integer::sum);

    }

    public int getMean() {
        return (int)(sum/total);
    }

    public int getMedian() {
        return maxMap.firstKey();
    }

    public int getMode() {
        return countMap.lastEntry().getValue().first();
    }
}

/**
 * Your StatisticsTracker object will be instantiated and called as such:
 * StatisticsTracker obj = new StatisticsTracker();
 * obj.addNumber(number);
 * obj.removeFirstAddedNumber();
 * int param_3 = obj.getMean();
 * int param_4 = obj.getMedian();
 * int param_5 = obj.getMode();
 */