import tree.domain.RedBlackTree;
import javax.swing.*;
import java.awt.*;

public final class RedBlackTreeVisualizer {
    private static void draw(Graphics g, RedBlackTree.View node, int x, int y, int gap) {
        if (node == null) return;
        g.setColor(Color.GRAY);
        if (node.left() != null) g.drawLine(x,y,x-gap,y+60);
        if (node.right() != null) g.drawLine(x,y,x+gap,y+60);
        g.setColor(node.red() ? Color.RED : Color.BLACK); g.fillOval(x-18,y-18,36,36);
        g.setColor(Color.WHITE); g.drawString(String.valueOf(node.key()),x-10,y+5);
        draw(g,node.left(),x-gap,y+60,Math.max(16,gap/2));
        draw(g,node.right(),x+gap,y+60,Math.max(16,gap/2));
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RedBlackTree tree = new RedBlackTree();
            JFrame frame = new JFrame("Árvore rubro-negra");
            JPanel canvas = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g); draw(g,tree.snapshot(),getWidth()/2,30,getWidth()/4);
                }
            };
            JTextField input = new JTextField(10);
            JLabel status = new JLabel("Insira um número inteiro; duplicatas são ignoradas.");
            JPanel controls = new JPanel(); controls.add(new JLabel("Valor:")); controls.add(input);
            for (String action : new String[]{"Inserir", "Remover"}) {
                JButton button = new JButton(action);
                button.addActionListener(event -> {
                    try {
                        int value = Integer.parseInt(input.getText().trim());
                        if (action.equals("Inserir")) tree.insert(value); else tree.remove(value);
                        tree.validate(); status.setText("Em ordem: " + tree.values()); canvas.repaint(); input.setText("");
                    } catch (NumberFormatException error) { status.setText("Informe um número inteiro válido."); }
                });
                controls.add(button);
            }
            frame.add(controls,BorderLayout.NORTH); frame.add(canvas); frame.add(status,BorderLayout.SOUTH);
            frame.setSize(900,600); frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); frame.setVisible(true);
        });
    }
}
