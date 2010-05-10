package collab.fm.server.stats;

import java.util.ArrayList;
import java.util.List;

import collab.fm.server.stats.reporter.Reporter;

/**
 * Report information about existing feature models and tool users.
 * @author mark
 *
 */
public class ReporterRunner {
	
	public List<Reporter> reporters = new ArrayList<Reporter>();
	
	public ReporterRunner() {
		initReporters();
	}
	
	public void registerReporter(Reporter r) {
		reporters.add(r);
	}
	
	private void initReporters() {
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ReporterRunner runner = new ReporterRunner();
		for (Reporter r: runner.reporters) {
			r.report();
		}
	}

}
