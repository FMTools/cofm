package pku.ds.tgen;

import java.io.*;
import java.util.*;

import pku.ds.tgen.index.*;
import pku.ds.tgen.index.Tokenizer.Words;

public class App {
	//TODO: read input_file_name, output_file_name, K and BeginWords from console
	public static final String CFG_FILE_NAME = "tgen";
	
	public static final String PROP_INPUT = "input";
	public static final String PROP_OUTPUT = "output";
	public static final String PROP_K = "K";
	public static final String PROP_LEN = "len";
	public static final String PROP_LINE_LEN = "lineLen";
	public static final String PROP_SEED = "seedWords";
	
	private String inputFileName;
	private String outputFileName;
	private String seedWords;
	private int order;
	private int textLen;
	private int wordsPerLine;
	
	private Tokenizer tokenizer = null;
	private Indexer indexer = null;
	
	private Queue<String> recentWords = new LinkedList<String>();
	
	private Random random = new Random();
	
	private String nextWord() {
		StringBuilder key = new StringBuilder();
		int i = 0;
		for (String word: recentWords) {
			if (i++ == 0) {
				key.append(word);
			} else {
				key.append(" " + word);
			}
		}
		List<Integer> posCandidates = indexer.search(key.toString());
		if (posCandidates == null) {
			return null;
		}
		int nextWordBegin = posCandidates.get(random.nextInt(posCandidates.size()));
		Words nextWord = tokenizer.next(nextWordBegin, 1);
		if (nextWord == null) {
			return null;
		}
		recentWords.poll();
		recentWords.add(nextWord.text);
		return nextWord.text;
	}
	
	private void loadProperties() {
		ResourceBundle cfg = ResourceBundle.getBundle(CFG_FILE_NAME);
		inputFileName = cfg.getString(PROP_INPUT);
		outputFileName = cfg.getString(PROP_OUTPUT);
		order = new Integer(cfg.getString(PROP_K));
		textLen = new Integer(cfg.getString(PROP_LEN));
		wordsPerLine = new Integer(cfg.getString(PROP_LINE_LEN));
		seedWords = cfg.getString(PROP_SEED).trim();
	}
	
	public void run() {
		loadProperties();
		
		tokenizer = new SimpleTokenizer(inputFileName);
		indexer = new BstIndexer();
		indexer.index(tokenizer, order);
		
		String[] seeds = seedWords.split("\\s+");
		for (String word: seeds) {
			recentWords.add(word);
		}
		
		try {
			PrintWriter out = new PrintWriter(
					new OutputStreamWriter(
							new FileOutputStream(outputFileName), "utf-8"));
			out.write(seedWords + " ");
			
			for (int i = 0, j = 0; i < textLen - order; i++, j++) {
				String next = nextWord();
				if (next == null) {
					break;
				}
				if (j >= wordsPerLine) {
					out.write("\n");
					j = 0;
				}
				out.write((j == 0 ? "" : " ") + next);
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
