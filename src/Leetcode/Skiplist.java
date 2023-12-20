package Leetcode;



public class Skiplist {
    Node root;
    public Skiplist() {
        root = null;
    }
    
    public boolean search(int target) {
        return search(target, root);
    }

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
    
    public void add(int num) {
        root = addNode(num, root);
    }

    private Node addNode(int num, Node root){
        if(root==null) return new Node(num);

        if(root.val < num){
            root.right = addNode(num, root.right);
        }else if(root.val > num){
            root.left =addNode(num, root.left);
        }else{
            root.count++;
            return root;
        }

        root.height = Math.max(height(root.left), height(root.right))+1;

        int balance = balance(root);

        if(balance > 1){
            if(num < root.left.val){
                root = rightRotate(root);
            }else{
                root.left = leftRotate(root.left);
                root = rightRotate(root);
            }
        }else if(balance < 1){

        }

        return root;
    }

    private Node rightRotate(Node root){
        if(root==null) return root;
        Node child = root.left;
        Node grandChild = child.right;

        root.left = grandChild;
        child.right = root;
        root.height = max(height(root.left), height(root.right))+1;

        child.height =  max(height(child.left), height(child.right))+1;

        return child;
    }

    private Node leftRotate(Node root){
        if(root == null) return root;
        Node child = root.right;
        Node grandChild = child.left;

        root.right = grandChild;
        child.left = root;
        root.height = max(height(root.left), height(root.right))+1;
        child.height = max(height(child.left), height(child.right));

        return child;
    }

    private int max(int first, int second){
        return (first > second) ? first : second;
    }
    private int balance(Node root){
        if(root == null) return 0;

        return height(root.left) - height(root.right);
    }

    private int height(Node root){
        if(root==null) return 0;

        return root.height;
    }
    
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


