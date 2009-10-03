package pku.ds.tgen.index;

import java.io.*;
// Split text by whitespace.
public class SimpleTokenizer implements Tokenizer {

	private StringBuilder data = new StringBuilder();
	private int pos = 0;
	private int size = 0;
	private boolean isFull = false;
	private boolean isEnd = false;
	
	
	private BufferedReader src = null;
	
	public SimpleTokenizer(String srcFile) {
		try {
			//src = new BufferedReader(new FileReader(srcFile));
			src = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(srcFile), "utf-8"));
			
		} catch (IOException e) {
			e.printStackTrace();
			src = null;
		}
	}
	
	public Words next(int wordNum) {
		Words result = new Words();
		result.begin = nextWordBegin();
		if (result.begin < 0) {
			return null;
		}
		while (--wordNum > 0) {
			if (nextWordBegin() < 0) {
				return null;
			}
		}
		result.end = pos;
		result.text = data.substring(result.begin, result.end);
		return result;
	}

	public Words next(int start, int wordNum) {
		if (start < 0 || start >= size) {
			return null;
		}
		pos = start;
		return next(wordNum);
	}
	
	public void putBack(int wordNum) {
		// Preparation work.
		if (wordNum > 0 && pos >= size) {
			pos = size - 1;
		}
		while (wordNum-- > 0) {
			// Find first non-whitespace char before pos
			while (pos >= 0 && Character.isWhitespace(data.charAt(pos))) {
				pos--;
			}
			// Find first whitespace then.
			while (pos >= 0 && !Character.isWhitespace(data.charAt(pos))) {
				pos--;
			}
			if (pos < 0) {
				pos = 0;
				return;
			}
		}
	}
	
	/**
	 * Find begin index of next word, set pos to the end of next word
	 * @return the index, -1 if there's no next word.
	 */
	private int nextWordBegin() {
		while (true) {
			while (pos >= size) {
				if (parseLine() < 0) {
					return -1;
				}
			}
			int begin = pos;
			// Find first non-whitespace char 
			while (Character.isWhitespace(data.charAt(begin))) {
				begin++;
			}
			pos = begin;
			// Then find first whitespace (as the end of next word)
			while (pos < size && !Character.isWhitespace(data.charAt(pos))) {
				pos++;
			}
			return begin;
		}
	}
	
	/**
	 * Parse next line in "src" to words. 
	 * @return words in that line, or -1 if reached the end of file.
	 */
	private int parseLine() {
		if (src != null && !isEnd && !isFull) {
			try {
				String line = src.readLine();
				if (line == null) {
					isEnd = true;
					return -1;
				}
				// Trim head and tail whitespace.
				line = line.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
				if (line.equals("")) {
					return 0;
				}
				String[] words = line.split("\\s+");
				int wordLen, count = 0;
				for (String word: words) {
					wordLen = word.length();
					if (wordLen + size > Integer.MAX_VALUE) {
						isFull = true;
						break;
					}
					if (size == 0) { // We have the first word
						data.append(word);
					} else {
						data.append(" " + word); // Insert a whitespace between words
						size++;  // "++" for the extra whitespace
					}
					size += wordLen;
					count++;
				}
				return count;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
}
