package collab.fm.mining;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import edu.stanford.nlp.ling.WordTag;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

// The data structure representing a piece of text
public class TextData {

	static Logger logger = Logger.getLogger(TextData.class);
	
	// The word tags assigned by Stanfod POS Tagger, the tags are defined in
	//        http://www.computing.dcu.ie/~acahill/tagset.html
	public static final String TAG_VERB = "VB";  // The verb-tags contain the substring "VB"
	public static final String TAG_NOUN = "NN";  // The noun-tags contain the substring "NN"
	public static final String TAG_OTHER = "OTHER";
	// Tags for stop words
	public static final String[] TAG_STOPWORDS = {
		"CC",  // Coordinating conjunction, e.g. and,but,or...
		"DT",  // Determiner, e.g. "the, those...", see "English Determiners" here: 
		       //    http://en.wikipedia.org/wiki/Determiner_%28linguistics%29#English_determiners
		"EX",  // Existential there, i.e. the "There" in the sentence "There is a book on the desk."
		"IN",  // Preposition or subordinating conjunction, e.g. "in", "of", "after"...
		"TO",  // "to"
		"PRP", // Personal Pronoun, i.e. "I", "you", "he"...
		"PRP$", // Possessive PRP, i.e. "my", "your", "his"...
		"WDT", // Wh-Determiner, i.e. "which", "that"
		"WP",  // Wh-Pronoun, i.e. "who", "whom", "what"...
		"WP$", // Possessive WP, i.e. "whose" ...
		"WRB", // Wh-adverb, i.e. "why", "where", "how"
		"UH"   // Interjection, e.g. "uh", "well", "yes"...
	};
	
	private Map<String, Integer> taggedTermVector = new HashMap<String, Integer>();
	
	private static Map<String, Integer> documentVector = new HashMap<String, Integer>();
	private static int numDocument = 0;
	
	private static MaxentTagger tagger;
	static {
		try {
			tagger = new MaxentTagger("left3words-wsj-0-18.tagger");
		} catch (IOException e) {
			logger.warn("Cannot create tagger.", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Cannot create tagger.", e);
		}
		
	}
	
	public static void resetDfVector() {
		setNumDocument(0);
		documentVector.clear();
	}
	
	public TextData(String text) {
		calcTermVector(text);
	}
	
	public static Map<String, Integer> getDocumentVector() {
		return documentVector;
	}
	
	// Get the term vectors for calculating text similarity (See TextSimilarity class)
	
	public Map<String, Integer> getUntaggedTermVector() {
		return this.getFilteredTermVector(new TermPolicy() {

			public boolean keepTaggedWord(String term) {
				// Keep all words
				return true;
			}
			
		});
	}
	
	public Map<String, Integer> getVerbVector() {
		return this.getFilteredTermVector(new TermPolicy() {

			public boolean keepTaggedWord(String word) {
				// Keep all verbs (starts with "VB")
				return word.startsWith(TAG_VERB);
			}
			
		});
	}
	
	public Map<String, Integer> getNounVector() {
		return this.getFilteredTermVector(new TermPolicy() {

			public boolean keepTaggedWord(String word) {
				// Keep all nouns
				return word.startsWith(TAG_NOUN);
			}
			
		});
	}
	
	// A misc term is a word other than Verb or Noun.
	public Map<String, Integer> getMiscTermVector() {
		return this.getFilteredTermVector(new TermPolicy() {

			public boolean keepTaggedWord(String word) {
				return !word.startsWith(TAG_NOUN) && !word.startsWith(TAG_VERB);
			}
			
		});
	}
	
	// Term vector without tag
	private Map<String, Integer> getFilteredTermVector(TermPolicy policy) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		for (String key: taggedTermVector.keySet()) {
			if (!policy.keepTaggedWord(key)) {
				continue;
			}
			// Extract the word
			String s = key.substring(key.indexOf("_") + 1);
			
			// Compute the term-frequency (tf)
			int curTf = taggedTermVector.get(key);
			Integer val = result.get(s);
			if (val == null) {
				val = curTf;
			} else {
				val += curTf;
			}
			result.put(s, val);
		}
		return result;
	}
	
	private String convertTag(String t) {
		if (t.startsWith(TextData.TAG_NOUN)) {
			return TextData.TAG_NOUN;
		} else if (t.startsWith(TextData.TAG_VERB)) {
			return TextData.TAG_VERB;
		}
		return t;
	}
	
	private void calcTermVector(String text) {
		TextData.setNumDocument(TextData.getNumDocument() + 1);
		// Tag the words with the Stanford POS Tagger API
		String tagText = tagger.tagString(text);
		
		Set<String> termEncountered = new TreeSet<String>();

		// Stem and store each word (term) and its info
		for (String word: tagText.split("\\s")) {
			WordTag wt = Morphology.stemStatic(WordTag.valueOf(word));

			// Skip the stop words and punctuation. 
			if (ArrayUtils.contains(TAG_STOPWORDS, wt.tag()) 
					|| wt.word().length() < 2) {
				continue;
			}

			String myTag = this.convertTag(wt.tag());
			String myWord = wt.word().toLowerCase();
			this.checkAndInc(this.taggedTermVector, myTag + "_" + myWord);
			
			// If the term appears in current document (the "text") for the 
			// first time, we increase the document-frequency (df).
			// The df doesn't count different tags.
			if (!termEncountered.contains(myWord)) {
				this.checkAndInc(TextData.documentVector, myWord);
				termEncountered.add(myWord);
			}
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
	
	private static interface TermPolicy {
		public boolean keepTaggedWord(String term);
	}
	
	public static void main(String[] argv) {
		TextData.resetDfVector();
		TextData td = new TextData("You can search in the search result. Do some search.");
		TextData td2 = new TextData("I can know you know I Know.");
		System.out.println("df: " + TextData.getDocumentVector().toString());
		System.out.println("tw: " + td.getUntaggedTermVector().toString());
		System.out.println("vw: " + td.getVerbVector().toString());
		System.out.println("nw: " + td.getNounVector().toString());
		System.out.println("mw: " + td.getMiscTermVector().toString());
	}

	public static void setNumDocument(int numDocument) {
		TextData.numDocument = numDocument;
	}

	public static int getNumDocument() {
		return numDocument;
	}
}
