package collab.fm.mining;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

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
			// Normalize the word, i.e. remove any punctuation before/after the letters, 
			// and convert all letters to lower-case.
			w = w.replaceAll("^\\W*", "").replaceAll("\\W*$", "").toLowerCase();
			
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
				"Beginning belligerently (by Bruckner's standards), this movement reaches a triumphant conclusion using themes (or at least rhythmic impressions of these) from all four movements. The form of this movement is complex, derived from a three-subject sonata structure but, like the opening movement of Bruckner's Seventh symphony, highly individualised. The scale and complexity of this movement are both on a different level from that in the opening of the Seventh Symphony, however, not least in that this movement must synthesise the entire symphony (as it reworks old ideas and new ones into a coherent whole), and forms what must be a satisfactory conclusion for the whole work. The opening theme is a powerful chorale, originally given over a march, in which the rhythmic thundering of the timpani recalls certain passages in the opening movement. The second subject, a song-theme, is remarkable in that it recollects not only its counterpart in the first movement but also the Adagio. The third subject is a march-theme, which is a direct reworking of the introduction to the third subject group of the opening movement. In the recapitulation, this third theme is presented as a fugue which leads to the solemn coda and the splendid, bright finish to the symphony. The development presents these three themes and other elements in ways which recollect earlier parts of the symphony, both episodically and in simultaneously parallel combinations. The thematic treatment is subtle and counterpoint is frequently used in the presentation of themes. It therefore seems natural that such a synthesis concludes by contrapuntally combining all the main themes of the symphony: the coda begins in a solemn C minor in which the opening theme of the Finale reaches a powerful climax. This is answered quietly by the woodwind giving out the same theme, then more optimistically by the full orchestra, from which, in a flurry of trumpets and timpani, the Scherzo theme heralds a remarkably succinct combination of all the themes in C major. For all its grandeur, the ending is remarkably concise, and the perorations are more terse than those of, say, Bruckner's own Symphony No. 5 in B flat major.", 
				"Bruckner had conceived the entire movement; whether the manuscripts he left would have made up the final form of the Finale is debatable. Several bifolios of the emerging autograph score survived, consecutively numbered by Bruckner himself, as well as numerous discarded bifolios and particellos sketches. The surviving manuscripts were all systematically ordered and published in a notable facsimile reprint, edited by J. A. Phillips, in the Bruckner Complete Edition, Vienna. Because of Bruckner's individual composing habits, reconstructing the Finale is in some ways easier, and in some ways harder, than it would be to reconstruct an unfinished piece by another composer. Compounding the problem, collectible hunters ransacked Bruckner's house soon after his death. Sketches for the Finale have been found as far away from Austria as Washington D.C. Large portions of the movement were almost completely orchestrated, and even some eminent sketches have been found for the coda (the initial crescendo/28 bars, and the progression towards the final cadenza, even proceeding into the final tonic pedalpoint/in all 32 bars), but only hearsay suggesting the coda would have integrated themes from all four movements: The Bruckner scholars Max Graf and Max Auer reported that they have actually seen such a sketch when they had access to the manuscripts, at that time in the possession of Franz Schalk. Today such a sketch appears to be lost. More importantly than the loss of the score bifolios of the coda itself, composer and Bruckner scholar Robert Simpson asserts in his book The essence of Bruckner, is that the sketches that survive do not support the momentum to support such a conclusion. Some people[who?] think that there is no real inner continuity or coherence inherent to indicate an organically growing musical structure. But in fact, the publications of the Bruckner-Gesamtausgabe edited by John Phillips revealed that Bruckner has left an emerging autograph score, numbered consecutively bifolio by bifolio, which constituted the intact score, at least up to the beginning of the coda. Around 50% of this final phase must be considered lost today. Bruckner knew he might not live to complete this Symphony and suggested his Te Deum to be played at the end of the concert. The presence in the sketches of the figuration heard in quarter-notes at the outset of the Te Deum led to a supposition that Bruckner was composing a link or transition between the two works. In fact, the sketch for such a transition can be found on two bifolios of the emerging autograph score. Some people think, at best this would have been a makeshift solution. The C major setting of the Te Deum conflicts with the D minor setting of the rest of the symphony. Because of this tonal clash, using the Te Deum as the Finale is rarely carried out. Others think one should better follow the composer's own wish and argue against the tonal clash theory, since the Adagio ends in another key (E major) as well."));
		System.out.println(TextSimilarity.termVector.toString());
	}
}
