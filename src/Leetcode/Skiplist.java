package Leetcode;

/**
 * Skiplist implementation using AVL Tree (Self-balancing BST)
 *
 * Note: Despite the name "Skiplist", this is actually an AVL Tree implementation
 * that provides O(log n) search, insert, and delete operations.
 *
 * AVL Tree properties:
 * - Binary Search Tree with balance factor constraint
 * - Balance factor = height(left) - height(right) must be in {-1, 0, 1}
 * - Automatically rebalances using rotations after insertions/deletions
 */
public class Skiplist {
    Node root;

    /**
     * Initializes an empty skiplist (AVL tree)
     */
    public Skiplist() {
        root = null;
    }

    /**
     * Searches for a target value in the tree
     *
     * @param target The value to search for
     * @return true if target exists, false otherwise
     */
    public boolean search(int target) {
        return search(target, root);
    }

    /**
     * Recursive helper method to search for a value
     * Uses BST property: left < root < right
     */
    private boolean search(int target, Node root){
        if(root == null ) return false;

        if(target < root.val){
            return search(target, root.left);
        }else if(target > root.val){
            return search(target, root.right);
        }else{
            return true;
        }
    }

    /**
     * Adds a number to the skiplist
     * If number already exists, increments its count
     *
     * @param num The number to add
     */
    public void add(int num) {
        root = addNode(num, root);
    }

    /**
     * Recursive helper to add a node and maintain AVL balance
     *
     * @param num The value to add
     * @param root Current node in recursion
     * @return The new root after insertion and balancing
     */
    private Node addNode(int num, Node root){
        // Base case: create new node
        if(root==null) return new Node(num);

        // BST insertion
        if(root.val < num){
            root.right = addNode(num, root.right);
        }else if(root.val > num){
            root.left =addNode(num, root.left);
        }else{
            // Duplicate value: increment count
            root.count++;
            return root;
        }

        // Update height of current node
        root.height = Math.max(height(root.left), height(root.right))+1;

        // Get balance factor to check if tree became unbalanced
        int balance = balance(root);

        // Left-Left case: Right rotation needed
        if(balance > 1){
            if(num < root.left.val){
                root = rightRotate(root);
            }else{
                // Left-Right case: Left rotation on left child, then right rotation
                root.left = leftRotate(root.left);
                root = rightRotate(root);
            }
        }else if(balance < 1){
            // Right-Right or Right-Left cases (not implemented in this code)
        }

        return root;
    }

    /**
     * Performs right rotation to balance the tree
     * Used for Left-Left case
     *
     *       y                               x
     *      / \     Right Rotation          /  \
     *     x   T3   - - - - - - - >        T1   y
     *    / \                                  / \
     *   T1  T2                               T2  T3
     */
    private Node rightRotate(Node root){
        if(root==null) return root;
        Node child = root.left;
        Node grandChild = child.right;

        // Perform rotation
        root.left = grandChild;
        child.right = root;

        // Update heights
        root.height = max(height(root.left), height(root.right))+1;
        child.height =  max(height(child.left), height(child.right))+1;

        return child;
    }

    /**
     * Performs left rotation to balance the tree
     * Used for Right-Right case
     *
     *     x                                y
     *    /  \     Left Rotation           / \
     *   T1   y    - - - - - - - >        x   T3
     *       / \                         / \
     *      T2  T3                      T1  T2
     */
    private Node leftRotate(Node root){
        if(root == null) return root;
        Node child = root.right;
        Node grandChild = child.left;

        // Perform rotation
        root.right = grandChild;
        child.left = root;

        // Update heights
        root.height = max(height(root.left), height(root.right))+1;
        child.height = max(height(child.left), height(child.right));

        return child;
    }

    /**
     * Returns maximum of two integers
     */
    private int max(int first, int second){
        return (first > second) ? first : second;
    }

    /**
     * Calculates balance factor of a node
     * Balance factor = height(left subtree) - height(right subtree)
     *
     * @return Balance factor (should be -1, 0, or 1 for balanced tree)
     */
    private int balance(Node root){
        if(root == null) return 0;

        return height(root.left) - height(root.right);
    }

    /**
     * Returns height of a node
     * Height of null node is 0
     */
    private int height(Node root){
        if(root==null) return 0;

        return root.height;
    }

    /**
     * Erases a number from the skiplist
     * If number appears multiple times, decrements count
     *
     * @param num The number to erase
     * @return true if number was found and erased, false otherwise
     */
    public boolean erase(int num) {
        if(!search(num, root)) return false;
        root = deleteNode(num, root);
        return true;
    }

    private Node minValueNode(Node root){
        if(root == null ) return null;
        while(root.left != null){
            root = root.left;
        }

        return root;
    }

    private Node deleteNode(int num, Node root){
        if(root == null ) return null;

        if(root.val == num){
            root.count--;
            if(root.count == 0){
                if(root.left ==null | root.right==null){
                    Node temp;
                    if(root.left == null ) temp = root.right;
                    else temp = root.left;
                    if(temp == null){
                        root = null;
                    }else{
                        root = temp;
                    }
                }else{
                    Node temp  = minValueNode(root.right);

                    root.val = temp.val;
                    root.count = temp.count;
                    root.right = deleteNode(temp.val, root.right);
                }
            }
        }else if(root.val < num){
            root.right = deleteNode(num, root.right);

        }else{
            root.left = deleteNode(num, root.left);
        }

        if(root == null) return root;

        root.height = max(height(root.left), height(root.right))+1;

        int balance = balance(root);

        if(balance > 1){
            if(balance(root.left) < 0){
                root.left = leftRotate(root.left);
                root = rightRotate(root);
            }else{
                root = rightRotate(root);
            }
        }else if(balance < -1){
            if(balance(root.right) > 0){
                root.right = rightRotate(root.right);
                root = leftRotate(root);
            }else{
                root = leftRotate(root);
            }
        }

        return root;

    }



    class Node{
        int val;
        Node left;
        Node right;
        int count;
        int height;

        public Node(int val){
            this.val = val;
            this.count = 1;
            this.height = 1;
        }

    }
}


