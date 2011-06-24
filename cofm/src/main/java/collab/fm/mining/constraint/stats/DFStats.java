package collab.fm.mining.constraint.stats;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import collab.fm.mining.TextData;
import collab.fm.mining.constraint.FeaturePair;
import collab.fm.mining.constraint.SVM;
import collab.fm.server.util.Pair;

public class DFStats implements DataStats {

	static Logger logger = Logger.getLogger(DFStats.class);
	
	private static final int NUM_TOP_WORDS = 30;
	private Map<String, Integer> dfNonCons = new HashMap<String, Integer>();
	private Map<String, Integer> dfCons = new HashMap<String, Integer>();
	private Map<String, Integer> vbNonCons = new HashMap<String, Integer>();
	private Map<String, Integer> vbCons = new HashMap<String, Integer>();
	
	public void report(BufferedWriter out) throws IOException {
		List<Map.Entry<String, Integer>> countNonCons = new ArrayList<Map.Entry<String, Integer>>(dfNonCons.entrySet());
		List<Map.Entry<String, Integer>> countCons = new ArrayList<Map.Entry<String, Integer>>(dfCons.entrySet());
		List<Map.Entry<String, Integer>> countVbNonCons = new ArrayList<Map.Entry<String, Integer>>(vbNonCons.entrySet());
		List<Map.Entry<String, Integer>> countVbCons = new ArrayList<Map.Entry<String, Integer>>(vbCons.entrySet());
		
		Comparator<Map.Entry<String, Integer>> comp = new Comparator<Map.Entry<String, Integer>>() {

			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				return -1 * o1.getValue().compareTo(o2.getValue());
			}
		};
			
		Collections.sort(countNonCons, comp);
		Collections.sort(countCons, comp);
		Collections.sort(countVbNonCons, comp);
		Collections.sort(countVbCons, comp);
		
		out.write("*** Top " + NUM_TOP_WORDS + " nouns & verbs in pairs:\n");
		out.write(String.format("%23s%23s%23s%23s\n", "Non-Constained (Noun)", "Constrained (Noun)",
				"Non-Constrained (Verb)", "Constrained (Verb)"));
		out.write("-------------------------------------------------\n");
		for (int i = 0; i < NUM_TOP_WORDS; i++) {
			out.write(String.format("%15s%8d%15s%8d%15s%8d%15s%8d\n", 
					countNonCons.get(i).getKey(), countNonCons.get(i).getValue(),
					countCons.get(i).getKey(), countCons.get(i).getValue(),
					countVbNonCons.get(i).getKey(), countVbNonCons.get(i).getValue(),
					countVbCons.get(i).getKey(), countVbCons.get(i).getValue()));
		}

	}

	public void update(List<FeaturePair> data) {
		for (FeaturePair pair: data) {
			Map<String, Integer> df = (pair.getLabel() == FeaturePair.NO_CONSTRAINT ? dfNonCons : dfCons);
			Map<String, Integer> vb = (pair.getLabel() == FeaturePair.NO_CONSTRAINT ? vbNonCons : vbCons);
			
			Pair<TextData, TextData> text = pair.getPairText();
			
			Set<String> allNoun = new HashSet<String>();
			Set<String> allVerb = new HashSet<String>();
			allNoun.addAll(text.first.getNounVector().keySet());
			allNoun.addAll(text.second.getNounVector().keySet());
			allVerb.addAll(text.first.getVerbVector().keySet());
			allVerb.addAll(text.second.getVerbVector().keySet());
			for (String word: allNoun) {
				Integer count = df.get(word);
				if (count == null) {
					count = new Integer(0);
				}
				count++;
				df.put(word, count);
			}
			for (String word: allVerb) {
				Integer count = vb.get(word);
				if (count == null) {
					count = new Integer(0);
				}
				count++;
				vb.put(word, count);
			}
		}
	}
	
	@Deprecated
	private void listCommonNoun(BufferedWriter out, FeaturePair pair, int mode) {
		if (out != null && (mode == SVM.MODE_TRAIN_ALL || mode == SVM.MODE_TRAIN_ONLY_CON)) {
			if (pair.getLabel() == FeaturePair.EXCLUDE || pair.getLabel() == FeaturePair.REQUIRE) {
				Pair<TextData, TextData> text = pair.getPairText();
				// Output common nouns in constrained pairs in train sets
				// Format: 
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
	}


}
