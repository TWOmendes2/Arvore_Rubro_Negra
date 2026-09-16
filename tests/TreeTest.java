import tree.domain.RedBlackTree;
import java.util.*;

public class TreeTest {
    static void check(RedBlackTree tree, TreeSet<Integer> expected) {
        tree.validate();
        if (!tree.values().equals(new ArrayList<>(expected))) throw new AssertionError("TreeSet mismatch");
    }
    public static void main(String[] args) {
        for (int seed=0; seed<20; seed++) {
            Random random = new Random(seed);
            RedBlackTree tree = new RedBlackTree();
            TreeSet<Integer> expected = new TreeSet<>();
            for (int i=0; i<2000; i++) {
                int key = random.nextInt(400)-200;
                if (random.nextBoolean()) { tree.insert(key); expected.add(key); }
                else { tree.remove(key); expected.remove(key); }
                check(tree,expected);
                if (tree.contains(key) != expected.contains(key)) throw new AssertionError("Search mismatch");
            }
            for(int key : new int[]{Integer.MIN_VALUE,Integer.MAX_VALUE}) { tree.insert(key); expected.add(key); check(tree,expected); }
            for(int key : new ArrayList<>(expected)) { tree.remove(key); expected.remove(key); check(tree,expected); }
            tree.remove(0); check(tree,expected);
        }
        System.out.println("Tree tests passed: 40,000 seeded operations plus extremes and complete deletion");
    }
}
