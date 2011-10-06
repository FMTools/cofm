package collab.fm.mining;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import collab.fm.mining.GrammaticalParser.ParsedWord;

import edu.stanford.nlp.ling.WordTag;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

// The data structure representing a piece of text
public class TextData {

	static Logger logger = Logger.getLogger(TextData.class);
	
	private static GrammaticalParser parser = new GrammaticalParser();
	
	// Term Vector of stemmed words
	private Map<String, Integer> termVector = new HashMap<String, Integer>();
	private Map<String, Integer> objectTermVector = new HashMap<String, Integer>();
	
	// Document Vector (a TextData is a Document)
	private static Map<String, Integer> documentVector = new HashMap<String, Integer>();	
	private static int numDocument = 0;
	
	public static void resetDocumentVector() {
		setNumDocument(0);
		documentVector.clear();
	}
	
	public TextData(String text, boolean needGrammarParse) {
		calcTermVector(text, needGrammarParse);
	}
	
	public static Map<String, Integer> getDocumentVector() {
		return documentVector;
	}
	
	private void calcTermVector(String text, boolean needGrammarParse) {
		TextData.setNumDocument(TextData.getNumDocument() + 1);
		
		parser.parse(text, needGrammarParse);
		
		// All Words
		Set<String> termEncountered = new TreeSet<String>();
		for (ParsedWord word: parser.getWords()) {
			String stemmed = word.stem;
			
			this.checkAndInc(this.termVector, stemmed);
			
			// If the term appears in current document for the 
			// first time, we increase the document-frequency (df) vector.
			if (!termEncountered.contains(stemmed)) {
				this.checkAndInc(TextData.documentVector, stemmed);
				termEncountered.add(stemmed);
			}
		}
		
		// Only the objects
		for (ParsedWord word: parser.getObjects()) {
			this.checkAndInc(this.objectTermVector, word.stem);
		}
	}
	
	private void checkAndInc(Map<String, Integer> vector, String key) {	
		Integer val = vector.get(key);
		if (val == null) {
			val = 1;
		} else {
			val++;
		}
		vector.put(key, val);
	}
	
	public static void main(String[] argv) {
		TextData.resetDocumentVector();
		TextData td = new TextData("You can search in the search result. Do some search.", true);
		TextData td2 = new TextData("I can know you know I Know.", true);
		System.out.println("df: " + TextData.getDocumentVector().toString());
		System.out.println("td terms: " + td.getTermVector().toString());
		System.out.println("td objects: " + td.getObjectTermVector().toString());
	}

	public static void setNumDocument(int numDocument) {
		TextData.numDocument = numDocument;
	}

	public static int getNumDocument() {
		return numDocument;
	}

	public void setTermVector(Map<String, Integer> termVector) {
		this.termVector = termVector;
	}

	public Map<String, Integer> getTermVector() {
		return termVector;
	}

	public void setObjectTermVector(Map<String, Integer> objectTermVector) {
		this.objectTermVector = objectTermVector;
	}

	public Map<String, Integer> getObjectTermVector() {
		return objectTermVector;
	}
}
