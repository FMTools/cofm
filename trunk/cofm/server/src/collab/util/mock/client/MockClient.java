package collab.util.mock.client;

import java.io.*;
import java.util.*;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import collab.data.bean.*;
import collab.data.*;
import collab.util.*;
import collab.util.mock.OpBean;

public class MockClient {
	
	static Logger logger = Logger.getLogger(MockClient.class);
	
	private static Map<String, Handler> handlers = new HashMap<String, Handler>();
	
	private static final Engine engine = new ScriptEngine();
	
	private static int requestCount = 0;
	
	private static List<User> registeredUsers = new ArrayList<User>();
	private static List<Feature> features = new ArrayList<Feature>();
	
	private static Object userLock = new Object();
	private static Object featLock = new Object();
	private static Object fdLock = new Object();
	private static Object udLock = new Object();
	
	private static List<String> featureDict = new ArrayList<String>(2000);
	private static SortedSet<String> skipFeatureNames = new TreeSet<String>();
	private static List<String> userDict = new ArrayList<String>(6000);
	private static SortedSet<String> skipUserNames = new TreeSet<String>();
	
	static {
		try {
			BufferedReader in = new BufferedReader(
					new FileReader("res/locale/dict/features"));
			String s;
			while ((s = in.readLine()) != null) {
				featureDict.add(s);
			}
			in.close();
			BufferedReader in2 = new BufferedReader(
					new FileReader("res/locale/dict/names"));
			while ((s = in2.readLine()) != null) {
				userDict.add(s);
			}
			in2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addHandler(String requestName, Handler handler) {
		handlers.put(requestName, handler);
	}
	
	public void onSuccess(Response.Body body) {
		logger.debug("Success: " + Utils.beanToJson(body.getSource()));
	}
	
	public void onDenied(Response.Body body) {
		if (body.getData() == null) {
			body.setData(new String(""));
		}
		logger.info("Denied: " + body.getData().toString() + " " + Utils.beanToJson(body.getSource()));
	}
	
	public void onForward(Response.Body body) {
		logger.debug("Forwarded: " + Utils.beanToJson(body.getSource()));
	}
	
	public void onError(Response.Body body) {
		if (body.getData() == null) {
			body.setData(new String(""));
		}
		logger.info("Server Error: " + body.getData().toString() + " " + Utils.beanToJson(body.getSource()));
	}
	
	public void addRegUser(User u) {
		synchronized (userLock) {
			registeredUsers.add(u);
		}
	}
	
	public void removeAllFeatures() {
		synchronized (featLock) {
			features.clear();
		}
	}
	
	public Feature getFeature(int id) {
		synchronized (featLock) {
			if (id < features.size()) {
				return features.get(id);
			} else {
				Feature f = new Feature();
				f.setId(id);
				features.add(f);
				return f;
			}
		}
	}
	
	public void addFeature(Feature f) {
		synchronized (featLock) {
			features.add(f);
		}
	}
	
	public void setFeature(Feature f) {
		synchronized (featLock) {
			if (f.getId() > features.size()) {
				logger.warn("Inconsistent feature list in client: max = " + features.size() + ", but getId() returns " + f.getId());
				return;
			}
			features.set(f.getId(), f);
		}
	}
	
	public void printFeatures() {
		synchronized (featLock) {
			
		}
	}
	
	public String sendRequest() {
		Pair<String, ? extends HandlerOptions> next = engine.nextRequest();
		if (next.first != null) {
			try {
				Handler handler = handlers.get(next.first);
				Request req = handler.send(next.second);
				req.setAddress("localhost");
				req.setId(nextId());
				return Utils.beanToJson(req);
			} catch (Exception e) {
				logger.warn("Sender exception.", e);
				return null;
			}
		}
		return null;
	}
	
	public void dispatchResponse(Response rsp) {
		try {
			Map<String, Class> clsMap = new HashMap<String, Class>();
			clsMap.put("source", Response.Body.Source.class);
			Response.Body body = Utils.jsonToBean(rsp.getBody(),
					Response.Body.class, clsMap);

			Handler handler = handlers.get(body.getSource().getName());
			handler.recv(body);
		} catch (Exception e) {
			logger.warn("Receiver exception.", e);
		}
	}
	
	public String randomUserName() {
		synchronized (udLock) {
			return randomSelect(userDict, skipUserNames);
		}
	}
	
	public String randomFeatureName() {
		synchronized (fdLock) {
			return randomSelect(featureDict, skipFeatureNames);
		}
	}
	
	public User randomUser() {
		synchronized (userLock) {
			return randomSelect(registeredUsers, null);
		}
	}
	
	public Feature randomFeature() {
		synchronized (featLock) {
			return randomSelect(features, null);
		}
	}
	
	private <T> T randomSelect(List<T> list, SortedSet<T> skipped) {
		int max = list.size();
		T result;
		int i = 0;
		do {
			int index = RandomUtils.nextInt(max);
			result = list.get(index);
		} while (i++ < max && skipped != null && skipped.contains(result));
		if (skipped != null) {
			skipped.add(result);
		}
		return result;
	}

	private static synchronized int nextId() {
		return requestCount++;
	}
}
