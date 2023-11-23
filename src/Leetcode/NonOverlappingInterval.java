package Leetcode;

// Problem link : https://leetcode.com/problems/non-overlapping-intervals/

import java.util.Arrays;

public class NonOverlappingInterval {
    public int eraseOverlapIntervals(int [][] intervals){
        Arrays.sort(intervals, (a,b)->{     //sort the intervals by increasing order of their end time.
            return a[1]-b[1];
        });
        int cnt = 1;    //this will track the number of interval sets that can be formed by combining all the overlaps
        int end = intervals[0][1];  //set the end time of overlapping intervals with end time of first itnerval.

        for(int i=1;i<intervals.length;i++){
            if(intervals[i][0]>=end){   //now check if the current intervals is overlapping with the previous overlapped interval
                cnt++;                      
                end = intervals[i][1];  // if not overlapping then this intervals end time will become end time of new interval
            }
        }
        return intervals.length - cnt;  // subtract the number of intervals after combining all the possible overlaps from the total intervals
    }
}
