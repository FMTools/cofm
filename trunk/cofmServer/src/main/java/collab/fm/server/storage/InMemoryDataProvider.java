package collab.fm.server.storage;

import java.util.*;

import org.apache.log4j.Logger;

import collab.fm.server.bean.*;

public class InMemoryDataProvider implements DataProvider {
	
	static Logger logger = Logger.getLogger(InMemoryDataProvider.class);
	
	private static int opId = 0;
	private static int featureId = 0;
	private static int userId = 0;
	
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

	public Operation commitOperation(Operation op) {
		op.setId(nextOpId());
		synchronized (opLock) {
			ops.add(op);
		}
		return op;
	}

	public Feature getFeatureById(Integer id) {
		synchronized (featLock) {
			if (id < 0 || id > features.size()) {
				return null;
			}
			return features.get(id);
		}
	}

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

	public List<Feature> getRecentFeatures(Integer beginId) {
		List<Feature> result = null;
		synchronized (featLock) {
			if (beginId <= features.size()) {
				if (beginId < 0) {
					beginId = 0;
				}
				result = new ArrayList<Feature>();
				for (int i = beginId; i < features.size(); i++) {
					result.add(features.get(i));
				}
			}
		}
		return result;
	}

	public List<Operation> getRecentOperations(Integer beginId) {
		List<Operation> result = null;
		synchronized (opLock) {
			if (beginId <= ops.size()) {
				if (beginId < 0) {
					beginId = 0;
				}
				result = new ArrayList<Operation>();
				for (int i = beginId; i < ops.size(); i++) {
					result.add(ops.get(i));
				}
			}
		}
		return result;
	}

	public Integer getUserIdByName(String username) {
		synchronized (userLock) {
			return userNames.get(username);
		}
	}

	public boolean updateFeature(Feature f) {
		synchronized (featLock) {
			if (f.getId() < 0 || f.getId() > features.size()) {
				return false;
			}
			features.set(f.getId(), f);
			return true;
		}
	}

	public User addUser(User user) {
		synchronized (userLock) {
			user.setId(nextUserId());
			users.add(user);
			return user;
		}
	}

}