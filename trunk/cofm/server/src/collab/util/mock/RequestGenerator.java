package collab.util.mock;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import collab.data.*;
import collab.util.*;

public class RequestGenerator {
	
	static Logger logger = Logger.getLogger(RequestGenerator.class);
	
	private static int count = 0;
	private static final int maxUserId = 30;
	private static final int maxUserNameLen = 8;
	private static final String[] userNames = {};
	
	public static String nextIPv4() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			sb.append(trimHeadZeros(RandomStringUtils.randomNumeric(3)));
			if (i < 3) {
				sb.append('.');
			}
		}
		return sb.toString();
	}
	
	private static String trimHeadZeros(String s) {
		boolean needTrim = true;
		for (int i = 0; i < s.length(); i++) {
			if (!needTrim) {
				return s.substring(i);
			}
			if (s.charAt(i) != '0') {
				needTrim = false;
			}
		}
		return "0"; // trim at most s.length - 1 zeros.
	}
	
	public static String nextCommit() {
		return nextCommitWithOp(OpGenerator.nextOp());
	}
	
	public static String nextCommit(String op) {
		return nextCommitWithOp(OpGenerator.nextOp(op));
		
	}
	
	private static String nextCommitWithOp(Object op) {
		Request req = new Request();
		req.setData(op);
		req.setId(nextCount());
		req.setName(Resources.REQ_COMMIT);
		req.setUser(Utils.randomIdOrName(maxUserId, maxUserNameLen, userNames).toString());
		// In a "real" client, no Address field will be sent, so we need to skip it here.
		return Utils.beanToJson(req, new String[] {"address"}); 
	}
	
	
	private static synchronized int nextCount() {
		return ++count;
	}
	
}
