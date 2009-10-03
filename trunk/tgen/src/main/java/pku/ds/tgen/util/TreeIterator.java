package pku.ds.tgen.util;

public interface TreeIterator<K, V> {
	public V next();
	public K getKey();
	public boolean hasNext();
}
