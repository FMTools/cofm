package collab.fm.server.stats.reporter;

import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.entity.*;
import collab.fm.server.persistence.*;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.exception.BeanPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

/** Report overview of all feature models
 * @author mark
 *
 */
public class ModelOverviewReporter implements Reporter {
	
	private static Logger logger = Logger.getLogger(ModelOverviewReporter.class);

	private static final String TEMPLATE = "[Model Overview Stats]" + NL +
			"Number of feature models: $nfm" + NL +
			"Number of users: $nu" + NL;
	
	@SuppressWarnings("unchecked")
	public void report() {
		try {
			List<Model> models = DaoUtil.getModelDao().getAll();
			String rslt = TEMPLATE.replaceFirst("\\$nfm", (models == null ? "0" : String.valueOf(models.size())));
			
			List<User> users = DaoUtil.getUserDao().getAll();
			rslt = rslt.replaceFirst("\\$nu", (users == null ? "0" : String.valueOf(users.size())));
			
			logger.info(rslt);
		} catch (BeanPersistenceException e) {
			logger.warn("Model overview report failed.", e);
		} catch (StaleDataException e) {
			logger.warn("Model overview report failed.", e);
		}
		
	}

}
