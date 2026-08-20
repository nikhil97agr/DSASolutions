package Leetcode;
//Problem Link: https://leetcode.com/problems/number-of-pairs-after-increment/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Solution - Number of Pairs with Sqrt Decomposition and Lazy Propagation
 *
 * PROBLEM STATEMENT:
 * Given two arrays nums1 and nums2, process queries of two types:
 * 1. Type 1 (Update): Add value to nums2[x..y] (range update)
 * 2. Type 2 (Query): Count pairs (i,j) where nums1[i] + nums2[j] == total
 *
 * APPROACH - SQRT DECOMPOSITION WITH LAZY PROPAGATION:
 *
 * The challenge is handling range updates efficiently while being able to query
 * quickly. Direct approach would be O(Q * n * m) which is too slow.
 *
 * KEY TECHNIQUE - SQRT DECOMPOSITION:
 * 1. Divide nums2 into blocks of size √n
 * 2. For each block, maintain a frequency map of values
 * 3. Use lazy propagation for complete block updates
 * 4. For partial blocks, update individual elements
 *
 * LAZY PROPAGATION:
 * - When entire block is updated, just store the delta in lazy[blockId]
 * - No need to update individual elements until necessary
 * - When querying, adjust the required value by lazy offset
 *
 * TIME COMPLEXITY:
 * - Type 1 (Update): O(√n) - at most 2 partial blocks + O(1) for full blocks
 * - Type 2 (Query): O(m * √n) where m = nums1.length
 * - Overall: O(Q * m * √n)
 *
 * SPACE COMPLEXITY: O(n + √n) for maps and lazy array
 */
public class NumberOfPairsAfterIncrement{

    /**
     * Process queries to count pairs and handle range updates.
     *
     * @param nums1 first array (values to add with nums2 elements)
     * @param nums2 second array (supports range updates)
     * @param queries array of queries: [type, ...params]
     *                Type 1: [1, x, y, val] - add val to nums2[x..y]
     *                Type 2: [2, total] - count pairs summing to total
     * @return array of counts for each Type 2 query
     */
    public int[] numberOfPairs(int[] nums1, int[] nums2, int[][] queries) {

        int n = nums2.length;
        var result = new ArrayList<Integer>();

        // SQRT DECOMPOSITION SETUP
        // Divide array into blocks of size √n for efficient range operations
        int blockSize = (int) Math.sqrt(n);

        // Calculate number of blocks needed (ceiling division)
        int totalBlocks = (n + blockSize - 1) / blockSize;

        // INITIALIZATION
        // Create frequency map for each block
        // blockFrequencyMaps[i] stores frequency of each value in block i
        @SuppressWarnings("unchecked")
        Map<Long, Integer> blockFrequencyMaps[] = new HashMap[totalBlocks];

        // Transform nums2 to long to avoid overflow issues
        // currentValues[i] = current value at index i (may be updated by queries)
        long[] currentValues = Arrays.stream(nums2).mapToLong(x -> 1L * x).toArray();

        // Build initial frequency maps for each block
        for (int blockId = 0; blockId < totalBlocks; blockId++) {
            blockFrequencyMaps[blockId] = new HashMap<>();

            // Calculate range for this block: [blockStart, blockEnd]
            int blockStart = blockId * blockSize;
            int blockEnd = Math.min((blockId + 1) * blockSize, n) - 1;

            // Add all values in this block to its frequency map
            for (int idx = blockStart; idx <= blockEnd; idx++) {
                addToFrequencyMap(blockFrequencyMaps[blockId], currentValues[idx]);
            }
        }

        // LAZY PROPAGATION ARRAY
        // lazyDelta[blockId] = cumulative delta to add to all elements in block blockId
        // This allows O(1) updates for complete blocks
        long[] lazyDelta = new long[totalBlocks];

        // PROCESS QUERIES
        for (int[] query : queries) {
            int queryType = query[0];

            if (queryType == 1) {
                // ========================================================================
                // TYPE 1: RANGE UPDATE - Add val to nums2[rangeStart..rangeEnd]
                // ========================================================================
                int rangeStart = query[1];
                int rangeEnd = query[2];
                long valueToAdd = query[3];

                // Determine which blocks are affected by the range [rangeStart, rangeEnd]
                int firstBlock = rangeStart / blockSize;
                int lastBlock = rangeEnd / blockSize;

                // Process each affected block
                for (int blockId = firstBlock; blockId <= lastBlock; blockId++) {
                    int blockStart = blockId * blockSize;
                    int blockEnd = Math.min((blockId + 1) * blockSize, n) - 1;

                    // CASE 1: Entire block is within update range
                    // Use lazy propagation - just increment the lazy delta
                    if (rangeStart <= blockStart && blockEnd <= rangeEnd) {
                        lazyDelta[blockId] += valueToAdd;
                    }
                    // CASE 2: Partial block overlap
                    // Must update individual elements and their frequency maps
                    else {
                        for (int idx = blockStart; idx <= blockEnd; idx++) {
                            // Only update if index is within the query range
                            if (rangeStart <= idx && idx <= rangeEnd) {
                                // Remove old value from frequency map
                                removeFromFrequencyMap(blockFrequencyMaps[blockId], currentValues[idx]);

                                // Update the value
                                currentValues[idx] += valueToAdd;

                                // Add new value to frequency map
                                addToFrequencyMap(blockFrequencyMaps[blockId], currentValues[idx]);
                            }
                        }
                    }
                }
            }
            else {
                // ========================================================================
                // TYPE 2: COUNT PAIRS - Count pairs where nums1[i] + nums2[j] == targetSum
                // ========================================================================
                long targetSum = query[1];

                int pairCount = 0;

                // For each element in nums1
                for (int num1Value : nums1) {
                    // Calculate required value from nums2: nums2[j] = targetSum - nums1[i]
                    long requiredValue = targetSum - num1Value;

                    // Check each block for elements equal to requiredValue
                    for (int blockId = 0; blockId < totalBlocks; blockId++) {
                        // CRITICAL: Adjust for lazy propagation
                        // If block has lazy delta, the actual values are shifted
                        // So we need to look for (requiredValue - lazyDelta) in the frequency map
                        long adjustedRequiredValue = requiredValue - lazyDelta[blockId];

                        // Add count of elements with adjustedRequiredValue in this block
                        pairCount += blockFrequencyMaps[blockId].getOrDefault(adjustedRequiredValue, 0);
                    }
                }

                result.add(pairCount);
            }
        }

        // Convert ArrayList<Integer> to int[]
        return result.stream().mapToInt(count -> count).toArray();
    }

    /**
     * Remove one occurrence of a value from the frequency map.
     *
     * If the count becomes 0, remove the key entirely to save space.
     *
     * @param frequencyMap the frequency map to update
     * @param value the value to remove one occurrence of
     */
    private void removeFromFrequencyMap(Map<Long, Integer> frequencyMap, long value) {
        // Decrement count by 1 (merge with -1)
        frequencyMap.merge(value, -1, Integer::sum);

        // If count reaches 0, remove the key from map
        if (frequencyMap.get(value) == 0) {
            frequencyMap.remove(value);
        }
    }

    /**
     * Add one occurrence of a value to the frequency map.
     *
     * If the value doesn't exist, initialize count to 1.
     * If it exists, increment the count by 1.
     *
     * @param frequencyMap the frequency map to update
     * @param value the value to add one occurrence of
     */
    private void addToFrequencyMap(Map<Long, Integer> frequencyMap, long value) {
        // Increment count by 1 (merge with +1)
        // If key doesn't exist, initialize with 1
        // If key exists, add 1 to existing count
        frequencyMap.merge(value, 1, Integer::sum);
    }
}

/*
 * EXAMPLE WALKTHROUGH:
 *
 * nums1 = [1, 2, 3]
 * nums2 = [4, 5, 6, 7]
 * queries = [[1, 0, 2, 10], [2, 15], [2, 20]]
 *
 * blockSize = sqrt(4) = 2
 * totalBlocks = 2
 * Block 0: indices [0, 1] → values [4, 5]
 * Block 1: indices [2, 3] → values [6, 7]
 *
 * Initial state:
 * blockFrequencyMaps[0] = {4: 1, 5: 1}
 * blockFrequencyMaps[1] = {6: 1, 7: 1}
 * lazyDelta = [0, 0]
 *
 * Query 1: [1, 0, 2, 10] - Add 10 to nums2[0..2]
 * - Block 0 (indices 0-1): Fully covered → lazyDelta[0] = 10
 * - Block 1 (indices 2-3): Partially covered (only index 2)
 *   → Update index 2: remove 6, add 16 to map[1]
 *
 * State after Query 1:
 * blockFrequencyMaps[0] = {4: 1, 5: 1} (unchanged, using lazy)
 * blockFrequencyMaps[1] = {16: 1, 7: 1}
 * lazyDelta = [10, 0]
 * currentValues = [4, 5, 16, 7] (actual values: [14, 15, 16, 7])
 *
 * Query 2: [2, 15] - Count pairs where nums1[i] + nums2[j] = 15
 * - For nums1[0] = 1: need nums2[j] = 14
 *   Block 0: look for 14 - 10 = 4 → found 1 ✓
 *   Block 1: look for 14 - 0 = 14 → not found
 * - For nums1[1] = 2: need nums2[j] = 13
 *   Block 0: look for 13 - 10 = 3 → not found
 *   Block 1: look for 13 - 0 = 13 → not found
 * - For nums1[2] = 3: need nums2[j] = 12
 *   Block 0: look for 12 - 10 = 2 → not found
 *   Block 1: look for 12 - 0 = 12 → not found
 * Result: 1 pair
 */