package collab.fm.mining;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import collab.fm.server.util.Pair;

// A utility class for calculating text similarity, e.g. similarity of two features' descriptions.

public class TextSimilarity {
	
	public static String[] stopWords = {
		"a", "an", "the", 
		"is", "are", "be",
		"of", "for", "in", "at", "by",
		"this", "that", "it", "its", "these", "those",
		"and"
	};
	
	public static final int FIRST = 1;
	public static final int SECOND = 2;
	
	// Key = term, Value = <tf in doc1, tf in doc2>
	private static Map<String, Pair<Integer, Integer>> termVector = 
		new HashMap<String, Pair<Integer, Integer>>();
	
	// Calculate by term-frequency (tf) of the terms in doc1 and doc2
	public static float bySimpleTf(String doc1, String doc2) {
		calcRawTermVectors(doc1, doc2);
		return termVectorDistance();
	}
	
	private static float termVectorDistance() {
		// (Vec1 * Vec2) / (length of Vec1 * length of Vec2)
		int dotproduct = 0;
		int firstVectorLen = 0;
		int secondVectorLen = 0;
		
		for (Pair<Integer, Integer> pair: termVector.values()) {
			dotproduct += pair.first * pair.second;
			firstVectorLen += pair.first * pair.first;
			secondVectorLen += pair.second * pair.second;
		}
		
		if (dotproduct == 0) {
			return 0;
		}
		double fvlen = Math.sqrt(new Integer(firstVectorLen).doubleValue());
		double svlen = Math.sqrt(new Integer(secondVectorLen).doubleValue());
		return new Double(dotproduct / (fvlen * svlen)).floatValue();
	}
	
	private static void calcRawTermVectors(String doc1, String doc2) {
		termVector.clear();
		calcRawTermVector(doc1, FIRST);
		calcRawTermVector(doc2, SECOND);
	}
	
	private static void calcRawTermVector(String doc, int position) {
		// Get raw words seperated by whitespaces.
		String[] words = doc.split("\\s"); 
		
		for (String w: words) {
			// Normalize the word, i.e. remove any punctuation before/after the letters.
			w = w.replaceAll("^\\W*", "").replaceAll("\\W*$", "");
			
			// Stemming the words (using SnowBall library)
			SnowballStemmer stemmer = new englishStemmer();
			stemmer.setCurrent(w);
			stemmer.stem();
			w = stemmer.getCurrent();
			
			// Convert to lower case
			w = w.toLowerCase();
			
			// Skip stop-words and empty words.
			if (ArrayUtils.contains(stopWords, w) || w.length() < 1) {
				continue;
			}
			Pair<Integer, Integer> tf = termVector.get(w);
			if (tf == null) {
				tf = Pair.make(new Integer(0), new Integer(0));
				termVector.put(w, tf);
			}
			if (position == FIRST) {
				tf.first++;
			} else if (position == SECOND) {
				tf.second++;
			}
		}
	}
	
	public static void main(String[] argv) {
		// Do a simple test
		System.out.println(TextSimilarity.bySimpleTf(
				"The song is good.", "The songs are good."));
		System.out.println(TextSimilarity.termVector.toString());
	}
}
