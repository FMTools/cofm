package collab.util.mock;

import java.util.*;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

import collab.data.bean.*;
import collab.data.*;
import collab.util.*;

public class MockClient {
	
	private static List<User> registeredUsers = new ArrayList<User>();
	private static List<Feature> features = new ArrayList<Feature>();
	
	private static Object userLock = new Object();
	private static Object featLock = new Object();
	
	public volatile int ratioOfVoteYes = 70;
	public volatile int ratioOfAddNewFeature = 40;
	
	private String commit() {
		Request req = new Request();
		
		OpBean op = new OpBean();
		op.setOp(Operation.NAMES[RandomUtils.nextInt(Operation.NAMES.length)]);
		Feature left = randomFeature();
		op.setLeft(selectIdOrName(left.getId(), left.names()));
		op.setRight(getRightOperand(op.getOp()));
		op.setVote(Utils.randomBool(ratioOfVoteYes));
		req.setData(op);
		
		req.setName(Resources.REQ_COMMIT);
		User u = randomUser();
		req.setUser(selectIdOrName(u.getId(), u.getName()).toString());
		
		return Utils.beanToJson(req, new String[] {"address"}); 
	}
	
	private Object getRightOperand(String name) {
		if (Resources.OP_ADDDES.equals(name)) {
			return RandomStringUtils.randomAlphabetic(30);
		}
		if (Resources.OP_ADDNAME.equals(name)) {
			//TODO: select meaningful name from a dictionary or from randomFeature()
			return "name";
		}
		if (Resources.OP_SETEXT.equals(name) || Resources.OP_SETOPT.equals(name)) {
			return "ignored";
		}
		if (Resources.OP_ADDCHILD.equals(name)) {
			//TODO: generate new feature for add_child, or from randomFeature();
		}
		
		Feature f = randomFeature();
		return selectIdOrName(f.getId(), f.names());
	}
	
	private User randomUser() {
		synchronized (userLock) {
			Integer i = registeredUsers.size();
			i = RandomUtils.nextInt(i);
			User u = registeredUsers.get(i);
			return u;
		}
	}
	
	private Feature randomFeature() {
		synchronized (featLock) {
			Integer featIndex = features.size();
			featIndex = RandomUtils.nextInt(featIndex);
			Feature f = features.get(featIndex);
			return f;
		}
	}
	
	private Object selectIdOrName(Integer id, String name) {
		return Utils.randomSelect(new Object[]{id, name});
	}
	
	private Object selectIdOrName(Integer id, String[] names) {
		String name = (String)Utils.randomSelect(names);
		return selectIdOrName(id, name);
	}
	
}
