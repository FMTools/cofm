package pku.ds.tgen.util;

import java.util.Comparator;
import java.util.Stack;

public class BinarySearchTree<Key, Val> implements SearchTree<Key, Val> {
	
	private Comparator<Key> comparator = null;
	
	private TreeNode<Key, Val> root = null;
	
	public BinarySearchTree(Comparator<Key> comparator) {
		this.comparator = comparator;
	}
	
	
	public Val get(Key key) {
		TreeNode<Key, Val> node = root;
		while (node != null) {
			int c = comparator.compare(key, node.getKey());
			if (c < 0) {
				node = node.getLeft();
			} else if (c > 0) {
				node = node.getRight();
			} else {
				return node.getVal();
			}
		}
		return null;
	}

	public Val insert(Key key, Val val) {
		if (root == null) {
			root = new TreeNode<Key, Val>(key, val);
			return val;
		}
		TreeNode<Key, Val> node = root;
		while (node != null) {
			int c = comparator.compare(key , node.getKey());
			if (c < 0) {
				if (node.getLeft() == null) {
					TreeNode<Key, Val> n = new TreeNode<Key, Val>(key, val);
					node.setLeft(n);
					return val;
				} else {
					node = node.getLeft();
				}
			} else if (c > 0) {
				if (node.getRight() == null) {
					TreeNode<Key, Val> n = new TreeNode<Key, Val>(key, val);
					node.setRight(n);
					return val;
				} else {
					node = node.getRight();
				}
			} else {
				return node.getVal();
			}
		}
		return null;
	}

	public Val remove(Key key) {
		//Unused for this program.
		throw new UnsupportedOperationException("Unused operation for tgen");
	}
	
	public TreeIterator<Key, Val> inOrderIterator() {
		return new InOrderIterator<Key, Val>(root);
	}
	
	// The Binary Tree Node
	public static class TreeNode<K, V> {
		private K key;
		private V val;
		private TreeNode<K, V> left = null;
		private TreeNode<K, V> right = null;
		
		public TreeNode() {
		}
		public TreeNode(K key, V val) {
			setKey(key);
			setVal(val);
		}
		public K getKey() {
			return key;
		}
		public void setKey(K key) {
			this.key = key;
		}
		public V getVal() {
			return val;
		}
		public void setVal(V val) {
			this.val = val;
		}
		public TreeNode<K, V> getLeft() {
			return left;
		}
		public void setLeft(TreeNode<K, V> left) {
			this.left = left;
		}
		public TreeNode<K, V> getRight() {
			return right;
		}
		public void setRight(TreeNode<K, V> right) {
			this.right = right;
		}
	}
	
	public static class InOrderIterator<K, V> implements TreeIterator<K, V> {
		
		private Stack<TreeNode<K, V>> context = null; 
		private TreeNode<K, V> cur = null;
		private TreeNode<K, V> latest = null;
		
		public InOrderIterator(TreeNode<K, V> root) {
			cur = root;
			latest = root;
			context = new Stack<TreeNode<K, V>>();
		}

		public boolean hasNext() {
			return context.size() > 0 || cur != null;
		}

		public V next() {
			while (cur != null) {
				context.push(cur);
				cur = cur.getLeft();
			} 
			cur = context.pop();
			latest = cur;
			cur = cur.getRight();
			return latest.getVal();	
		}

		public K getKey() {
			return latest.getKey();
		}
		
	}
}
