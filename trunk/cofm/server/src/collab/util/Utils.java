package collab.util;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
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
	
	public static String randomName(int maxlength, String[] candidates) {
		if (candidates != null && candidates.length > 0) {
			return candidates[RandomUtils.nextInt(candidates.length)];
		}
		return RandomStringUtils.randomAlphabetic(maxlength);
	}
	
	public static Object randomIdOrName(int maxVal, int maxStrLen, String[] candidates) {
		boolean needName = RandomUtils.nextBoolean();
		if (needName) {
			return Utils.randomName(maxStrLen, candidates);
		}
		return new Integer(RandomUtils.nextInt(maxVal) + 1);
	}
}
