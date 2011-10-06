package collab.fm.mining;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import edu.stanford.nlp.ling.WordTag;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.StringUtils;

public class GrammaticalParser {

	private class ParsedSentence {
		public List<String> rawWords = new ArrayList<String>();
		public List<WordTag> taggedWords = new ArrayList<WordTag>();
		public List<TypedDependency> tdl;
		
		public String toRawText() {
			return StringUtils.join(rawWords, WORD_DELIMITER);
		}
		
		public String toString() {
			return "-> " + toRawText() + "\n" + tdl.toString();
		}
	}
	
	public static class ParsedWord {
		public String word;
		public String tag;
		public String stem;
		public int index; // starts from 1
		
		public ParsedWord(WordTag wordTag, int index) {
			word = wordTag.word().toLowerCase();
			tag = wordTag.tag();
			this.index = index;
			
			WordTag stemmed = Morphology.stemStatic(wordTag);
			stem = stemmed.word().toLowerCase();
		}
		
		public String toString() {
			return index + "/" + word + "/" + tag + "/" + stem;
		}
	}
	
	private MaxentTagger tagger;
	private LexicalizedParser lp;
	
	private String text;
	private List<ParsedSentence> sentences;
	private boolean grammaticalParsing;
	
	private static final String END_OF_SENTENCE = ".";
	private static final String WORD_DELIMITER = " ";
	
	private static final int MIN_WORD_LENGTH = 2;
	
	// The word tags assigned by Stanford POS Tagger, the tags are defined in
	//        http://www.computing.dcu.ie/~acahill/tagset.html
	private static final String TAG_VERB = "VB";  // The verb-tags contain the substring "VB"
	private static final String TAG_NOUN = "NN";  // The noun-tags contain the substring "NN"
	
	// The grammar relations assigned by Stanford Parser
	private static final String TYPE_OBJECT = "obj";
	private static final String TYPE_DEPEND = "dep";
	private static final String TYPE_COMPOUND_NOUN = "nn";
	private static final String TYPE_CONJ = "conj";
	
	// Stop words
	private static final Set<String> STOPWORDS = 
		new TreeSet<String>(Arrays.asList( new String[]  // From "SnowBall"
		{"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your"
			, "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers"
			, "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what"
			, "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are"
			, "was", "were", "be", "been", "being", "have", "has", "had", "having", "do"
			, "does", "did", "doing", "would", "should", "could", "ought", "i'm", "you're", "he's"
			, "she's", "it's", "we're", "they're", "i've", "you've", "we've", "they've", "i'd", "you'd"
			, "he'd", "she'd", "we'd", "they'd", "i'll", "you'll", "he'll", "she'll", "we'll", "they'll"
			, "isn't", "aren't", "wasn't", "weren't", "hasn't", "haven't", "hadn't", "doesn't", "don't", "didn't"
			, "won't", "wouldn't", "shan't", "shouldn't", "can't", "cannot", "couldn't", "mustn't", "let's", "that's"
			, "who's", "what's", "here's", "there's", "when's", "where's", "why's", "how's", "a", "an"
			, "the", "and", "but", "if", "or", "because", "as", "until", "while", "of"
			, "at", "by", "for", "with", "about", "against", "between", "into", "through", "during"
			, "before", "after", "above", "below", "to", "from", "up", "down", "in", "out"
			, "on", "off", "over", "under", "again", "further", "then", "once", "here", "there"
			, "when", "where", "why", "how", "all", "any", "both", "each", "few", "more"
			, "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same"
			, "so", "than", "too", "very"}));;
	
	public GrammaticalParser() {
		sentences = new ArrayList<ParsedSentence>();
		lp = new LexicalizedParser("englishPCFG.ser.gz");
		lp.setOptionFlags("-maxLength", "80", "-retainTmpSubcategories");
		
		try {
			tagger = new MaxentTagger("left3words-wsj-0-18.tagger");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void parse(String text, boolean needGrammar) {
		this.text = text;
		this.sentences.clear();
		this.grammaticalParsing = needGrammar;
		
		textToSentences();
		
		if (this.grammaticalParsing) {
			for (ParsedSentence ps: this.sentences) {
				
				Tree parseTree = lp.apply(ps.toRawText());
				TreebankLanguagePack tlp = new PennTreebankLanguagePack();
				GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
				GrammaticalStructure gs = gsf.newGrammaticalStructure(parseTree);
				
				ps.tdl = (List<TypedDependency>) gs.typedDependenciesCollapsed();
				
				//System.out.println(ps);
			}
		}
		
	}
	
	private void textToSentences() {
		String taggedText = tagger.tagString(text);
		String[] taggedWords = taggedText.split("\\s");
		WordTag[] tags = new WordTag[taggedWords.length];
		
		// Find sentence delimiter
		int begin = 0;
		for (int i = 0; i < taggedWords.length; i++) {
			tags[i] = WordTag.valueOf(taggedWords[i]);
			
			if (tags[i].tag().equals(END_OF_SENTENCE)) {
				// Paste taggedWords (begin to i) into a sentence
				ParsedSentence ps = new ParsedSentence();
				for (int j = begin; j < i; j++) {
					ps.taggedWords.add(tags[j]);
					ps.rawWords.add(tags[j].word());
				}
				if (ps.rawWords.size() > 0) {
					this.sentences.add(ps);
				}
				begin = i+1;
			}
		}
		
		if (begin == 0) {
			// No "END_OF_SENTENCE" found, we treat the whole text as a sentence
			ParsedSentence ps = new ParsedSentence();
			for (int i = 0; i < tags.length; i++) {
				ps.taggedWords.add(tags[i]);
				ps.rawWords.add(tags[i].word());
			}
			this.sentences.add(ps);
		}
		
	}

	private void removeStopWords(List<ParsedWord> words) {
		for (Iterator<ParsedWord> it = words.iterator(); it.hasNext();) {
			ParsedWord pw = it.next();
			if (pw.word.length() < MIN_WORD_LENGTH ||
					STOPWORDS.contains(pw.word) || STOPWORDS.contains(pw.stem)) {
				it.remove();
			}
		}
	}
	
	public List<ParsedWord> getWords() {
		List<ParsedWord> result = new ArrayList<ParsedWord>();
		for (ParsedSentence ps: this.sentences) {
			int i = 1;
			for (WordTag wt: ps.taggedWords) {
				ParsedWord pw = new ParsedWord(wt, i++);
				result.add(pw);
			}
		}
		removeStopWords(result);
		return result;
	}
	
	public List<ParsedWord> getObjects() {
		if (!this.grammaticalParsing) {
			return new ArrayList<ParsedWord>(); // empty
		}
		List<ParsedWord> result = new ArrayList<ParsedWord>();
		List<Integer> objIndex = new ArrayList<Integer>();
		
		for (ParsedSentence ps: this.sentences) {
			// Explicit object-relation are marked with "*obj" or "dep"
			for (TypedDependency td: ps.tdl) {
				int i = td.dep().index() - 1;  // Type Dependency Index starts from 1
				WordTag wt = ps.taggedWords.get(i);
				if (td.reln().getShortName().contains(TYPE_OBJECT) ||
						(td.reln().getShortName().equals(TYPE_DEPEND) && isNoun(wt))) {
					
					ParsedWord pw = new ParsedWord(wt, td.dep().index());
					result.add(pw);
					objIndex.add(td.dep().index());
				} 
			}
			
			// Implicit objects include "nn" and "conj" dependent of explicit objects.
			for (TypedDependency td: ps.tdl) {
				if ((td.reln().getShortName().equals(TYPE_COMPOUND_NOUN) ||
						td.reln().getShortName().contains(TYPE_CONJ)) && 
						objIndex.contains(td.gov().index())) {
					int i = td.dep().index() - 1;
					ParsedWord pw = new ParsedWord(ps.taggedWords.get(i), i+1);
					result.add(pw);
				}
			}
		}
		
		removeStopWords(result);
		return result;
	}
	
	private boolean isNoun(WordTag taggedWord) {
		return taggedWord.tag().startsWith(TAG_NOUN);
	}
	
	public static void main(String[] args) {
		GrammaticalParser gp = new GrammaticalParser();
		gp.parse("I've create and share your people, events and data.", true);
		System.out.println(gp.getObjects());
		System.out.println(gp.getWords());
	}
}
