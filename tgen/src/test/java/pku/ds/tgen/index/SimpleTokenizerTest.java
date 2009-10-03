package pku.ds.tgen.index;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import pku.ds.tgen.index.Tokenizer.Words;

public class SimpleTokenizerTest {

	@Ignore
	@Test
	public void testNext2() {
		Tokenizer tok = new SimpleTokenizer("src/test/resources/SampleText.txt");
		Words words = null;
		while ((words = tok.next(2)) != null) {
			System.out.println(words.text + " (" + words.begin + ", " + words.end + ")");
		}
	}
	
	@Ignore
	@Test
	public void testNext2PutBack1() {
		Tokenizer tok = new SimpleTokenizer("src/test/resources/SampleText.txt");
		Words words = null;
		while ((words = tok.next(2)) != null) {
			System.out.println(words.text + " (" + words.begin + ", " + words.end + ")");
			tok.putBack(1);
		}
	}
}
