package Leetcode;

//Problem Link : https://leetcode.com/problems/partition-array-into-two-arrays-to-minimize-sum-difference/

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

//Solved using Meet in the Middle technique
public class PartitionArrayMinimizeSumDifference {
    public int minimumDifference(int[] nums) {
        int n = nums.length;
        int setA[] = new int[n / 2];
        int setB[] = new int[n / 2];
        int total = 0;

        // divide the array into two halfs
        total = generateSubset(nums, 0, n / 2, setA);
        total += generateSubset(nums, n / 2, n, setB);

        Map<Integer, List<Integer>> subsetSumA = new HashMap<>();
        Map<Integer, List<Integer>> subsetSumB = new HashMap<>();

        // generate all the possible subset sum of each f the halves
        generateSubsetSum(0, n / 2, subsetSumA, 0, 0, setA);
        generateSubsetSum(0, n / 2, subsetSumB, 0, 0, setB);

        // Sort the sums of each possible length so that it'll be easy to search using
        // binary search algo
        for (Map.Entry<Integer, List<Integer>> entry : subsetSumB.entrySet()) {
            Collections.sort(entry.getValue());
        }

        int ans = Integer.MAX_VALUE;

        for (int i = 0; i <= n / 2; i++) {
            for (int x : subsetSumA.get(i)) {
                /*
                 * target = total - 2*x. why?
                 * to find the abs(sum1 - sum2), since sum2 = total - sum1
                 * i.e. abs(sum1 - (total-sum1)) => abs(2*sum1 - total)
                 * now, for min possible absolute value the difference inside abs() = 0
                 * => 2*sum1 = total
                 * sum1 will be equal to sum of corresponding subset sums of first half & second
                 * half
                 * i.e. sum1 = x + valB => 2*(x+valB) = total
                 * => valB = (total - 2*x)/2
                 * 
                 */
                int target = (total - 2 * x) / 2;
                int bSize = (n / 2) - i;
                int valB = binarySearch(subsetSumB.get(bSize), target);

                ans = Math.min(ans, Math.abs(2 * (x + valB) - total));
            }
        }

        return ans;

    }

    private int binarySearch(List<Integer> list, int target) {
        int start = 0;
        int end = list.size() - 1;
        while (start + 1 < end) {
            int mid = start + (end - start) / 2;

            if (list.get(mid) <= target) {
                start = mid;
            } else {
                end = mid;
            }
        }
        // find the number which will be closest to the target
        return Math.abs(list.get(start) - target) > Math.abs(list.get(end) - target) ? list.get(end) : list.get(start);
    }

    private void generateSubsetSum(int i, int n, Map<Integer, List<Integer>> subsetSum, int total, int cnt,
            int nums[]) {
        if (i == n) {
            subsetSum.computeIfAbsent(cnt, len -> new ArrayList<>()).add(total);
            return;
        }

        generateSubsetSum(i + 1, n, subsetSum, total + nums[i], cnt + 1, nums);
        generateSubsetSum(i + 1, n, subsetSum, total, cnt, nums);
    }

    private int generateSubset(int nums[], int start, int end, int set[]) {
        int sum = 0;
        for (int i = start; i < end; i++) {
            sum += nums[i];
            set[i - start] = nums[i];
        }

        return sum;
    }

}
