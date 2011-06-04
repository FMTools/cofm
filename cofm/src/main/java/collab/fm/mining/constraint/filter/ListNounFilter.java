package collab.fm.mining.constraint.filter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import collab.fm.mining.TextData;
import collab.fm.mining.constraint.FeaturePair;
import collab.fm.mining.constraint.PairFilter;
import collab.fm.mining.constraint.SVM;
import collab.fm.server.util.Pair;

public class ListNounFilter implements PairFilter {

	static Logger logger = Logger.getLogger(ListNounFilter.class);
	// Output common nouns in constrained pairs in train sets
	public boolean keepPair(FeaturePair pair, int mode) {
		if (out != null && (mode == SVM.MODE_TRAIN_ALL || mode == SVM.MODE_TRAIN_ONLY_CON)) {
			if (pair.getLabel() == FeaturePair.EXCLUDE || pair.getLabel() == FeaturePair.REQUIRE) {
				Pair<TextData, TextData> text = pair.getPairText();
				// Output Format: 
				// -m INT    (the model ID)
				// -i Feature Pair Info  (pair.getPairInfo)
				// -c common noun
				// -N number of all nouns
				try {
					out.write("-m " + pair.getModel().getId() + "\n");
					out.write("-i " + pair.getPairInfo() + "\n-c");
					Set<String> nouns = new HashSet<String>();
					for (String s: text.first.getNounVector().keySet()) {
						nouns.add(s);
					}
					nouns.retainAll(text.second.getNounVector().keySet());
					for (String word: nouns) {
						out.write(" " + word);
					}
					out.write("\n");
					Set<String> all = new HashSet<String>();
					all.addAll(text.first.getNounVector().keySet());
					all.addAll(text.second.getNounVector().keySet());
					out.write("-N " + all.size() + "\n");
				} catch (IOException e) {
					logger.warn("Error in list_noun");
				}
				

			}
		}
		return true;  // always return true1
	}
	
	private BufferedWriter out;
	
	public ListNounFilter(String listNounFile) {
		if (listNounFile != null && !listNounFile.isEmpty()) {
			try {
				out = new BufferedWriter(new FileWriter(listNounFile));
			} catch (IOException e) {
				logger.warn("Cannot open list_noun_file.");
				out = null;
			}
		}
	}
	public void dispose() {
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				logger.warn("Close list_noun_file failed.");
			}
		}
	}

}
