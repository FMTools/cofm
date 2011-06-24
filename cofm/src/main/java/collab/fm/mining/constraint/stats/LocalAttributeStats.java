package collab.fm.mining.constraint.stats;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import collab.fm.mining.constraint.FeaturePair;

public class LocalAttributeStats implements DataStats {

	private int[] simTotal = new int[33];
	private int[] simVerb = new int[33];
	private int[] simNoun = new int[33];
	
	private int[] reqOutNo = {0, 0, 0}; // require outsider = NO
	private int[] reqOutYes = {0, 0, 0}; // require outsider = 1 or 2
	
	private int[] excOutNo = {0, 0, 0}; // exclude outsider = NO
	private int[] excOutYes = {0, 0, 0}; // exclude outsider = 1 or 2
	
	public void report(BufferedWriter out) throws IOException {
		String head = String.format("*** Attribute distribution over class.\n"
				+ "%10s%10s%10s%10s\n"
				+ "-------------------------------------------------------\n",
				" ", "NO_CONS", "REQUIRE", "EXCLUDE");
		String simAttrs = formatSimInfo("Sim_Total", simTotal)
				+ formatSimInfo("Sim_Verb", simVerb)
				+ formatSimInfo("Sim_Noun", simNoun);
		String otherAttrs = String.format(
				"%10s%10d%10d%10d\n"   // No require out
				+ "%10s%10d%10d%10d\n"   // Has 
				+ "%10s%10d%10d%10d\n"   // No exclude out
				+ "%10s%10d%10d%10d",    // Has
				"No_Req_Out", reqOutNo[0], reqOutNo[1], reqOutNo[2],
				"Has", reqOutYes[0], reqOutYes[1], reqOutYes[2],
				"No_Exc_Out", excOutNo[0], excOutNo[1], excOutNo[2],
				"Has", excOutYes[0], excOutYes[1], excOutYes[2]
				);
		out.write(head + simAttrs + otherAttrs + "\n");
	}

	public void update(List<FeaturePair> pairs) {
		for (FeaturePair pair: pairs) {
			addSimInfo(pair.getTotalSim(), pair.getLabel(), this.simTotal);
			addSimInfo(pair.getVerbSim(), pair.getLabel(), this.simVerb);
			addSimInfo(pair.getNounSim(), pair.getLabel(), this.simNoun);
			
			int index = (pair.getLabel() == FeaturePair.NO_CONSTRAINT ? 0 : 
				(pair.getLabel() == FeaturePair.REQUIRE ? 1 : 2));
			if (pair.getRequireOut() == FeaturePair.NO) {
				reqOutNo[index]++;
			} else if (pair.getRequireOut() >= 1) {
				reqOutYes[index]++;
			}
			if (pair.getExcludeOut() == FeaturePair.NO) {
				excOutNo[index]++;
			} else if (pair.getExcludeOut() >= 1) {
				excOutYes[index]++;
			}
		}

	}

	private void addSimInfo(double sim, int label, int[] a) {
		int offset = (label == FeaturePair.NO_CONSTRAINT ? 0 :
			(label == FeaturePair.REQUIRE ? 1 : 2));
		int base = Double.valueOf(Math.floor(10 * sim)).intValue();
		a[base + offset]++;
	}
	
	private String formatSimInfo(String title, int[] sim) {
		StringBuilder s = new StringBuilder(title + "\n");
		for (int i = 0; i < 11; i++) {
			s.append(String.format("%10s%10d%10d%10d\n", 
					(i ==10 ? "1.0" : "[0." + i + "~"), 
					sim[i*3], sim[i*3+1], sim[i*3+2]));
		}
		return s.toString();
	}
}
