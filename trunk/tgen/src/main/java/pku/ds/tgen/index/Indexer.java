package pku.ds.tgen.index;

import java.util.List;

public interface Indexer {
	
	public void index(Tokenizer tokenizer, int order);
	
	public List<Integer> search(String keyword);
}
