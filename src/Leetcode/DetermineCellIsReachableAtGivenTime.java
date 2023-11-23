package Leetcode;

public class DetermineCellIsReachableAtGivenTime {
    public boolean isReachableAtTime(int sx, int sy, int fx, int fy, int t) {
        int minx = Math.abs(sx-fx);
        int miny = Math.abs(sy-fy);
        int minDist = Math.max(minx, miny);
        if(sx==fx && sy==fy){
            if(t==1) return false;
            return true;
        }
        
        return t >= minDist;
    }
}
