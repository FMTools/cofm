package collab.fm.server.stats;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import collab.fm.server.persistence.HibernateUtil;
import collab.fm.server.stats.reporter.ModelOverviewReporter;
import collab.fm.server.stats.reporter.Reporter;

/**
 * Report information about existing feature models and tool users.
 * @author mark
 *
 */
public class ReporterRunner {
	
	private static Logger logger = Logger.getLogger(ReporterRunner.class);
	
	public List<Reporter> reporters = new ArrayList<Reporter>();
	
	public ReporterRunner() {
		initReporters();
	}
	
	public void registerReporter(Reporter r) {
		reporters.add(r);
	}
	
	private void initReporters() {
		registerReporter(new ModelOverviewReporter());
		//registerReporter(new ModelReporter());
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ReporterRunner runner = new ReporterRunner();
		logger.info("----------------------------------------");
		logger.info((new Date().toString()));
		logger.info("----------------------------------------");
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		for (Reporter r: runner.reporters) {
			r.report();
			logger.info("  "); // log a new line
		}
		
		session.getTransaction().commit();
	}

}
