package collab.util.mock;

import org.apache.log4j.Logger;

import collab.data.*;
import collab.util.*;

/**
 * @deprecated
 * @author Administrator
 *
 */
public class CommitGenerator implements Generator<String, String> {
	
	static Logger logger = Logger.getLogger(CommitGenerator.class);
	
	private static CommitGenerator gen = null;
	
	private static int count = 0;
	private static final int maxUserId = 30;
	private static final int maxUserNameLen = 8;
	private static final String[] userNames = {};

	private CommitGenerator() {
		
	}
	
	public static synchronized CommitGenerator getInstance() {
		if (gen == null) {
			gen = new CommitGenerator();
		}
		return gen;
	}
	
	public String next() {
		return nextWithOp(OpGenerator.getInstance().next());
	}
	
	public String next(String opName) {
		return nextWithOp(OpGenerator.getInstance().next(opName));
	}
	
	private String nextWithOp(Object op) {
		Request req = new Request();
		req.setData(op);
		req.setId(nextCount());
		req.setName(Resources.REQ_COMMIT);
		req.setUser(Utils.randomIntOrString(maxUserId, maxUserNameLen, userNames).toString());
		// In a "real" client, no Address field will be sent, so we need to skip it here.
		return Utils.beanToJson(req, new String[] {"address"}); 
	}
	
	
	private static synchronized int nextCount() {
		return ++count;
	}
	
}
