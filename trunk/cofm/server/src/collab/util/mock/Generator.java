package collab.util.mock;

public interface Generator<T, V> {
	public T next();
	public T next(V specifiedValues);
}
