package pku.ds.tgen;

import java.io.*;
import java.util.*;

import pku.ds.tgen.index.*;
import pku.ds.tgen.index.Tokenizer.Words;

public class App {
	//TODO: read input_file_name, output_file_name, K and BeginWords from console
	public static final String INPUT_FILE_NAME = "src/main/resources/input";
	public static final String OUTPUT_FILE_NAME = "target/output";
	public static final int K = 2;
	public static final int MAX_TEXT_LEN = 100000;
	public static final int WORDS_PER_LINE = 20;
	
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
	
	public void run() {
		tokenizer = new SimpleTokenizer(INPUT_FILE_NAME);
		indexer = new BstIndexer();
		indexer.index(tokenizer, K);
		// The generated text will begin with "It is..."
		recentWords.add("It");
		recentWords.add("is");
		
		try {
			FileWriter out = new FileWriter(OUTPUT_FILE_NAME);
			out.write("It is");
			for (int i = 0, j = 0; i < MAX_TEXT_LEN - K; i++, j++) {
				String next = nextWord();
				if (next == null) {
					break;
				}
				out.write(" " + next);
				if (j >= WORDS_PER_LINE) {
					out.write("\n");
					j = 0;
				}
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
