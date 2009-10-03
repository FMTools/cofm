package pku.ds.tgen.index;

import java.util.*;

import pku.ds.tgen.index.Tokenizer.Words;
import pku.ds.tgen.util.*;

public class BstIndexer implements Indexer {
	
	class StringComp implements Comparator<String> {

		public int compare(String o1, String o2) {
			return o1.compareTo(o2);
		}
		
	}
	
	private SearchTree<String, Integer> topIndex = null;
	private List<List<Integer>> posIndex = null;  
	private int curPosIndex = 0;
	
	public void index(Tokenizer tokenizer, int order) {
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
