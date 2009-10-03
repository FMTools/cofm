package pku.ds.tgen.index;

import java.util.*;

import org.apache.log4j.Logger;

import pku.ds.tgen.index.Tokenizer.Words;
import pku.ds.tgen.util.*;

public class BstIndexer implements Indexer {
	
	static Logger logger = Logger.getLogger(BstIndexer.class);
	
	class StringComp implements Comparator<String> {

		public int compare(String o1, String o2) {
			return o1.compareTo(o2);
		}
		
	}
	
	private SearchTree<String, Integer> topIndex = null;
	private List<List<Integer>> posIndex = null;  
	private Tokenizer tokenizer;
	private int curPosIndex = 0;
	
	public void dump() {
		logger.debug("---------------------------------------");
		logger.debug("	Indexer Dump");
		logger.debug("---------------------------------------");
		TreeIterator<String, Integer> it = topIndex.inOrderIterator();
		while (it.hasNext()) {
			int value = it.next();
			String key = it.getKey();
			List<Integer> posValues = posIndex.get(value);
			if (posValues != null) {
				for (Integer i: posValues) {
					Words words = tokenizer.next(i, 1);
					logger.debug(key + " " + (words == null ? "<NULL>" : words.text));
				}
			}
		}
		logger.debug("---------------------------------------");
		logger.debug("	Indexer Dump END");
		logger.debug("---------------------------------------");
	}
	
	public void index(Tokenizer tokenizer, int order) {
		this.tokenizer = tokenizer;
		curPosIndex = 0;
		topIndex = new BinarySearchTree<String, Integer>(new StringComp());
		posIndex = new ArrayList<List<Integer>>();  
		
		Words topKeyWords = null;
		while ((topKeyWords = tokenizer.next(order)) != null) {
			Integer posIndexKey = topIndex.insert(topKeyWords.text, curPosIndex);
			if (posIndexKey == curPosIndex) {
				++curPosIndex;
			}
			while (posIndex.size() <= posIndexKey) {
				posIndex.add(new ArrayList<Integer>());
			}
			posIndex.get(posIndexKey).add(topKeyWords.end);
			tokenizer.putBack(order - 1);
		}
		
		dump();
	}

	public List<Integer> search(String keyword) {
		if (topIndex == null || posIndex == null) {
			return null;
		}
		Integer posIndexKey = topIndex.get(keyword); 
		if (posIndexKey != null) {
			return posIndex.get(posIndexKey);
		}
		return null;
	}

}
