package pku.ds.tgen.index;

public interface Tokenizer {
	// Return next K words. (K == wordNum)
	// return null if no enough words left.
	public String next(int wordNum);
	
	public String convert(String text);
}
