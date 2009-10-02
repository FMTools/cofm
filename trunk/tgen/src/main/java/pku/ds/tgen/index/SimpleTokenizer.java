package pku.ds.tgen.index;

// Split text by whitespace.
public class SimpleTokenizer implements Tokenizer {

	public String convert(String text) {
		return text.replaceAll("^\\s+", "").replaceAll("\\s+$", "").replaceAll("\\s+", " ");
	}

	public String next(int wordNum) {
		// TODO Auto-generated method stub
		return null;
	}

}
