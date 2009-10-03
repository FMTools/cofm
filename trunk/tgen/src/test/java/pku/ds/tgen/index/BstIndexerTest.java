package pku.ds.tgen.index;

import org.junit.Ignore;
import org.junit.Test;

public class BstIndexerTest {

	@Ignore
	@Test
	public void testIndex() {
		Tokenizer tok = new SimpleTokenizer("src/test/resources/SampleText.txt");
		Indexer ind = new BstIndexer();
		ind.index(tok, 2);
	}
}
