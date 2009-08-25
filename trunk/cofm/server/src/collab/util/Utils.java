package collab.util;

import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import net.sf.json.*;
import net.sf.json.util.PropertyFilter;

public class Utils {
	
	static Logger logger = Logger.getLogger(Utils.class);
	
	public static String beanToJson(Object bean) {
		return beanToJson(bean, null);
	}
	
	public static String beanToJson(Object bean, final String[] skipFields) {
		try {
			JsonConfig cfg = new JsonConfig();
			cfg.setJsonPropertyFilter(new PropertyFilter() {    
			    public boolean apply(Object source, String name, Object value) {
			    	if (skipFields != null) {
			    		for (String skip: skipFields) {
			    			if (skip.equals(name)) {
			    				return true;
			    			}
			    		}
			    	}
			    	return false;
			    }
			});
			JSONObject jsonObj = (JSONObject) JSONSerializer.toJSON(bean, cfg);
			return jsonObj.toString();
		} catch (Exception e) {
			logger.warn("Can't build JSON string from bean: " + bean.toString());
			return "{beanToJsonFailure: true}";
		}
	} 
	
	public static <T> T jsonToBean(Object srcJson, Class<T> beanClass, Map<String, Class> clsMap) {
		JSON json = JSONSerializer.toJSON(srcJson);
		JsonConfig cfg = new JsonConfig();
		cfg.setRootClass(beanClass);
		if (clsMap != null) {
			cfg.setClassMap(clsMap);
		}
		return beanClass.cast(JSONSerializer.toJava(json, cfg));
	}
	
	public static Boolean randomBool(int possibilityOfTrue) {
		int p = RandomUtils.nextInt(100);
		if (p <= possibilityOfTrue) {
			return true;
		}
		return false;
	}
	
	public static <T> T randomSelect(T[] candidates) {
		return candidates[RandomUtils.nextInt(candidates.length)];
	}
	
	public static String randomString(int maxlength, String[] candidates) {
		if (candidates != null && candidates.length > 0) {
			return candidates[RandomUtils.nextInt(candidates.length)];
		}
		return RandomStringUtils.randomAlphabetic(maxlength);
	}
	
	public static Object randomIntOrString(int maxVal, int maxStrLen, String[] candidates) {
		boolean needName = RandomUtils.nextBoolean();
		if (needName) {
			return Utils.randomString(maxStrLen, candidates);
		}
		return new Integer(RandomUtils.nextInt(maxVal) + 1);
	}
	
	public static String randomSocketAddress() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			sb.append(randomIPv4Part(i > 0));
			if (i < 3) {
				sb.append('.');
			}
		}
		sb.append(":");
		sb.append(randomPort());
		return sb.toString();
	}
	
	private static String randomIPv4Part(boolean allowZero) {
		Integer num = null;
		if (allowZero) {
			num = new Integer(RandomUtils.nextInt(256)); // 0 to 255
		} else {
			num = new Integer(RandomUtils.nextInt(255) + 1);  // 1 to 255
		}
		return num.toString();
	}
	
	private static String randomPort() {
		StringBuilder sb = new StringBuilder();
		sb.append(RandomUtils.nextInt(9) + 1); // 1 to 9
		sb.append(RandomStringUtils.randomNumeric(RandomUtils.nextInt(2) + 3)); // 3 to 4 digits
		Integer port = new Integer(sb.toString());
		if (port > 65535) {
			port -= 65535;
		}
		if (port < 1000) {
			port += 1000;
		}
		return port.toString();
	}
}
