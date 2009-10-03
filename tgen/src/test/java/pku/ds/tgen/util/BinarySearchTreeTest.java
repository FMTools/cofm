package pku.ds.tgen.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class BinarySearchTreeTest {
	
	class IntComp implements Comparator<Integer> {

		public int compare(Integer o1, Integer o2) {
			return o1 - o2;
		}
		
	}
	
	@Ignore
	@Test
	public void testInOrderTraverse() {
		// Test against the search tree of java.
		SearchTree<Integer, Integer> bst = new BinarySearchTree<Integer, Integer>(new IntComp());
		TreeSet<Integer> javaSearchTree = new TreeSet<Integer>();
		Random rand = new Random();
		for (int i = 0; i < 100000; i++) {
			Integer it = new Integer(rand.nextInt(1000000)); 
			bst.insert(it, it);
			javaSearchTree.add(it);
		}
		Integer[] result1 = javaSearchTree.toArray(new Integer[0]);
		
		TreeIterator<Integer> t = bst.inOrderIterator();
		List<Integer> list = new ArrayList<Integer>();
		while (t.hasNext()) {
			list.add(t.next());
		}
		Integer[] result2 = list.toArray(new Integer[0]);
		assertArrayEquals(result1, result2);
	}
}
