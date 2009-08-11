package collab.util.mock;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import collab.data.bean.Feature;
import collab.data.bean.Operation;
import collab.data.bean.User;

import collab.storage.DataProvider;
import collab.util.Utils;
/**@deprecated
 * Mock class for unit test. Almost do nothing.
 * @author Yi Li
 *
 */
public class MockDataProvider implements DataProvider {

	static Logger logger = Logger.getLogger(MockDataProvider.class);
	
	@Override
	public Operation commitOperation(Operation op) {
		op.setId(1);
		logger.info("Committed " + op.toString());
		return op;
	}

	@Override
	public Feature getFeatureById(Integer id) {
		Feature feat = new Feature();
		feat.setId(id);
		return feat;
	}

	@Override
	public Integer getFeatureIdByName(String name) {
		return 1;
	}

	@Override
	public List<Feature> getRecentFeatures(Integer beginId) {
		Feature feat = new Feature();
		feat.setId(beginId);
		Feature feat2 = new Feature();
		feat2.setId(beginId + 1);
		return Arrays.asList(feat, feat2);
	}

	@Override
	public List<Operation> getRecentOperations(Integer beginId) {
		Operation op = new Operation();
		op.setId(beginId);
		Operation op2 = new Operation();
		op2.setId(beginId + 1);
		return Arrays.asList(op, op2);
	}

	@Override
	public Integer getUserIdByName(String username) {
		return 1;
	}

	@Override
	public boolean updateFeature(Feature f) {
		logger.info("Updated " + f.toString());
		return true;
	}

	@Override
	public User addUser(User user) {
		user.setId(1);
		return user;
	}

}
