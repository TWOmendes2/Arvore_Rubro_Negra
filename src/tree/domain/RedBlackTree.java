package tree.domain;

class Node {
    int key;
    Node left;
    Node right;
    Node parent;
    String color;

    public Node(int key, Node parent, String color) {
        this.key = key;
        this.parent = parent;
        this.color = color;
        this.left = null;
        this.right = null;
    }
}

public final class RedBlackTree {
    private final Node NIL_LEAF = new Node(0, null, "BLACK");
    private Node root = NIL_LEAF;

    public RedBlackTree() {
        NIL_LEAF.left = NIL_LEAF;
        NIL_LEAF.right = NIL_LEAF;
    }
    public record View(int key, boolean red, View left, View right) {}
    public View snapshot() { return view(root); }
    private View view(Node node) {
        return node == NIL_LEAF ? null : new View(node.key, node.color.equals("RED"), view(node.left), view(node.right));
    }
    public boolean contains(int key) { return findNode(key) != null; }
    public java.util.List<Integer> values() {
        java.util.List<Integer> result = new java.util.ArrayList<>();
        collect(root, result);
        return java.util.List.copyOf(result);
    }
    private void collect(Node node, java.util.List<Integer> result) {
        if (node == NIL_LEAF) return;
        collect(node.left, result); result.add(node.key); collect(node.right, result);
    }
    public void validate() {
        if (!NIL_LEAF.color.equals("BLACK") || !root.color.equals("BLACK")) throw new IllegalStateException("Raiz/folha deve ser preta");
        if (root != NIL_LEAF && root.parent != null) throw new IllegalStateException("Pai da raiz inválido");
        validateNode(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }
    private int validateNode(Node node, long low, long high) {
        if (node == NIL_LEAF) return 1;
        if (node.key <= low || node.key >= high) throw new IllegalStateException("Ordenação inválida");
        if (node.left != NIL_LEAF && node.left.parent != node || node.right != NIL_LEAF && node.right.parent != node) throw new IllegalStateException("Vínculo parental inválido");
        if (node.color.equals("RED") && (node.left.color.equals("RED") || node.right.color.equals("RED"))) throw new IllegalStateException("Vermelhos consecutivos");
        int left = validateNode(node.left, low, node.key), right = validateNode(node.right, node.key, high);
        if (left != right) throw new IllegalStateException("Altura preta desigual");
        return left + (node.color.equals("BLACK") ? 1 : 0);
    }
    public void insert(int key) {
        if (contains(key)) return; 
        Node new_node = new Node(key, NIL_LEAF, "RED");
        new_node.left = NIL_LEAF;
        new_node.right = NIL_LEAF;

        Node current = root;
        Node parent = null;

        while (current != NIL_LEAF) {
            parent = current;
            if (key < current.key) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        new_node.parent = parent;
        if (parent == null) {
            root = new_node;
        } else if (key < parent.key) {
            parent.left = new_node;
        } else {
            parent.right = new_node;
        }

        if (new_node.parent == null) {
            new_node.color = "BLACK";
            return;
        }

        if (new_node.parent.parent == null) {
            return;
        }

        fixInsert(new_node);
    }

    private void fixInsert(Node node) {
        while (node != root && node.parent.color.equals("RED")) {
            if (node.parent == node.parent.parent.right) {
                Node uncle = node.parent.parent.left;
                if (uncle.color.equals("RED")) {
                    node.parent.color = "BLACK";
                    uncle.color = "BLACK";
                    node.parent.parent.color = "RED";
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.left) {
                        node = node.parent;
                        rightRotate(node);
                    }
                    node.parent.color = "BLACK";
                    node.parent.parent.color = "RED";
                    leftRotate(node.parent.parent);
                }
            } else {
                Node uncle = node.parent.parent.right;
                if (uncle.color.equals("RED")) {
                    node.parent.color = "BLACK";
                    uncle.color = "BLACK";
                    node.parent.parent.color = "RED";
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.right) {
                        node = node.parent;
                        leftRotate(node);
                    }
                    node.parent.color = "BLACK";
                    node.parent.parent.color = "RED";
                    rightRotate(node.parent.parent);
                }

                if (node == root) {
                    break;
                }
            }
        }

        root.color = "BLACK";
    }

    public void remove(int key) {
        Node node = findNode(key);
        if (node != null) {
            deleteNode(node);
        }
    }

    private Node findNode(int key) {
        Node current = root;
        while (current != NIL_LEAF) {
            if (key == current.key) {
                return current;
            }
            if (key < current.key) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return null;
    }

    private void deleteNode(Node node) {
        if (node == NIL_LEAF) {
            return;
        }

        Node y = node;
        String yOriginalColor = y.color;

        Node x;
        if (node.left == NIL_LEAF) {
            x = node.right;
            transplant(node, node.right);
        } else if (node.right == NIL_LEAF) {
            x = node.left;
            transplant(node, node.left);
        } else {
            y = minimum(node.right);
            yOriginalColor = y.color;
            x = y.right;
            if (y.parent == node) {
                x.parent = y;
            } else {
                transplant(y, y.right);
                y.right = node.right;
                y.right.parent = y;
            }
            transplant(node, y);
            y.left = node.left;
            y.left.parent = y;
            y.color = node.color;
        }

        if (yOriginalColor.equals("BLACK")) {
            fixDelete(x);
        }
    }

    private void transplant(Node u, Node v) {
        if (u.parent == null) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        v.parent = u.parent;
    }

    private Node minimum(Node node) {
        while (node.left != NIL_LEAF) {
            node = node.left;
        }
        return node;
    }

    private void fixDelete(Node node) {
        while (node != root && node.color.equals("BLACK")) {
            if (node == node.parent.left) {
                Node sibling = node.parent.right;
                if (sibling.color.equals("RED")) {
                    sibling.color = "BLACK";
                    node.parent.color = "RED";
                    leftRotate(node.parent);
                    sibling = node.parent.right;
                }
                if (sibling.left.color.equals("BLACK") && sibling.right.color.equals("BLACK")) {
                    sibling.color = "RED";
                    node = node.parent;
                } else {
                    if (sibling.right.color.equals("BLACK")) {
                        sibling.left.color = "BLACK";
                        sibling.color = "RED";
                        rightRotate(sibling);
                        sibling = node.parent.right;
                    }
                    sibling.color = node.parent.color;
                    node.parent.color = "BLACK";
                    sibling.right.color = "BLACK";
                    leftRotate(node.parent);
                    node = root;
                }
            } else {
                Node sibling = node.parent.left;
                if (sibling.color.equals("RED")) {
                    sibling.color = "BLACK";
                    node.parent.color = "RED";
                    rightRotate(node.parent);
                    sibling = node.parent.left;
                }
                if (sibling.right.color.equals("BLACK") && sibling.left.color.equals("BLACK")) {
                    sibling.color = "RED";
                    node = node.parent;
                } else {
                    if (sibling.left.color.equals("BLACK")) {
                        sibling.right.color = "BLACK";
                        sibling.color = "RED";
                        leftRotate(sibling);
                        sibling = node.parent.left;
                    }
                    sibling.color = node.parent.color;
                    node.parent.color = "BLACK";
                    sibling.left.color = "BLACK";
                    rightRotate(node.parent);
                    node = root;
                }
            }
        }
        node.color = "BLACK";
    }

    private void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != NIL_LEAF) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    private void rightRotate(Node y) {
        Node x = y.left;
        y.left = x.right;
        if (x.right != NIL_LEAF) {
            x.right.parent = y;
        }
        x.parent = y.parent;
        if (y.parent == null) {
            root = x;
        } else if (y == y.parent.right) {
            y.parent.right = x;
        } else {
            y.parent.left = x;
        }
        x.right = y;
        y.parent = x;
    }

}
