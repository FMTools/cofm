package collab.fm.server.util;

public class Pair<A, B> {
	public A first;
	public B second;
	
	public Pair(A a, B b) {
		first = a;
		second = b;
	}
	
	public static <A, B> Pair<A, B> make(A a, B b) {
		return new Pair<A, B>(a, b);
	}
}
