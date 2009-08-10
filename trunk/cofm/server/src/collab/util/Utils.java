package collab.util;

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
}
