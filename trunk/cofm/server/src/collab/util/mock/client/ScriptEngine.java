package collab.util.mock.client;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

import collab.data.*;
import collab.util.*;
import collab.util.mock.client.*;

public class ScriptEngine implements Engine {
	
	static Logger logger = Logger.getLogger(ScriptEngine.class);

	public static volatile int ratioOfMaxAction = 90;
	
	private static Map<String, Pair<String, ? extends HandlerOptions>> codeMap = 
		new HashMap<String, Pair<String, ? extends HandlerOptions>>();
	static {
		codeMap.put("R", Pair.make(Resources.REQ_REGISTER, new HandlerOptions.NullOptions()));
		codeMap.put("L", Pair.make(Resources.REQ_LOGIN, new HandlerOptions.NullOptions()));
		codeMap.put("C", Pair.make(Resources.REQ_COMMIT, new Commit.Options(Commit.Options.NEW, null)));
		codeMap.put("V", Pair.make(Resources.REQ_COMMIT, new Commit.Options(Commit.Options.VOTE, null)));
		codeMap.put("CV", Pair.make(Resources.REQ_COMMIT, new Commit.Options(Commit.Options.RANDOM, null)));
	}
	
	private static class Action {
		private String code;
		private int lower;
		private int upper;
		private int count = 0;
		
		public Action(String code, int limit) {
			this.code = code;
			this.lower = limit;
			this.upper = limit;
		}
		
		public Action(String code, int lower, int upper) {
			this.code = code;
			this.lower = lower;
			this.upper = upper;
		}
		
		public String next() {
			if (count++ > upper) {
				return null;
			}
			if (count > lower) {
				if (Utils.randomBool(ratioOfMaxAction)) {
					return code;
				}
				return null;
			}
			return code;
		}
	}
	
	private static List<Action> actions = new LinkedList<Action>();
	static {
		try {
			BufferedReader in = new BufferedReader(new FileReader("req_script.txt"));
			String s;
			while((s = in.readLine()) != null) {
				String code;
				int lower;
				int upper;
				if (s.indexOf(" ") >= 0) {
					String[] parts = s.split(" ");
					code = parts[0];
					lower = parts.length > 1 ? new Integer(parts[1]) : 1;
					upper = parts.length > 2 ? new Integer(parts[2]) : lower;
				} else {
					code = s;
					lower = upper = 1;
				}
				actions.add(new Action(code, lower, upper));
			}
		} catch (Exception e) {
			logger.error("Cannot init script engine.", e);
		}
	}
	
	@Override
	public synchronized Pair<String, ? extends HandlerOptions> nextRequest() {
		Action action = actions.get(0);
		String code = action.next();
		while (code == null) {
			try {
				actions.remove(0);
				action = actions.get(0);
				code = action.next();
			} catch (Exception e) {
				return null;
			}
		}
		return codeMap.get(code);
	}

}
