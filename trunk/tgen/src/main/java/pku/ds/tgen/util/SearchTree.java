package pku.ds.tgen.util;

public interface SearchTree<Key, Val> {
	// if key is already in the tree, return the corresponding value
	public Val insert(Key key, Val val);
	
	public Val remove(Key key);
	
	public Val get(Key key);
	
	public TreeIterator<Key, Val> inOrderIterator();
}
