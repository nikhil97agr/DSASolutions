package Leetcode;

import java.util.ArrayList;
import java.util.List;

//Problem Link: https://leetcode.com/problems/range-xor-queries-with-subarray-reversals/

/**
 * Solution for range XOR queries with subarray reversal operations.
 *
 * Problem: Given an array and queries of three types: 1. Type 1 [1, index, value]: Update nums[index] = value 2. Type 2
 * [2, left, right]: Query XOR of nums[left..right] 3. Type 3 [3, left, right]: Reverse subarray nums[left..right]
 *
 * Example: nums = [1, 2, 3, 4] - Query [3, 1, 2]: Reverse nums[1..2] → [1, 3, 2, 4] - Query [2, 0, 3]: XOR of [1, 3, 2,
 * 4] → 1^3^2^4 = 4
 *
 * Key insight: AVL Tree with lazy propagation
 *
 * Why AVL Tree? - Supports efficient split and join operations: O(log n) - Maintains balance automatically - Allows
 * range reversals by splitting, flipping, and joining
 *
 * Data stored in each node: - val: value at this node (represents array element) - xor: XOR of all elements in subtree
 * - size: number of elements in subtree - height, balance: AVL tree properties - reverse: lazy flag indicating if
 * subtree needs reversal
 *
 * Core operations: 1. split(index): Split tree at index into [0..index-1], [index], [index+1..n-1] 2. join(left, this,
 * right): Join three parts into single tree 3. flip(): Toggle lazy reverse flag 4. reverse(): Apply pending reversal
 * (swap children, propagate flag)
 *
 * Reversal strategy (Type 3 query): - Split at position 'right' → [left_part, mid1, right_part] - Split left_part at
 * 'left' → [before, mid2, to_reverse] - Flip 'to_reverse' (mark for lazy reversal) - Join back: [before, reversed,
 * mid1, after]
 *
 * Time Complexity: O(q × log n) for q queries Space Complexity: O(n) for AVL tree
 */
public class RangeXorQueriesWithSubarrayReversals {

    /**
     * Processes all queries on the array.
     *
     * @param nums Initial array
     * @param queries Array of queries [type, params...]
     * @return Results for Type 2 queries (XOR queries)
     */
    public int[] getResults(int[] nums, int[][] queries) {

        // ================================================================
        // Initialize AVL tree from array
        // ================================================================
        AvlTree root = new AvlTree(nums, 0, nums.length - 1);
        List<Integer> result = new ArrayList<>();

        // ================================================================
        // Process each query
        // ================================================================
        for (var q : queries) {
            if (q[0] == 1) {
                // ========================================================
                // Type 1: Point update
                // ========================================================
                root.update(q[1], q[2]);

            } else if (q[0] == 2) {
                // ========================================================
                // Type 2: Range XOR query
                // ========================================================
                result.add(root.query(q[1], q[2]));

            } else if (q[1] < q[2]) {
                // ========================================================
                // Type 3: Range reversal (only if left < right)
                // ========================================================
                // Split at q[2]: [elements before q[2]], element at q[2], [rest]
                SplitResult r1 = root.split(q[2]);

                // Split left part at q[1]: [before q[1]], element at q[1], [to reverse]
                SplitResult r2 = r1.left != null ? r1.left.split(q[1]) : new SplitResult(null, null, null);

                var left = r2.left;    // Elements [0, q[1]-1]
                var right = r2.right;  // Elements [q[1]+1, q[2]-1] - to be reversed

                // Mark the middle part for reversal
                if (right != null) {
                    right.flip();  // Toggle lazy reverse flag
                }

                // Join: [left] + [reversed_right] + element at q[1]
                var joinedLeft = r1.mid.join(left, right);

                // Join: [joinedLeft] + element at q[2] + [remaining right part]
                root = r2.mid.join(joinedLeft, r1.right);
            }
        }

        return result.stream().mapToInt(x -> x).toArray();
    }
}

/**
 * AVL Tree node with support for range operations and lazy reversal.
 *
 * Augmented AVL tree where each node represents an array element and maintains aggregate information (XOR, size) for
 * its subtree.
 *
 * In-order traversal gives the array in logical order.
 */
class AvlTree {

    int size;       // Number of elements in subtree
    int height;     // Height of subtree (for AVL balancing)
    int balance;    // Balance factor (left.height - right.height)
    int val;        // Value stored at this node
    int xor;        // XOR of all elements in subtree
    boolean reverse; // Lazy flag: true if subtree needs reversal
    AvlTree left, right; // Children

    /**
     * Constructs balanced AVL tree from array segment.
     *
     * Builds tree recursively by choosing middle element as root to ensure initial balance.
     *
     * @param arr Array to build from
     * @param l Left boundary (inclusive)
     * @param r Right boundary (inclusive)
     */
    public AvlTree(int arr[], int l, int r) {

        // Choose middle element as root for balance
        var mid = l + (r - l) / 2;
        val = arr[mid];

        // Build left subtree from [l, mid-1]
        if (l < mid) {
            left = new AvlTree(arr, l, mid - 1);
        }

        // Build right subtree from [mid+1, r]
        if (mid < r) {
            right = new AvlTree(arr, mid + 1, r);
        }

        // Update aggregate values (size, xor, height, balance)
        update();
    }

    /**
     * Splits tree at given index into three parts.
     *
     * Result: - left: elements [0, ind-1] - mid: element at index ind - right: elements [ind+1, n-1]
     *
     * Strategy: - Apply pending reversals first - Compare index with left subtree size - Recursively split appropriate
     * subtree - Join pieces appropriately
     *
     * Example: Tree [1,2,3,4,5], split at index 2 - Returns: left=[1,2], mid=3, right=[4,5]
     *
     * @param ind Index to split at (0-based)
     * @return SplitResult with three parts
     */
    public SplitResult split(int ind) {

        // Apply any pending reversal before splitting
        reverse();
        int leftSize = left == null ? 0 : left.size;

        // ================================================================
        // Case 1: Split point is exactly at current node
        // ================================================================
        if (leftSize == ind) {
            AvlTree l = left, r = right;
            left = null;
            right = null;
            return new SplitResult(l, this, r);
        }

        // ================================================================
        // Case 2: Split point is in left subtree
        // ================================================================
        if (leftSize > ind) {
            SplitResult res = left.split(ind);
            // Current node goes to right part with original right child
            return new SplitResult(res.left, res.mid, join(res.right, right));
        }

        // ================================================================
        // Case 3: Split point is in right subtree
        // ================================================================
        SplitResult res = right.split(ind - leftSize - 1);
        // Current node goes to left part with original left child
        return new SplitResult(join(left, res.left), res.mid, res.right);
    }

    /**
     * Joins two trees with current node as root.
     *
     * Creates tree: [left] + [this] + [right] Maintains AVL balance by inserting at appropriate height.
     *
     * Strategy: - If trees have similar heights: make this the root directly - If one tree is much taller: insert into
     * that tree recursively - Rebalance as needed
     *
     * Example: join([1,2], 3, [4,5]) - Result: balanced tree representing [1,2,3,4,5]
     *
     * @param left Left subtree (smaller indices)
     * @param right Right subtree (larger indices)
     * @return Root of joined tree
     */
    public AvlTree join(AvlTree left, AvlTree right) {

        int leftHeight = left == null ? 0 : left.height;
        int rightHeight = right == null ? 0 : right.height;

        // ================================================================
        // Case 1: Right tree is much taller
        // ================================================================
        if (rightHeight > leftHeight + 1) {
            right.reverse();  // Apply pending reversals
            // Insert recursively into right tree's left side
            right.left = join(left, right.left);
            right.rebalance();
            return right;
        }

        // ================================================================
        // Case 2: Left tree is much taller
        // ================================================================
        if (leftHeight > rightHeight + 1) {
            left.reverse();  // Apply pending reversals
            // Insert recursively into left tree's right side
            left.right = join(left.right, right);
            left.rebalance();
            return left;
        }

        // ================================================================
        // Case 3: Trees have similar heights
        // ================================================================
        // Make current node the root with left and right as children
        this.left = left;
        this.right = right;
        update();
        return this;
    }

    /**
     * Updates value at given index.
     *
     * Point update using tree structure: - Apply pending reversals first - Navigate to correct position using left
     * subtree size - Update aggregate values on way back up
     *
     * @param index Index to update (0-based in logical array)
     * @param val New value
     */
    public void update(int index, int val) {

        reverse();  // Apply pending reversal
        int leftSize = left == null ? 0 : left.size;

        if (leftSize == index) {
            // Found the target index at current node
            this.val = val;
        } else if (leftSize > index) {
            // Target is in left subtree
            left.update(index, val);
        } else {
            // Target is in right subtree (adjust index)
            right.update(index - leftSize - 1, val);
        }

        // Update aggregate values (xor, size, etc.)
        update();
    }

    /**
     * Queries XOR of elements in range [l, r].
     *
     * Range query using tree structure: - If range covers entire subtree, return cached xor - Otherwise, apply
     * reversals and navigate to correct ranges - Combine XOR from left, current, and right as needed
     *
     * Example: Query [1, 4] in tree representing [1,2,3,4,5] - If root is 3 (index 2): need left[1,1] XOR root XOR
     * right[0,1]
     *
     * @param l Left boundary (inclusive, 0-based)
     * @param r Right boundary (inclusive, 0-based)
     * @return XOR of elements in range [l, r]
     */
    public int query(int l, int r) {

        // Optimization: if range covers entire subtree, return cached xor
        if (r - l + 1 == size) {
            return xor;
        }

        reverse();  // Apply pending reversal
        int leftSize = left == null ? 0 : left.size;

        // Case 1: Range entirely in left subtree
        if (r < leftSize) {
            return left.query(l, r);
        }

        // Case 2: Range entirely in right subtree
        if (l > leftSize) {
            return right.query(l - leftSize - 1, r - leftSize - 1);
        }

        // Case 3: Range spans multiple parts
        int ans = val;  // Include current node's value

        // Include left part if range starts before current node
        if (l < leftSize) {
            ans ^= left.query(l, leftSize - 1);
        }

        // Include right part if range extends past current node
        if (r > leftSize) {
            ans ^= right.query(0, r - leftSize - 1);
        }

        return ans;
    }


    /**
     * Rebalances AVL tree using rotations.
     *
     * AVL balance property: |balance| ≤ 1 Balance = left.height - right.height
     *
     * Four cases: 1. Left-Left (balance = 2, left.balance ≥ 0): Right rotate 2. Left-Right (balance = 2, left.balance =
     * -1): Left rotate left child, then right rotate 3. Right-Right (balance = -2, right.balance ≤ 0): Left rotate 4.
     * Right-Left (balance = -2, right.balance = 1): Right rotate right child, then left rotate
     *
     * Must apply pending reversals before checking balance and rotating.
     */
    public void rebalance() {

        update();  // Recompute balance

        // ================================================================
        // Left subtree too tall (Left-Left or Left-Right case)
        // ================================================================
        if (balance == 2) {
            left.reverse();  // Apply pending reversals

            // Left-Right case: need double rotation
            if (left.balance == -1) {
                left.right.reverse();
                left.leftRotate();  // Convert to Left-Left
            }
            rightRotate();  // Perform right rotation

        } else if (balance == -2) {
            // ================================================================
            // Right subtree too tall (Right-Right or Right-Left case)
            // ================================================================
            right.reverse();  // Apply pending reversals

            // Right-Left case: need double rotation
            if (right.balance == 1) {
                right.left.reverse();
                right.rightRotate();  // Convert to Right-Right
            }
            leftRotate();  // Perform left rotation
        }
    }


    /**
     * Applies pending reversal to this node.
     *
     * Lazy propagation: - If reverse flag is false, do nothing - Otherwise: 1. Swap left and right children 2. Negate
     * balance (since left/right are swapped) 3. Propagate reverse flag to children 4. Clear this node's reverse flag
     *
     * Why swap children? - In-order traversal gives: left → this → right - After swap: right → this → left (reversed
     * order) - Children also need reversal, so propagate flag
     *
     * Why negate balance? - balance = left.height - right.height - After swap: balance = right.height - left.height =
     * -(old balance)
     *
     * Example: Tree [1,2,3,4,5] with root 3 - Before: left=[1,2], right=[4,5] - After reverse: left=[4,5], right=[1,2]
     * - In-order now: [5,4] → 3 → [2,1] = [5,4,3,2,1]
     */
    public void reverse() {

        if (reverse) {
            // Swap left and right children
            AvlTree left = this.left;
            AvlTree right = this.right;
            this.right = left;
            this.left = right;

            // Propagate reverse flag to children
            if (left != null) {
                left.flip();
            }
            if (right != null) {
                right.flip();
            }

            // Negate balance since children are swapped
            balance = -balance;

            // Clear reverse flag
            reverse = false;
        }
    }

    /**
     * Toggles the lazy reverse flag.
     *
     * Marks this subtree for reversal without immediately applying it. Reversal will be applied lazily when needed.
     */
    public void flip() {

        reverse = !reverse;
    }


    /**
     * Performs right rotation at current node by value swapping.
     *
     * Optimized rotation that swaps values instead of changing parent pointers. Avoids need to update parent
     * references.
     *
     * Used to fix Left-Left imbalance.
     */
    public void rightRotate() {

        int lVal = left.val, rVal = val;
        AvlTree prevLeft = left;
        left = prevLeft.left;
        prevLeft.left = prevLeft.right;
        prevLeft.right = right;
        right = prevLeft;
        right.val = rVal;
        val = lVal;
        right.update();
        update();
    }

    /**
     * Performs left rotation at current node by value swapping.
     *
     * Optimized rotation that swaps values instead of changing parent pointers. Avoids need to update parent
     * references.
     *
     * Used to fix Right-Right imbalance.
     */
    public void leftRotate() {

        int lVal = this.val;
        int rVal = right.val;
        AvlTree prevRight = right;
        right = prevRight.right;
        prevRight.right = prevRight.left;
        prevRight.left = left;
        left = prevRight;
        left.val = lVal;
        val = rVal;
        left.update();
        update();
    }

    /**
     * Updates aggregate values from children.
     *
     * Recalculates: - xor: XOR of all elements in subtree - size: Total elements in subtree - balance: Left height -
     * right height - height: Max of children heights + 1
     *
     * Called after any structural change to maintain invariants.
     */
    public void update() {

        int leftXor = 0, rightXor = 0, leftSize = 0, rightSize = 0, leftHeight = 0, rightHeight = 0;

        if (left != null) {
            leftXor = left.xor;
            leftSize = left.size;
            leftHeight = left.height;
        }

        if (right != null) {
            rightXor = right.xor;
            rightSize = right.size;
            rightHeight = right.height;
        }

        // XOR of subtree = left XOR this XOR right
        xor = leftXor ^ rightXor ^ val;

        // Size of subtree = left + 1 + right
        size = leftSize + rightSize + 1;

        // Height of subtree
        height = Math.max(leftHeight, rightHeight) + 1;

        // Balance factor for AVL property
        balance = leftHeight - rightHeight;

    }
}

class SplitResult {

    AvlTree left, right, mid;

    public SplitResult(AvlTree left, AvlTree mid, AvlTree right) {

        this.left = left;
        this.mid = mid;
        this.right = right;
    }
}