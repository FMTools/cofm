package pku.ds.tgen.index;

public interface Tokenizer {
	public static class Words {
		public String text;
		public int begin;
		public int end;
		public Words() { }
		public Words(String text, int begin, int end) {
			this.text = text;
			this.begin = begin;
			this.end = end;
		}
	}
	
	/**
	 * Get next N words.
	 * @param wordNum (N)
	 * @return the words, null if no enough words left.
	 */
	public Words next(int wordNum);
	
	public Words next(int start, int wordNum);
	
	// Put back N words, this will affect the return value of "next()"
	public void putBack(int wordNum);
}
