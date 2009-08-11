package collab.storage;

import java.util.*;

import org.apache.log4j.Logger;

import collab.data.bean.Feature;
import collab.data.bean.Operation;
import collab.data.bean.User;

public class InMemoryDataProvider implements DataProvider {
	
	static Logger logger = Logger.getLogger(InMemoryDataProvider.class);
	
	private static int opId = 1;
	private static int featureId = 1;
	private static int userId = 1;
	
	private static Object opLock = new Object();
	private static Object featLock = new Object();
	private static Object userLock = new Object();
	
	private static int nextOpId() {
		synchronized (opLock) {
			return opId++;
		}
	}
	
	private static int nextFeatureId() {
		synchronized (featLock) {
			return featureId++;
		}
	}
	
	private static int nextUserId() {
		synchronized (userLock) {
			return userId++;
		}
	}
	
	private static List<Feature> features = new ArrayList<Feature>(100);
	private static Map<String, Integer> featureNames = new HashMap<String, Integer>();
	
	private static List<Operation> ops = new ArrayList<Operation>(1000);
	
	private static Map<String, Integer> userNames = new HashMap<String, Integer>();
	private static List<User> users = new ArrayList<User>(50);

	@Override
	public Operation commitOperation(Operation op) {
		op.setId(nextOpId());
		synchronized (opLock) {
			ops.add(op);
		}
		return op;
	}

	@Override
	public Feature getFeatureById(Integer id) {
		synchronized (featLock) {
			if (id < 1 || id > features.size()) {
				return null;
			}
			return features.get(id - 1);
		}
	}

	@Override
	public Integer getFeatureIdByName(String name) {
		synchronized (featLock) {
			Integer id = featureNames.get(name);
			if (id == null) {
				id = nextFeatureId();
				featureNames.put(name, id);
				Feature f = new Feature();
				f.setId(id);
				features.add(f);
			}
			return id;
		}
	}

	@Override
	public List<Feature> getRecentFeatures(Integer beginId) {
		List<Feature> result = null;
		synchronized (featLock) {
			if (beginId <= features.size()) {
				if (beginId < 1) {
					beginId = 1;
				}
				result = new ArrayList<Feature>();
				for (int i = beginId - 1; i < features.size(); i++) {
					result.add(features.get(i));
				}
			}
		}
		return result;
	}

	@Override
	public List<Operation> getRecentOperations(Integer beginId) {
		List<Operation> result = null;
		synchronized (opLock) {
			if (beginId <= ops.size()) {
				if (beginId < 1) {
					beginId = 1;
				}
				result = new ArrayList<Operation>();
				for (int i = beginId - 1; i < ops.size(); i++) {
					result.add(ops.get(i));
				}
			}
		}
		return result;
	}

	@Override
	public Integer getUserIdByName(String username) {
		synchronized (userLock) {
			return userNames.get(username);
		}
	}

	@Override
	public boolean updateFeature(Feature f) {
		synchronized (featLock) {
			if (f.getId() < 1 || f.getId() > features.size()) {
				return false;
			}
			features.set(f.getId() - 1, f);
			return true;
		}
	}

	@Override
	public User addUser(User user) {
		synchronized (userLock) {
			user.setId(nextUserId());
			users.add(user);
			return user;
		}
	}

}
