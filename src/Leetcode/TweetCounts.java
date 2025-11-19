package Leetcode;

import java.util.*;

//Problem Link: https://leetcode.com/problems/tweet-counts-per-frequency

public class TweetCounts {

    Map<String, List<Integer>> map;
    public TweetCounts() {
        map = new HashMap<>();


    }
    
    public void recordTweet(String tweetName, int time) {
        List<Integer> list = map.computeIfAbsent(tweetName, x-> new ArrayList<>());

        int ind = Collections.binarySearch(list, time);

        if(ind < 0){
            ind = Math.abs(ind) -1;
            if(ind == list.size()){
                list.add(time);
            }else{
                list.add(ind, time);
            }
        }else{
            list.add(ind, time);
        }
    }
    
    public List<Integer> getTweetCountsPerFrequency(String freq, String tweetName, int startTime, int endTime) {
        FrequenceyCalculator frequenceyCalculator = FrequencyCalculatorFactory.getCalculator(freq);

        return frequenceyCalculator.calculate(map.getOrDefault(tweetName, new ArrayList<>()), startTime, endTime);
    }
}

class FrequencyCalculatorFactory{
    public static FrequenceyCalculator getCalculator(String freq){
        switch(freq){
            case "minute": return new MinuteFreuqencyCalculator();

            case "hour": return new HourFreuqencyCalculator();

            case "day": return new DayFreuqencyCalculator();
        }

        return null;
    }
}

abstract class FrequenceyCalculator{
    public abstract List<Integer> calculate(List<Integer> times, int start, int end);

    public int binarySearch(List<Integer> list, int start, int end){
        int ind1= -1;
        int ind2 = -1;

        int s = 0;
        int e = list.size()-1;

        while(s <= e){
            int mid = (s+e)/2;

            if(list.get(mid)< start){
                s = mid + 1;
            }else{
                e = mid - 1;
                ind1= mid;
            }
        }

        s = 0; e= list.size()-1;

        while(s <= e){
            int mid = (s+e)/2;

            if(list.get(mid) > end){
                e = mid -1;
            }else{
                s = mid +1;
                ind2 =mid;
            }
        }

        if(ind1 == -1 || ind2==-1) return 0;

        return ind2 - ind1 + 1;
    }

}

class MinuteFreuqencyCalculator extends  FrequenceyCalculator{

    @Override
    public List<Integer> calculate(List<Integer> times, int start, int end) {
        int seconds = 1*60 - 1;
        List<Integer> result = new ArrayList<>();
        while(start <= end){
            int e = Math.min(end, start + seconds);
            result.add(binarySearch(times, start, e));

            start = e + 1;
        }

        return result;
    }
}

class HourFreuqencyCalculator extends  FrequenceyCalculator{

    @Override
    public List<Integer> calculate(List<Integer> times, int start, int end) {
        List<Integer> result = new ArrayList<>();
        int second = 1*60*60-1;
        while(start <= end){
            int e = Math.min(end, start + second);
            result.add(binarySearch(times, start, e));
            start = e + 1;
        }

        return result;
    }
}

class DayFreuqencyCalculator extends  FrequenceyCalculator{

    @Override
    public List<Integer> calculate(List<Integer> times, int start, int end) {
        List<Integer> result = new ArrayList<>();
        int second = 1*60*60*24-1;
        while(start <= end){
            int e = Math.min(end, start + second);
            result.add(binarySearch(times, start, e));
            start = e + 1;
        }

        return result;
    }
}