package collab.fm.mining.constraint.stats;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import collab.fm.mining.constraint.FeaturePair;

public class LocalAttributeStats implements DataStats {

	private int[] simTotal = new int[33];
	private int[] simObject = new int[33];
	private int[] sim1asObj = new int[33];
	private int[] sim2asObj = new int[33];
	
	public void report(BufferedWriter out) throws IOException {
		String head = String.format("*** Attribute distribution over class.\n"
				+ "%10s%10s%10s%10s\n"
				+ "-------------------------------------------------------\n",
				" ", "NO_CONS", "REQUIRE", "EXCLUDE");
		String simAttrs = formatSimInfo("Sim_Total", simTotal)
				+ formatSimInfo("Sim_Object", simObject)
				+ formatSimInfo("1_as_Obj", sim1asObj)
				+ formatSimInfo("2_as_Obj", sim2asObj);
		out.write(head + simAttrs + "\n");
	}

	public void update(List<FeaturePair> pairs) {
		for (FeaturePair pair: pairs) {
			addSimInfo(pair.getTotalSim(), pair.getLabel(), this.simTotal);
			addSimInfo(pair.getObjectSim(), pair.getLabel(), this.simObject);
			addSimInfo(pair.getFirstAsObject(), pair.getLabel(), this.sim1asObj);
			addSimInfo(pair.getSecondAsObject(), pair.getLabel(), this.sim2asObj);
			
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
