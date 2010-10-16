package collab.fm.server.stats;

import java.text.DecimalFormat;
import java.util.Collection;

public class StatsUtil {
	
	public static String nullSafeSize(Collection<?> c) {
		return String.valueOf(nullSafeIntSize(c));
	}
	
	public static int nullSafeIntSize(Collection<?> c) {
		return c == null ? 0 : c.size();
	}
	
	public static String zeroSafeAvg(int total, int divider) {
		if (divider <= 0) {
			return "N/A";
		}
		double avg = ((double)total) / divider;
		return new DecimalFormat(".00").format(avg);
	}
	
	public static float toPercentage(int yes, int no) {
		if (no == 0) {
			return (yes == 0 ? Float.NaN : 100.0f);
		}
		return (float) (100.0 * yes / (yes + no));
	}
}
