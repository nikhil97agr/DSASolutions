package Leetcode;

public class  LCWeekly442C {
    public static void main(String[] args) {
        System.out.println(new LCWeekly442C().minTime(new int[]{2,1,1,1,7,1}, new int[]{5,6,4,9,1}));
    }
public long minTime(int[] skill, int[] mana) {
    int n = skill.length;
    int m = mana.length;
    long curr[] = new long[n+1];
    for(int i=1;i<=n;i++){
        curr[i] = curr[i-1] + 1l*skill[i-1]*mana[0];
    }

    for(int i=1;i<m;i++){
        long temp[] = new long[n+1];
        temp[0] = curr[1];
        for(int j=1;j<=n;j++){
            long multi = 1l*skill[j-1]*mana[i];
            long a = temp[j-1] + multi;
            long b = curr[j] + multi;
            long c = j+1 <=n ? curr[j+1] : 0;

            temp[j] = Math.max(a, Math.max(b, c));
        }

        for(int j=n-1;j>=0;j--){
            temp[j] = temp[j+1] - 1l*skill[j] * mana[i];
        }

        curr = temp;
    }


    return curr[n];

}
}