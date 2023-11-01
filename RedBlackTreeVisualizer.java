import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


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

class RedBlackTree {
    public Node NIL_LEAF = new Node(0, null, "BLACK");
    private Node root = NIL_LEAF;

    public Node getRoot() {
        return root;
    }
    public void insert(int key) { 
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
        while (node.color.equals("RED") && node.parent.color.equals("RED")) {
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

    public Node findNode(int key) {
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

    public void deleteNode(Node node) {
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

    public void transplant(Node u, Node v) {
        if (u.parent == null) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        v.parent = u.parent;
    }

    public Node minimum(Node node) {
        while (node.left != NIL_LEAF) {
            node = node.left;
        }
        return node;
    }

    public void fixDelete(Node node) {
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

    public void leftRotate(Node x) {
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

    public void rightRotate(Node y) {
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

    public void visualize(JPanel panel) {
        JFrame frame = new JFrame("Pelo Amor de Deus me da 2 pontos?");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    public void inOrderTraversal(Node node, List<String> result) {
        if (node == NIL_LEAF) {
            return;
        }
        inOrderTraversal(node.left, result);
        result.add(node.key + " (" + node.color + ")");
        inOrderTraversal(node.right, result);
    }
     public void drawTree(Graphics g, Node node, int x, int y, int xOffset, int yOffset) {
        if (node != null && node != NIL_LEAF) {
            g.setColor(node.color.equals("RED") ? Color.RED : Color.BLACK);
            g.fillOval(x - 15, y - 15, 30, 30);
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(node.key), x - 5, y + 5);

            if (node.left != NIL_LEAF) {
                g.setColor(Color.BLACK);
                g.drawLine(x, y, x - xOffset, y + yOffset);
                drawTree(g, node.left, x - xOffset, y + yOffset, xOffset / 2, yOffset);
            }

            if (node.right != NIL_LEAF) {
                g.setColor(Color.BLACK);
                g.drawLine(x, y, x + xOffset, y + yOffset);
                drawTree(g, node.right, x + xOffset, y + yOffset, xOffset / 2, yOffset);
            }
        }
    }
     public void insertValue(int value) {
        insert(value);
    }

    // Método para remover um valor da árvore
    public void removeValue(int value) {
        remove(value);
    }
}
public class RedBlackTreeVisualizer {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RedBlackTree tree = new RedBlackTree();

            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);

                    if (tree != null) {
                        drawTree(g, tree.getRoot(), getWidth() / 2, 30, getWidth() / 4, 60);
                    }
                }

                // Este é o novo método para desenhar a árvore.
                private void drawTree(Graphics g, Node node, int x, int y, int xOffset, int yOffset) {
                    if (node != null && node != tree.NIL_LEAF) {
                        g.setColor(node.color.equals("RED") ? Color.RED : Color.BLACK);
                        g.fillOval(x - 15, y - 15, 30, 30);
                        g.setColor(Color.WHITE);
                        g.drawString(String.valueOf(node.key), x - 5, y + 5);

                        if (node.left != tree.NIL_LEAF) {
                            g.setColor(Color.BLACK);
                            g.drawLine(x, y, x - xOffset, y + yOffset);
                            drawTree(g, node.left, x - xOffset, y + yOffset, xOffset / 2, yOffset);
                        }

                        if (node.right != tree.NIL_LEAF) {
                            g.setColor(Color.BLACK);
                            g.drawLine(x, y, x + xOffset, y + yOffset);
                            drawTree(g, node.right, x + xOffset, y + yOffset, xOffset / 2, yOffset);
                        }
                    }
                }
            };

            List<String> inOrderResult = new ArrayList<>();
            // Criação da interface gráfica
            JFrame frame = new JFrame("Me da 2 Pontos? Pelo amor de Deus");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // Caixa de texto para inserir valores
            JTextField valueTextField = new JTextField(5);
            JButton insertButton = new JButton("Inserir");
            insertButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        int value = Integer.parseInt(valueTextField.getText());
                        tree.insertValue(value);
                        valueTextField.setText(""); // Limpa a caixa de texto
                        panel.repaint(); // Atualiza a visualização da árvore
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Ai tu quer resenha? Bote a boba do número");
                    }
                }
            });

            // Caixa de texto para remover valores
            JTextField removeTextField = new JTextField(5);
            JButton removeButton = new JButton("Remover");
            removeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        int value = Integer.parseInt(removeTextField.getText());
                        tree.removeValue(value);
                        removeTextField.setText(""); // Limpa a caixa de texto
                        panel.repaint(); // Atualiza a visualização da árvore
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Pq nâo remove as bolas animal?");
                    }
                }
            });

            // Painel de botões
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(new JLabel("Inserir valor:"));
            buttonPanel.add(valueTextField);
            buttonPanel.add(insertButton);
            buttonPanel.add(new JLabel("Remover valor:"));
            buttonPanel.add(removeTextField);
            buttonPanel.add(removeButton);

            frame.add(panel, BorderLayout.CENTER);
            frame.add(buttonPanel, BorderLayout.SOUTH);
            frame.setSize(800, 600);
            frame.setVisible(true);
            insertButton.setBackground(Color.BLUE);
            insertButton.setForeground(Color.WHITE);
            insertButton.setFont(new Font("Arial", Font.BOLD, 14));

            valueTextField.setBorder(BorderFactory.createLineBorder(Color.BLUE));
            valueTextField.setFont(new Font("Arial", Font.PLAIN, 14));

            panel.setBackground(Color.white);

            tree.inOrderTraversal(tree.getRoot(), inOrderResult);

            for (String value : inOrderResult) {
                System.out.println(value);
            }
        });
    }
}