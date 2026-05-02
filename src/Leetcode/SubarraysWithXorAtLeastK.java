package Leetcode;//Problem Link: https://leetcode.com/problems/subarrays-with-xor-at-least-k

/**
 * Solution for counting subarrays with XOR at least k.
 *
 * Problem: Given an array nums and integer k, count how many subarrays have XOR >= k.
 *
 * Key insights: 1. Subarray XOR property: XOR of subarray [i, j] = prefix_xor[j] ^ prefix_xor[i-1] - Where
 * prefix_xor[j] = nums[0] ^ nums[1] ^ ... ^ nums[j] 2. To find subarrays ending at index j with XOR >= k: - We need to
 * count how many previous prefix XORs p satisfy: (current_xor ^ p) >= k 3. Use a Binary Trie to efficiently count
 * prefix XORs that satisfy the condition
 *
 * Algorithm: 1. Maintain a trie of all prefix XOR values seen so far 2. For each new element, compute current prefix
 * XOR 3. Query the trie to count how many previous prefixes give XOR >= k 4. Insert current prefix XOR into trie for
 * future queries
 *
 * Example: nums = [1, 2, 3], k = 2 - Subarrays: [1]=1, [2]=2, [3]=3, [1,2]=3, [2,3]=1, [1,2,3]=0 - XOR >= 2: [2]=2,
 * [3]=3, [1,2]=3 - Answer: 3
 *
 * Time Complexity: O(n * 32) = O(n) where n is array length, 32 is bit length Space Complexity: O(n * 32) for the trie
 */
public class SubarraysWithXorAtLeastK {

    /**
     * Counts the number of subarrays with XOR at least k.
     *
     * @param nums Input array
     * @param k Minimum XOR threshold
     * @return Count of subarrays with XOR >= k
     */
    public long countXorSubarrays(int[] nums, int k) {

        Trie trie = new Trie();
        int xor = 0;  // Running prefix XOR

        // Insert 0 to handle subarrays starting from index 0
        // (empty prefix has XOR = 0)
        trie.insert(0);

        long ans = 0;

        // Process each element
        for (int x : nums) {
            // Update prefix XOR
            xor ^= x;

            // Count how many previous prefix XORs p satisfy: (xor ^ p) >= k
            // This gives us the count of subarrays ending at current index with XOR >= k
            ans += trie.query(xor, k);

            // Insert current prefix XOR for future queries
            trie.insert(xor);
        }

        return ans;
    }

    /**
     * Binary Trie data structure for storing integers and querying XOR properties.
     *
     * Structure: - Each node has two children: trie[0] for bit 0, trie[1] for bit 1 - Stores integers from most
     * significant bit (bit 31) to least significant (bit 0) - Each node tracks count of numbers passing through it
     *
     * This enables efficient querying of "how many stored numbers give XOR >= k"
     */
    class Trie {

        Trie trie[];  // Children: trie[0] for bit 0, trie[1] for bit 1
        int cnt;      // Count of numbers that pass through this node

        /**
         * Constructor for Trie node.
         */
        public Trie() {

            trie = new Trie[2];  // Binary tree (0 and 1)
            cnt = 0;             // Initially no numbers stored
        }

        /**
         * Inserts a value into the trie.
         *
         * Builds the binary representation from MSB to LSB and increments count at each node along the path.
         *
         * @param val Value to insert (prefix XOR)
         */
        public void insert(int val) {

            Trie root = this;

            // Process bits from most significant (31) to least significant (0)
            for (int i = 31; i >= 0; i--) {
                // Extract the i-th bit of val
                int bit = (val >> i) & 1;

                // Create child node if it doesn't exist
                if (root.trie[bit] == null) {
                    root.trie[bit] = new Trie();
                }

                // Move to child node
                root = root.trie[bit];

                // Increment count: one more number passes through this node
                root.cnt++;
            }

        }

        /**
         * Queries the trie to count how many stored values p satisfy: (prefix ^ p) >= k.
         *
         * Strategy: Build the XOR result bit by bit from MSB to LSB - For each bit position, decide which path to take
         * based on k's bit - If k's bit is 1, we MUST go the path that gives 1 in XOR result - If k's bit is 0, we CAN
         * go either path: * Path giving 1 in XOR: these definitely satisfy >= k (count them all) * Path giving 0 in
         * XOR: continue checking remaining bits
         *
         * Example: prefix = 5 (101), k = 3 (011), finding p where (5 ^ p) >= 3 - Bit 31-2: k=0, so we can add count
         * from paths that differ, then continue on same path - Bit 1: k=1, prefix=0, so we MUST go to path where p has
         * bit 1 (to get XOR=1) - Bit 0: k=1, prefix=1, so we MUST go to path where p has bit 0 (to get XOR=1)
         *
         * @param prefix Current prefix XOR value
         * @param k Threshold value
         * @return Count of stored values p such that (prefix ^ p) >= k
         */
        public int query(int prefix, int k) {

            Trie root = this;

            int ans = 0;

            // Process bits from most significant (31) to least significant (0)
            for (int i = 31; i >= 0 && root != null; i--) {
                int prefixBit = (prefix >> i) & 1;  // i-th bit of prefix
                int kBit = (k >> i) & 1;            // i-th bit of k

                // Case 1: k's i-th bit is 1
                // We NEED the XOR result to have bit 1 at position i (to be >= k)
                // XOR gives 1 when bits differ, so we need p's bit != prefix's bit
                if (kBit == 1) {
                    // Go to the path where p's bit is different from prefix's bit
                    root = root.trie[1 - prefixBit];
                }
                // Case 2: k's i-th bit is 0
                // We're still >= k so far, continue checking remaining bits
                else {
                    // Option A: p's bit differs from prefix's bit → XOR gives 1
                    // All numbers in this subtree will have XOR >= k at this bit position
                    // So we can count ALL of them (no need to check remaining bits)
                    if (root.trie[1 - prefixBit] != null) {
                        ans += root.trie[1 - prefixBit].cnt;
                    }

                    // Option B: p's bit equals prefix's bit → XOR gives 0
                    // Need to continue checking remaining bits
                    root = root.trie[prefixBit];
                }
            }

            // After processing all bits, if we haven't gone to null,
            // all remaining numbers in this subtree give XOR = k exactly
            // (or we've been taking the "same bit" path which means XOR < k at every bit)
            // Actually, if we reach here, it means XOR equals k exactly or is in valid range
            if (root != null) {
                ans += root.cnt;
            }

            return ans;
        }
    }
}