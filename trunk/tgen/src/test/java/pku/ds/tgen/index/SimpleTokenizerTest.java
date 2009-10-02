package pku.ds.tgen.index;

import static org.junit.Assert.*;

import org.junit.Test;

public class SimpleTokenizerTest {

	@Test
	public void testConvert() {
		String origin = "	    		this 	is a        string    with     lots      of        whitespace.     			";
		SimpleTokenizer st = new SimpleTokenizer();
		String result = st.convert(origin);
		assertEquals(new String("this is a string with lots of whitespace."), result);
	}
}
