package collab.fm.mining;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.ArrayUtils;

import collab.fm.server.util.Pair;
import edu.stanford.nlp.ling.WordTag;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

// A utility class for calculating text similarity, e.g. similarity of two features' descriptions.

public class TextSimilarity {
	
	// Similarity by term-frequency * inverse-document-frequency (tf-idf) method:
	//      Sim(Vec1, Vec2) = (Vec1 * Vec2) / (Length_of_Vec1 * Length_of_Vec2)
	public static double byTfIdf(Map<String, Integer> tfVec1, Map<String, Integer> tfVec2, 
			Map<String, Integer> dfVec, int docCount) {
		double dotProduct = 0.0, len1 = 0.0, len2 = 0.0;
		Set<String> computedTerm = new TreeSet<String>();
		
		for (Map.Entry<String, Integer> entry1: tfVec1.entrySet()) {
			TermSim ts = calcTermSim(entry1.getKey(), entry1.getValue(), 
					tfVec2, dfVec, docCount);
			if (!ts.isZero) {
				computedTerm.add(entry1.getKey());
			}
			dotProduct += ts.dotProduct;
			len1 += ts.len1;
			len2 += ts.len2;
		}
		
		for (Map.Entry<String, Integer> entry2: tfVec2.entrySet()) {
			if (computedTerm.contains(entry2.getKey())) {
				continue;
			}
			TermSim ts = calcTermSim(entry2.getKey(), entry2.getValue(),
					tfVec1, dfVec, docCount);
			dotProduct += ts.dotProduct;
			len1 += ts.len1;
			len2 += ts.len2;	
		}
		
		return dotProduct / (Math.sqrt(len1) * Math.sqrt(len2));
	}
	
	private static TermSim calcTermSim(String term, int tf1,
			Map<String, Integer> tfVec2, Map<String, Integer> dfVec, int docCount) {
		TermSim ts = new TermSim();
		double idf = idf(dfVec.get(term), docCount);
		double val1 = tf1 * idf;
		Integer tf2 = tfVec2.get(term);
		if (tf2 != null) {
			double val2 = tf2 * idf;
			ts.dotProduct = val1 * val2;
			ts.len2 = val2 * val2;
			ts.isZero = false;
		}
		ts.len1 = val1 * val1;
		return ts;
	}
	
	// val = tf * idf = tf * log (docCount / df)
	private static double idf (int df, int docCount) {
		return Math.log(1.0 * docCount / df);
	}
	
	private static class TermSim { // Term Similarity
		public double dotProduct = 0.0;
		public double len1 = 0.0;
		public double len2 = 0.0;
		public boolean isZero = true;
	}
	
	public static void main(String[] argv) throws IOException, ClassNotFoundException {
		// Do a simple test
//		System.out.println(TextSimilarity.bySimpleTf(
//				"The song is good.", "The songs are good."));
//		System.out.println(TextSimilarity.termVector.toString());
		
		TextData.clearDocumentSet();
		TextData td = new TextData("You can search in the search result. Do some search.");
		TextData td2 = new TextData("I can know you know I Know.");
		
		
		
	}
}
